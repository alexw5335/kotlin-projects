@file:Suppress("Unused")

package assembler

import core.Core
import core.binary.BinaryWriter
import core.memory.NativeWriter
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.measureTimeMillis



fun main() {
	//link()
}



private fun assemble() {
	val lexerResult = Lexer(Core.readResourceText("/testing.eyre")).lex()
	val parserResult = Parser(lexerResult).parse()
	val assemblerResult = Assembler(parserResult).assemble()
	Files.write(Paths.get("test.bin"), assemblerResult.text)
	Core.run("ndisasm -b64 test.bin")
}



private fun link() {
	val lexerResult = Lexer(Core.readResourceText("/testing.eyre")).lex()
	val parserResult = Parser(lexerResult).parse()
	val assemblerResult = Assembler(parserResult).assemble()
	val linkerResult = Linker(assemblerResult).link()
	Files.write(Paths.get("test.exe"), linkerResult)
}