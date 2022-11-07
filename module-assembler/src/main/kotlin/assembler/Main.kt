package assembler

import java.nio.file.Files
import java.nio.file.Paths




const val input = """
import kernel32.WriteFile
import kernel32.ExitProcess

var message db "Message"

main:
	sub rsp, 56
	mov ecx, -11
	lea rdx, [message]
	mov r8d, 8
	mov r9, [rsp + 40]
	mov qword [rsp + 32], 0
	call WriteFile
	call ExitProcess
"""



fun main() {
	val lexerResult = Lexer(input.toCharArray()).lex()
	val parserResult = Parser(lexerResult).parse()
	val assemblerResult = Assembler(parserResult).assemble()
	//Files.write(Paths.get("test.bin"), assemblerResult.text)
	//Core.run("ndisasm -b64 test.bin")
	val linkerResult = Linker(assemblerResult).link()
	Files.write(Paths.get("test.exe"), linkerResult)
}