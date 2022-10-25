package assembler

import core.associateFlatMap
import core.binary.BinaryWriter

class Linker(private val assemblerResult: AssemblerResult) {


	private val writer = BinaryWriter()

	private val sectionAlignment = 0x1000

	private val fileAlignment = 0x200

	private val importDataDirectoryPos = 208

	private val rdataHeaderPos = 368

	private val dataHeaderPos = 408

	private val pEntryPointPos = 104

	private val imageSizePos = 144

	private val Int.roundToSectionAlignment get() = (this + sectionAlignment - 1) and -sectionAlignment

	private val Int.roundToFileAlignment get() = (this + fileAlignment - 1) and -fileAlignment

	private var importDirectoryRva = 0

	private var importDirectorySize = 0

	private var textRva = 0x1000

	private var rdataRva = 0

	private var rdataSize = 0

	private var dataRva = 0

	private var rdataPos = 0

	private val Section.rva get() = when(this) {
		Section.TEXT  -> textRva
		Section.RDATA -> rdataRva
		Section.DATA  -> dataRva
		Section.BSS   -> dataRva
	}



	fun link(): ByteArray {
		writer.u16(0x5A4D)
		writer.zeroTo(0x3C)
		writer.u32(0x40)

		writer.u32(0x4550)     // signature
		writer.u16(0x8664)     // machine
		writer.u16(2)          // numSections
		writer.u32(0)          // timeDateStamp
		writer.u32(0)          // pSymbolTable
		writer.u32(0)          // numSymbols
		writer.u16(0xF0)       // optionalHeaderSize
		writer.u16(0x0022)     // characteristics, DYNAMIC_BASE | LARGE_ADDRESS_AWARE | EXECUTABLE

		writer.u16(0x20B)      // magic
		writer.u16(0)          // linkerVersion
		writer.u32(0)          // sizeOfCode
		writer.u32(0)          // sizeOfInitialisedData
		writer.u32(0)          // sizeOfUninitialisedData
		writer.u32(0x1000)     // pEntryPoint
		writer.u32(0)          // baseOfCode
		writer.u64(0x400000)   // imageBase
		writer.u32(0x1000)     // sectionAlignment
		writer.u32(0x200)      // fileAlignment
		writer.u16(6)          // majorOSVersion
		writer.u16(0)          // minorOSVersion
		writer.u32(0)          // imageVersion
		writer.u16(6)          // majorSubsystemVersion
		writer.u16(0)          // minorSubsystemVersion
		writer.u32(0)          // win32VersionValue
		writer.u32(0x2000)     // sizeOfImage
		writer.u32(0x200)      // sizeOfHeaders
		writer.u32(0)          // checksum
		writer.u16(3)          // subsystem
		writer.u16(0x140)      // dllCharacteristics
		writer.u64(0x100000)   // stackReserve
		writer.u64(0x1000)     // stackCommit
		writer.u64(0x100000)   // heapReserve
		writer.u64(0x1000)     // heapCommit
		writer.u32(0)          // loaderFlags
		writer.u32(16)         // numDataDirectories

		writer.zero(16 * 8) // data directories

		// .text: Contains code
		// .rdata: Contains imports and read-only data
		// .data: Contains initialised read-write data and uninitialised read-write data

		val textRva = 0x1000
		val textSize = assemblerResult.text.size.roundToFileAlignment
		val textPos = 0x200

		writer.ascii8(".text") // name
		writer.u32(textSize) // virtualSize
		writer.u32(textRva) // virtualAddress
		writer.u32(textSize) // rawDataSize
		writer.u32(textPos) // pRawData
		writer.zero(12) // irrelevant
		writer.u32(0x60_00_00_20) // characteristics

		writer.ascii8(".rdata") // name
		writer.u32(0) // virtualSize
		writer.u32(0) // virtualAddress
		writer.u32(0) // rawDataSize
		writer.u32(0) // pRawData
		writer.zero(12) // irrelevant
		writer.u32(0x40_00_00_40) // characteristics

		writer.ascii8(".data") // name
		writer.u32(0) // virtualSize
		writer.u32(0) // virtualAddress
		writer.u32(0) // rawDataSize
		writer.u32(0) // pRawData
		writer.zero(12) // irrelevant
		writer.u32(0xC0_00_00_00) // characteristics

		writer.zeroTo(textPos)
		writer.bytes(assemblerResult.text)
		writer.zeroTo(textPos + textSize)

		rdataPos = writer.pos
		rdataRva = (textRva + textSize).roundToSectionAlignment
		writeImportDirectory(rdataRva)
		writer.alignTo(fileAlignment)
		rdataSize = writer.pos - rdataPos
		writer.u32(rdataHeaderPos + 8, rdataSize)
		writer.u32(rdataHeaderPos + 12, rdataRva)
		writer.u32(rdataHeaderPos + 16, rdataSize)
		writer.u32(rdataHeaderPos + 20, rdataPos)
		writer.u32(importDataDirectoryPos, importDirectoryRva)
		writer.u32(importDataDirectoryPos + 4, importDirectorySize)
		writer.u32(imageSizePos, rdataRva + rdataSize.roundToSectionAlignment)

		for(relocation in assemblerResult.relocations) {
			if(relocation.width != Width.BIT32) error("Invalid relocation width")
			if(relocation.value is ImmNode) {
				val value = relocation.value.value
				if(value is IdNode) {
					val symbol = assemblerResult.symbols[value.name]
					if(symbol !is ImportSymbol) error("Invalid symbol")
					val diff = symbol.pos + symbol.section.rva - relocation.base!!.pos - relocation.base.section.rva
					writer.u32(textPos + relocation.position, diff)
				} else { error("") }
			} else if(relocation.value is IdNode) {
				val symbol = assemblerResult.symbols[relocation.value.name]
				if(symbol !is LabelSymbol) error("Invalid symbol")
				if(relocation.width.isNot32) error("Invalid relocation")
				val diff = symbol.pos - relocation.base!!.pos
				writer.u32(textPos + relocation.position, diff)
			} else {
				error("")
			}
		}

		return writer.trimmedBytes()
	}



	private fun writeImportDirectory(rva: Int) {
		val dlls = assemblerResult.imports.associateFlatMap { it.dll }
		val dif = writer.pos - rva

		importDirectoryRva = rva
		importDirectorySize = dlls.size * 20 + 20
		val idtsPos = writer.pos
		val idtsSize = dlls.size * 20 + 20

		writer.zero(idtsSize)

		for((dllIndex, pair) in dlls.entries.withIndex()) {
			val dll = pair.key
			val imports = pair.value

			val idtPos = idtsPos + dllIndex * 20

			val dllNamePos = writer.pos

			writer.asciiNT("$dll.dll")
			writer.align8()

			val iltPos = writer.pos

			writer.zero(imports.size * 8 + 8)

			val iatPos = writer.pos

			writer.zero(imports.size * 8 + 8)

			for((importIndex, import) in imports.withIndex()) {
				writer.u32(iltPos + importIndex * 8, writer.pos - dif)
				writer.u32(iatPos + importIndex * 8, writer.pos - dif)
				writer.u16(0)
				writer.asciiNT(import.symbol.name)
				writer.alignEven()
				import.symbol.pos = iatPos + importIndex * 8 - idtsPos
			}

			writer.u32(idtPos, iltPos - dif)
			writer.u32(idtPos + 12, dllNamePos - dif)
			writer.u32(idtPos + 16, iatPos - dif)
		}
	}



}