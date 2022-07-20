package assembler

enum class Width(val string: String) {

	BIT8("byte"),

	BIT16("word"),

	BIT32("dword"),

	BIT64("qword"),

	BIT128("128-bit"),

	BIT256("256-bit"),

	BIT512("512-bit");

}