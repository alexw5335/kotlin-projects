package assembler



fun main() {
	val input = """
		testing:
			add rax, 1 << 8 | 2 << 16 | 3 << -0
	"""

	val lexResult = Lexer(input.toCharArray()).lex()
	println("Lex Result:")
	lexResult.tokens.forEach { println(it.printableString) }

	val parseResult = Parser(lexResult).parse()
	println("\nParse Result:")
	parseResult.nodes.forEach { println(it.printableString) }
}