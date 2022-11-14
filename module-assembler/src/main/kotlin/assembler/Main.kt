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
	test1()
}


val bytes = byteArrayOf(123,123,123,34,1,1,2,54,56,2,3,56,76,4,23,12,1,2,32,5,34,23,21,43,3,2,12,35,2)


fun test2() {
	val writer = BinaryWriter()
	measureTimeMillis {
		for(i in 0 until 1000) {
			writer.pos = 0
			for(j in 0 until 100000)
				writer.bytes(bytes)
		}
	}.also(::println)
}



fun test1() {
	val writer = NativeWriter()
	measureTimeMillis {
		for(i in 0 until 1000) {
			writer.pos = 0
			for(j in 0 until 100000)
				writer.bytes(bytes)
		}
	}.also(::println)
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