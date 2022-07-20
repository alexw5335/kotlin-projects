package assembler

fun main() {
	val input = "add qword [rax], rax"

	val lexResult = Lexer(input.toCharArray()).lex()
	println("Lex Result:")
	lexResult.tokens.forEach { println(it.printableString) }

	val parseResult = Parser(lexResult).parse()
	println("\nParse Result:")
	parseResult.nodes.forEach { println(it.printableString) }

	val assembler = Assembler(parseResult)
	assembler.assemble()
}