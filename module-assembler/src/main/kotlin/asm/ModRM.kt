package asm

import core.bin233

class ModRM {

	var value = 0

	fun set(mod: Int, reg: Int, rm: Int) {
		value = (mod shl 6) or (reg shl 3) or rm
	}

	val hasSib get() = rm == 0b100 && mod != 0b11

	val hasDisp8 get() = mod == 0b01

	val hasDisp32 get() = mod == 0b10

	val hasRipRelative get() = mod == 0b00 && rm == 0b101

	var mod
		get() = (value and 0b11_000_000) shr 6
		set(mod) { value = value or (mod shl 6) }

	var reg
		get() = (value and 0b00_111_000) shr 3
		set(reg) { value = value or (reg shl 3) }

	var rm
		get() = (value and 0b000_000_111)
		set(rm) { value = value or rm }

	override fun toString() = value.bin233

}