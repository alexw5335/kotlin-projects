package eyre

enum class BinaryOp(
	val symbol          : String,
	val precedence      : Int,
	val leftPositivity  : Int,
	val rightPositivity : Int,
	val calculate       : (Long, Long) -> Long
) {

	DOT(".",  5, 0, 0,  { _, _ -> 0L }),
	MUL("*",  4, 0, 0,  Long::times),
	DIV("/",  4, 0, 0,  Long::div),
	ADD("+",  3, 1, 1,  Long::plus),
	SUB("-",  3, 1, -1, Long::minus),
	SHL("<<", 2, 0, 0,  { a, b -> a shl b.toInt() }),
	SHR(">>", 2, 0, 0,  { a, b -> a shr b.toInt() }),
	AND("&",  1, 0, 0,  Long::and),
	XOR("^",  1, 0, 0,  Long::xor),
	OR( "|",  1, 0, 0,  Long::or);

}