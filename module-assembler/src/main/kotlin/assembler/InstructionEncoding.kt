package assembler

data class InstructionEncoding(
	val opcode     : Int,
	val opcodeType : OpcodeType,
	val extension  : Int,
	val operands   : Operands,
	val encoding   : OperandsEncoding
)