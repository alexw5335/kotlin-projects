package eyre

import core.collection.BitList
import core.collection.IntList
import java.nio.file.Path

class SrcFile(
	val name     : Intern,
	val path     : Path,
	val relPath  : Path,
	val relParts : InternArray,
	val contents : CharArray
) {
	lateinit var tokens: List<Token>
	lateinit var newlines: BitList
	lateinit var lineNumbers: IntList
	lateinit var nodes: List<AstNode>
	var resolved = false
	var resolving = false
	override fun toString() = relPath.toString()
}