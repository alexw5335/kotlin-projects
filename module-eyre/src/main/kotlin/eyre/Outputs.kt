package eyre

import core.collection.BitList



class LexOutput(
	val srcFile  : SrcFile,
	val tokens   : List<Token>,
	val newlines : BitList
)



class ParseOutput(
	val srcFile     : SrcFile,
	val nodes       : List<AstNode>,
	val fileImports : List<InternArray>
)