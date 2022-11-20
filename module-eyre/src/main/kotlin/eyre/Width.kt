package eyre

import java.lang.Integer.min

enum class Width(
	val string       : String,
	val varString    : String,
	val bytes        : Int,
	val opcodeOffset : Int = 0,
	val rexW         : Int = 0,
) {

	BIT8("byte", "db", 1),
	BIT16("word", "dw", 2, opcodeOffset = 1),
	BIT32("dword", "dd", 4, opcodeOffset = 1),
	BIT64("qword", "dq", 8, opcodeOffset = 1, rexW = 1),
	BIT80("tword", "dt", 10),
	BIT128("xword", "dx", 16);

	val is8 get() = this == BIT8
	val is16 get() = this == BIT16
	val is32 get() = this == BIT32
	val is64 get() = this == BIT64

	val isNot8 get() = this != BIT8
	val isNot16 get() = this != BIT16
	val isNot32 get() = this != BIT32
	val isNot64 get() = this != BIT64

	val bit = 1 shl ordinal

	val immLength = min(bytes, 4)

	val min = -(1 shl ((bytes shl 3) - 1))
	val max = (1 shl ((bytes shl 3) - 1)) - 1

	operator fun contains(value: Int) = value in min..max
	operator fun contains(value: Long) = value in min..max

	companion object {
		val values = values()
	}

}