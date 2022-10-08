package asm

import core.binary.BinaryWriter
import java.nio.file.Files
import java.nio.file.Paths

class Linker(assembleResult: AssembleResult) {


	private val writer = BinaryWriter()

	private val text = assembleResult.text

	private val exports = assembleResult.exports



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

		writer.u16(0x20B) // magic, PE32+
		writer.u16(0) // linkerVersion           irrelevant
		writer.u32(0) // sizeOfCode              irrelevant
		writer.u32(0) // sizeOfInitialisedData   irrelevant
		writer.u32(0) // sizeOfUninitialisedData irrelevant
		writer.u32(4096) // addressOfEntryPoint  IMPORTANT
		writer.u32(0) // baseOfCode              irrelevant
		writer.u64(0x00_40_00_00) // imageBase   IMPORTANT
		writer.u32(4096) // sectionAlignment     IMPORTANT
		writer.u32(512) // fileAlignment         IMPORTANT
		writer.u16(4) // majorOSVersion          IMPORTANT
		writer.u16(0) // minorOSVersion          IMPORTANT
		writer.u32(0) // imageVersion            irrelevant
		writer.u16(3) // majorSubsystemVersion   irrelevant
		writer.u16(10) // minorSubsystemVersion
		writer.u32(0) // win32VersionValue       irrelevant
		writer.u32(8192) // sizeOfImage          IMPORTANT
		writer.u32(0x200) // sizeOfHeaders       IMPORTANT
		writer.u32(0) // checksum                irrelevant
		writer.u16(3) // subsystem               IMPORTANT
		writer.u16(0x40) // dllCharacteristics   IMPORTANT,  DYNAMIC_BASE
		writer.u64(0x10_00_00) // stackReserve   irrelevant
		writer.u64(0x10_00)    // stackCommit    irrelevant
		writer.u64(0x10_00_00) // heapReserve    irrelevant
		writer.u64(0x10_00)    // heapCommit     irrelevant
		writer.u32(0) // loaderFlags             irrelevant
		writer.u32(16) // numDataDirectories     irrelevant

		writer.u64(0)
		writer.u32(0x2000)
		writer.u32(0x200)
		for(i in 0 until 14) writer.u64(0)

		writer.ascii8(".text") // name
		writer.u32(4096) // virtualSize
		writer.u32(4096) // virtualAddress
		writer.u32(0x40) // sizeOfRawData
		writer.u32(0x200) // pRawData
		writer.u32(0) // pRelocations
		writer.u32(0) // pLineNumbers
		writer.u16(0) // numRelocations
		writer.u16(0) // numLineNumbers
		writer.u32(0x60_00_00_20) // characteristics

		writer.ascii8(".rdata") // name
		writer.u32(0x1000)
		writer.u32(0x2000)
		writer.u32(0x150)
		writer.u32(0x400)
		writer.u32(0)
		writer.u32(0)
		writer.u16(0)
		writer.u16(0)
		writer.u32(0x40_00_00_40)

		writer.zeroTo(0x200)
		writer.u8(0xC3)
		writer.zeroTo(0x400)

		// 0x2000: import directory
		writer.u32(0x2000 + 40) // ILT rva
		writer.u32(0) // time-date stamp
		writer.u32(0) // forwarder chain
		writer.u32(0x2000 + 48 + 2 + 10) // name RVA
		writer.u32(0x2100) // IAT rva
		writer.zero(20) // null IDT
		//0x2000 + 40: KERNEL32 ILT
		writer.u64(0x2000 + 48)
		//0x2000 + 48: Hint/Name Table + DLL names
		writer.u16(0)
		writer.asciiNT("WriteFile")
		writer.asciiNT("KERNEL32.dll")
		writer.zeroTo(0x500)
		writer.u64(0x2000 + 48)
		writer.zeroTo(0x600)

		return writer.trimmedBytes()
	}
	
}