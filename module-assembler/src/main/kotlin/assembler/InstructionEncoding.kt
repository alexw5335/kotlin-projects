package assembler

/**
 * Extension is a ModRM byte.
 */
data class InstructionEncoding(
	val opcode    : Int,
	val operands  : Operands,
	val extension : Int = 0
)