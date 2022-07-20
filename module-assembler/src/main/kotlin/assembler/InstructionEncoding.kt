package assembler

class InstructionEncoding(
	val mnemonic : Mnemonic,
	val opcode   : Int,
	val opEnc    : OperandEncoding,
	val op1      : Operand?,
	val op2      : Operand?,
	val op3      : Operand?,
	val op4      : Operand?,
) {

	val operandCount = when {
		op1 == null -> 0
		op2 == null -> 1
		op3 == null -> 2
		op4 == null -> 3
		else        -> 4
	}

}