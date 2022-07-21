package assembler



interface Symbol {

	val name: String

}



class IntSymbol(override val name: String, val value: Long) : Symbol



class LabelSymbol(override val name: String, val node: AstNode) : Symbol