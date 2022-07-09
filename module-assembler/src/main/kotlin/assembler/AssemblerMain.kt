package assembler



fun main() {
	val input = "add rax, [rax]"

	val lexResult = Lexer(input.toCharArray()).lex()
	println("Lex Result:")
	lexResult.printTokens()

	val parseResult = Parser(lexResult).parse()
	println("\nParse Result:")
	parseResult.printNodes()
}