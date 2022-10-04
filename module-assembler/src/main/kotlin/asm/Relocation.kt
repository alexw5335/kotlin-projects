package asm

data class Relocation(
	val symbol   : Symbol,
	val symbol2  : Symbol?,
	val position : Int,
	val width    : Width,
	val disp     : Long
)