@file:Suppress("Unused")

package asm

import core.Core
import core.bin233
import core.hex8
import java.nio.file.Files
import java.nio.file.Paths



private const val input = """
	add rax, [label]
	add rax, rax
	label:
	add rcx, rdx
"""

private const val input2 = """
	add rax, [rel label]
	add rax, rax
	label:
	add rcx, rdx
"""


fun main() {
	assemble("enterw 64, 5")
	//assembleAndCompare("leave")
	//assembleAndCompare(input, input2)
}




private fun assemble(input: String) {
	val parseResult = Parser.parse(Lexer.lex(input))
	val bytes = Assembler(parseResult).assemble()
	Files.write(Paths.get("test.bin"), bytes)
	Core.run("ndisasm test.bin -b64")
	Files.delete(Paths.get("test.bin"))
}



private fun assembleAndCompare(input: String, input2: String = input) {
	val parseResult = Parser.parse(Lexer.lex(input))
	val bytes = Assembler(parseResult).assemble()
	Files.write(Paths.get("test.bin"), bytes)
	Core.run("ndisasm test.bin -b64")
	Files.delete(Paths.get("test.bin"))
	val nasmBytes = Core.nasmAssemble(input2)
	if(nasmBytes.contentEquals(bytes)) println("Equal") else println("Not equal")
	for(n in nasmBytes) println("${n.hex8}  ${n.bin233}")
}