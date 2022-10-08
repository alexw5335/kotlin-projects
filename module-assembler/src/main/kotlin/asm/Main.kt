@file:Suppress("Unused")

package asm

import core.*
import java.nio.file.Files
import java.nio.file.Paths



private const val input = """
main:
	sub rsp, 56
	mov ecx, -11
	lea rdx, [main]
	mov r8d, 1
	mov r9, [rsp + 40]
	mov qword [rsp + 32], 0
"""

private const val input2 = """
	
"""



fun main() {
/*	val parseResult = Parser.parse(Lexer.lex(input))
	val bytes = Assembler(parseResult).assemble()
	Files.write(Paths.get("test.bin"), bytes)*/
	val bytes2 = Linker(AssembleResult(ByteArray(0), emptyList())).link()
	Files.write(Paths.get("test.exe"), bytes2)
	//Core.run("DUMPBIN /ALL test.exe")
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