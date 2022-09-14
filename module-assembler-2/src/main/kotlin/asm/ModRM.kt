package asm

class ModRM {

	var value = 0

	fun set(mod: Int, reg: Int, rm: Int) {
		value = value or (mod shl 6) or (reg shl 3) or rm
	}

	var mod
		get() = (value and 0b11_000_000) shr 6
		set(mod) { value = value or (mod shl 6) }

	var reg
		get() = (value and 0b00_111_000) shr 3
		set(reg) { value = value or (reg shl 3) }

	var rm
		get() = (value and 0b000_000_111)
		set(rm) { value = value or rm }

}