package assembler

/**
 * Extension is a ModRM byte.
 */
data class InstructionEncoding(
	val opcode    : Int,
	val opType    : OpType,
	val operands  : Operands,
	val modRM     : Int = -1,
	val extension : Int = 0
)