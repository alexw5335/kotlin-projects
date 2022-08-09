package assembler

enum class Specialisation {

	NONE,
	A_IMM,
	RM_IMM8,
	RM_CL,
	RM_ONE;

	val bit = 1 shl (ordinal - 1)

	fun inFlags(flags: Int) = flags and bit != 0

}