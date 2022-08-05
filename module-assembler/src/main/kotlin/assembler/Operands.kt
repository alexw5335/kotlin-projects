package assembler

enum class Operands(val specialisation: Specialisation = Specialisation.NONE) {


	NONE,

	R8,
	R16,
	R32,
	R64,
	M,
	IMM,

	R8_R8,
	R16_R16,
	R32_R32,
	R64_R64,

	R8_M8,
	R16_M16,
	R32_M32,
	R64_M64,

	M8_R8,
	M16_R16,
	M32_R32,
	M64_R64,

	R8_IMM8,
	R16_IMM16,
	R32_IMM32,
	R64_IMM32,

	M8_IMM8,
	M16_IMM16,
	M32_IMM32,
	M64_IMM32,

	R16_IMM8(Specialisation.R_IMM8),
	R32_IMM8(Specialisation.R_IMM8),
	R64_IMM8(Specialisation.R_IMM8),

	M16_IMM8(Specialisation.M_IMM8),
	M32_IMM8(Specialisation.M_IMM8),
	M64_IMM8(Specialisation.M_IMM8),

	AL_IMM8(Specialisation.A_IMM),
	AX_IMM16(Specialisation.A_IMM),
	EAX_IMM32(Specialisation.A_IMM),
	RAX_IMM32(Specialisation.A_IMM);



	val bit = 1L shl ordinal

	fun inFlags(flags: Long) = flags and bit != 0L


}