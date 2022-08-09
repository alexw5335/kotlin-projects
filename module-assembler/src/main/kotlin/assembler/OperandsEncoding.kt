package assembler

enum class OperandsEncoding {
	NONE,
	I,
	MI,
	RM,
	MR,
	/** op1: ModRM:r/m */
	M;
}