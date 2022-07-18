package assembler

enum class Operands(
	val op1: Operand? = null,
	val op2: Operand? = null,
	val op3: Operand? = null,
	val op4: Operand? = null
) {

	R8_R8   (Operand.R8, Operand.R8),
	R16_R16 (Operand.R16, Operand.R16),
	R32_R32 (Operand.R32, Operand.R32),
	R64_R64 (Operand.R64, Operand.R64),
	R8_M8   (Operand.R8, Operand.M8),
	R16_M16 (Operand.R16, Operand.M16),
	R32_M32 (Operand.R32, Operand.M32),
	R64_M64 (Operand.R64, Operand.M64),
	M8_R8   (Operand.M8, Operand.R8),
	M16_R16 (Operand.M16, Operand.R16),
	M32_R32 (Operand.M32, Operand.R32),
	M64_R64 (Operand.M64, Operand.R64);

}