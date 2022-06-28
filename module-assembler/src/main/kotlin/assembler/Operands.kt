package assembler

enum class Operands(
	operand1: OperandFlags = OperandFlags.NONE,
	operand2: OperandFlags = OperandFlags.NONE,
	operand3: OperandFlags = OperandFlags.NONE,
	operand4: OperandFlags = OperandFlags.NONE
) {

	NONE,
	AL_IMM8  (OperandFlags.AL,  OperandFlags.IMM8),
	A_IMM    (OperandFlags.A,   OperandFlags { IMM + IMM8 }),
	RM8_IMM8 (OperandFlags.RM8, OperandFlags.IMM8),
	RM_IMM   (OperandFlags.RM,  OperandFlags { IMM + IMM8 }),
	RM_IMM8  (OperandFlags.RM,  OperandFlags.IMM8),
	RM8_R8   (OperandFlags.RM8, OperandFlags.R8),
	RM_R     (OperandFlags.RM,  OperandFlags.R),
	R8_RM8   (OperandFlags.R8,  OperandFlags.RM8),
	R_RM     (OperandFlags.R,   OperandFlags.RM);

	val flags = (operand1 shl 0) or (operand2 shl 8) or (operand3 shl 16) or (operand4 shl 24)

}