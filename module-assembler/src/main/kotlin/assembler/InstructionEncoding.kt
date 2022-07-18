package assembler

class InstructionEncoding(
	val mnemonic : Mnemonic,
	val opcode   : Int,
	val operands : Operands
)