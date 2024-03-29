package eyre



class Instruction(
	val opcode    : Int,
	val extension : Int,
	val prefix    : Int,
	val widths    : Widths
)



class InstructionGroup(
	val instructions  : List<Instruction>,
	val operandsBits  : Int,
	val specifierBits : Int
) {
	operator fun contains(operands: Operands) = operandsBits and operands.bit != 0
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
	RM_1(Specifier.RM_1),
	RM_CL(Specifier.RM_CL),

	CUSTOM1,
	CUSTOM2;

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