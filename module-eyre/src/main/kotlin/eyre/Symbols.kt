package eyre



class SymbolTable {

	private val map = HashMap<Intern, Symbol>()

	operator fun get(intern: Intern) = map[intern]

	operator fun set(intern: Intern, symbol: Symbol) = map.put(intern, symbol)

	fun add(symbol: Symbol) = map.put(symbol.name, symbol)

}



interface Symbol {
	val name: Intern
}



interface Ref : Symbol {
	val section: Section
	val pos: Int
}



class AnonSymbol(
	override val name    : Intern,
	override var section : Section = Section.NONE,
	override val pos     : Int = 0
) : Ref



data class LabelSymbol(
	override val name    : Intern,
	override var section : Section = Section.NONE,
	override var pos     : Int = 0
) : Ref



data class ImportSymbol(
	override val name    : Intern,
	override var section : Section = Section.NONE,
	override var pos     : Int = 0
) : Ref



data class VarSymbol(
	override val name    : Intern,
	override var section : Section = Section.NONE,
	override var pos     : Int = 0
) : Ref



data class ResSymbol(
	override val name    : Intern,
	override var section : Section = Section.NONE,
	override var pos     : Int = 0
) : Ref



data class NamespaceSymbol(
	override val name : Intern,
	val symbols       : SymbolTable = SymbolTable()
) : Symbol