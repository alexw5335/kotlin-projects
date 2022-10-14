package asm



interface Ref {
	var pos: Int
}



private data class RefImpl(override var pos: Int) : Ref

fun PosRef(pos: Int = 0): Ref = RefImpl(pos)



interface Symbol {
	val name: String
}



data class IntSymbol(override val name: String, var value: Long = 0L) : Symbol

data class LabelSymbol(override val name: String, override var pos: Int = 0) : Symbol, Ref

data class ImportSymbol(override val name: String, override var pos: Int = 0) : Symbol, Ref

data class ValSymbol(override val name: String, override var pos: Int = 0) : Symbol, Ref