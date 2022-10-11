package asm



interface PosRef {
	var pos: Int
}



private class PosRefImpl(override var pos: Int) : PosRef

fun PosRef(pos: Int = 0): PosRef = PosRefImpl(pos)



interface Symbol {
	val name: String
}



class IntSymbol(override val name: String, var value: Long = 0L) : Symbol

class LabelSymbol(override val name: String, override var pos: Int = 0) : Symbol, PosRef

class ImportSymbol(override val name: String, override var pos: Int = 0) : Symbol, PosRef