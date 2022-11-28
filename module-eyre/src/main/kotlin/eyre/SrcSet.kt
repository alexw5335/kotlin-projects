package eyre

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.relativeTo

class SrcSet(val root: Path) {


	val files: List<SrcFile> = Files
		.walk(root)
		.filter { it.extension == "eyre" }
		.map(::createSrcFile)
		.toList()

	val map = files.associateBy { it.relParts }



	private fun createSrcFile(path: Path): SrcFile {
		val name = Interner.add(path.nameWithoutExtension)
		val relPath = path.relativeTo(root)
		val relNames = relPath.toList()

		val relParts = IntArray(relNames.size) {
			if(it == relNames.size - 1)
				name.id
			else
				Interner.add(relNames[it].name).id
		}

		val rawContents = Files.readString(path)
		val contents = CharArray(rawContents.length + 4).also(rawContents::toCharArray)
		return SrcFile(name, path, relPath, InternArray(relParts), contents)
	}



	operator fun get(relParts: InternArray) = map[relParts]


}