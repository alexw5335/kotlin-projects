package assembler

import core.collection.BitList



data class Relocation(
	val section  : Section,
	val position : Int,
	val width    : Width,
	val value    : AstNode,
	val base     : Ref?
)


class DllImport(val dll: Interned, val symbol: ImportSymbol)



class LexerResult(
	val tokens        : List<Token>,
	val newlines      : BitList
)



class ParserResult(
	val nodes   : List<AstNode>,
	val symbols : SymbolTable,
	val imports : List<DllImport>
)



class AssemblerResult(
	val text        : ByteArray,
	val data        : ByteArray,
	val bssSize     : Int,
	val imports     : List<DllImport>,
	val relocations : List<Relocation>,
	val symbols     : SymbolTable,
)