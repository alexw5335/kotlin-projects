package assembler



fun main() {
	val input = "add rax, [rax + rcx * 2 + 10]"

	val lexResult = Lexer(input.toCharArray()).lex()
	println("Lex Result:")
	lexResult.tokens.forEach { if(it !is EndToken) println(it.printableString) }

	val parseResult = Parser(lexResult).parse()
	println("\nParse Result:")
	parseResult.printNodes()
}