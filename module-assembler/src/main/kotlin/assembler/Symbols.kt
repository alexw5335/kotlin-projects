package assembler



class Symbol(
	val namespace: Namespace,
	val name: String,
	val type: SymbolType,
	var data: SymbolData? = null
)



enum class SymbolType {
	LABEL,
	INT;
}



interface SymbolData

class IntSymbolData(val value: Long) : SymbolData

class LabelSymbolData(val value: Long) : SymbolData

class EnumSymbolData(val name: String, val entries: List<EnumEntry>)

class EnumEntry(val name: String, val value: AstNode?)