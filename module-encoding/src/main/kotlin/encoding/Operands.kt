package encoding



enum class Operands(
	val parts: Array<OpClass>? = null,
	val sm: Boolean = false,
	val sm2: Boolean = false,
	val sm3: Boolean = false,
) {
	NONE,
	A(arrayOf(OperandType.A)),
	R(arrayOf(OperandType.R)),
	M(arrayOf(OperandType.M)),
	I(arrayOf(OperandType.I)),
	ST(arrayOf(OperandType.ST)),
	REL(arrayOf(OperandType.REL)),

	R_R(arrayOf(OperandType.R, OperandType.R), sm = true),
	R_M(arrayOf(OperandType.R, OperandType.M), sm = true),
	M_R(arrayOf(OperandType.M, OperandType.R), sm = true),
	R_I(arrayOf(OperandType.R, OperandType.I), sm = true),
	A_I(arrayOf(OperandType.A, OperandType.I), sm = true),
	M_I(arrayOf(OperandType.M, OperandType.I), sm = true),
	R_I8(arrayOf(OperandType.R, Operand.I8)),
	M_I8(arrayOf(OperandType.M, Operand.I8)),
	R_1(arrayOf(OperandType.R, Operand.ONE)),
	M_1(arrayOf(OperandType.M, Operand.ONE)),
	ST_ST0(arrayOf(Operand.ST, Operand.ST0)),
	ST0_ST(arrayOf(Operand.ST0, Operand.ST)),
	R_CL(arrayOf(OperandType.R, Operand.CL)),
	M_CL(arrayOf(OperandType.M, Operand.CL)),

	R_R_I8(arrayOf(OperandType.R, OperandType.R, Operand.I8), sm2 = true),
	R_M_I8(arrayOf(OperandType.R, OperandType.M, Operand.I8), sm2 = true),
	M_R_I8(arrayOf(OperandType.M, OperandType.R, Operand.I8), sm2 = true),
	R_R_CL(arrayOf(OperandType.R, OperandType.R, Operand.CL), sm2 = true),
	M_R_CL(arrayOf(OperandType.M, OperandType.R, Operand.CL), sm2 = true),
	R_R_I(arrayOf(OperandType.R, OperandType.R, OperandType.I), sm = true),
	R_M_I(arrayOf(OperandType.R, OperandType.M, OperandType.I), sm = true),
	M_R_I(arrayOf(OperandType.M, OperandType.R, OperandType.I), sm = true),

	S_M32_I8(arrayOf(OperandType.S, Operand.M32, Operand.I8)),
	S_S_S_I8(arrayOf(OperandType.S, OperandType.S, OperandType.S, Operand.I8), sm3 = true),
	S_S_M64_I8(arrayOf(OperandType.S, OperandType.S, Operand.M64, Operand.I8), sm2 = true),
	K_X_X_I8(arrayOf(OperandType.K, OperandType.S, OperandType.S, Operand.I8), sm3 = true),

	// Custom
	A_I8,
	I8_A,
	A_DX,
	DX_A,
	I16_I8,
	REL8_ECX(arrayOf(Operand.REL8, Operand.ECX)),
	REL8_RCX(arrayOf(Operand.REL8, Operand.RCX));

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
	MISC;

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
	VM32X(OperandType.MISC, "xmem32"),
	VM64X(OperandType.MISC, "xmem64"),
	VM32Y(OperandType.MISC, "ymem32"),
	VM64Y(OperandType.MISC, "ymem64"),
	VM32Z(OperandType.MISC, "zmem32"),
	VM64Z(OperandType.MISC, "zmem64"),
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