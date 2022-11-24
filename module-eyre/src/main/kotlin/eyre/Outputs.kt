package eyre

import core.collection.BitList
import java.nio.file.Path



class SrcFile(
	val path         : Path,
	val relativePath : Path,
	val text         : CharArray
)



class LexOutput(
	val file     : SrcFile,
	val tokens   : List<Token>,
	val newlines : BitList
)



class ParseOutput(
	val file        : SrcFile,
	val nodes       : List<AstNode>
)
