package eyre

enum class UnaryOp(
	val symbol     : String,
	val positivity : Int,
	val calculate  : (Long) -> Long,
) {

	POS("+", 1,  { it }),
	NEG("-", -1, { -it }),
	NOT("~", 0,  { it.inv() });

}