package assembler.generator

enum class Operand {

	A,
	AL,
	R,
	R8,
	RM,
	RM8,
	IMM,
	IMM8,
	REL32,

	/**
	 * One of: m16:16, m16:32, m16:64
	 */
	M16_;

}