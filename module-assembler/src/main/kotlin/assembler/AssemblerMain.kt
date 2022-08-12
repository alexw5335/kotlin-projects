package assembler



private const val INPUT = """

const test = 1 + 2 + rax * 2

"""



fun main() {
	assemble(INPUT)
}



@Suppress("SameParameterValue")
private fun assemble(input: String) {
	val lexResult = Lexer.lex(input)
	//println("Lexer:")
	//for(t in lexResult.tokens) println(t.printableString)

	val parseResult = Parser(lexResult).parse()
	//println("\nParser:")
	//for(n in parseResult.nodes) println(n.printableString)

	//val assembler = Assembler(parseResult)
	//println("\nAssembler:")
	//assembler.assemble()
}