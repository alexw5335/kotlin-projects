package assembler

fun main() {
	val input = """
		const myValue  = 2
		const myValue2 = myValue * 2 + 1
	"""

	val lexResult = Lexer(input.toCharArray()).lex()
	println("Lexer:")
	lexResult.tokens.forEach { print('\t'); println(it.printableString) }

	val parseResult = Parser(lexResult).parse()
	println("\nParser:")
	parseResult.nodes.forEach { print('\t'); println(it.printableString) }

	val assembler = Assembler(parseResult)
	println("\nAssembler:")
	assembler.assemble()
}