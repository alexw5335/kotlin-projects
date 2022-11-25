package eyre

import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.relativeTo

class InternDir(val rootPath: Path) {


	private val files: List<SrcFile> = Files.walk(rootPath).map(::createSrcFile).toList()

	private val map: Map<InternArray, SrcFile> = files.associateBy { it.relParts }

	operator fun get(relParts: InternArray) = map[relParts]



	private val srcFiles = ArrayList<SrcFile>()

	private val root = populate(rootPath)



	private fun createSrcFile(path: Path): SrcFile {
		val name = Interner.add(path.fileName.toString())
		val relPath = path.relativeTo(rootPath)
		val relNames = relPath.map(Path::toString)
		val relParts = IntArray(relNames.size) { Interner.add(relNames[it]).id }
		return SrcFile(name, path, relPath, InternArray(relParts))
	}



	fun read() {
		for(srcFile in srcFiles) {
			val rawContents = Files.readString(srcFile.path)
			val contents = CharArray(rawContents.length + 4)
			rawContents.toCharArray(contents)
			srcFile.contents = contents
		}
	}



	private fun populate(path: Path): Node {
		if(path.isDirectory())
			return Node(null, Files.list(path).map(::populate).toList())

		if(path.extension != "eyre")
			error("Invalid file: $path")

		val name       = Interner.add(path.fileName.toString())
		val relPath    = path.relativeTo(rootPath)
		val relNames   = relPath.map(Path::toString)
		val relInterns = IntArray(relNames.size) { Interner.add(relNames[it]).id }
		val srcFile    = SrcFile(name, path, path.relativeTo(rootPath), relInterns)
		srcFiles.add(srcFile)
		Arrays.hashCode()
		return Node(srcFile, emptyList())
	}



	private class Node(val file: SrcFile?, val children: List<Node>)



	fun getFile(internNames: IntArray): SrcFile? {
		var node = root
		for(i in internNames) {
			if(node.file != null) return null
			for(n in node.children)
				if(n.file)
		}
	}

}