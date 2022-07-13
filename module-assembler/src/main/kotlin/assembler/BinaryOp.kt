package assembler

enum class BinaryOp(val symbol: String, val precedence: Int) {

	MUL("*", 4),
	DIV("/", 4),

	ADD("+", 3),
	SUB("-", 3),

	SHL("<<", 2),
	SHR(">>", 2),

	AND("&", 1),
	XOR("^", 1),
	OR("|", 1);

	fun calculate(a: Long, b: Long) = when(this) {
		MUL -> a * b
		DIV -> a / b
		ADD -> a + b
		SUB -> a - b
		SHL -> a shl b.toInt()
		SHR -> a shr b.toInt()
		AND -> a and b
		XOR -> a xor b
		OR  -> a or b
	}

}