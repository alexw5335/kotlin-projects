package assembler

enum class Operands(
	op1: OperandFlags = OperandFlags.NONE,
	op2: OperandFlags = OperandFlags.NONE,
	op3: OperandFlags = OperandFlags.NONE,
	op4: OperandFlags = OperandFlags.NONE,
) {


	//OPREG3264(OperandFlags { R32 + R64 }),
	AL_IMM8    (OperandFlags.AL,  OperandFlags.IMM8),
	AX_IMM16   (OperandFlags.AX,  OperandFlags.IMM),
	EAX_IMM32  (OperandFlags.EAX, OperandFlags.IMM),
	RAX_IMM32  (OperandFlags.RAX, OperandFlags.IMM),
	R8_IMM8    (OperandFlags.R8,  OperandFlags.IMM8),
	MEM_IMM8   (OperandFlags.MEM, OperandFlags.IMM8),
	R16_IMM16  (OperandFlags.R16, OperandFlags.IMM),
	R32_IMM32  (OperandFlags.R32, OperandFlags.IMM),
	R64_IMM32  (OperandFlags.R64, OperandFlags.IMM),
	MEM_IMM    (OperandFlags.MEM, OperandFlags.IMM),
	R16_IMM8   (OperandFlags.R16, OperandFlags.IMM8),
	R32_IMM8   (OperandFlags.R32, OperandFlags.IMM8),
	R64_IMM8   (OperandFlags.R64, OperandFlags.IMM8),
	R8_R8      (OperandFlags.R8,  OperandFlags.R8),
	MEM_R8     (OperandFlags.MEM, OperandFlags.R8),
	R16_R16    (OperandFlags.R16, OperandFlags.R16),
	R32_R32    (OperandFlags.R32, OperandFlags.R32),
	R64_R64    (OperandFlags.R64, OperandFlags.R64),
	MEM_R16    (OperandFlags.MEM, OperandFlags.R16),
	MEM_R32    (OperandFlags.MEM, OperandFlags.R32),
	MEM_R64    (OperandFlags.MEM, OperandFlags.R64),
	R8_MEM     (OperandFlags.R8,  OperandFlags.MEM),
	R16_MEM    (OperandFlags.R16, OperandFlags.MEM),
	R32_MEM    (OperandFlags.R32, OperandFlags.MEM),
	R64_MEM    (OperandFlags.R64, OperandFlags.MEM);



	val type = op1.type or (op2.type shl 8) or (op3.type shl 16) or (op4.type shl 24)

	val flags = op1.flags or (op2.flags shl 4) or (op3.flags shl 8) or (op4.flags shl 12)


}