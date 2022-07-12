package assembler

enum class Register(val value: Int, val width: Int) {

	// 64-bit general-purpose
	RAX(0, 4),
	RCX(1, 4),
	RDX(2, 4),
	RBX(3, 4),
	RSP(4, 4),
	RBP(5, 4),
	RSI(6, 4),
	RDI(7, 4),
	R8(8, 4),
	R9(9, 4),
	R10(10, 4),
	R11(11, 4),
	R12(12, 4),
	R13(13, 4),
	R14(14, 4),
	R15(15, 4),

	// 32-bit general-purpose
	EAX(0, 3),
	ECX(1, 3),
	EDX(2, 3),
	EBX(3, 3),
	ESP(4, 3),
	EBP(5, 3),
	ESI(6, 3),
	EDI(7, 3),
	R8D(8, 3),
	R9D(9, 3),
	R10D(10, 3),
	R11D(11, 3),
	R12D(12, 3),
	R13D(13, 3),
	R14D(14, 3),
	R15D(15, 3),

	// 16-bit general-purpose
	AX(0, 2),
	CX(1, 2),
	DX(2, 2),
	BX(3, 2),
	SP(4, 2),
	BP(5, 2),
	SI(6, 2),
	DI(7, 2),
	R8W(8, 2),
	R9W(9, 2),
	R10W(10, 2),
	R11W(11, 2),
	R12W(12, 2),
	R13W(13, 2),
	R14W(14, 2),
	R15W(15, 2),

	// 8-bit general-purpose
	AL(0, 1),
	CL(1, 1),
	DL(2, 1),
	BL(3, 1),
	AH(4, 1),
	CH(5, 1),
	DH(6, 1),
	BH(7, 1),
	R8B(8, 1),
	R9B(9, 1),
	R10B(10, 1),
	R11B(11, 1),
	R12B(12, 1),
	R13B(13, 1),
	R14B(14, 1),
	R15B(15, 1),

	// high 8-bit general purpose
	SPL(4, 1),
	BPL(4, 1),
	SIL(4, 1),
	DIL(4, 1);

	val string = name.lowercase()

}