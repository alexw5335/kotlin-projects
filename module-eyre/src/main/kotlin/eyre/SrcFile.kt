package eyre

import core.collection.BitList
import core.collection.IntList
import java.nio.file.Path

class SrcFile(
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
}