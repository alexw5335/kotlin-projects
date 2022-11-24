package eyre

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.relativeTo

class Compiler(private val srcFiles: List<SrcFile>) {


	val globalNamespace = NamespaceSymbol(Interns.EMPTY)



	fun compile() {
		val lexOutputs = srcFiles.map { Lexer(it).lex() }
		val parsers = lexOutputs.map { Parser(it, globalNamespace) }
		val parseOutputs = parsers.map { it.parse() }
		for(p in parseOutputs) Resolver(globalNamespace.symbols, p).resolve()

		for(p in parseOutputs)
			for(n in p.nodes)
				println(n.printableString)
	}



	companion object {

		fun create(srcDir: Path, fileNames: List<String>): Compiler {
			val srcFiles = fileNames.map { name ->
				val path = srcDir.resolve("$name.eyre")
				val content = Files.readString(path)
				SrcFile(path, path.relativeTo(srcDir), CharArray(content.length + 4).let(content::toCharArray))
			}

			return Compiler(srcFiles)
		}

		fun create(srcDir: Path): Compiler {
			val srcFiles = Files
				.walk(srcDir)
				.filter { it.extension == "eyre" }
				.map {
					val content = Files.readString(it)
					SrcFile(
						it,
						it.relativeTo(srcDir),
						CharArray(content.length + 4).let(content::toCharArray)
					)
				}
				.toList()

			return Compiler(srcFiles)
		}

	}


}