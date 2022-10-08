package asm

enum class Widths(val bits: Int) {

	NONE  (0b0000),
	ALL   (0b1111),
	NO8   (0b1110),
	NO16  (0b1100),
	NO832 (0b1010),
	NO864 (0b0110),
	ONLY8 (0b0001),
	ONLY16(0b0010),
	ONLY64(0b1000);

	/**
	 * 1 if this is 64-bit default, 0 if not. I.e. if 32-bit width is
	 * allowed by this [Widths].
	 */
	val rexMod = (bits shr 2) and 1

	operator fun contains(width: Width) = bits and width.bit != 0

}