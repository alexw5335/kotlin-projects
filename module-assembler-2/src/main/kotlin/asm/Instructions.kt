package asm



enum class Specifier {

	NONE,
	A_IMM,
	RM_IMM8,
	RM_CL,
	RM_ONE;

	val bit = 1 shl (ordinal - 1)
	fun inFlags(flags: Int) = flags and bit != 0

}



enum class Operands(val specifier: Specifier = Specifier.NONE) {

	NONE,
	RM8_R8,
	R8_RM8,
	RM_R,
	R_RM,
	RM_IMM,
	RM_IMM8(Specifier.RM_IMM8),
	RM8_IMM8,
	RM8_ONE(Specifier.RM_ONE),
	RM8_CL(Specifier.RM_CL),
	RM_ONE(Specifier.RM_ONE),
	RM_CL(Specifier.RM_CL),
	AL_IMM8(Specifier.A_IMM),
	A_IMM(Specifier.A_IMM);

	val bit = 1 shl ordinal
	fun inFlags(flags: Int) = flags and bit != 0

}



enum class Mnemonic {

	ADD,
	OR,
	ADC,
	SBB,
	AND,
	SUB,
	XOR,
	CMP,

	ROL,
	ROR,
	RCL,
	RCR,
	SHL,
	SHR,
	SAR;

	val string = name.lowercase()

}



data class Instruction(
	val mnemonic  : Mnemonic,
	val opcode    : Int,
	val operands  : Operands,
	val extension : Int = -1
) {

	val opcodeLength = (((32 - opcode.countLeadingZeroBits() + 7) and -8) shr 3).coerceAtLeast(1)

}



class InstructionGroup(
	val instructions: List<Instruction>,
	val operandsFlags: Int,
	val specifierFlags: Int
)



private fun group(vararg instructions: Instruction): InstructionGroup {
	val sorted = instructions.sortedBy { it.operands.ordinal }
	var operandsFlags = 0
	var specifierFlags = 0

	for(instruction in instructions) {
		operandsFlags = operandsFlags or instruction.operands.bit
		specifierFlags = specifierFlags or instruction.operands.specifier.bit
	}

	return InstructionGroup(sorted, operandsFlags, specifierFlags)
}



val mnemonicsToInstructions = mapOf(
	Mnemonic.ADD to group(
		Instruction(Mnemonic.ADD, 0x00, Operands.RM8_R8),
		Instruction(Mnemonic.ADD, 0x01, Operands.RM_R),
		Instruction(Mnemonic.ADD, 0x02, Operands.R8_RM8),
		Instruction(Mnemonic.ADD, 0x03, Operands.R_RM),
		Instruction(Mnemonic.ADD, 0x04, Operands.AL_IMM8),
		Instruction(Mnemonic.ADD, 0x05, Operands.A_IMM),
		Instruction(Mnemonic.ADD, 0x80, Operands.RM8_IMM8, extension = 0),
		Instruction(Mnemonic.ADD, 0x81, Operands.RM_IMM, extension = 0),
		Instruction(Mnemonic.ADD, 0x83, Operands.RM_IMM8, extension = 0)
	)
)