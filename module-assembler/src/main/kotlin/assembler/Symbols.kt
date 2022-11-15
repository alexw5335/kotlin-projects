package assembler



class SymbolTable {

	private val map = HashMap<Intern, Symbol>()

	operator fun get(intern: Intern) = map[intern]

	operator fun set(intern: Intern, symbol: Symbol) = map.put(intern, symbol)

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
	val name: Intern
}



data class IntSymbol(
	override val name : Intern,
	var value         : Long
) : Symbol



data class LabelSymbol(
	override val name    : Intern,
	override var section : Section = Section.NONE,
	override var pos     : Int = 0
) : Symbol, Ref



data class ImportSymbol(
	override val name    : Intern,
	override var section : Section = Section.NONE,
	override var pos     : Int = 0
) : Symbol, Ref



data class VarSymbol(
	override val name    : Intern,
	override var section : Section = Section.NONE,
	override var pos     : Int = 0
) : Symbol, Ref



data class ResSymbol(
	override val name    : Intern,
	override var section : Section = Section.NONE,
	override var pos     : Int = 0
) : Symbol, Ref



data class Namespace(
	override val name: Intern,
	val symbols: SymbolTable
) : Symbol