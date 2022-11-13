package assembler

enum class BinaryOp(
	val symbol      : String,
	val precedence  : Int,
	val calculate   : (Long, Long) -> Long
) {

	MUL("*",  4, Long::times),
	DIV("/",  4, Long::div),

	ADD("+",  3, Long::plus),
	SUB("-",  3, Long::minus),

	SHL("<<", 2, { a, b -> a shl b.toInt() }),
	SHR(">>", 2, { a, b -> a shr b.toInt() }),

	AND("&",  1, Long::and),
	XOR("^",  1, Long::xor),
	OR ("|",  1, Long::or);

	val leftPositivity get() = when(this) {
		ADD,
		SUB  -> 1
		else -> 0
	}

	val rightPositivity get() = when(this) {
		ADD  -> 1
		SUB  -> -1
		else -> 0
	}

}