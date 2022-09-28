package asm

enum class Width(
	val string       : String,
	val opcodeOffset : Int = 0,
	val rex          : Int = 0
) {

	BIT8("byte", opcodeOffset = 0),
	BIT16("word", opcodeOffset = 1),
	BIT32("dword", opcodeOffset = 1),
	BIT64("qword", opcodeOffset = 1, rex = 1),
	BIT128("128-bit"),
	BIT256("256-bit"),
	BIT512("512-bit");

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