package assembler

enum class BinaryOp(
	val symbol: String,
	val precedence: Int,
	val canCalculateInt: Boolean,
	val calculateInt: ((Long, Long) -> Long)
) {

	DOT(".", 5, false, { _, _ -> 0 }),

	MUL("*", 4, true, Long::times),
	DIV("/", 4, true, Long::div),

	ADD("+", 3, true, Long::plus),
	SUB("-", 3, true, Long::minus),

	SHL("<<", 2, true, { a, b -> a shl b.toInt() }),
	SHR(">>", 2, true, { a, b -> a shr b.toInt() }),

	AND("&", 1, true, Long::and),
	XOR("^", 1, true, Long::xor),
	OR("|", 1,  true, Long::or),

}