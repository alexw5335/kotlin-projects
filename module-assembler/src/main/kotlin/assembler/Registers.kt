package assembler



interface Register {
	val value: Int
}



interface GPRegister : Register



enum class GP8Register : GPRegister {

	AL,
	CL,
	DL,
	BL,
	BPL,
	SPL,
	DIL,
	SIL,
	R8B,
	R9B,
	R10B,
	R11B,
	R12B,
	R13B,
	R14B,
	R15B;

	override val value = ordinal

}



enum class GP16Register : GPRegister {

	AX,
	CX,
	DX,
	BX,
	BP,
	SP,
	DI,
	SI,
	R8W,
	R9W,
	R10W,
	R11W,
	R12W,
	R13W,
	R14W,
	R15W;

	override val value = ordinal

}



enum class GP32Register : GPRegister {

	EAX,
	ECX,
	EDX,
	EBX,
	EBP,
	ESP,
	EDI,
	ESI,
	R8D,
	R9D,
	R10D,
	R11D,
	R12D,
	R13D,
	R14D,
	R15D;

	override val value = ordinal
}



enum class GP64Register : GPRegister {

	RAX,
	RCX,
	RDX,
	RBX,
	RBP,
	RSP,
	RDI,
	RSI,
	R8,
	R9,
	R10,
	R11,
	R12,
	R13,
	R14,
	R15;

	override val value = ordinal

}
