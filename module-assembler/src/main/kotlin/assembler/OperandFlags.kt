package assembler

@JvmInline
value class OperandFlags(val value: Int) {

	operator fun contains(other: OperandFlags) = value and other.value == other.value

	operator fun plus(other: OperandFlags) = OperandFlags(value or other.value)

	infix fun shl(count: Int) = OperandFlags(value shl count)

	infix fun or(other: OperandFlags) = OperandFlags(value or other.value)

	val isMEM get() = MEM in this
	val isREG get() = REG in this
	val isREG8 get() = REG8 in this
	val isREG16 get() = REG16 in this
	val isREG32 get() = REG32 in this
	val isREG64 get() = REG64 in this
	val isIMM get() = IMM in this
	val isIMM8 get() = IMM8 in this
	val isA get() = A in this
	val isAL get() = AL in this
	val isCL get() = CL in this
	val isR get() = R in this
	val isR8 get() = R8 in this
	val isRM get() = RM in this
	val isRM8 get() = RM8 in this

	companion object {

		val NONE  = OperandFlags(0)
		val MEM   = OperandFlags(1 shl 0)
		val REG   = OperandFlags(1 shl 1)
		val REG8  = OperandFlags(1 shl 2)
		val REG16 = OperandFlags(1 shl 3)
		val REG32 = OperandFlags(1 shl 4)
		val REG64 = OperandFlags(1 shl 5)
		val IMM   = OperandFlags(1 shl 6)
		val IMM8  = OperandFlags(1 shl 7)
		val A     = OperandFlags(1 shl 8)
		val AL    = OperandFlags(1 shl 9)
		val CL    = OperandFlags(1 shl 10)
		val R     = REG + REG16 + REG32 + REG64 + A
		val R8    = REG8 + AL + CL
		val RM    = R + MEM
		val RM8   = R8 + MEM

	}

}



inline fun OperandFlags(block: OperandFlags.Companion.() -> OperandFlags) = block(OperandFlags)