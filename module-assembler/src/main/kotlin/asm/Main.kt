@file:Suppress("Unused")

package asm

import core.*
import java.nio.file.Files
import java.nio.file.Paths



private const val input = """
import KERNEL32:WriteFile
import KERNEL32:ExitProcess

main:
	sub rsp, 56
	mov ecx, -11
	lea rdx, [main]
	mov r8d, 1
	mov r9, [rsp + 40]
	mov qword [rsp + 32], 0
	call WriteFile
	call ExitProcess
"""

private const val input2 = """
	
"""



fun main() {
	assemble(input)
	//Core.nasmPrint("call far word [rax]")
	//val linkResult = TestLinker().link()
	//Files.write(Paths.get("test.exe"), linkResult)
	//Core.runPrint("dumpbin /all test.exe")
	//assemble(input)
}



private fun link(input: String) {
	val linkResult = Linker(Assembler(Parser.parse(Lexer.lex(input))).assemble()).link()
	Files.write(Paths.get("test.exe"), linkResult)
	Core.runPrint("dumpbin /all test.exe")
}



private fun assemble(input: String) {
	val assembleResult = Assembler(Parser.parse(Lexer.lex(input))).assemble()
	println("relocations:")
	for(r in assembleResult.relocations)
		println("\t$r")
	Files.write(Paths.get("test.bin"), assembleResult.text)
	Core.run("ndisasm test.bin -b64")
	Files.delete(Paths.get("test.bin"))
}



private fun assembleAndCompare(input: String, input2: String = input) {
	val assembleResult = Assembler(Parser.parse(Lexer.lex(input))).assemble()
	Files.write(Paths.get("test.bin"), assembleResult.text)
	Core.run("ndisasm test.bin -b64")
	Files.delete(Paths.get("test.bin"))
	val nasmBytes = Core.nasmAssemble(input2)
	if(nasmBytes.contentEquals(assembleResult.text)) println("Equal") else println("Not equal")
	for(n in nasmBytes) println("${n.hex8}  ${n.bin233}")
}
