package eyre

import core.collection.BitList



class LexOutput(
	val tokens   : List<Token>,
	val newlines : BitList
)



class ParseOutput(
	val nodes       : List<AstNode>,
	val fileImports : List<InternArray>
)