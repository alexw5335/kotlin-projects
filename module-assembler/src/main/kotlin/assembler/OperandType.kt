package assembler

/**
 * These are not mutually exclusive.
 */
enum class OperandType {

	NONE,
	A,
	AL,
	C,
	CL,
	D,
	DL,
	R,
	R8,
	IMM,
	IMM8,
	MEM;

	val value = ordinal

}