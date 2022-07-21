package assembler



class Symbol(val name: String, val type: SymbolType, val node: AstNode)



enum class SymbolType {

	CONST,

	LABEL;

}



sealed interface ResolvedSymbol {
	val name: String
}



class IntSymbol(override val name: String, val value: Long) : ResolvedSymbol