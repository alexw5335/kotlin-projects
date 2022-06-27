package assembler.generator

enum class Operands {

	R8_MEM,
	R16_MEM,
	R32_MEM,
	R64_MEM,

	MEM_R64,
	MEM_R32,
	MEM_R16,
	MEM_R8,

	R8_R8,
	R16_R16,
	R32_R32,
	R64_R64,

	MEM,

	R8,
	R16,
	R32,
	R64,

	MEM_IMM8,
	MEM_IMM16,
	MEM_IMM32,

	R8_IMM8,
	R16_IMM16,
	R32_IMM32,
	R64_IMM32,
	R64_IMM64;


}