package assembler

enum class Operands(
	op1: OperandFlags = OperandFlags.NONE,
	op2: OperandFlags = OperandFlags.NONE,
	op3: OperandFlags = OperandFlags.NONE,
	op4: OperandFlags = OperandFlags.NONE,
) {

	//OPREG3264(OperandFlags { R32 + R64 }),
	AL_IMM8(OperandFlags.AL, OperandFlags.IMM8),
	A_IMM(OperandFlags.A, OperandFlags.IMM),
	RM8_IMM8(OperandFlags.RM8, OperandFlags.IMM8),
	RM_IMM(OperandFlags.RM, OperandFlags.IMM),
	RM_IMM8(OperandFlags.RM, OperandFlags.IMM8),
	RM8_R8(OperandFlags.RM8, OperandFlags.REG8),
	RM_R(OperandFlags.RM, OperandFlags.R),
	R8_RM8(OperandFlags.REG8, OperandFlags.RM8),
	R_RM(OperandFlags.R, OperandFlags.RM);

	val flags = (op1 shl 0) + (op2 shl 16) + (op3 shl 32) + (op4 shl 48)

}