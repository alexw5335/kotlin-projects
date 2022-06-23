package assembler

enum class Register {

	AL,
	CL,
	DL,
	BL,
	SPL,
	BPL,
	SIL,
	DIL,
	R8B,
	R9B,
	R10B,
	R11B,
	R12B,
	R13B,
	R14B,
	R15B,

	AX,
	CX,
	DX,
	BX,
	SP,
	BP,
	SI,
	DI,
	R8W,
	R9W,
	R10W,
	R11W,
	R12W,
	R13W,
	R14W,
	R15W,

	EAX,
	ECX,
	EDX,
	EBX,
	ESP,
	EBP,
	ESI,
	EDI,
	R8D,
	R9D,
	R10D,
	R11D,
	R12D,
	R13D,
	R14D,
	R15D,

	RAX,
	RCX,
	RDX,
	RBX,
	RSP,
	RBP,
	RSI,
	RDI,
	R8,
	R9,
	R10,
	R11,
	R12,
	R13,
	R14,
	R15;

	val value get() = ordinal % 16
	val isGP get() = ordinal in AX.ordinal.. R15.ordinal
	val isGP8 get() = ordinal in AL.ordinal.. R15B.ordinal
	val isGP16 get() = ordinal in AX.ordinal.. R15W.ordinal
	val isGP32 get() = ordinal in EAX.ordinal.. R15D.ordinal


}