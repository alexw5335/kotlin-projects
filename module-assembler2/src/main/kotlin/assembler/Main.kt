package assembler

import core.Core
import java.nio.file.Files
import java.nio.file.Paths



private const val input = """
	
import KERNEL32:WriteFile
import KERNEL32:ExitProcess

call WriteFile

"""



fun main() {
	assemble(input)
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