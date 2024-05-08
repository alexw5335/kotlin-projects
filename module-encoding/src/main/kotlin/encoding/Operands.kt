package encoding



enum class Operands(vararg val parts: OpClass) {
	NONE,

	MEM(Operand.MEM),
	A(OperandType.A),
	R(OperandType.R),
	M(OperandType.M),
	I(OperandType.I),
	ST(OperandType.ST),
	REL(OperandType.REL),

	R_R(OperandType.R, OperandType.R),
	R_M(OperandType.R, OperandType.M),
	M_R(OperandType.M, OperandType.R),
	R_I(OperandType.R, OperandType.I),
	A_I(OperandType.A, OperandType.I),
	M_I(OperandType.M, OperandType.I),
	R_I8(OperandType.R, Operand.I8),
	M_I8(OperandType.M, Operand.I8),
	R_1(OperandType.R, Operand.ONE),
	M_1(OperandType.M, Operand.ONE),
	ST_ST0(Operand.ST, Operand.ST0),
	ST0_ST(Operand.ST0, Operand.ST),
	R_CL(OperandType.R, Operand.CL),
	M_CL(OperandType.M, Operand.CL),

	R_R_I8(OperandType.R, OperandType.R, Operand.I8),
	R_M_I8(OperandType.R, OperandType.M, Operand.I8),
	M_R_I8(OperandType.M, OperandType.R, Operand.I8),
	R_R_CL(OperandType.R, OperandType.R, Operand.CL),
	M_R_CL(OperandType.M, OperandType.R, Operand.CL),
	R_R_I(OperandType.R, OperandType.R, OperandType.I),
	R_M_I(OperandType.R, OperandType.M, OperandType.I),
	M_R_I(OperandType.M, OperandType.R, OperandType.I),

	S_M32_I8(OperandType.S, Operand.M32, Operand.I8),
	S_S_S_I8(OperandType.S, OperandType.S, OperandType.S, Operand.I8),
	S_S_M64_I8(OperandType.S, OperandType.S, Operand.M64, Operand.I8),
	K_X_X_I8(OperandType.K, OperandType.S, OperandType.S, Operand.I8),

	R64_M128(Operand.R64, Operand.M128),
	MM_R(Operand.MM, OperandType.R),
	MM_M(Operand.MM, OperandType.M),
	R_MM(OperandType.R, Operand.MM),
	M_MM(OperandType.M, Operand.MM),
	MM_MM(Operand.MM, Operand.MM),
	MM_I8(Operand.MM, Operand.I8),
	S_S(OperandType.S, OperandType.S),
	R_X(OperandType.R, Operand.X),

	MM_R_I8(Operand.MM, OperandType.R, Operand.I8),
	MM_M_I8(Operand.MM, OperandType.M, Operand.I8),

	VM32S_S(OperandType.VM32S, OperandType.S),
	VM64S_S(OperandType.VM64S, OperandType.S),
	S_VM32S(OperandType.S, OperandType.VM32S),
	S_VM64S(OperandType.S, OperandType.VM64S),
	R_R_R(OperandType.R, OperandType.R, OperandType.R),
	R_R_M(OperandType.R, OperandType.R, OperandType.M),
	R_M_R(OperandType.R, OperandType.M, OperandType.R),
	S_S_S_S(OperandType.S, OperandType.S, OperandType.S, OperandType.S),
	S_S_M32(OperandType.S, OperandType.S, Operand.M32),
	S_S_S(OperandType.S, OperandType.S, OperandType.S),

	S_M(OperandType.S, OperandType.M),
	M_S(OperandType.M, OperandType.S),

	S_M16(OperandType.S, Operand.M16),
	S_M32(OperandType.S, Operand.M32),
	S_M64(OperandType.S, Operand.M64),
	S_M128(OperandType.S, Operand.M128),

	X_R(Operand.X, OperandType.R),

	X_Y(Operand.X, Operand.Y),
	Y_X(Operand.Y, Operand.X),
	Y_Z(Operand.Y, Operand.Z),
	Z_Y(Operand.Z, Operand.Y),
	Z_X(Operand.Z, Operand.X),
	X_Z(Operand.X, Operand.Z),
	K_S(Operand.K, OperandType.S),
	K_K(Operand.K, Operand.K),
	K_K_K(Operand.K, Operand.K, Operand.K),

	K_S_S(Operand.K, OperandType.S, OperandType.S),
	K_S_M(Operand.K, OperandType.S, OperandType.M),



	T(Operand.T),
	T_MEM(Operand.T, Operand.MEM),
	MEM_T(Operand.MEM, Operand.T),
	T_T_T(Operand.T, Operand.T, Operand.T),

	// Custom
	A_I8(OperandType.A, Operand.I8),
	I8_A(Operand.I8, OperandType.A),
	A_DX(OperandType.A, Operand.DX),
	DX_A(Operand.DX, OperandType.A),
	I16_I8(Operand.I16, Operand.I8),
	REL8_ECX(Operand.REL8, Operand.ECX),
	REL8_RCX(Operand.REL8, Operand.RCX);

	companion object {
		val values = values()
	}
}



sealed interface OpClass {
	operator fun contains(operand: Operand) = when(this) {
		is OperandType -> operand.type == this
		is Operand -> operand == this
	}
}



