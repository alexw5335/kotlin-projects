package assembler

enum class UnaryOp(val symbol: String) {

	POS("+"),
	NEG("-"),
	NOT("~");

	fun calculate(value: Long) = when(this) {
		POS -> value
		NEG -> -value
		NOT -> value.inv()
	}

}