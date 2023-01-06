package eyre

import core.collection.BitList
import core.collection.IntList
import java.nio.file.Path

class SrcFile(
	val root     : Path,
	val path     : Path,
	val relPath  : Path,
	val contents : CharArray
) {

	lateinit var tokens      : List<Token>
	lateinit var newlines    : BitList
	lateinit var terminators : BitList
	lateinit var nodes       : ArrayList<AstNode>

	var resolving = false
	var resolved = false

	var lineNumbers: IntList? = null

	override fun toString() = "$relPath"

}