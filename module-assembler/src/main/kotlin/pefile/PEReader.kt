package pefile

import core.binary.BinaryReader
import core.Core

class PEReader(private val reader: BinaryReader) {


	constructor(path: String) : this(BinaryReader(Core.readResourceBytes(path)))


	private var isImage = false

	private var isPe32Plus = false

	lateinit var coffHeader: CoffHeader

	lateinit var optionalHeader: OptionalHeader

	lateinit var dataDirectories: List<DataDirectory>

	lateinit var sections: List<Section>

	lateinit var stringTable: List<String>

	lateinit var symbols: List<Symbol>



	fun read(): PEFile {
		if(reader.u16() != 0x5A4D) {
			reader.pos -= 2
			isImage = false
		} else {
			isImage = true
			reader.pos = 0x3C
			reader.pos = reader.u32()
			if(reader.u32() != 0x00004550) error("Invalid PE signature")
		}

		coffHeader = readCoffHeader()

		if(isImage) {
			isPe32Plus = reader.u16() == 0x20B
			if(!isPe32Plus) error("Non PE32+ image files not supported")
			reader.pos -= 2
			optionalHeader = readOptionalHeader()
			dataDirectories = List(optionalHeader.numberOfRvaAndSizes) { DataDirectory(it, reader.u32(), reader.u32()) }
		} else {
			dataDirectories = emptyList()
		}
		
		sections = List(coffHeader.numSections) { readSection() }
		stringTable = readStringTable()
		symbols = readSymbolTable()

		return PEFile(
			isImage,
			coffHeader,
			if(::optionalHeader.isInitialized) optionalHeader else null,
			dataDirectories,
			sections,
			stringTable,
			symbols
		)
	}


	private fun readStringTable(): List<String> {
		if(coffHeader.pSymbolTable == 0) return emptyList()
		val startPos = coffHeader.pSymbolTable + coffHeader.numSymbols * 18
		reader.pos = startPos
		val length = reader.u32()

		if(length == 4) return emptyList()

		val strings = ArrayList<String>()
		while(reader.pos < startPos + length)
			strings.add(reader.asciiNt())

		return strings
	}



	/*
	Symbol reading
	 */



	private fun readAuxSymbol1(): AuxSymbol1 {
		val tagIndex = reader.u32()
		val totalSize = reader.u32()
		val pLineNumber = reader.u32()
		val pNextFunction = reader.u32()
		reader.pos += 2
		return AuxSymbol1(tagIndex, totalSize, pLineNumber, pNextFunction)
	}



	private fun readAuxSymbol2(): AuxSymbol2 {
		reader.pos += 4
		val lineNumber = reader.u16()
		reader.pos += 6
		val pnextFunction = reader.u32()
		reader.pos += 2
		return AuxSymbol2(lineNumber, pnextFunction)
	}



	private fun readAuxSymbol3(): AuxSymbol3 {
		val tagIndex = reader.u32()
		val characteristics = reader.u32()
		reader.pos += 10
		return AuxSymbol3(tagIndex, characteristics)
	}



	private fun readAuxSymbol4(): AuxSymbol4 {
		return AuxSymbol4(reader.ascii(18))
	}



	private fun readAuxSymbol5(): AuxSymbol5 {
		val length = reader.u32()
		val relocationCount = reader.u16()
		val lineNumberCount = reader.u16()
		val checkSum = reader.u32()
		val number = reader.u16()
		val selection = reader.u8()
		reader.pos += 3
		return AuxSymbol5(length, relocationCount, lineNumberCount, checkSum, number, selection)
	}



