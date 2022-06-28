package assembler.generator

enum class Operand {

	A,
	AL,
	R,
	R8,
	R16,
	R32,
	R64,
	RM,
	RM8,
	RM16,
	RM32,
	RM64,
	IMM,
	IMM8,
	IMM16,
	IMM32,
	IMM64,
	REL8,
	REL16,
	REL32,
	M64,
	M128,
	M1616,
	M1632,
	M1664,
	M16AND64,
	NONE;

	val isNone get() = this == NONE

}