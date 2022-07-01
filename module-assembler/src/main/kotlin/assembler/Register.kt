package assembler

enum class Register(
	val value: Int,
	val width: Int,
	val flags: OperandFlags
) {

	// 64-bit general-purpose
	RAX(0, 4, OperandFlags.A),
	RCX(1, 4, OperandFlags.REG),
	RDX(2, 4, OperandFlags.REG),
	RBX(3, 4, OperandFlags.REG),
	RSP(4, 4, OperandFlags.REG),
	RBP(5, 4, OperandFlags.REG),
	RSI(6, 4, OperandFlags.REG),
	RDI(7, 4, OperandFlags.REG),
	R8( 8, 4, OperandFlags.REG),
	R9( 9, 4, OperandFlags.REG),
	R10(10, 4, OperandFlags.REG),
	R11(11, 4, OperandFlags.REG),
	R12(12, 4, OperandFlags.REG),
	R13(13, 4, OperandFlags.REG),
	R14(14, 4, OperandFlags.REG),
	R15(15, 4, OperandFlags.REG),

	// 32-bit general-purpose
	EAX(0, 3, OperandFlags.A),
	ECX(1, 3, OperandFlags.REG),
	EDX(2, 3, OperandFlags.REG),
	EBX(3, 3, OperandFlags.REG),
	ESP(4, 3, OperandFlags.REG),
	EBP(5, 3, OperandFlags.REG),
	ESI(6, 3, OperandFlags.REG),
	EDI(7, 3, OperandFlags.REG),
	R8D(8, 3, OperandFlags.REG),
	R9D(9, 3, OperandFlags.REG),
	R10D(10, 3, OperandFlags.REG),
	R11D(11, 3, OperandFlags.REG),
	R12D(12, 3, OperandFlags.REG),
	R13D(13, 3, OperandFlags.REG),
	R14D(14, 3, OperandFlags.REG),
	R15D(15, 3, OperandFlags.REG),

	// 16-bit general-purpose
	AX(0, 2, OperandFlags.A),
	CX(1, 2, OperandFlags.REG),
	DX(2, 2, OperandFlags.DX),
	BX(3, 2, OperandFlags.REG),
	SP(4, 2, OperandFlags.REG),
	BP(5, 2, OperandFlags.REG),
	SI(6, 2, OperandFlags.REG),
	DI(7, 2, OperandFlags.REG),
	R8W(8, 2, OperandFlags.REG),
	R9W(9, 2, OperandFlags.REG),
	R10W(10, 2, OperandFlags.REG),
	R11W(11, 2, OperandFlags.REG),
	R12W(12, 2, OperandFlags.REG),
	R13W(13, 2, OperandFlags.REG),
	R14W(14, 2, OperandFlags.REG),
	R15W(15, 2, OperandFlags.REG),

	// 8-bit general-purpose
	AL(0, 1, OperandFlags.AL),
	CL(1, 1, OperandFlags.CL),
	DL(2, 1, OperandFlags.REG8),
	BL(3, 1, OperandFlags.REG8),
	AH(4, 1, OperandFlags.REG8),
	CH(5, 1, OperandFlags.REG8),
	DH(6, 1, OperandFlags.REG8),
	BH(7, 1, OperandFlags.REG8),
	R8B(8, 1, OperandFlags.REG8),
	R9B(9, 1, OperandFlags.REG8),
	R10B(10, 1, OperandFlags.REG8),
	R11B(11, 1, OperandFlags.REG8),
	R12B(12, 1, OperandFlags.REG8),
	R13B(13, 1, OperandFlags.REG8),
	R14B(14, 1, OperandFlags.REG8),
	R15B(15, 1, OperandFlags.REG8),

	// high 8-bit general purpose
	SPL(4, 1, OperandFlags.REG8),
	BPL(4, 1, OperandFlags.REG8),
	SIL(4, 1, OperandFlags.REG8),
	DIL(4, 1, OperandFlags.REG8);

}