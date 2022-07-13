package assembler



fun main() {
	val input = """
		add rax, [rax + rcx * 8 + 32 + -MY_BASE]
	"""

	val lexResult = Lexer(input.toCharArray()).lex()
	println("Lex Result:")
	lexResult.tokens.forEach { println(it.printableString) }

	val parseResult = Parser(lexResult).parse()
	println("\nParse Result:")
	parseResult.nodes.forEach { println(it.printableString) }
}