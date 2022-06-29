package assembler

import assembler.RegWidth.*

enum class GpReg(val value: Int, val width: RegWidth) {

	RAX(0, BIT64),
	RCX(1, BIT64),
	RDX(2, BIT64),
	RBX(3, BIT64),
	RSP(4, BIT64),
	RBP(5, BIT64),
	RSI(6, BIT64),
	RDI(7, BIT64),
	R8(8, BIT64),
	R9(9, BIT64),
	R10(10, BIT64),
	R11(11, BIT64),
	R12(12, BIT64),
	R13(13, BIT64),
	R14(14, BIT64),
	R15(15, BIT64),
	
	EAX(0, BIT32),
	ECX(1, BIT32),
	EDX(2, BIT32),
	EBX(3, BIT32),
	ESP(4, BIT32),
	EBP(5, BIT32),
	ESI(6, BIT32),
	EDI(7, BIT32),
	R8D(8, BIT32),
	R9D(9, BIT32),
	R10D(10, BIT32),
	R11D(11, BIT32),
	R12D(12, BIT32),
	R13D(13, BIT32),
	R14D(14, BIT32),
	R15D(15, BIT32),

	AX(0, BIT16),
	CX(1, BIT16),
	DX(2, BIT16),
	BX(3, BIT16),
	SP(4, BIT16),
	BP(5, BIT16), 
	SI(6, BIT16),
	DI(7, BIT16),
	R8W(8, BIT16),
	R9W(9, BIT16),
	R10W(10, BIT16),
	R11W(11, BIT16),
	R12W(12, BIT16),
	R13W(13, BIT16),
	R14W(14, BIT16),
	R15W(15, BIT16),

	AL(0, BIT8),
	CL(1, BIT8),
	DL(2, BIT8),
	BL(3, BIT8),
	AH(4, BIT8),
	CH(5, BIT8),
	DH(6, BIT8),
	BH(7, BIT8),
	R8B(8, BIT8),
	R9B(9, BIT8),
	R10B(10, BIT8),
	R11B(11, BIT8),
	R12B(12, BIT8),
	R13B(13, BIT8),
	R14B(14, BIT8),
	R15B(15, BIT8),


}



enum class RegType {
	GP
}
enum class RegWidth {
	
	BIT8,
	BIT16,
	BIT32,
	BIT64;
	
}