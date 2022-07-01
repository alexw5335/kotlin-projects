package assembler

@JvmInline
value class OperandFlags(val value: Long) {

	operator fun plus(other: OperandFlags) = OperandFlags(value or other.value)

	operator fun contains(other: OperandFlags) = other.value and value == other.value

	infix fun shl(count: Int) = OperandFlags(value shl count)

	companion object {

		val NONE = OperandFlags(0)
		val M = OperandFlags(1 shl 0)
		val REG8 = OperandFlags(1 shl 1)
		val REG = OperandFlags(1 shl 2)
		val A = OperandFlags(1 shl 3)
		val DX = OperandFlags(1 shl 4)
		val AL = OperandFlags(1 shl 5)
		val CL = OperandFlags(1 shl 6)
		val IMM8 = OperandFlags(1 shl 7)
		val IMM16 = OperandFlags(1 shl 8)
		val IMM32 = OperandFlags(1 shl 9)
		val XMM = OperandFlags(1 shl 10)

		val R = REG + A + DX
		val R8 = REG8 + AL + CL
		val IMM = IMM8 + IMM16 + IMM32
		val RM = R + M
		val RM8 = REG8 + M

	}

}



inline fun OperandFlags(block: OperandFlags.Companion.() -> OperandFlags) = block(OperandFlags)