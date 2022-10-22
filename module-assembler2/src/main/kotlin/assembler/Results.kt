package assembler

import core.BitList



class Relocation(
	val position : Int,
	val width    : Width,
	val value    : AstNode?,
	val base     : Ref?
) {

	override fun toString() = "position: $position, width: $width, value: ${value?.printableString}, base: ${base?.pos}"

}



class DllImport(val dll: String, val symbol: ImportSymbol)



class LexerResult(val tokens: List<Token>, val newlines: BitList)



class ParserResult(
	val nodes   : List<AstNode>,
	val symbols : Map<String, Symbol>,
	val imports : List<DllImport>
)



class AssemblerResult(
	val text        : ByteArray,
	val data        : ByteArray,
	val bssSize     : Int,
	val imports     : List<DllImport>,
	val relocations : List<Relocation>,
	val symbols     : Map<String, Symbol>,
)