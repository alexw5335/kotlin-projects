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
	OR(1),

}