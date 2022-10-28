package assembler

enum class Width(val string: String, val bytes: Int) {

	BIT8("byte", 1),
	BIT16("word", 2),
	BIT32("dword", 4),
	BIT64("qword", 8),
	BIT80("tword", 10),
	BIT128("xword", 16);

	val is8 get() = this == BIT8
	val is16 get() = this == BIT16
	val is32 get() = this == BIT32
	val is64 get() = this == BIT64

	val isNot8 get() = this != BIT8
	val isNot16 get() = this != BIT16
	val isNot32 get() = this != BIT32
	val isNot64 get() = this != BIT64

	val bit = 1 shl ordinal

	val min = -(1 shl bytes)
	val max = (1 shl bytes) - 1

	operator fun contains(value: Int) = value in min..max
	operator fun contains(value: Long) = value in min..max

}