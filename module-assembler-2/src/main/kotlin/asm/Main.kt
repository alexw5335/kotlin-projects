package asm

import core.Core
import core.bin233
import core.hex8
import java.nio.file.Files
import java.nio.file.Paths



fun main() {
	assemble("nop")
}



private fun assemble(input: String) {
	val parseResult = Parser.parse(Lexer.lex(input))
	val bytes = Assembler2(parseResult).assemble()
	Files.write(Paths.get("test.bin"), bytes)
	Core.run("ndisasm test.bin -b64")
	val nasmBytes = Core.nasmAssemble(input)
	if(nasmBytes.contentEquals(bytes)) println("Equal") else println("Not equal")
	for(n in nasmBytes) println("${n.hex8}  ${n.bin233}")
}