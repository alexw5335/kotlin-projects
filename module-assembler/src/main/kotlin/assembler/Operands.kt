package assembler

enum class Operands(
	val assembler: Assembler.Context.() -> Unit = { },
	val specialisation: Specialisation = Specialisation.NONE
) {


	NONE,
	RM8_R8,
	R8_RM8,
	RM_R,
	R_RM,
	RM_IMM,
	RM_IMM8(specialisation = Specialisation.RM_IMM8),
	RM8_IMM8,
	RM8_ONE(specialisation = Specialisation.RM_ONE),
	RM8_CL(specialisation = Specialisation.RM_CL),
	RM_ONE(specialisation = Specialisation.RM_ONE),
	RM_CL(specialisation = Specialisation.RM_CL),
	AL_IMM8(specialisation = Specialisation.A_IMM),
	A_IMM(specialisation = Specialisation.A_IMM);



	val bit = 1L shl ordinal

	fun inFlags(flags: Long) = flags and bit != 0L


}