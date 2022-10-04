package asm

enum class Widths(val bits: Int) {
	ALL(0b1111),
	NO_8(0b1110),
	NO_8_32(0b1010),
	NO_8_64(0b0110),
	NO_8_16(0b1100),
	ONLY_8(0b0001),
	ONLY_64(0b1000);

	/**
	 * 1 if this is 64-bit default, 0 if not. I.e. if 32-bit width is
	 * allowed by this [Widths].
	 */
	val rexMod = (bits shr 2) and 1

	operator fun contains(width: Width) = bits and width.bit != 0

}