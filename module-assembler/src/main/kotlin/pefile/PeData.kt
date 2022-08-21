package pefile

import core.hexFull



data class CoffHeader(
	val machine: Int,
	val numSections: Int,
	val timeDateStamp: Int,
	val pSymbolTable: Int,
	val numSymbols: Int,
	val sizeOfOptionalHeader: Int,
	val characteristics: Int
)



data class OptionalHeader(
	val magic: Int,
	val majorLinkerVersion: Int,
	val minorLinkerVersion: Int,
	val sizeOfCode: Int,
	val sizeOfInitialisedData: Int,
	val sizeOfUninitialisedData: Int,
	val addressOfEntryPoint: Int,
	val baseOfCode: Int,
	val baseOfData: Int,
	val imageBase: Long,
	val sectionAlignment: Int,
	val fileAlignment: Int,
	val majorOSVersion: Int,
	val minorOSVersion: Int,
	val majorImageVersion: Int,
	val minorImageVersion: Int,
	val majorSubsystemVersion: Int,
	val minorSubsystemVersion: Int,
	val win32VersionValue: Int,
	val sizeOfImage: Int,
	val sizeOfHeaders: Int,
	val checkSum: Int,
	val subsystem: Int,
	val dllCharacteristics: Int,
	val sizeOfStackReserve: Long,
	val sizeOfStackCommit: Long,
	val sizeOfHeapReserve: Long,
	val sizeOfHeapCommit: Long,
	val loaderFlags: Int,
	val numberOfRvaAndSizes: Int
)



data class Section(
	val name			: String,
	val virtualSize		: Int,
	val virtualAddress	: Int,
	val rawDataSize	    : Int,
	val pRawData		: Int,
	val pRelocations	: Int = 0,
	val pLineNumbers	: Int = 0,
	val relocationCount	: Int = 0,
	val lineNumberCount	: Int = 0,
	val characteristics	: SectionFlags     = SectionFlags(0),
	val relocations     : List<Relocation> = emptyList()
)



data class Relocation(
	val virtualAddress: Int,
	val symbolTableIndex: Int,
	val type: Int
)



class DataDirectory(
	val index: Int,
	val virtualAddress: Int,
	val size: Int
)



class PEFile(
	val isImage         : Boolean,
	val coffHeader      : CoffHeader,
	val optionalHeader  : OptionalHeader?,
	val dataDirectories : List<DataDirectory>,
	val sections        : List<Section>,
	val stringTable     : List<String>,
	val symbols         : List<Symbol>
) {


	fun fileOffset(rva: Int): Int {
		for(section in sections)
			if(rva in section.virtualAddress..section.virtualAddress+section.virtualSize)
				return section.pRawData + rva - section.virtualAddress
		throw RuntimeException("Invalid RVA: $rva")
	}


}



data class Symbol(
	val name          : String,
	val value         : Int,
	val sectionNumber : Int,
	val type          : Int,
	val storageClass  : StorageClass,
	val auxSymbols    : List<AuxSymbol>
)



sealed interface AuxSymbol



data class AuxSymbol1(
	val tagIndex      : Int,
	val totalSize     : Int,
	val pLineNumber   : Int,
	val pNextFunction : Int
) : AuxSymbol



data class AuxSymbol2(
	val lineNumber: Int,
	val pNextFunction: Int
) : AuxSymbol



data class AuxSymbol3(
	val tagIndex: Int,
	val characteristics: Int
) : AuxSymbol



data class AuxSymbol4(
	val fileName: String
) : AuxSymbol



data class AuxSymbol5(
	val length: Int,
	val relocationCount: Int,
	val lineNumberCount: Int,
	val checkSum: Int,
	val number: Int,
	val selection: Int
) : AuxSymbol



data class AuxSymbolUnrecognised(
	val int0: Int,
	val int1: Int,
	val int2: Int,
	val int3: Int,
	val int4: Int
) : AuxSymbol



data class ImportDirectoryTable(
	val importLookupTableRVA: Int,
	val timeDateStamp: Int,
	val forwarderChain: Int,
	val nameRVA: Int,
	val importAddressTableRVA: Int
)



fun CoffHeader.print() {
	println("""
		COFF Header:
			machine             ${machine.hexFull}
			sectionCount        $numSections
			timeDateStamp       $timeDateStamp
			pSymbolTable        ${pSymbolTable.hexFull} ($pSymbolTable)
			symbolCount         $numSymbols
			optionalHeaderSize  ${sizeOfOptionalHeader.hexFull} ($sizeOfOptionalHeader)
			characteristics     ${characteristics.hexFull} ${CoffFlags(characteristics)}
	""".trimIndent())
}



