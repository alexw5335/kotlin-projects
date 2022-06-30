package assembler

enum class Register(
	val value: Int,
	val width: Int,
	val flags: OperandFlags
) {

	// 64-bit general-purpose
	RAX(0, 4, OperandFlags.R64 + OperandFlags.RAX),
	RCX(1, 4, OperandFlags.R64),
	RDX(2, 4, OperandFlags.R64),
	RBX(3, 4, OperandFlags.R64),
	RSP(4, 4, OperandFlags.R64),
	RBP(5, 4, OperandFlags.R64),
	RSI(6, 4, OperandFlags.R64),
	RDI(7, 4, OperandFlags.R64),
	R8( 8, 4, OperandFlags.R64),
	R9( 9, 4, OperandFlags.R64),
	R10(10, 4, OperandFlags.R64),
	R11(11, 4, OperandFlags.R64),
	R12(12, 4, OperandFlags.R64),
	R13(13, 4, OperandFlags.R64),
	R14(14, 4, OperandFlags.R64),
	R15(15, 4, OperandFlags.R64),

	// 32-bit general-purpose
	EAX(0, 3, OperandFlags.R32 + OperandFlags.EAX),
	ECX(1, 3, OperandFlags.R32),
	EDX(2, 3, OperandFlags.R32),
	EBX(3, 3, OperandFlags.R32),
	ESP(4, 3, OperandFlags.R32),
	EBP(5, 3, OperandFlags.R32),
	ESI(6, 3, OperandFlags.R32),
	EDI(7, 3, OperandFlags.R32),
	R8D(8, 3, OperandFlags.R32),
	R9D(9, 3, OperandFlags.R32),
	R10D(10, 3, OperandFlags.R32),
	R11D(11, 3, OperandFlags.R32),
	R12D(12, 3, OperandFlags.R32),
	R13D(13, 3, OperandFlags.R32),
	R14D(14, 3, OperandFlags.R32),
	R15D(15, 3, OperandFlags.R32),

	// 16-bit general-purpose
	AX(0, 2, OperandFlags.R16 + OperandFlags.AX),
	CX(1, 2, OperandFlags.R16),
	DX(2, 2, OperandFlags.R16 + OperandFlags.DX),
	BX(3, 2, OperandFlags.R16),
	SP(4, 2, OperandFlags.R16),
	BP(5, 2, OperandFlags.R16),
	SI(6, 2, OperandFlags.R16),
	DI(7, 2, OperandFlags.R16),
	R8W(8, 2, OperandFlags.R16),
	R9W(9, 2, OperandFlags.R16),
	R10W(10, 2, OperandFlags.R16),
	R11W(11, 2, OperandFlags.R16),
	R12W(12, 2, OperandFlags.R16),
	R13W(13, 2, OperandFlags.R16),
	R14W(14, 2, OperandFlags.R16),
	R15W(15, 2, OperandFlags.R16),

	// 8-bit general-purpose
	AL(0, 1, OperandFlags.R8 + OperandFlags.AL),
	CL(1, 1, OperandFlags.R8 + OperandFlags.CL),
	DL(2, 1, OperandFlags.R8),
	BL(3, 1, OperandFlags.R8),
	AH(4, 1, OperandFlags.R8),
	CH(5, 1, OperandFlags.R8),
	DH(6, 1, OperandFlags.R8),
	BH(7, 1, OperandFlags.R8),
	R8B(8, 1, OperandFlags.R8),
	R9B(9, 1, OperandFlags.R8),
	R10B(10, 1, OperandFlags.R8),
	R11B(11, 1, OperandFlags.R8),
	R12B(12, 1, OperandFlags.R8),
	R13B(13, 1, OperandFlags.R8),
	R14B(14, 1, OperandFlags.R8),
	R15B(15, 1, OperandFlags.R8),

	// high 8-bit general purpose
	SPL(4, 1, OperandFlags { R + R8REX }),
	BPL(4, 1, OperandFlags { R + R8REX }),
	SIL(4, 1, OperandFlags { R + R8REX }),
	DIL(4, 1, OperandFlags { R + R8REX });

}