package assembler

class MemOperand(
	val base         : Register?,
	val index        : Register?,
	val scale        : Int,
	val displacement : AstNode
)