package assembler



fun main() {
	val input = "const testing = 1 + 2 * 3 + 4 / 5"

	val lexResult = Lexer(input.toCharArray()).lex()
	println("Lex Result:")
	lexResult.tokens.forEach { println(it.printableString) }

	val parseResult = Parser(lexResult).parse()
	println("\nParse Result:")
	parseResult.nodes.forEach { println(it.printableString) }
}