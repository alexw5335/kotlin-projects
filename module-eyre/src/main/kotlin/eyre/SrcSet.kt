package eyre

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension

class SrcSet(val root: Path, val srcFiles: List<SrcFile>) {


	companion object {

		fun create(root: Path) = Files
			.walk(root)
			.filter { it.extension == "eyre" }
			.map { SrcFile.create(root, it) }
			.toList()
			.let { SrcSet(root, it) }

		fun create(root: Path, vararg filePaths: String) = filePaths
			.map { SrcFile.create(root, root.resolve(it)) }
			.let { SrcSet(root, it) }

	}


}