package asm

enum class Width(
	val string       : String,
	val bytes        : Int,
	val opcodeOffset : Int = 0,
	val rex          : Int = 0
) {

	BIT8("byte", 1, opcodeOffset = 0),
	BIT16("word", 2, opcodeOffset = 1),
	BIT32("dword", 4, opcodeOffset = 1),
	BIT64("qword", 8, opcodeOffset = 1, rex = 1),
	BIT128("128-bit", 16),
	BIT256("256-bit", 32),
	BIT512("512-bit", 64);

	val is8 get() = this == BIT8
	val is16 get() = this == BIT16
	val is32 get() = this == BIT32
	val is64 get() = this == BIT64

	val isNot8 get() = this != BIT8
	val isNot16 get() = this != BIT16
	val isNot32 get() = this != BIT32
	val isNot64 get() = this != BIT64

	val bit = 1 shl ordinal

}