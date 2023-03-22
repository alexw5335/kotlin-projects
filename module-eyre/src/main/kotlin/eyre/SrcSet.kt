package eyre

import core.Core
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.extension
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.relativeTo

class SrcSet(val srcFiles: List<SrcFile>) {


	companion object {

		fun create(root: Path) = Files
			.walk(root)
			.filter { it.extension == "eyre" }
			.map { createSrcFile(root, it) }
			.toList()
			.let(::SrcSet)

		fun create(root: String) = create(Paths.get(root))

		fun create(root: Path, vararg filePaths: String) = filePaths
			.map { createSrcFile(root, root.resolve(it)) }
			.let(::SrcSet)

		fun createSrcFile(root: Path, path: Path): SrcFile {
			val relPath     = path.relativeTo(root)
			val rawContents = Files.readString(path)
			val contents    = CharArray(rawContents.length + 4).also(rawContents::toCharArray)
			return SrcFile(path, relPath, contents)
		}

	}


}