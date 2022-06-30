package assembler

enum class Register(
	val value: Int,
	val width: Int,
	val type: OperandType
) {

	// 64-bit general-purpose
	RAX(0, 4, OperandType.A),
	RCX(1, 4, OperandType.C),
	RDX(2, 4, OperandType.D),
	RBX(3, 4, OperandType.R),
	RSP(4, 4, OperandType.R),
	RBP(5, 4, OperandType.R),
	RSI(6, 4, OperandType.R),
	RDI(7, 4, OperandType.R),
	R8( 8, 4, OperandType.R),
	R9( 9, 4, OperandType.R),
	R10(10, 4, OperandType.R),
	R11(11, 4, OperandType.R),
	R12(12, 4, OperandType.R),
	R13(13, 4, OperandType.R),
	R14(14, 4, OperandType.R),
	R15(15, 4, OperandType.R),

	// 32-bit general-purpose
	EAX(0, 3, OperandType.A),
	ECX(1, 3, OperandType.C),
	EDX(2, 3, OperandType.D),
	EBX(3, 3, OperandType.R),
	ESP(4, 3, OperandType.R),
	EBP(5, 3, OperandType.R),
	ESI(6, 3, OperandType.R),
	EDI(7, 3, OperandType.R),
	R8D(8, 3, OperandType.R),
	R9D(9, 3, OperandType.R),
	R10D(10, 3, OperandType.R),
	R11D(11, 3, OperandType.R),
	R12D(12, 3, OperandType.R),
	R13D(13, 3, OperandType.R),
	R14D(14, 3, OperandType.R),
	R15D(15, 3, OperandType.R),

	// 16-bit general-purpose
	AX(0, 2, OperandType.A),
	CX(1, 2, OperandType.C),
	DX(2, 2, OperandType.D),
	BX(3, 2, OperandType.R),
	SP(4, 2, OperandType.R),
	BP(5, 2, OperandType.R),
	SI(6, 2, OperandType.R),
	DI(7, 2, OperandType.R),
	R8W(8, 2, OperandType.R),
	R9W(9, 2, OperandType.R),
	R10W(10, 2, OperandType.R),
	R11W(11, 2, OperandType.R),
	R12W(12, 2, OperandType.R),
	R13W(13, 2, OperandType.R),
	R14W(14, 2, OperandType.R),
	R15W(15, 2, OperandType.R),

	// 8-bit general-purpose
	AL(0, 1, OperandType.AL),
	CL(1, 1, OperandType.CL),
	DL(2, 1, OperandType.DL),
	BL(3, 1, OperandType.R8),
	AH(4, 1, OperandType.R8),
	CH(5, 1, OperandType.R8),
	DH(6, 1, OperandType.R8),
	BH(7, 1, OperandType.R8),
	R8B(8, 1, OperandType.R8),
	R9B(9, 1, OperandType.R8),
	R10B(10, 1, OperandType.R8),
	R11B(11, 1, OperandType.R8),
	R12B(12, 1, OperandType.R8),
	R13B(13, 1, OperandType.R8),
	R14B(14, 1, OperandType.R8),
	R15B(15, 1, OperandType.R8),

	// high 8-bit general purpose
	SPL(4, 1, OperandType.R8),
	BPL(4, 1, OperandType.R8),
	SIL(4, 1, OperandType.R8),
	DIL(4, 1, OperandType.R8);

}