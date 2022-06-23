package assembler

data class InstructionEncoding(
	val mnemonic        : Mnemonic,
	val opcode          : Int,
	val optype          : OpType,
	val operand1        : OperandEncoding?,
	val operand2        : OperandEncoding?,
	val operand3        : OperandEncoding?,
	val operand4        : OperandEncoding?,
	val extension       : Int,
	val mandatoryPrefix : Int
)