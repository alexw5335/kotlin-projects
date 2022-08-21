package assembler

enum class BinaryOp(
	val symbol: String,
	val precedence: Int,
	val calculateInt: (Long, Long) -> Long
) {

	MUL("*", 4, { a, b -> a * b }),
	DIV("/", 4, { a, b -> a / b }),

	ADD("+", 3, { a, b -> a + b }),
	SUB("-", 3, { a, b -> a - b }),

	SHL("<<", 2, { a, b -> a shl b.toInt() }),
	SHR(">>", 2, { a, b -> a shr b.toInt() }),

	AND("&", 1, { a, b -> a and b }),
	XOR("^", 1, { a, b -> a xor b }),
	OR("|", 1,  { a, b -> a or b });

}