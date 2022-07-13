package assembler

enum class Mnemonic {

	ADD,
	OR,
	ADC,
	SBB,
	AND,
	SUB,
	XOR,
	CMP;

	val string = name.lowercase()

}