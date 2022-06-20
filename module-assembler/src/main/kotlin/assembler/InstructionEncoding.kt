package assembler

class InstructionEncoding(
	val mnemonic: Mnemonic,
	val opcode: Int,
	val optype: Optype,
	val operands: OperandsEncoding,
	val extension: Int
)