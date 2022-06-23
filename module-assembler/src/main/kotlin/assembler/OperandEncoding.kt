package assembler

enum class OperandEncoding(val matcher: (OperandNode) -> Boolean = { false }) {

	AL( { it is RegisterNode && it.register == Register.AL }),
	AX( { it is RegisterNode && it.register == Register.AX }),
	EAX( { it is RegisterNode && it.register == Register.EAX }),
	RAX( { it is RegisterNode && it.register == Register.RAX }),
	IMM8,
	IMM16,
	IMM32,
	IMM64,
	R8,
	R16,
	R32,
	R64,
	RM8,
	RM16,
	RM32,
	RM64;

}



/*
2.2.1.7 Default 64-Bit Operand Size
In 64-bit mode, two groups of instructions have a default operand size of 64 bits (do not need a REX prefix for this operand size). These are:
• Near branches.
• All instructions, except far branches, that implicitly reference the RSP.
R8
R16
R32
R64
RM8
RM16
RM32
RM64
IMM8
IMM16
IMM32
IMM64

 */