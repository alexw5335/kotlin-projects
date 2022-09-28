package asm

import core.Core
import core.bin233
import core.hex8
import java.nio.file.Files
import java.nio.file.Paths



fun main() {
	//assemble("add rax, [-10 + rax - 10 + rcx * 2 + 10]")
}



private fun assemble(input: String) {
	val parseResult = Parser.parse(Lexer.lex(input))
	val bytes = Assembler(parseResult).assemble()
	Files.write(Paths.get("test.bin"), bytes)
	Core.run("ndisasm test.bin -b64")
	Files.delete(Paths.get("test.bin"))
	val nasmBytes = Core.nasmAssemble(input)
	if(nasmBytes.contentEquals(bytes)) println("Equal") else println("Not equal")
	for(n in nasmBytes) println("${n.hex8}  ${n.bin233}")
}