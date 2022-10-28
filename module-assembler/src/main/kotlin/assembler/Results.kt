package assembler

import core.BitList
import core.ByteList



class DllImport(val dll: String, val symbol: ImportSymbol)



class LexerResult(
	val tokens        : List<Token>,
	val newlines      : BitList,
	val newlineCounts : ByteList
)



class ParserResult(
	val nodes   : List<AstNode>,
	val symbols : Map<String, Symbol>,
	val imports : List<DllImport>
)