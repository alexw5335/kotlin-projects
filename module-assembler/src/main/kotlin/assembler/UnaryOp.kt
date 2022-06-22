package assembler

enum class UnaryOp {

	POS,
	NEG,
	NOT;

	fun calculate(value: Long) = when(this) {
		POS -> value
		NEG -> -value
		NOT -> value.inv()
	}

}