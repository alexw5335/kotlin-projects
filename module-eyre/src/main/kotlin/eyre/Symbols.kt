package eyre



class SymbolTable(map: MutableMap<Intern, Symbol> = HashMap()) {

	private val map = map

	operator fun get(intern: Intern) = map[intern]

	fun add(symbol: Symbol) = map.put(symbol.name, symbol)

	override fun toString() = map.toString()

	fun clear() = map.clear()

}



class SymbolStack {

	private val frames = ArrayList<Frame>()

	private var pos = 0

	private var current = Frame().also(frames::add)

	fun push(localTable: SymbolTable) {
		pos++
		if(pos >= frames.size) frames.add(Frame())
		current = frames[pos]
		current.localTable = localTable
		current.importedTables.clear()
		current.importedSymbols.clear()
	}

	fun addImportedTable(table: SymbolTable) = current.importedTables.add(table)

	fun addImportedSymbol(symbol: Symbol) = current.importedSymbols.add(symbol)

	fun pop() {
		pos--
		if(pos < 0) error("Stack underflow")
		current = frames[pos]
	}

	fun get(name: Intern): Symbol? {
		for(i in frames.indices.reversed()) {
			val frame = frames[i]
			frame.localTable[name]?.let { return it }

			frame.importedSymbols[name]?.let { return it }

			for(table in frame.importedTables)
				table[name]?.let { return it }
		}

		return null
	}

	private class Frame {
		lateinit var localTable : SymbolTable
		val importedTables      : ArrayList<SymbolTable>  = ArrayList()
		val importedSymbols     : SymbolTable             = SymbolTable()
	}

}



sealed interface Symbol {
	val name: Intern
	val visibility: Visibility
}



data class IntSymbol(
	override val name       : Intern,
	override val visibility : Visibility,
	val value               : Long
) : Symbol



data class Namespace(
	override val name       : Intern,
	override val visibility : Visibility,
	val symbolTable         : SymbolTable
) : Symbol