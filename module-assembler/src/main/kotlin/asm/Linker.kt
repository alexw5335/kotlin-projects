package asm

import core.associateFlatMap
import core.binary.BinaryWriter



class TestLinker {


	private val writer = BinaryWriter()



	fun link(): ByteArray {
		writer.u16(0x5A4D)
		writer.zeroTo(0x3C)
		writer.u32(0x40)

		writer.u32(0x4550)     // signature
		writer.u16(0x8664)     // machine
		writer.u16(1)          // numSections
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
		writer.u32(0x1200)     // sizeOfImage
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

		writer.zero(8)
		writer.u32(0x1050)
		writer.u32(0x28)
		writer.zero(14 * 8)

		writer.ascii8(".text")    // name
		writer.u32(0x200)         // virtualSize
		writer.u32(0x1000)        // virtualAddress
		writer.u32(0x200)         // rawDataSize
		writer.u32(0x200)         // pRawData
		writer.zero(12)           // irrelevant
		writer.u32(0x60_00_00_00) // characteristics

		writer.zeroTo(0x200)

		val iatRva = 0x1050 + 40 + 24 + 12 + 14 + 14
		val textRva = 0x1000
		val textPos = 0x200

		writer.bytes(0x48, 0x83, 0xEC, 0x38)
		writer.bytes(0xB9, 0xF5, 0xFF, 0xFF, 0xFF)
		writer.bytes(0x48, 0x8D, 0x15, 0x00, 0x00, 0x00, 0x00)
		writer.bytes(0x41, 0xB8, 0x01, 0x00, 0x00, 0x00)
		writer.bytes(0x4C, 0x8B, 0x4C, 0x24, 0x28)
		writer.bytes(0x48, 0xC7, 0x44, 0x24, 0x20, 0x00, 0x00, 0x00, 0x00)
		writer.bytes(0xFF, 0b00_010_101); writer.u32((iatRva) - (writer.pos + 4 - textPos + textRva))
		writer.bytes(0xFF, 0b00_010_101); writer.u32((iatRva + 8) - (writer.pos + 4 - textPos + textRva))

		writer.zeroTo(0x250)
		writer.u32(0x1050 + 40) // iltRva
		writer.u32(0)      // timeDateStamp
		writer.u32(0)      // forwarderChain
		writer.u32(0x1050 + 40 + 24 + 12 + 14) // nameRva
		writer.u32(0x1050 + 40 + 24 + 12 + 14 + 14) // iatRva
		writer.zero(20)    // NULL IDT

		writer.u64(0x1050 + 40 + 24)
		writer.u64(0x1050 + 40 + 24 + 12)
		writer.u64(0)

		writer.u16(0)
		writer.asciiNT("WriteFile")
		writer.u16(0)
		writer.asciiNT("ExitProcess")
		writer.asciiNT("KERNEL32.dll")
		writer.u8(0)
		writer.u16(0)
		writer.asciiNT("WriteFile")
		writer.u16(0)
		writer.asciiNT("ExitProcess")

		writer.zeroTo(0x400)

		return writer.trimmedBytes()
	}


}



class Linker(assembleResult: AssembleResult) {


	private val textBytes = assembleResult.text

	private val imports: Map<String, List<DllImport>> = assembleResult.imports.associateFlatMap { it.dll }

	private val relocations = assembleResult.relocations



	private val writer = BinaryWriter()



	private val sectionAlignment = 0x1000

	private val fileAlignment = 0x200

	private val Int.roundToSectionAlignment get() = (this + sectionAlignment - 1) and -sectionAlignment

	private val Int.roundToFileAlignment get() = (this + fileAlignment - 1) and -fileAlignment

	private val Int.byte get() = toUByte().toByte()



	private val coffHeaderPos = 0x40

	private val optionalHeaderPos = 0x58

	private val pEntryPointPos = optionalHeaderPos + 16

	private val sizeOfImagePos = optionalHeaderPos + 56

	private val dataDirectoriesPos = 0xC8

	private val textHeaderPos = 0x148

	private val rdataHeaderPos = 0x170

	private var rdataSize = 0



