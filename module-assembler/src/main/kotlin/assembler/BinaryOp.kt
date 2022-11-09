package assembler

enum class BinaryOp(
	val symbol      : String,
	val precedence  : Int,
	val calculate   : (Long, Long) -> Long,
	val canCalcInt  : Boolean = true
) {

	DOT(".", 5,  Lambdas::zero),
	MUL("*",  4, Long::times),
	DIV("/",  4, Long::div),

	ADD("+",  3, Long::plus),
	SUB("-",  3, Long::minus),

	SHL("<<", 2, Lambdas::shl),
	SHR(">>", 2, Lambdas::shr),

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

	private object Lambdas {

		fun zero(a: Long, b: Long) = 0L

		fun shl(a: Long, b: Long) = a shl b.toInt()

		fun shr(a: Long, b: Long) = a shr b.toInt()
	}

}