package asm



enum class Specifier {

	NONE,
	A_IMM,
	RM_IMM8,
	RM_CL,
	RM_ONE;

	val bit = 1 shl (ordinal - 1)

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

}



enum class Mnemonic(val stringWidth: Width? = null) {

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
	SAL,
	SHR,
	SAR,

	CBW,
	CWDE,
	CDQE,

	CWD,
	CDQ,
	CQO,

	CLC,
	CLD,
	CLI,
	CLTS,
	CMC,

	STC,
	STI,
	STD,

	BSWAP,
	
	CMPSB(stringWidth = Width.BIT8),
	CMPSW(stringWidth = Width.BIT16),
	CMPSD(stringWidth = Width.BIT32),
	CMPSQ(stringWidth = Width.BIT64),

	SCASB(stringWidth = Width.BIT8),
	SCASW(stringWidth = Width.BIT16),
	SCASD(stringWidth = Width.BIT32),
	SCASQ(stringWidth = Width.BIT64),

	STOSB(stringWidth = Width.BIT8),
	STOSW(stringWidth = Width.BIT16),
	STOSD(stringWidth = Width.BIT32),
	STOSQ(stringWidth = Width.BIT64),

	LODSB(stringWidth = Width.BIT8),
	LODSW(stringWidth = Width.BIT16),
	LODSD(stringWidth = Width.BIT32),
	LODSQ(stringWidth = Width.BIT64),

	MOVSB(stringWidth = Width.BIT8),
	MOVSW(stringWidth = Width.BIT16),
	MOVSD(stringWidth = Width.BIT32),
	MOVSQ(stringWidth = Width.BIT64),

	INSB(stringWidth = Width.BIT8),
	INSW(stringWidth = Width.BIT16),
	INSD(stringWidth = Width.BIT32),

	OUTSB(stringWidth = Width.BIT8),
	OUTSW(stringWidth = Width.BIT16),
	OUTSD(stringWidth = Width.BIT32),

	CPUID,

	RET,
	RETF,

	SHLD,
	SHRD,

	CMOVA,

	PUSH,
	POP,

	MOVSX,
	MOVSXD,

	INT1,
	INT3,
	INT,

	HLT,

	INC,
	DEC,

	PUSHF,
	PUSHFQ,
	LAHF,

	IN,
	OUT,

	WAIT,
	FWAIT,

	NOP;

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
) {

	operator fun get(operands: Operands) = instructions[(operandsFlags and (operands.bit - 1)).countOneBits()]
	operator fun contains(operands: Operands) = operandsFlags and operands.bit != 0
	operator fun contains(specifier: Specifier) = specifierFlags and specifier.bit != 0

}



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