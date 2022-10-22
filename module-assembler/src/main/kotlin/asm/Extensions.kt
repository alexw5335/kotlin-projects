package asm

import core.binary.BinaryWriter

fun BinaryWriter.u24(value: Int) { u32(value); pos-- }

fun BinaryWriter.uint(width: Width, value: Int) {
	when(width) {
		Width.BIT8  -> u8(value)
		Width.BIT16 -> u16(value)
		else        -> u32(value)
	}
}

fun BinaryWriter.uint(width: Width, value: Long) {
	when(width) {
		Width.BIT8  -> u8(value.toInt())
		Width.BIT16 -> u16(value.toInt())
		Width.BIT32 -> u32(value.toInt())
		else        -> u64(value)
	}
}

fun BinaryWriter.uint(pos: Int, width: Width, value: Int) {
	when(width) {
		Width.BIT8  -> u8(pos, value)
		Width.BIT16 -> u16(pos, value)
		else        -> u32(pos, value)
	}
}

fun BinaryWriter.uint(pos: Int, width: Width, value: Long) {
	when(width) {
		Width.BIT8  -> u8(pos, value.toInt())
		Width.BIT16 -> u16(pos, value.toInt())
		Width.BIT32 -> u32(pos, value.toInt())
		else        -> u64(pos, value)
	}
}



val Int.isImm8 get() = this in Byte.MIN_VALUE..Byte.MAX_VALUE

val Int.isImm16 get() = this in Short.MIN_VALUE..Short.MAX_VALUE

val Long.isImm8 get() = this in Byte.MIN_VALUE..Byte.MAX_VALUE

val Long.isImm16 get() = this in Short.MIN_VALUE..Short.MAX_VALUE

val Long.isImm32 get() = this in Int.MIN_VALUE..Int.MAX_VALUE