fun OptionalHeader.print() {
	println("""
		Optional Header:
			magic                    ${magic.hexFull}
			majorLinkerVersion       $majorLinkerVersion
			minorLinkerVersion       $minorLinkerVersion
			sizeofCode               ${sizeOfCode.hexFull} ($sizeOfCode)
			sizeOfInitialisedData    ${sizeOfInitialisedData.hexFull} ($sizeOfInitialisedData)
			sizeOfUninitialisedData  ${sizeOfUninitialisedData.hexFull} ($sizeOfUninitialisedData)
			addressOfEntryPoint      ${addressOfEntryPoint.hexFull} ($addressOfEntryPoint)
			baseOfCode               ${baseOfCode.hexFull} ($baseOfCode)
			baseOfData               ${baseOfData.hexFull} ($baseOfData)
			imageBase                ${imageBase.toInt().hexFull} ($imageBase)
			sectionAlignment         ${sectionAlignment.hexFull} ($sectionAlignment)
			fileAlignment            ${fileAlignment.hexFull} ($fileAlignment)
			majorOSVersion           $majorOSVersion
			minorOSVersion           $minorOSVersion
			majorImageVersion        $majorImageVersion
			minorImageVersion        $minorImageVersion
			majorSubsystemVersion    $majorSubsystemVersion
			minorSubsystemVersion    $minorSubsystemVersion
			win32VersionValue        $win32VersionValue
			sizeOfImage              ${sizeOfImage.hexFull} ($sizeOfImage)
			sizeOfHeaders            ${sizeOfHeaders.hexFull} ($sizeOfHeaders)
			checkSum                 $checkSum
			subsystem                $subsystem
			dllCharacteristics       ${dllCharacteristics.hexFull} ${DllFlags(dllCharacteristics)}
			sizeOfStackReserve       ${sizeOfStackReserve.hexFull} ($sizeOfStackReserve)
			sizeOfStackCommit        ${sizeOfStackCommit.hexFull} ($sizeOfStackCommit)
			sizeOfHeapReserve        ${sizeOfHeapReserve.hexFull} ($sizeOfHeapReserve)
			sizeOfHeapCommit         ${sizeOfHeapCommit.hexFull} ($sizeOfHeapCommit)
			loaderFlags              $loaderFlags
			numberOfRvaAndSizes      $numberOfRvaAndSizes	
	""".trimIndent())
}



fun Section.print() {
	println("""
		$name:
			virtualSize      ${virtualSize.hexFull} ($virtualSize)
			virtualAddress   ${virtualAddress.hexFull} ($virtualSize)
			pRawData         ${pRawData.hexFull} ($pRawData)
			rawDataSize      ${rawDataSize.hexFull} ($rawDataSize)
			pRelocations     ${pRelocations.hexFull} ($pRelocations)
			numRelocations   ${relocationCount.hexFull} ($relocationCount)
			characteristics  ${characteristics.value.hexFull} $characteristics
	""".trimIndent())
}



fun DataDirectory.print() {
	val name = when(index) {
		0 -> "Export"
		1 -> "Import"
		2 -> "Resource"
		3 -> "Exception"
		4 -> "Certificate"
		5 -> "Base Relocation"
		6 -> "Debug"
		7 -> "Architecture"
		8 -> "Global Ptr"
		9 -> "TLS Table"
		10 -> "Load Config Table"
		11 -> "Bound Import"
		12 -> "Import Address Table"
		13 -> "Delay Import Descriptor"
		14 -> "CLR Runtime Header"
		15 -> "Reserved"
		else -> throw RuntimeException("Too many data directories")
	}

	println("$name rva=${virtualAddress.hexFull} (${virtualAddress}), size=${size.hexFull} (${size})")
}



fun PEFile.print() {
	if(isImage)
		println("Image file\n")
	else
		println("Object file\n")

	coffHeader.print()

	println()

	optionalHeader?.let { it.print(); println() }

	if(sections.isNotEmpty()) {
		println("Sections:")
		for(section in sections) section.print()
	} else {
		println("No sections")
	}

	println()

	if(dataDirectories.isNotEmpty()) {
		println("Data directories:")
		for(d in dataDirectories)
			if(d.size != 0 && d.virtualAddress != 0)
				d.print()
		val emptyCount = dataDirectories.count { it.size == 0 || it.virtualAddress == 0 }
		if(emptyCount != 0) println("$emptyCount empty directory entries")
	} else {
		println("No data directories")
	}

	println()

	if(stringTable.isNotEmpty()) {
		println("String Table:")
		for(s in stringTable)
			println("\t\"$s\"")
	} else {
		println("No string table")
	}

	println()

	if(symbols.isNotEmpty()) {
		println("Symbol Table:")
		for(s in symbols)
			println("\t$s")
	} else {
		println("No symbol table")
	}
}
