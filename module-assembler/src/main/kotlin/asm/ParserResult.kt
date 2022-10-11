package asm

class ParserResult(
	val nodes   : List<AstNode>,
	val symbols : Map<String, Symbol>,
	val imports : List<DllImport>
)