	private fun readSymbolTable(): List<Symbol> {
		if(coffHeader.numSymbols == 0) return emptyList()

		val symbols = ArrayList<Symbol>(coffHeader.numSymbols)

		reader.pos = coffHeader.pSymbolTable

		var index = 0
		while(index++ < coffHeader.numSymbols) {
			val name = if(reader.u32() == 0) {
				val returnPos = reader.pos + 4
				reader.pos = coffHeader.pSymbolTable + coffHeader.numSymbols * 18 + reader.u32()
				val name = reader.asciiNt()
				reader.pos = returnPos
				name
			} else {
				reader.pos -= 4
				reader.ascii(8)
			}

			val value = reader.u32()
			val sectionNumber = reader.s16()
			val typeLSB = reader.u8()
			val typeMSB = reader.u8()
			val type = (typeLSB shl 8) or typeMSB
			val storageClass = reader.u8()
			var auxSymbolCount = reader.u8()
			val auxSymbols = ArrayList<AuxSymbol>()

			index += auxSymbolCount

			// MSB Values: NULL(0), POINTER(1), FUNCTION(2), ARRAY(3)
			// sectionNumber special values: UNDEFINED(0), ABSOLUTE(-1), DEBUG(-2)

			while(auxSymbolCount-- > 0) {
				auxSymbols.add(
					when {
						storageClass == 2 && typeMSB == 2 && sectionNumber > 0 -> readAuxSymbol1()
						storageClass == 101 -> readAuxSymbol2()
						storageClass == 2 && sectionNumber == 0 -> readAuxSymbol3()
						storageClass == 103 -> readAuxSymbol4()
						storageClass == 3 -> readAuxSymbol5()
						else -> AuxSymbolUnrecognised(reader.u32(), reader.u32(), reader.u32(), reader.u32(), reader.u16())
					}
				)
			}

			symbols.add(Symbol(name, value, sectionNumber, type, StorageClass.map[storageClass]!!, auxSymbols))
		}

		return symbols
	}



	/*
	Header
	 */



	private fun readCoffHeader() = CoffHeader(
		reader.u16(),
		reader.u16(),
		reader.u32(),
		reader.u32(),
		reader.u32(),
		reader.u16(),
		reader.u16()
	)



	private fun readSection(): Section {
		val name = reader.ascii(8)
		val virtualSize = reader.u32()
		val virtualAddress = reader.u32()
		val rawDataSize = reader.u32()
		val pRawData = reader.u32()
		val pRelocations = reader.u32()
		val pLineNumbers = reader.u32()
		val relocationCount = reader.u16()
		val lineNumberCount = reader.u16()
		val characteristics = reader.u32()

		val relocations = if(relocationCount <= 0) {
			emptyList()
		} else {
			reader.retainPos {
				reader.pos = pRelocations
				List(relocationCount) {
					Relocation(reader.u32(), reader.u32(), reader.u16())
				}
			}
		}

		return Section(
			name,
			virtualSize,
			virtualAddress,
			rawDataSize,
			pRawData,
			pRelocations,
			pLineNumbers,
			relocationCount,
			lineNumberCount,
			SectionFlags(characteristics),
			relocations
		)
	}



	private fun readOptionalHeader() = OptionalHeader(
		reader.u16(),
		reader.u8(),
		reader.u8(),
		reader.u32(),
		reader.u32(),
		reader.u32(),
		reader.u32(),
		reader.u32(),
		if(isPe32Plus) 0 else reader.u32(),
		if(isPe32Plus) reader.u64() else reader.u32().toLong(),
		reader.u32(),
		reader.u32(),
		reader.u16(),
		reader.u16(),
		reader.u16(),
		reader.u16(),
		reader.u16(),
		reader.u16(),
		reader.u32(),
		reader.u32(),
		reader.u32(),
		reader.u32(),
		reader.u16(),
		reader.u16(),
		if(isPe32Plus) reader.u64() else reader.u32().toLong(),
		if(isPe32Plus) reader.u64() else reader.u32().toLong(),
		if(isPe32Plus) reader.u64() else reader.u32().toLong(),
		if(isPe32Plus) reader.u64() else reader.u32().toLong(),
		reader.u32(),
		reader.u32()
	)


}