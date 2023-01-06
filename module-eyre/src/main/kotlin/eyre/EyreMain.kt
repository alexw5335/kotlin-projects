package eyre

import java.nio.file.Files
import java.nio.file.Paths
import javax.sound.sampled.AudioSystem
import kotlin.io.path.relativeTo



fun main() {
	val compiler = Compiler("samples", "main.eyre")
	compiler.compile()
	compiler.printAst()
}



private fun Compiler(directory: String, vararg files: String): Compiler {
	val srcFiles = files.map {
		val root        = Paths.get(directory)
		val path        = root.resolve(it)
		val relPath     = path.relativeTo(root)
		val rawContents = Files.readString(path)
		val contents    = CharArray(rawContents.length + 4).also(rawContents::toCharArray)
		SrcFile(root, path, relPath, contents)
	}

	return Compiler(srcFiles)
}