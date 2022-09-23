package asm

class Sib {

	var value = 0

	fun set(scale: Int, index: Int, base: Int) {
		value = (scale shl 6) or (index shl 3) or base
	}

	var scale
		get() = (value and 0b11_000_000) shr 6
		set(scale) { value = value or (scale shl 6) }

	var index
		get() = (value and 0b00_111_000) shr 3
		set(index) { value = value or (index shl 3) }

	var base
		get() = (value and 0b000_000_111)
		set(base) { value = value or base }

}