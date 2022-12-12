package eyre

data class Relocation(
	val section  : Section,
	val position : Int,
	val width    : Width,
	val value    : AstNode,
	val base     : Ref?
)