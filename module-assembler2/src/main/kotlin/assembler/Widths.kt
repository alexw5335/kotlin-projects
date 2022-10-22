package assembler

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

	/**
	 * 1 if this is 64-bit default, 0 if not. I.e. If 32-bit is not allowed but at least two others are. MOVSXD is
	 * not 64-bit default.
	 */
	val rexMod = if(bits.countOneBits() == 1) 0 else (bits shr 2) and 1

	operator fun contains(width: Width) = bits and width.bit != 0

}