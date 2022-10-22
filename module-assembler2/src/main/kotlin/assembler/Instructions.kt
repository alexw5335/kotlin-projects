package assembler



data class Instruction(
	val opcode    : Int,
	val extension : Int,
	val prefix    : Int,
	val widths    : Widths
)



data class InstructionGroup(
	val instructions  : List<Instruction>,
	val operandsBits  : Int,
	val specifierBits : Int
) {

	operator fun contains(specifier: Specifier) = specifierBits and specifier.bit != 0

}



enum class Operands(val specifier: Specifier = Specifier.NONE) {

	NONE,

	R,
	M,
	O(Specifier.O),
	I8(Specifier.I8),
	I16(Specifier.I16),
	I32(Specifier.I32),
	FS,
	GS,
	REL8(Specifier.REL8),
	REL32(Specifier.REL32),

	R_R,
	R_M,
	M_R,
	R_I,
	R_I8(Specifier.RM_I8),
	M_I,
	M_I8(Specifier.RM_I8),
	A_I(Specifier.A_I),
	R_1(Specifier.RM_1),
	M_1(Specifier.RM_1),
	R_CL(Specifier.RM_CL),
	M_CL(Specifier.RM_CL),

	R_RM8,
	R_RM16,
	R64_RM32,

	R_RM_I8(Specifier.RM_I8),
	R_RM_I;

	val bit = 1 shl ordinal

}



enum class Specifier {

	NONE,
	RM_I8,
	RM_CL,
	RM_1,
	A_I,
	I8,
	I16,
	I32,
	O,
	REL8,
	REL32;

	val bit = 1 shl ordinal

}