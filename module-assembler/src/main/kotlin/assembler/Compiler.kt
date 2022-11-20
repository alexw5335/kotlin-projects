package assembler

import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.io.path.*



class SrcFile(val path: Path, val relativePath: Path, val chars: CharArray)



private val String.padded get() = CharArray(length + 2).let(::toCharArray)




class Compiler(val srcFiles: List<SrcFile>) {


	private val lexerResults = ArrayList<LexerResult>()



	fun compile() {
		lex()
	}



	private fun lex() {
		for(file in srcFiles)
			lexerResults.add(Lexer(file.chars).lex())
	}



	companion object {

		fun create(srcDir: Path): Compiler {
			if(!srcDir.exists())
				error("Source directory does not exist: $srcDir")

			if(!srcDir.isDirectory())
				error("Source directory is not a directory: $srcDir")

			val srcFiles = Files
				.walk(srcDir)
				.filter { it.extension == "eyre" }
				.map { SrcFile(it, it.relativeTo(srcDir), Files.readString(it).padded) }
				.toList()

			return Compiler(srcFiles)
		}

	}


}