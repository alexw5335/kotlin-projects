package asm

data class Relocation(
	val pos      : PosRef,
	val neg      : PosRef,
	val position : Int,
	val width    : Width,
	val disp     : Long
)