package asm



private data class RefImpl(override var section: Section, override var pos: Int) : Ref

fun Ref(section: Section = Section.NONE, pos: Int = 0): Ref = RefImpl(section, pos)



interface Ref {
	var section: Section
	var pos: Int
}



interface Symbol {
	val name: String
}



/**
 * A compile-time integer constant.
 */
data class IntSymbol(
	override val name : String,
	var value         : Long = 0L
) : Symbol



data class LabelSymbol(
	override val name    : String,
	override var section : Section,
	override var pos     : Int     = 0
) : Symbol, Ref



data class ImportSymbol(
	override val name    : String,
	override var section : Section,
	override var pos     : Int     = 0
) : Symbol, Ref



data class VarSymbol(
	override val name    : String,
	override var section : Section,
	override var pos     : Int     = 0
) : Symbol, Ref