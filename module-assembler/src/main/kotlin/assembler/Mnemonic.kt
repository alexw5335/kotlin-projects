package assembler

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