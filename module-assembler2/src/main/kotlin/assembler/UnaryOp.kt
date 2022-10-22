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

	fun calculate(value: Int) = when(this) {
		POS -> value
		NEG -> -value
		NOT -> value.inv()
	}

	val positivity get() = when(this) {
		POS  -> 1
		NEG  -> -1
		else -> 0
	}

}