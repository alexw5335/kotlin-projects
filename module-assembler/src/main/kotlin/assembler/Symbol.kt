package assembler

class Symbol(val name: String, val type: Type, val node: AstNode) {

	enum class Type {
		CONST, LABEL;
	}

}