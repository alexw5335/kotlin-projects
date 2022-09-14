package asm

/**
 * - W: Specifies 64-bit operand size
 * - R: Extends ModRM reg
 * - X: Extends SIB index
 * - B: Extends ModRM rm, SIB base, or opcode reg
 */
class Rex {

	var value = 0

	fun set(w: Int, r: Int, x: Int, b: Int) {
		value = value or (w shl 3) or (r shl 2) or (x shl 1) or b
	}

	var w
		get() = (value and 0b0000_1000) shr 3
		set(w) { value = value or (w shl 3) }

	var r
		get() = (value and 0b0000_0100) shr 2
		set(r) { value = value or (r shl 2) }

	var x
		get() = (value and 0b0000_0010) shr 1
		set(x) { value = value or (x shl 1) }

	var b
		get() = value and 0b0000_0001
		set(b) { value = value or b }

}