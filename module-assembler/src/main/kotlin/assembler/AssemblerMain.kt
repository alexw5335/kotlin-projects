package assembler



private const val INPUT = """

message db "test"
//add rax, [20 * 5+ rax + rcx * 8 + 15]

"""



fun main() {
	assemble(INPUT)
	//Linker().link()
	//PEReader("/generated.exe").read().print()
	//PeWriter().write()
}



private fun assemble(input: String) {
	val lexResult = Lexer.lex(input)
	println("Lexer:")
	for(t in lexResult.tokens) println(t.printableString)

	val parseResult = Parser(lexResult).parse()
	println("\nParser:")
	for(n in parseResult.nodes) println(n.printableString)

	//val assembler = Assembler(parseResult)
	//println("\nAssembler:")
	//assembler.assemble()
}