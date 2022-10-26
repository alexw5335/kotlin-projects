@file:Suppress("Unused")

package assembler

import core.Core
import core.binary.BinaryWriter
import core.memory.NativeWriter
import core.memory.Unsafe
import core.swapEndian16
import core.swapEndian32
import core.swapEndian64
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.max



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



fun main() {
	//link(input)
	//assemble(input)
	//link(input)
}



private fun link(input: String) {
	val lexResult = Lexer(input.toCharArray()).lex()
	val parseResult = Parser(lexResult).parse()
	val assemblerResult = Assembler(parseResult).assemble()
	val linkerResult = Linker(assemblerResult).link()
	Files.write(Paths.get("test.exe"), linkerResult)
}



private fun assemble(input: String) {
	val lexResult = Lexer(input.toCharArray()).lex()
	val parseResult = Parser(lexResult).parse()
	val assemblerResult = Assembler(parseResult).assemble()

	if(assemblerResult.relocations.isNotEmpty()) {
		println("Relocations:")
		for(r in assemblerResult.relocations) {
			println("\t$r")
		}
	}

	Files.write(Paths.get("test.bin"), assemblerResult.text)
	Core.run("ndisasm test.bin -b64")
	Files.delete(Paths.get("test.bin"))
}