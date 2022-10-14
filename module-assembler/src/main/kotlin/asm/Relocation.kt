package asm

data class Relocation(
	val ref      : Ref,
	val negRef   : Ref,
	val position : Int,
	val width    : Width,
	val disp     : Long
)