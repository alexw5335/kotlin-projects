package assembler



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



data class NamespaceSymbol(override val name: Interned) : Symbol {

	val symbols = HashMap<Interned, Symbol>()

}