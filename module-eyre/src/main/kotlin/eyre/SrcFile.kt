package eyre

import core.collection.BitList
import core.collection.IntList
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.relativeTo

class SrcFile(
	val root     : Path,
	val path     : Path,
	val relPath  : Path,
	val contents : CharArray
) {

	lateinit var tokens: List<Token>
	lateinit var newlines: BitList
	lateinit var nodes: List<AstNode>

	var resolved = false
	var resolving = false
	var lineNumbers: IntList? = null

	override fun toString() = relPath.toString()

	companion object {
		fun create(root: Path, path: Path): SrcFile {
			val relPath     = path.relativeTo(root)
			val rawContents = Files.readString(path)
			val contents    = CharArray(rawContents.length + 4).also(rawContents::toCharArray)
			return SrcFile(root, path, relPath, contents)
		}
	}

}