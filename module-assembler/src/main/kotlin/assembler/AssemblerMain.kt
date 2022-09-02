package assembler



private const val INPUT = """

add rax, modrm.rm + sib.base.inv * sib.index

"""



fun main() {
	assemble(INPUT)
	//Linker().link()
	//PEReader("/generated.exe").read().print()
	//PeWriter().write()
}



private fun LexResult.printFormatted() {
	println("Lexer:")
	for(t in tokens) {
		if(t == EndToken) break
		print('\t')
		println(t.printableString)
	}
}



private fun ParseResult.printFormatted() {
	println("Parser:")
	for(n in nodes) {
		print('\t')
		println(n.printableString)
	}
}



private fun assemble(input: String) {
	val lexResult = Lexer.lex(input).also { it.printFormatted() }
	val parseResult = Parser.parse(lexResult).also { it.printFormatted() }

	//val assembler = Assembler(parseResult)
	//println("\nAssembler:")
	//assembler.assemble()
}