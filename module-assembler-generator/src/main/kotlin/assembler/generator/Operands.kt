package assembler.generator

enum class Operands {

	AL_IMM8,
	A_IMM,
	RM8_IMM8,
	RM_IMM,
	RM_IMM8,
	RM8_R8,
	RM_R,
	R8_RM8,
	R_RM,
	R32_OR_64,
	REL32,
	M16_,
	RM,
	M8,
	M,
	// rex.w for 128
	M64_OR_128,
	R32_RM8,
	R32_RM,
}