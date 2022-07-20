package assembler

enum class OperandEncoding {

	/**
	 * REG, RM
	 */
	RM,

	/**
	 * RM, REG
	 */
	MR,

	/**
	 * IMM
	 */
	I,

	/**
	 * RM, IMM
	 */
	MI;

}