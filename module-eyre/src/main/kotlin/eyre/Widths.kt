package eyre

enum class Widths(val bits: Int) {

	NONE  (0b0000),
	ALL   (0b1111),
	NO8   (0b1110),
	NO64  (0b0111),
	NO816 (0b1100),
	NO832 (0b1010),
	NO864 (0b0110),
	ONLY8 (0b0001),
	ONLY16(0b0010),
	ONLY64(0b1000);

	val rexMod = (bits shr 2) and 1

	operator fun contains(width: Width) = bits and width.bit != 0

}