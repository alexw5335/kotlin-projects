package assembler

enum class BinaryOp(val precedence: Int) {

	MUL(5),
	DIV(5),

	ADD(4),
	SUB(4),

	SHL(3),
	SHR(3),

	AND(1),
	XOR(1),
	OR(1);

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