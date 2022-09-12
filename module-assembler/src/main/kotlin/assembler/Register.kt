package assembler

/**
 * An 8-, 16-, 32-, or 64-bit general purpose register.
 */
enum class Register(
	val value : Int,
	val width : Width,
	val rex   : Int = 0,
	val rex8  : Boolean = false,
	val noRex : Boolean = false
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
	R8(0, Width.BIT64, rex = 1),
	R9(1, Width.BIT64, rex = 1),
	R10(2, Width.BIT64, rex = 1),
	R11(3, Width.BIT64, rex = 1),
	R12(4, Width.BIT64, rex = 1),
	R13(5, Width.BIT64, rex = 1),
	R14(6, Width.BIT64, rex = 1),
	R15(7, Width.BIT64, rex = 1),

	// 32-bit
	EAX(0, Width.BIT32),
	ECX(1, Width.BIT32),
	EDX(2, Width.BIT32),
	EBX(3, Width.BIT32),
	ESP(4, Width.BIT32),
	EBP(5, Width.BIT32),
	ESI(6, Width.BIT32),
	EDI(7, Width.BIT32),
	R8D(0, Width.BIT32, rex = 1),
	R9D(1, Width.BIT32, rex = 1),
	R10D(2, Width.BIT32, rex = 1),
	R11D(3, Width.BIT32, rex = 1),
	R12D(4, Width.BIT32, rex = 1),
	R13D(5, Width.BIT32, rex = 1),
	R14D(6, Width.BIT32, rex = 1),
	R15D(7, Width.BIT32, rex = 1),

	// 16-bit
	AX(0, Width.BIT16),
	CX(1, Width.BIT16),
	DX(2, Width.BIT16),
	BX(3, Width.BIT16),
	SP(4, Width.BIT16),
	BP(5, Width.BIT16),
	SI(6, Width.BIT16),
	DI(7, Width.BIT16),
	R8W(0, Width.BIT16, rex = 1),
	R9W(1, Width.BIT16, rex = 1),
	R10W(2, Width.BIT16, rex = 1),
	R11W(3, Width.BIT16, rex = 1),
	R12W(4, Width.BIT16, rex = 1),
	R13W(5, Width.BIT16, rex = 1),
	R14W(6, Width.BIT16, rex = 1),
	R15W(7, Width.BIT16, rex = 1),

	// 8-bit
	AL(0, Width.BIT8),
	CL(1, Width.BIT8),
	DL(2, Width.BIT8),
	BL(3, Width.BIT8),
	AH(4, Width.BIT8, noRex = true),
	CH(5, Width.BIT8, noRex = true),
	DH(6, Width.BIT8, noRex = true),
	BH(7, Width.BIT8, noRex = true),
	R8B(0, Width.BIT8, rex = 1),
	R9B(1, Width.BIT8, rex = 1),
	R10B(2, Width.BIT8, rex = 1),
	R11B(3, Width.BIT8, rex = 1),
	R12B(4, Width.BIT8, rex = 1),
	R13B(5, Width.BIT8, rex = 1),
	R14B(6, Width.BIT8, rex = 1),
	R15B(7, Width.BIT8, rex = 1),

	// 8-bit with any REX prefix
	SPL(4, Width.BIT8, rex8 = true),
	BPL(5, Width.BIT8, rex8 = true),
	SIL(6, Width.BIT8, rex8 = true),
	DIL(7, Width.BIT8, rex8 = true);

	val string = name.lowercase()

}