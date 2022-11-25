package eyre

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.relativeTo

class SrcSet(val root: Path) {


	val files: List<SrcFile> = Files
		.walk(root)
		.filter { it.extension == "eyre" }
		.map(::createSrcFile)
		.toList()

	val map: Map<InternArray, SrcFile> = files.associateBy { it.relParts }



	private fun createSrcFile(path: Path): SrcFile {
		val name        = Interner.add(path.fileName.toString())
		val relPath     = path.relativeTo(root)
		val relParts    = InternArray(relPath.map(Path::toString))
		val rawContents = Files.readString(path)
		val contents    = CharArray(rawContents.length + 4).also(rawContents::toCharArray)
		return SrcFile(name, path, relPath, relParts, contents)
	}



	operator fun get(relParts: InternArray) = map[relParts]


}