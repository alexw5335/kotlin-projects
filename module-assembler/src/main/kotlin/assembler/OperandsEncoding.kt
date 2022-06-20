package assembler


enum class OperandType {

	A,
	AL,

	R,
	R8,

	RM,
	RM8;

	val value = 1 shl ordinal

}



enum class OperandsEncoding {

	AL_IMM8,
	A_IMM,
	RM8_IMM8,
	RM_IMM,
	RM_IMM8,
	RM8_R8,
	RM_R,
	R8_RM8,
	R_RM;

}