package asm

enum class Width(val string: String) {

	BIT8("byte"),
	BIT16("word"),
	BIT32("dword"),
	BIT64("qword"),
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

}