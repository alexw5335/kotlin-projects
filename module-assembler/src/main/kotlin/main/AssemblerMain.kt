package assembler



fun main() {
	EncodingReader::class.java
		.getResourceAsStream("/instructions.txt")!!
		.bufferedReader()
		.readText()
		.toCharArray()
		.let(::EncodingReader)
		.read()

	//val input = "add eax, 1"
	//val tokens = Lexer(input.toCharArray()).lex()
	//val nodes = Parser(tokens).parse()
	//for(n in nodes) println(n.printableString)
}



fun calculate(node: AstNode): Long = when(node) {
	is IntNode -> node.value
	is UnaryOpNode -> node.op.calculate(calculate(node.node))
	is BinaryOpNode -> node.op.calculate(calculate(node.left), calculate(node.right))
	else -> error("Invalid node: $node")
}



val AstNode.printableString: String get() = when(this) {
	is BinaryOpNode    -> "$op(${left.printableString}, ${right.printableString})"
	is IdNode          -> value
	is InstructionNode -> buildString {
		append(mnemonic)
		if(operand1 != null) append(" ${operand1.printableString}")
		if(operand2 != null) append(", ${operand2.printableString}")
		if(operand3 != null) append(", ${operand3.printableString}")
		if(operand4 != null) append(", ${operand4.printableString}")
	}
	is IntNode         -> value.toString()
	is RegisterNode    -> register.name.lowercase()
	is ImmediateNode   -> value.toString()
	is OperandNode     -> "ERROR: Unhandled operand"
	is UnaryOpNode     -> "$op($node)"
}



fun printToken(token: Token) {
	when(token) {
		is Symbol        -> println("SYMBOL    ${token.string}")
		is Identifier    -> println("ID        ${token.value}")
		is IntLiteral    -> println("INT       ${token.value}")
		is MnemonicToken -> println("MNEMONIC  ${token.value.name.lowercase()}")
		is RegisterToken -> println("REGISTER  ${token.value.name.lowercase()}")
	}
}