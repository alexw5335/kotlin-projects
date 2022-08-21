package assembler

import core.Core
import core.binary.BinaryWriter
import core.hex8
import pefile.Section
import pefile.SectionFlags
import java.nio.file.Files
import java.nio.file.Paths

class Linker {


	private val writer = BinaryWriter()

	private val sectionAlignment = 4096

	private val fileAlignment = 512

	private val pEntryPoint = sectionAlignment

	private val sizeOfImage = 0x3000

	private val sizeOfHeaders = 0x108

	private val dllCharacteristics = 0x8160

	private val sections = ArrayList<Section>()

	private val Int.roundToFileAlignment get() = (this + fileAlignment - 1) and -fileAlignment

	private val Int.roundToSectionAlignment get() = (this + sectionAlignment - 1) and -sectionAlignment



	companion object {

		private val Int.u8 get() = toUByte().toByte()

		private val dosStub = byteArrayOf(
			0x4D, 0x5A, 0x90.u8, 0x00, 0x03, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0xFF.u8, 0xFF.u8, 0x00, 0x00,
			0xB8.u8, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x80.u8, 0x00, 0x00, 0x00,
			0x0E, 0x1F, 0xBA.u8, 0x0E, 0x00, 0xB4.u8, 0x09, 0xCD.u8, 0x21, 0xB8.u8, 0x01, 0x4C, 0xCD.u8, 0x21, 0x54, 0x68,
			0x69, 0x73, 0x20, 0x70, 0x72, 0x6F, 0x67, 0x72, 0x61, 0x6D, 0x20, 0x63, 0x61, 0x6E, 0x6E, 0x6F,
			0x74, 0x20, 0x62, 0x65, 0x20, 0x72, 0x75, 0x6E, 0x20, 0x69, 0x6E, 0x20, 0x44, 0x4F, 0x53, 0x20,
			0x6D, 0x6F, 0x64, 0x65, 0x2E, 0x0D, 0x0D, 0x0A, 0x24, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		)

	}



	class DllImport(val dllName: String, val imports: Collection<String>)

	

	private fun writeSection(section: Section) {
		writer.ascii8(section.name)
		writer.u32(section.virtualSize)
		writer.u32(section.virtualAddress)
		writer.u32(section.rawDataSize)
		writer.u32(section.pRawData)
		writer.u32(section.pRelocations)
		writer.u32(section.pLineNumbers)
		writer.u16(section.relocationCount)
		writer.u16(section.lineNumberCount)
		writer.u32(section.characteristics.value)
	}



	fun link() {
		sections.add(Section(
			name            = ".text",
			virtualSize     = sectionAlignment,
			virtualAddress  = sectionAlignment,
			rawDataSize     = fileAlignment,
			pRawData        = fileAlignment,
			characteristics = SectionFlags { EXECUTE + READ + CODE }
		))

		sections.add(Section(
			name = ".rdata",
			virtualSize = sectionAlignment,
			virtualAddress = sectionAlignment * 2,
			rawDataSize = fileAlignment,
			pRawData = fileAlignment * 2,
			characteristics = SectionFlags { INITIALISED_DATA + READ }
		))

		// DOS stub
		writer.bytes(dosStub)

		// COFF Header
		writer.u32(0x00_00_45_50) // PE signature
		writer.u16(0x86_64) // machine
		writer.u16(sections.size) // numSections
		writer.u32(0) // timeDateStamp
		writer.u32(0) // pSymbolTable
		writer.u32(0) // symbolCount
		writer.u16(0x00_F0) // optionalHeaderSize
		writer.u16(0x00_22) // characteristics (EXECUTABLE | LARGE_ADDRESS_AWARE)

		// Optional Header
		writer.u16(0x02_0B) // magic number, PE32+
		writer.u16(0) // linker version
		writer.u32(0) // sizeOfCode
		writer.u32(0) // sizeOfInitialisedData
		writer.u32(0) // sizeOfUninitialisedData
		writer.u32(pEntryPoint)
		writer.u32(0) // base of code
		writer.u64(0x40_00_00) // image base
		writer.u32(sectionAlignment)
		writer.u32(fileAlignment)
		writer.u16(4) // major os
		writer.u16(0) // minor os
		writer.u16(0) // major image
		writer.u16(0) // minor image
		writer.u16(5) // major subsystem
		writer.u16(2) // minor subsystem
		writer.u32(0) // win32 version value
		writer.u32(sizeOfImage)
		writer.u32(sizeOfHeaders)
		writer.u32(0) // checksum
		writer.u16(3) // subsystem
		writer.u16(dllCharacteristics) // dll characteristics
		writer.u64(0x10_00_00) // size of stack reserve
		writer.u64(0x00_10_00) // size of stack commit
		writer.u64(0x10_00_00) // size of heap reserve
		writer.u64(0x00_10_00) // size of heap commit
		writer.u32(0) // loader flags
		writer.u32(16) // data directory count
		writer.u64(0) // Export data directory
		writer.u32(0) // import data directory size
		writer.u32(0) // import data directory address
		for(i in 0 until 14) writer.u64(0) // data directories

		// SECTION HEADERS
		sections.forEach(::writeSection)

		writer.zero(fileAlignment - writer.pos)
		writer.u8(0xC3)
		writer.zero(fileAlignment - 1)
		writer.zero(fileAlignment)

		val imports = listOf(
			DllImport("KERNEL32.dll", listOf(
				"WriteFile",
				"ExitProcess"
			))
		)

		Files.write(Paths.get("generated.exe"), writer.trimmedBytes())
		Core.runOrExit("dumpbin /headers generated.exe")
		Core.runOrExit("./generated.exe")
	}


}