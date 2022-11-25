package eyre

import eyre.Width.*

enum class Register(
	val value  : Int,
	val width  : Width,
	val rex    : Int = 0,
	val isA    : Boolean = false,
	val isSP   : Boolean = false,
	val rex8   : Boolean = false,
	val noRex8 : Boolean = false
) {

	// 64-bit
	RAX(0, BIT64, isA = true),
	RCX(1, BIT64),
	RDX(2, BIT64),
	RBX(3, BIT64),
	RSP(4, BIT64, isSP = true),
	RBP(5, BIT64),
	RSI(6, BIT64),
	RDI(7, BIT64),
	R8(0, BIT64, rex = 1),
	R9(1, BIT64, rex = 1),
	R10(2, BIT64, rex = 1),
	R11(3, BIT64, rex = 1),
	R12(4, BIT64, rex = 1),
	R13(5, BIT64, rex = 1),
	R14(6, BIT64, rex = 1),
	R15(7, BIT64, rex = 1),

	// 32-bit
	EAX(0, BIT32, isA = true),
	ECX(1, BIT32),
	EDX(2, BIT32),
	EBX(3, BIT32),
	ESP(4, BIT32, isSP = true),
	EBP(5, BIT32),
	ESI(6, BIT32),
	EDI(7, BIT32),
	R8D(0, BIT32, 1),
	R9D(1, BIT32, 1),
	R10D(2, BIT32, 1),
	R11D(3, BIT32, 1),
	R12D(4, BIT32, 1),
	R13D(5, BIT32, 1),
	R14D(6, BIT32, 1),
	R15D(7, BIT32, 1),

	// 16-bit
	AX(0, BIT16, isA = true),
	CX(1, BIT16),
	DX(2, BIT16),
	BX(3, BIT16),
	SP(4, BIT16, isSP = true),
	BP(5, BIT16),
	SI(6, BIT16),
	DI(7, BIT16),
	R8W(0, BIT16, 1),
	R9W(1, BIT16, 1),
	R10W(2, BIT16, 1),
	R11W(3, BIT16, 1),
	R12W(4, BIT16, 1),
	R13W(5, BIT16, 1),
	R14W(6, BIT16, 1),
	R15W(7, BIT16, 1),

	// 8-bit
	AL(0, BIT8, isA = true),
	CL(1, BIT8),
	DL(2, BIT8),
	BL(3, BIT8),
	AH(4, BIT8, noRex8 = true),
	CH(5, BIT8, noRex8 = true),
	DH(6, BIT8, noRex8 = true),
	BH(7, BIT8, noRex8 = true),
	R8B(0, BIT8, 1),
	R9B(1, BIT8, 1),
	R10B(2, BIT8, 1),
	R11B(3, BIT8, 1),
	R12B(4, BIT8, 1),
	R13B(5, BIT8, 1),
	R14B(6, BIT8, 1),
	R15B(7, BIT8, 1),

	// 8-bit with any REX prefix
	SPL(4, BIT8, isSP = true, rex8 = true),
	BPL(5, BIT8, rex8 = true),
	SIL(6, BIT8, rex8 = true),
	DIL(7, BIT8, rex8 = true);

	val string = name.lowercase()

	companion object {
		val values = values()
	}

}