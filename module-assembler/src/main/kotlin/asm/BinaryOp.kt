package asm

enum class BinaryOp(
	val symbol          : String,
	val precedence      : Int,
	val canCalculateInt : Boolean,
	val calculateInt    : (Long, Long) -> Long,
	val calculateInt32  : (Int, Int) -> Int
) {

	DOT(".", 5, false, { _, _ -> 0 }, { _, _ -> 0 }),

	MUL("*", 4, true, Long::times, Int::times),
	DIV("/", 4, true, Long::div, Int::div),

	ADD("+", 3, true, Long::plus, Int::plus),
	SUB("-", 3, true, Long::minus, Int::minus),

	SHL("<<", 2, true, { a, b -> a shl b.toInt() }, Int::shl),
	SHR(">>", 2, true, { a, b -> a shr b.toInt() }, Int::shr),

	AND("&", 1, true, Long::and, Int::and),
	XOR("^", 1, true, Long::xor, Int::xor),
	OR("|", 1,  true, Long::or, Int::or),

}