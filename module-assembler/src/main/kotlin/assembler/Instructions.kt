package assembler



class InstructionEncoding(
	val opcode   : Int,
	val opType   : OpType,
	val modrm    : Int,
	val operands : Operands
)



enum class OpType {

	SINGLE,
	DOUBLE,
	TRIP38,
	TRIP3A;

}



enum class Operands(
	val op1: OperandType? = null,
	val op2: OperandType? = null,
	val op3: OperandType? = null,
	val op4: OperandType? = null
) {

	R8      (OperandType.R8),
	R16     (OperandType.R16),
	R32     (OperandType.R32),
	R64     (OperandType.R64),
	M8      (OperandType.M8),
	M16     (OperandType.M16),
	M32     (OperandType.M32),
	M64     (OperandType.M64),
	R8_R8   (OperandType.R8,   OperandType.R8),
	R16_R16 (OperandType.R16,  OperandType.R16),
	R32_R32 (OperandType.R32,  OperandType.R32),
	R64_R64 (OperandType.R64,  OperandType.R64),
	R8_M8   (OperandType.R8,   OperandType.M8),
	R16_M16 (OperandType.R16,  OperandType.M16),
	R32_M32 (OperandType.R32,  OperandType.M32),
	R64_M64 (OperandType.R64,  OperandType.M64),
	M8_R8   (OperandType.M8,   OperandType.R8),
	M16_R16 (OperandType.M16,  OperandType.R16),
	M32_R32 (OperandType.M32,  OperandType.R32),
	M64_R64 (OperandType.M64,  OperandType.R64);

}



enum class OperandType {

	R8,
	R16,
	R32,
	R64,
	M8,
	M16,
	M32,
	M64,
	IMM8,
	IMM16,
	IMM32,
	IMM64;

}