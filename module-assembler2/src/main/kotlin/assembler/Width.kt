package assembler

import kotlin.math.min

enum class Width(
	val string       : String,
	val bytes        : Int,
	val min          : Long = Long.MIN_VALUE,
	val max          : Long = Long.MAX_VALUE,
	val opcodeOffset : Int = 0,
	val rex          : Int = 0
) {

	BIT8  ("byte",    1,  Short.MIN_VALUE.toLong(), Short.MAX_VALUE.toLong(), opcodeOffset = 0),
	BIT16 ("word",    2,  Short.MIN_VALUE.toLong(), Short.MAX_VALUE.toLong(), opcodeOffset = 1),
	BIT32 ("dword",   4,  Int.MIN_VALUE.toLong(),   Int.MAX_VALUE.toLong(),   opcodeOffset = 1),
	BIT64 ("qword",   8,  Long.MIN_VALUE,           Long.MAX_VALUE,           opcodeOffset = 1, rex = 1),
	BIT80 ("tword",   10),
	BIT128("oword",   16),
	BIT256("256-bit", 32),
	BIT512("512-bit", 64);

	val is8 get() = this == BIT8
	val is16 get() = this == BIT16
	val is32 get() = this == BIT32
	val is64 get() = this == BIT64
	val is80 get() = this == BIT80

	val isNot8 get() = this != BIT8
	val isNot16 get() = this != BIT16
	val isNot32 get() = this != BIT32
	val isNot64 get() = this != BIT64
	val isNot80 get() = this != BIT80

	val immLength = min(bytes, 4)

	val bit = 1 shl ordinal

	operator fun contains(value: Int) = value in min..max
	operator fun contains(value: Long) = value in min..max


}