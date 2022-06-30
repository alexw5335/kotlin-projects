package assembler

import assembler.OperandType.*

enum class Operands(
	op1: OperandType = NONE,
	op2: OperandType = NONE,
	op3: OperandType = NONE,
	op4: OperandType = NONE,
) {

	AL_IMM8(AL, IMM8),
	A_IMM(A, IMM),
	R8_IMM8(R8, IMM8),
	MEM_IMM8(MEM, IMM8),
	R_IMM(R, IMM),
	MEM_IMM(MEM, IMM),
	R_IMM8(R, IMM8),
	R_R(R, R),
	MEM_R(MEM, R),
	R8_R8(R8, R8),
	R8_MEM(R8, MEM),
	R_MEM(R, MEM);

	val value =
		(op1.value shl 0) or
		(op2.value shl 8) or
		(op3.value shl 16) or
		(op4.value shl 24)

}