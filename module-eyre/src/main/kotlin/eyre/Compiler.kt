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
		val lexOutputs   = srcFiles.map { Lexer(it).lex() }
		val parseOutputs = lexOutputs.map { Parser(it, globalNamespace).parse() }

		for(p in parseOutputs) {
			for(i in p.fileImports) {
				val path = Paths.get(i.components.joinToString(separator = "/") { it.value } + ".eyre")
				val file = srcFiles.first { it.relativePath == path }
				p.file.dependencies.add(file)
			}
		}

		fun processDependencies(file: SrcFile) {
			if(file.parsed) return
			for(p in file.dependencies)
				processDependencies(p)
			// second-stage parsing
			file.parsed = true
		}

		for(file in srcFiles)
			processDependencies(file)
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