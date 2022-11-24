package eyre



class SymbolTable {

	private val map = HashMap<Intern, Symbol>()

	operator fun get(intern: Intern) = map[intern]

	operator fun set(intern: Intern, symbol: Symbol) = map.put(intern, symbol)

	fun add(symbol: Symbol) = map.put(symbol.name, symbol)

	override fun toString() = map.toString()

}



class SymbolStack {

	private val list = ArrayList<ArrayList<SymbolTable>>()

	private var pos = 0

	private var current = ArrayList<SymbolTable>().also(list::add)

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

	fun add(table: SymbolTable) {
		current.add(table)
	}

	fun get(intern: Intern): Symbol? {
		for(tables in list)
			for(table in tables)
				table[intern]?.let { return it }
		return null
	}

}



interface Symbol {
	val name: Intern
}



interface Ref : Symbol {
	val section: Section
	val pos: Int
}



class IntSymbol(override val name: Intern, var value: Long = 0) :Symbol



class LabelSymbol(
	override val name    : Intern,
	override var section : Section = Section.NONE,
	override var pos     : Int     = 0
) : Ref



data class NamespaceSymbol(
	override val name : Intern,
	val symbols       : SymbolTable = SymbolTable()
) : Symbol