enum class OperandType : OpClass {
	R,
	M,
	I,
	A,
	C,
	D,
	S,
	ST,
	K,
	MM,
	REL,
	COMPOUND,
	ONE,
	MISC,
	VM32S,
	VM64S;

	val isR get() = this == R
	val isM get() = this == M
	val isI get() = this == I
	val isA get() = this == A
	val isS get() = this == S
	val isC get() = this == C
	val isRel get() = this == REL
	val isST get() = this == ST
}



enum class Operand(
	val type         : OperandType,
	val string       : String?,
	val width        : Width? = null,
	vararg val parts : Operand
) : OpClass {

	R8(OperandType.R, "reg8", Width.BYTE),
	R16(OperandType.R, "reg16", Width.WORD),
	R32(OperandType.R, "reg32", Width.DWORD),
	R64(OperandType.R, "reg64", Width.QWORD),
	MEM(OperandType.M, null, null),
	M8(OperandType.M, "mem8", Width.BYTE),
	M16(OperandType.M, "mem16", Width.WORD),
	M32(OperandType.M, "mem32", Width.DWORD),
	M64(OperandType.M, "mem64", Width.QWORD),
	M80(OperandType.M, "mem80", Width.TWORD),
	M128(OperandType.M, "mem128", Width.XWORD),
	M256(OperandType.M, "mem256", Width.YWORD),
	M512(OperandType.M, "mem512", Width.ZWORD),
	I8(OperandType.I, "imm8", Width.BYTE),
	I16(OperandType.I, "imm16", Width.WORD),
	I32(OperandType.I, "imm32", Width.DWORD),
	I64(OperandType.I, "imm64", Width.QWORD),
	AL(OperandType.A, "reg_al", Width.BYTE),
	AX(OperandType.A, "reg_ax", Width.WORD),
	EAX(OperandType.A, "reg_eax", Width.DWORD),
	RAX(OperandType.A, "reg_rax", Width.QWORD),
	DX(OperandType.D, "reg_dx", Width.WORD),
	CL(OperandType.C, "reg_cl", Width.BYTE),
	ECX(OperandType.C, "reg_ecx", Width.DWORD),
	RCX(OperandType.C, "reg_rcx", Width.QWORD),
	ONE(OperandType.ONE, "unity"),
	REL8(OperandType.REL, null, Width.BYTE),
	REL16(OperandType.REL, null, Width.WORD),
	REL32(OperandType.REL, null, Width.DWORD),
	ST(OperandType.ST, "fpureg", Width.TWORD),
	ST0(OperandType.ST, "fpu0", Width.TWORD),
	MM(OperandType.MM, "mmxreg", Width.QWORD),
	X(OperandType.S, "xmmreg", Width.XWORD),
	X0(OperandType.S, "xmm0", Width.XWORD),
	Y(OperandType.S, "ymmreg", Width.YWORD),
	Z(OperandType.S, "zmmreg", Width.ZWORD),
	VM32X(OperandType.VM32S, "xmem32", Width.XWORD),
	VM64X(OperandType.VM64S, "xmem64", Width.XWORD),
	VM32Y(OperandType.VM32S, "ymem32", Width.YWORD),
	VM64Y(OperandType.VM64S, "ymem64", Width.YWORD),
	VM32Z(OperandType.VM32S, "zmem32", Width.ZWORD),
	VM64Z(OperandType.VM64S, "zmem64", Width.ZWORD),
	K(OperandType.K, "kreg"),
	BND(OperandType.MISC, "bndreg"),
	T(OperandType.MISC, "tmmreg"),

	RM8(OperandType.COMPOUND, "rm8", Width.BYTE, R8, M8),
	RM16(OperandType.COMPOUND, "rm16", Width.WORD, R16, M16),
	RM32(OperandType.COMPOUND, "rm32", Width.DWORD, R32, M32),
	RM64(OperandType.COMPOUND, "rm64", Width.QWORD, R64, M64),
	MMM64(OperandType.COMPOUND, "mmxrm64", Width.QWORD, MM, M64),
	XM8(OperandType.COMPOUND, "xmmrm8", null, X, M8),
	XM16(OperandType.COMPOUND, "xmmrm16", null, X, M16),
	XM32(OperandType.COMPOUND, "xmmrm32", null, X, M32),
	XM64(OperandType.COMPOUND, "xmmrm64", null, X, M64),
	XM128(OperandType.COMPOUND, "xmmrm128", null, X, M128),
	XM256(OperandType.COMPOUND, "xmmrm256", null, X, M256),
	YM16(OperandType.COMPOUND, "ymmrm16", null, Y, M16),
	YM128(OperandType.COMPOUND, "ymmrm128", null, Y, M128),
	YM256(OperandType.COMPOUND, "ymmrm256", null, Y, M256),
	ZM16(OperandType.COMPOUND, "zmmrm16", null, Z, M16),
	ZM128(OperandType.COMPOUND, "zmmrm128", null, Z, M128),
	ZM512(OperandType.COMPOUND, "zmmrm512", null, Z, M512),
	KM8(OperandType.COMPOUND, "krm8", null, K, M8),
	KM16(OperandType.COMPOUND, "krm16", null, K, M16),
	KM32(OperandType.COMPOUND, "krm32", null, K, M32),
	KM64(OperandType.COMPOUND, "krm64", null, K, M64);

}