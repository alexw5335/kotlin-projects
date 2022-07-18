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
	MOV;

	val string = name.lowercase()

}