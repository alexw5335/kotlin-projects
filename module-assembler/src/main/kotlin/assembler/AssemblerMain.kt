package assembler

fun main() {
	val input = """
		mov qword [rax], 1
	"""

	val lexResult = Lexer.lex(input)
	println("Lexer:")
	lexResult.tokens.forEach { print('\t'); println(it.printableString) }

	val parseResult = Parser(lexResult).parse()
	println("\nParser:")
	parseResult.nodes.forEach { print('\t'); println(it.printableString) }

	val assembler = Assembler(parseResult)
	println("\nAssembler:")
	assembler.assemble()
}