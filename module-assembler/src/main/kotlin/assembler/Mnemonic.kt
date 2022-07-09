package assembler

enum class Mnemonic {

	NONE,
	ADD,
	OR,
	ADC,
	SBB,
	AND,
	SUB,
	XOR,
	CMP,
	ADCX,
	ADOX;

	val string = name.lowercase()

}