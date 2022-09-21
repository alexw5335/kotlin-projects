package asm

/**
 * - W: Specifies 64-bit operand size
 * - R: Extends ModRM reg
 * - X: Extends SIB index
 * - B: Extends ModRM rm, SIB base, or opcode reg
 */
class Rex {

	var value = 0

	val present get() = value != 0

	val final get() = value or 0b0100_0000

	fun set(w: Int, r: Int, x: Int, b: Int) {
		value = (w shl 3) or (r shl 2) or (x shl 1) or b
	}

	/**
	 * Specifies 64-bit operand size
	 */
	var w
		get() = (value and 0b0000_1000) shr 3
		set(w) { value = value or (w shl 3) }

	/**
	 * Extends ModRM:reg
	 */
	var r
		get() = (value and 0b0000_0100) shr 2
		set(r) { value = value or (r shl 2) }

	/**
	 * Extends SIB:index
	 */
	var x
		get() = (value and 0b0000_0010) shr 1
		set(x) { value = value or (x shl 1) }

	/**
	 * Extends ModRM:rm, SIB:base, or opcode:reg
	 */
	var b
		get() = value and 0b0000_0001
		set(b) { value = value or b }

}