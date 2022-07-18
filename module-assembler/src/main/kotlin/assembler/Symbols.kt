package assembler

class Symbol(val name: String, val type: Type, val node: AstNode) {

	enum class Type {
		CONST,
		LABEL;
	}

}



sealed interface ResolvedSymbol {
	val name: String
}



class IntSymbol(override val name: String, val value: Long) : ResolvedSymbol