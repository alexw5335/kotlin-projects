package asm

import core.binary.BinaryWriter

class Linker {


	private val writer = BinaryWriter()

	private val imports = listOf(
		"KERNEL32.dll" to listOf("WriteFile", "ExitProcess")
	)



	private val sectionAlignment = 0x1000

	private val fileAlignment = 0x200

	private val Int.roundToSectionAlignment get() = (this + sectionAlignment - 1) and -sectionAlignment

	private val Int.roundToFileAlignment get() = (this + fileAlignment - 1) and -fileAlignment

	private val Int.byte get() = toUByte().toByte()

	private val textBytes = byteArrayOf(0xC3.byte)



	private val coffHeaderPos = 0x40

	private val optionalHeaderPos = 0x58

	private val pEntryPointPos = optionalHeaderPos + 20

	private val sizeOfImagePos = pEntryPointPos + 40

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
		writer.u32(0)          // pEntryPoint
		writer.u32(0)          // baseOfCode
		writer.u64(0x00_40_00_00) // imageBase
		writer.u32(sectionAlignment) // sectionAlignment
		writer.u32(fileAlignment) // fileAlignment
		writer.u16(4)          // majorOSVersion
		writer.u16(0)          // minorOSVersion
		writer.u32(0)          // imageVersion
		writer.u16(3)          // majorSubsystemVersion
		writer.u16(10)         // minorSubsystemVersion
		writer.u32(0)          // win32VersionValue
		writer.u32(8192)       // sizeOfImage
		writer.u32(0x200)      // sizeOfHeaders
		writer.u32(0)          // checksum
		writer.u16(3)          // subsystem
		writer.u16(0x40)       // dllCharacteristics
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

		writer.u32(rdataHeaderPos + 8, rdataSize)
		writer.u32(rdataHeaderPos + 16, rdataSize)
		writer.u32(dataDirectoriesPos + 8, rdataRva)
		writer.u32(dataDirectoriesPos + 12, rdataSize)

		return writer.trimmedBytes()
	}



	private fun writeImportDirectory(rva: Int) {
		val dllCount = imports.size
		val dif = writer.pos - rva

		val idtsPos = writer.pos
		val idtsSize = dllCount * 20 + 20

		writer.zero(idtsSize)

		for(dllIndex in imports.indices) {
			val dll = imports[dllIndex].first
			val imports = imports[dllIndex].second

			val idtPos = idtsPos + dllIndex * 20

			val dllNamePos = writer.pos

			writer.asciiNT(dll)
			writer.align8()

			val iltPos = writer.pos

			writer.zero(imports.size * 8 + 8)

			val iatPos = writer.pos

			writer.zero(imports.size * 8 + 8)

			for((importIndex, import) in imports.withIndex()) {
				writer.u32(iltPos + importIndex * 8, writer.pos - dif)
				writer.u32(iatPos + importIndex * 8, writer.pos - dif)
				writer.u16(0)
				writer.asciiNT(import)
				writer.alignEven()
			}

			writer.u32(idtPos, iltPos - dif)
			writer.u32(idtPos + 12, dllNamePos - dif)
			writer.u32(idtPos + 16, iatPos - dif)
		}

		rdataSize = writer.pos - idtsPos
	}


}