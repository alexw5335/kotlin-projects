package asm

/**
 * An 8-, 16-, 32-, or 64-bit general purpose register.
 */
enum class Register(
	val value : Int,
	val width : Width
) {

	// 64-bit
	RAX(0, Width.BIT64),
	RCX(1, Width.BIT64),
	RDX(2, Width.BIT64),
	RBX(3, Width.BIT64),
	RSP(4, Width.BIT64),
	RBP(5, Width.BIT64),
	RSI(6, Width.BIT64),
	RDI(7, Width.BIT64),
	R8(8, Width.BIT64),
	R9(9, Width.BIT64),
	R10(10, Width.BIT64),
	R11(11, Width.BIT64),
	R12(12, Width.BIT64),
	R13(13, Width.BIT64),
	R14(14, Width.BIT64),
	R15(15, Width.BIT64),

	// 32-bit
	EAX(0, Width.BIT32),
	ECX(1, Width.BIT32),
	EDX(2, Width.BIT32),
	EBX(3, Width.BIT32),
	ESP(4, Width.BIT32),
	EBP(5, Width.BIT32),
	ESI(6, Width.BIT32),
	EDI(7, Width.BIT32),
	R8D(8, Width.BIT32),
	R9D(9, Width.BIT32),
	R10D(10, Width.BIT32),
	R11D(11, Width.BIT32),
	R12D(12, Width.BIT32),
	R13D(13, Width.BIT32),
	R14D(14, Width.BIT32),
	R15D(15, Width.BIT32),

	// 16-bit
	AX(0, Width.BIT16),
	CX(1, Width.BIT16),
	DX(2, Width.BIT16),
	BX(3, Width.BIT16),
	SP(4, Width.BIT16),
	BP(5, Width.BIT16),
	SI(6, Width.BIT16),
	DI(7, Width.BIT16),
	R8W(8, Width.BIT16),
	R9W(9, Width.BIT16),
	R10W(10, Width.BIT16),
	R11W(11, Width.BIT16),
	R12W(12, Width.BIT16),
	R13W(13, Width.BIT16),
	R14W(14, Width.BIT16),
	R15W(15, Width.BIT16),

	// 8-bit
	// AH, CH, DH, and BH cannot have a REX prefix
	// SPL, BPL, SIL, and DIL must have a REX prefix 
	AL(0, Width.BIT8),
	CL(1, Width.BIT8),
	DL(2, Width.BIT8),
	BL(3, Width.BIT8),
	AH(4, Width.BIT8),
	CH(5, Width.BIT8),
	DH(6, Width.BIT8),
	BH(7, Width.BIT8),
	R8B(8, Width.BIT8),
	R9B(9, Width.BIT8),
	R10B(10, Width.BIT8),
	R11B(11, Width.BIT8),
	R12B(12, Width.BIT8),
	R13B(13, Width.BIT8),
	R14B(14, Width.BIT8),
	R15B(15, Width.BIT8),
	SPL(4, Width.BIT8),
	BPL(5, Width.BIT8),
	SIL(6, Width.BIT8),
	DIL(7, Width.BIT8);

	val rex = if(value >= 8) 1 else 0

	val string = name.lowercase()
	
}