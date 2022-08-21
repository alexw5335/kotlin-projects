package assembler

import core.Core
import core.binary.BinaryWriter
import core.hexFull
import pefile.CoffFlags
import pefile.DllFlags
import pefile.SectionFlags
import java.nio.file.Files
import java.nio.file.Paths

class PeWriter {


	private val writer = BinaryWriter(8192)



	fun write() {
		val sectionAlignment = 4096
		val fileAlignment = 512
		val sizeOfImage = sectionAlignment * 3
		val sizeOfHeaders = fileAlignment
		val numSections = 2
		val coffFlags = CoffFlags { EXECUTABLE_IMAGE + LARGE_ADDRESS_AWARE }
		val dllFlags = DllFlags { HIGH_ENTROPY_VA + DYNAMIC_BASE + NX_COMPAT + TERMINAL_SERVER_AWARE }

		writer.u16(0x5A_4D)
		writer.zeroTo(0x3C)
		writer.u8(0x40)
		writer.zeroTo(0x40)

		// COFF header
		writer.u32(0x4550) // PE signature
		writer.u16(0x8664) // machine
		writer.u16(numSections) // numSections
		writer.u32(0) // timeDateStamp
		writer.u32(0) // pSymbolTable
		writer.u32(0) // symbolCount
		writer.u16(0x00F0) // optionalHeaderSize
		writer.u16(coffFlags.value) // characteristics

		// Optional Header
		writer.u16(0x02_0B) // magic number, PE32+
		writer.u16(0) // linker version
		writer.u32(0) // sizeOfCode
		writer.u32(0) // sizeOfInitialisedData
		writer.u32(0) // sizeOfUninitialisedData
		writer.u32(sectionAlignment) // pEntryPoint
		writer.u32(0) // base of code
		writer.u64(0x40_00_00) // image base
		writer.u32(sectionAlignment) // sectionAlignment
		writer.u32(fileAlignment) // fileAlignment
		writer.u16(4) // major os
		writer.u16(0) // minor os
		writer.u16(0) // major image
		writer.u16(0) // minor image
		writer.u16(5) // major subsystem
		writer.u16(2) // minor subsystem
		writer.u32(0) // win32 version value
		writer.u32(sizeOfImage) // sizeOfImage
		writer.u32(sizeOfHeaders) // sizeOfHeaders
		writer.u32(0) // checksum
		writer.u16(3) // subsystem
		writer.u16(dllFlags.value) // dll characteristics
		writer.u64(0x10_00_00) // size of stack reserve
		writer.u64(0x00_10_00) // size of stack commit
		writer.u64(0x10_00_00) // size of heap reserve
		writer.u64(0x00_10_00) // size of heap commit
		writer.u32(0) // loader flags
		writer.u32(16) // data directory count

		// data directories
		writer.u64(0) // export data directory
		writer.u32(sectionAlignment * 2) // import data directory address
		writer.u32(fileAlignment) // import data directory size
		for(i in 0 until 14)
			writer.u64(0)

		// Section header 1
		writer.ascii8(".text")
		writer.u32(sectionAlignment) // virtualSize
		writer.u32(sectionAlignment) // virtualAddress
		writer.u32(fileAlignment) // rawDataSize
		writer.u32(fileAlignment) // rawDataAddress
		writer.u32(0)
		writer.u32(0)
		writer.u16(0)
		writer.u16(0)
		writer.u32(SectionFlags { EXECUTE + READ + CODE }.value)

		// Section header 2
		writer.ascii8(".rdata")
		writer.u32(sectionAlignment)
		writer.u32(sectionAlignment * 2)
		writer.u32(fileAlignment)
		writer.u32(fileAlignment * 2)
		writer.u32(0)
		writer.u32(0)
		writer.u16(0)
		writer.u16(0)
		writer.u32(SectionFlags { INITIALISED_DATA + READ }.value)

		writer.zeroTo(fileAlignment)

		writer.u8(0xE8)
		//writer.u32(0)
		//writer.u16(0x25_FF)
		val currentRva = sectionAlignment + writer.pos - fileAlignment + 4
		val exitProcessRva = sectionAlignment * 2 + 132
		writer.u32(exitProcessRva - currentRva)
		writer.u8(0xC3)

		writer.zeroTo(fileAlignment * 2)
		writer.zeroTo(fileAlignment * 3)

		val importRva = sectionAlignment * 2

		writer.pos = fileAlignment * 2
		writer.u32(importRva + 40)
		writer.u32(0)
		writer.u32(0)
		writer.u32(importRva + 64)
		writer.u32(importRva + 132)

		writer.u32(0)
		writer.u32(0)
		writer.u32(0)
		writer.u32(0)
		writer.u32(0)

		writer.u64(importRva + 100L)
		writer.u64(0)
		writer.ascii("KERNEL32.dll")
		writer.u8(0)
		writer.pos = fileAlignment * 2 + 100

		writer.u16(0)
		writer.ascii("WriteFile")
		writer.u8(0)
		writer.alignEven()

		writer.pos = fileAlignment * 2 + 132
		writer.u64(importRva + 100L)
		writer.u64(0)


/*		writer.pos = fileAlignment * 2
		writer.u32(sectionAlignment * 2 + 40)
		writer.u32(0)
		writer.u32(0)
		writer.u32(sectionAlignment * 2 + 40 + 16 + 12)
		writer.u32(sectionAlignment * 2 + 40 + 16 + 12 + 20)
		writer.pos = fileAlignment * 2 + 40
		writer.u64(sectionAlignment * 2 + 40 + 16L)
		writer.u64(0)
		writer.u16(0)
		writer.ascii("ExitProcess")
		writer.u8(0)
		writer.alignEven()
		writer.ascii("KERNEL32.dll")
		writer.pos = sectionAlignment * 2 + 40 + 16 + 12 + 20
		writer.u64(sectionAlignment * 2 + 40 + 16L)*/

/*		val dllImports = listOf("KERNEL32.dll" to listOf("ExitProcess", "WriteFile"))
		val totalImports = dllImports.sumOf { it.second.size }

		val idtsPos = fileAlignment * 2
		val iltsPos = idtsPos + dllImports.size * 20 + 20
		val namesPos = iltsPos + totalImports * 8

		var dllIndex = 0
		val dllNamePositions = IntArray(dllImports.size)
		val importNamePositions = IntArray(totalImports)
		var importNameIndex = 0

		writer.pos = namesPos

		for((dllName, imports) in dllImports) {
			dllNamePositions[dllIndex++] = writer.pos
			writer.ascii(dllName)
			for(import in imports) {
				importNamePositions[importNameIndex++] = writer.pos - idtsPos + sectionAlignment * 2
				writer.u16(0)
				writer.ascii(import)
			}
		}

		val iatsPos = writer.pos
		importNameIndex = 0
		for(dllImport in dllImports) {
			for(import in dllImport.second) {
				writer.u64(importNamePositions[importNameIndex].toLong())
			}
		}*/

		writer.pos = fileAlignment * 3
		finish()
	}



	private fun finish() {
		val path = "generated2.exe"
		Files.write(Paths.get(path), writer.trimmedBytes())
		Core.runOrExit("dumpbin /headers $path")
		Core.runOrExit("./$path")
	}


}