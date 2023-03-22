package eyre

import core.memory.NativeWriter
import java.time.Instant

class Linker(
	private val compiler        : Compiler,
	private val assemblerOutput : AssemblerOutput
) {


	private val time = Instant.now().epochSecond.toInt()

	private val writer = NativeWriter()

	private val sectionAlignment = 0x1000

	private val fileAlignment = 0x200

	private val Int.roundToSectionAlignment get() = (this + sectionAlignment - 1) and -sectionAlignment

	private val Int.roundToFileAlignment get() = (this + fileAlignment - 1) and -fileAlignment



	/*
	Sections
	 */



	private var nextSectionRva = sectionAlignment

	private var nextSectionPos = fileAlignment

	private var numSections = 0

	private val sectionAddresses = IntArray(Section.values().size)

	private val sectionPositions = IntArray(Section.values().size)

	private val Section.address get() = sectionAddresses[ordinal]

	private val Section.position get() = sectionPositions[ordinal]



	private fun writeVirtualSection(
		name            : String,
		characteristics : Int,
		size            : Int,
		section         : Section
	) {
		val virtualAddress = nextSectionRva

		writer.pos = sectionHeadersPos + numSections * 40
		numSections++

		writer.ascii8(name)
		writer.i32(size)
		writer.i32(virtualAddress)
		writer.i32(0)
		writer.i32(0)
		writer.zero(12)
		writer.i32(characteristics)

		nextSectionRva += size.roundToSectionAlignment

		writer.pos = nextSectionPos

		sectionPositions[section.ordinal] = 0
		sectionAddresses[section.ordinal] = virtualAddress
	}



	private fun writeSection(
		name            : String,
		characteristics : Int,
		bytes           : ByteArray,
		extraSize       : Int,
		section         : Section
	) {
		val virtualAddress = nextSectionRva // must be aligned to sectionAlignment
		val rawDataPos     = nextSectionPos // must be aligned to fileAlignment
		val rawDataSize    = bytes.size.roundToFileAlignment // must be aligned to fileAlignment
		val virtualSize    = bytes.size + extraSize // no alignment requirement, may be smaller than rawDataSize

		writer.bytes(bytes)

		nextSectionPos += rawDataSize
		nextSectionRva += virtualSize.roundToSectionAlignment

		writer.zeroTo(nextSectionPos)

		writer.pos = sectionHeadersPos + numSections * 40
		numSections++

		writer.ascii8(name)
		writer.i32(virtualSize)
		writer.i32(virtualAddress)
		writer.i32(rawDataSize)
		writer.i32(rawDataPos)
		writer.zero(12)
		writer.i32(characteristics)

		writer.pos = nextSectionPos

		sectionPositions[section.ordinal] = rawDataPos
		sectionAddresses[section.ordinal] = virtualAddress
	}



	/*
	Linking
	 */




	fun link(): ByteArray {
		writeHeaders()
		writeSections()

		for(relocation in assemblerOutput.relocations)
			writeRelocation(relocation)

		val entryPoint = compiler.entryPoint ?: error("No entry point")
		writer.i32(entryPointPosPos, entryPoint.pos + entryPoint.section.address)
		writer.i32(imageSizePos, nextSectionRva)
		writer.i32(numSectionsPos, numSections)

		return writer.getTrimmedBytes(nextSectionPos)
	}



	private fun writeSections() {
		val text = assemblerOutput.text
		val data = assemblerOutput.data
		val bssSize = assemblerOutput.bssSize
		
		if(text.isEmpty())
			error("No text section")

		writeSection(".text", 0x60_00_00_20, text, 0, Section.TEXT)

		if(data.isNotEmpty())
			writeSection(".data", 0xC0_00_00_40L.toInt(), data, 0, Section.DATA)

		if(bssSize > 0)
			writeVirtualSection(".bss", 0xC0_00_00_80L.toInt(), bssSize, Section.BSS)

		writeImports()
	}



	private fun writeHeaders() {
		writer.i16(0x5A4D)
		writer.zeroTo(0x3C)
		writer.i32(0x40)

		writer.i32(0x4550)     // signature
		writer.i16(0x8664)     // machine
		writer.i16(0)          // numSections    (fill in later)
		writer.i32(time)       // timeDateStamp
		writer.i32(0)          // pSymbolTable
		writer.i32(0)          // numSymbols
		writer.i16(0xF0)       // optionalHeaderSize
		writer.i16(0x0022)     // characteristics, DYNAMIC_BASE | LARGE_ADDRESS_AWARE | EXECUTABLE

		writer.i16(0x20B)      // magic
		writer.i16(0)          // linkerVersion
		writer.i32(0)          // sizeOfCode
		writer.i32(0)          // sizeOfInitialisedData
		writer.i32(0)          // sizeOfUninitialisedData
		writer.i32(0x1000)     // pEntryPoint    (fill in later)
		writer.i32(0)          // baseOfCode
		writer.i64(0x400000)   // imageBase
		writer.i32(0x1000)     // sectionAlignment
		writer.i32(0x200)      // fileAlignment
		writer.i16(6)          // majorOSVersion
		writer.i16(0)          // minorOSVersion
		writer.i32(0)          // imageVersion
		writer.i16(6)          // majorSubsystemVersion
		writer.i16(0)          // minorSubsystemVersion
		writer.i32(0)          // win32VersionValue
		writer.i32(0)          // sizeOfImage    (fill in later)
		writer.i32(0x200)      // sizeOfHeaders
		writer.i32(0)          // checksum
		writer.i16(3)          // subsystem
		writer.i16(0x140)      // dllCharacteristics
		writer.i64(0x100000)   // stackReserve
		writer.i64(0x1000)     // stackCommit
		writer.i64(0x100000)   // heapReserve
		writer.i64(0x1000)     // heapCommit
		writer.i32(0)          // loaderFlags
		writer.i32(16)         // numDataDirectories
		writer.zero(16 * 8)    // dataDirectories

		writer.advanceTo(0x200) // section headers    (fill in later)

		nextSectionPos = 0x200
		nextSectionRva = 0x1000
	}



	private fun writeImports() {
		val dlls = compiler.dllImports.dllMap.values

		if(dlls.isEmpty()) return

		val idtsRva = nextSectionRva

		val idtsPos = writer.pos
		val idtsSize = dlls.size * 20 + 20

		val offset = idtsPos - idtsRva

		writer.i32(importDataDirectoryPos, idtsRva)
		writer.i32(importDataDirectoryPos + 4, dlls.size * 20 + 20)

		writer.zero(idtsSize)

		for((dllIndex, dll) in dlls.withIndex()) {
			val imports = dll.map.values

			val idtPos = idtsPos + dllIndex * 20

			val dllNamePos = writer.pos

			writer.asciiNT("${dll.name.string}.dll")
			writer.align8()

			val iltPos = writer.pos

			writer.zero(imports.size * 8 + 8)

			val iatPos = writer.pos

			writer.zero(imports.size * 8 + 8)

			for((importIndex, import) in imports.withIndex()) {
				writer.i32(iltPos + importIndex * 8, writer.pos - offset)
				writer.i32(iatPos + importIndex * 8, writer.pos - offset)
				writer.i16(0)
				writer.asciiNT(import.name.string)
				writer.alignEven()
				import.symbol.pos = iatPos + importIndex * 8 - idtsPos
			}

			writer.i32(idtPos, iltPos - offset)
			writer.i32(idtPos + 12, dllNamePos - offset)
			writer.i32(idtPos + 16, iatPos - offset)
		}

		val size = writer.pos - idtsPos

		writeSection(".idata", 0x40_00_00_40, writer.getTrimmedBytes(idtsPos, size), 0, Section.RDATA)
	}



	private fun AstNode.resolveRelocation(): Long {
		if(this is ImmNode)    return value.resolveRelocation()
		if(this is IntNode)    return value
		if(this is UnaryNode)  return op.calculate(node.resolveRelocation())
		if(this is BinaryNode) return op.calculate(left.resolveRelocation(), right.resolveRelocation())

		fun symbol(symbol: Symbol?): Long {
			if(symbol == null) error("Missing symbol")
			if(symbol is IntSymbol) return symbol.value
			if(symbol !is Ref) error("Invalid symbol: $symbol")
			return symbol.pos + symbol.section.address.toLong()
		}

		if(this is DotNode) return symbol(right.symbol)
		if(this is SymNode) return symbol(symbol)

		error("Invalid node: $this")
	}



	private fun writeRelocation(relocation: Relocation) {
		if(relocation.width != Width.BIT32)
			error("Invalid relocation width: $relocation")

		var value = relocation.value.resolveRelocation()

		if(relocation.base != null)
			value -= relocation.base.section.address + relocation.base.pos

		if(!value.isImm32) error("relocation out of range")

		writer.i32(relocation.position + relocation.section.position, value.toInt())
	}


}



/*
Header positions
 */



private const val numSectionsPos = 70

private const val entryPointPosPos = 104

private const val imageSizePos = 144

private const val importDataDirectoryPos = 208

private const val sectionHeadersPos = 328