package assembler

import core.memory.NativeWriter

val Int.isImm8 get() = this in Byte.MIN_VALUE..Byte.MAX_VALUE

val Int.isImm16 get() = this in Short.MIN_VALUE..Short.MAX_VALUE

val Long.isImm8 get() = this in Byte.MIN_VALUE..Byte.MAX_VALUE

val Long.isImm16 get() = this in Short.MIN_VALUE..Short.MAX_VALUE

val Long.isImm32 get() = this in Int.MIN_VALUE..Int.MAX_VALUE

fun NativeWriter.int(width: Width, value: Int) {
	when(width) {
		Width.BIT8  -> i8(value)
		Width.BIT16 -> i16(value)
		Width.BIT32 -> i32(value)
		else        -> i64(value.toLong())
	}
}

fun NativeWriter.int(width: Width, value: Long) {
	when(width) {
		Width.BIT8  -> i8(value.toInt())
		Width.BIT16 -> i16(value.toInt())
		Width.BIT32 -> i32(value.toInt())
		else        -> i64(value)
	}
}