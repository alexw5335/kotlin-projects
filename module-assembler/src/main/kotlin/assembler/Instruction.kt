package assembler

class Instruction(
	val opType    : OpType,
	val opcode    : Int,
	val extension : Int,
	val operands  : Operands,
	val custom    : Boolean,
	val oso       : Boolean,
	val rexw      : Boolean
)