	fun link(): ByteArray {
		writer.u16(0x5A_4D)
		writer.zeroTo(0x3C)
		writer.u32(0x40)

		writer.u32(0x00_00_45_50) // signature
		writer.u16(0x8664) // machine
		writer.u16(2) // numSections
		writer.u32(0) // timeDateStamp
		writer.u32(0) // pSymbolTable
		writer.u32(0) // numSymbols
		writer.u16(0xF0) // optionalHeaderSize
		writer.u16(0x00_22) // characteristics, DYNAMIC_BASE | LARGE_ADDRESS_AWARE | EXECUTABLE

		writer.u16(0x20B)      // magic
		writer.u16(0)          // linkerVersion
		writer.u32(0)          // sizeOfCode
		writer.u32(0)          // sizeOfInitialisedData
		writer.u32(0)          // sizeOfUninitialisedData
		writer.u32(0)          // pEntryPoint        fill in later
		writer.u32(0)          // baseOfCode
		writer.u64(0x00_40_00_00) // imageBase
		writer.u32(sectionAlignment) // sectionAlignment
		writer.u32(fileAlignment) // fileAlignment
		writer.u16(6)          // majorOSVersion
		writer.u16(0)          // minorOSVersion
		writer.u32(0)          // imageVersion
		writer.u16(6)          // majorSubsystemVersion
		writer.u16(0)          // minorSubsystemVersion
		writer.u32(0)          // win32VersionValue
		writer.u32(0)          // sizeOfImage        fill in later
		writer.u32(0x200)      // sizeOfHeaders
		writer.u32(0)          // checksum
		writer.u16(3)          // subsystem
		writer.u16(0x140)      // dllCharacteristics
		writer.u64(0x10_00_00) // stackReserve
		writer.u64(0x10_00)    // stackCommit
		writer.u64(0x10_00_00) // heapReserve
		writer.u64(0x10_00)    // heapCommit
		writer.u32(0)          // loaderFlags
		writer.u32(16)         // numDataDirectories

		writer.zero(16 * 8)

		writer.ascii8(".text") // name
		writer.u32(textBytes.size.roundToSectionAlignment) // virtualSize
		writer.u32(sectionAlignment) // virtualAddress
		writer.u32(textBytes.size) // rawDataSize
		writer.u32(0x200) // pRawData
		writer.zero(12) // irrelevant
		writer.u32(0x60_00_00_20) // characteristics

		val rdataRva = sectionAlignment + textBytes.size.roundToSectionAlignment
		val rdataPos = 0x200 + textBytes.size.roundToFileAlignment

		writer.ascii8(".rdata") // name
		writer.u32(0) // virtualSize (fill in later)
		writer.u32(rdataRva) // virtualAddress
		writer.u32(0) // rawDataSize (fill in later)
		writer.u32(rdataPos) // pRawData
		writer.zero(12) // irrelevant
		writer.u32(0x40_00_00_40) // characteristics

		writer.zeroTo(0x200)
		writer.bytes(textBytes)
		writer.zeroTo(rdataPos)

		writeImportDirectory(rdataRva)
		writer.zeroTo(rdataPos + rdataSize.roundToFileAlignment)

		val rDataVirtualSize = rdataSize.roundToSectionAlignment

		writer.u32(rdataHeaderPos + 8, rDataVirtualSize)
		writer.u32(rdataHeaderPos + 16, rdataSize.roundToFileAlignment)
		writer.u32(dataDirectoriesPos + 8, rdataRva)
		writer.u32(dataDirectoriesPos + 12, imports.size * 20 + 20)

		for(r in relocations) {
			val value = r.disp + r.ref.pos - r.negRef.pos
			when(r.width) {
				Width.BIT8  -> writer.s8(0x200 + r.position, value.toInt())
				Width.BIT16 -> writer.s16(0x200 + r.position, value.toInt())
				Width.BIT32 -> writer.s32(0x200 + r.position, value.toInt())
				else        -> writer.s64(0x200 + r.position, value)
			}
		}

		writer.u32(pEntryPointPos, sectionAlignment)
		writer.u32(sizeOfImagePos, rdataRva + rDataVirtualSize)

		return writer.trimmedBytes()
	}



	private fun writeImportDirectory(rva: Int) {
		val dllCount = imports.size
		val dif = writer.pos - rva

		val idtsPos = writer.pos
		val idtsSize = dllCount * 20 + 20

		writer.zero(idtsSize)

		for((dllIndex, pair) in imports.entries.withIndex()) {
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
				import.symbol.pos = iatPos + importIndex * 8 - dif - 1000
			}

			writer.u32(idtPos, iltPos - dif)
			writer.u32(idtPos + 12, dllNamePos - dif)
			writer.u32(idtPos + 16, iatPos - dif)
		}

		rdataSize = writer.pos - idtsPos
	}


}