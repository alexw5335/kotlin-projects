package assembler

class Instruction(
	val prefix    : Int,
	val opType    : OpType,
	val opcode    : Int,
	val extension : Int,
	val operands  : Operands,
	val oso       : Boolean,
	val rexw      : Boolean
)