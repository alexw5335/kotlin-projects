package assembler

class Instruction(
	val opType    : OpType,
	val opcode    : Int,
	val extension : Int,
	val operand1  : Operand,
	val operand2  : Operand,
	val operand3  : Operand,
	val operand4  : Operand,
	val encoding  : Operands,
	val oso       : Boolean,
	val rexw      : Boolean
)