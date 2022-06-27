package assembler.generator

import core.Core

fun main() {
	val tokens = Lexer(Core.readResourceText("/instructions.txt")).lex()
	Parser(tokens).parse()
/*	val instructions = InstructionReader::class.java
		.getResourceAsStream("/instructions.txt")!!
		.reader()
		.readText()
		.toCharArray()
		.let(::InstructionReader)
		.readInstructions()

	instructions.forEach(::println)
*/
}