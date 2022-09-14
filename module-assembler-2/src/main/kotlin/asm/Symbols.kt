package asm



class Symbol(
	val name: String,
	val type: SymbolType,
	var data: SymbolData? = null
)



enum class SymbolType {

	LABEL,
	INT;

}



sealed interface SymbolData

class IntSymbolData(val value: Long) : SymbolData

class LabelSymbolData(val value: Int) : SymbolData