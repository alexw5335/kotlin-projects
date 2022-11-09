package assembler



class SymbolTable {

	private val map = HashMap<Interned, Symbol>()

	operator fun get(interned: Interned) = map[interned]

	operator fun set(interned: Interned, symbol: Symbol) = map.put(interned, symbol)

	fun add(symbol: Symbol) = map.put(symbol.name, symbol)

}



class SymbolStack {

	private val list = ArrayList<ArrayList<SymbolTable>>().also { it.add(ArrayList()) }

	private var pos = 0

	private var current = list[0]

	fun push() {
		pos++
		if(pos >= list.size) list.add(ArrayList())
		current = list[pos]
		current.clear()
	}

	fun pop() {
		pos--
		if(pos < 0) error("Stack underflow")
		current = list[pos]
	}

}



interface Ref {
	var section: Section
	var pos: Int
}



private data class RefImpl(
	override var section : Section,
	override var pos     : Int
) : Ref



fun Ref(section: Section, pos: Int): Ref = RefImpl(section, pos)



interface Symbol {
	val name: Interned
}



data class IntSymbol(
	override val name : Interned,
	var value         : Long
) : Symbol



data class LabelSymbol(
	override val name    : Interned,
	override var section : Section = Section.NONE,
	override var pos     : Int = 0
) : Symbol, Ref



data class ImportSymbol(
	override val name    : Interned,
	override var section : Section = Section.NONE,
	override var pos     : Int = 0
) : Symbol, Ref



data class VarSymbol(
	override val name    : Interned,
	override var section : Section = Section.NONE,
	override var pos     : Int = 0
) : Symbol, Ref



data class ResSymbol(
	override val name    : Interned,
	override var section : Section = Section.NONE,
	override var pos     : Int = 0
) : Symbol, Ref



interface Namespace {
	val symbols: SymbolTable
}



data class EnumSymbol(
	override val name    : Interned,
	override val symbols : SymbolTable = SymbolTable()
) : Symbol, Namespace