package assembler

@JvmInline
value class OperandFlags(val value: Int) {

	operator fun plus(flags: OperandFlags) = OperandFlags(value or flags.value)

	operator fun contains(flags: OperandFlags) = value and flags.value == flags.value

	infix fun or(flags: OperandFlags) = OperandFlags(value or flags.value)

	infix fun shl(count: Int) = OperandFlags(value shl count)

	companion object {

		val NONE = OperandFlags(0)
		val REG8 = OperandFlags(1 shl 0)
		val REG  = OperandFlags(1 shl 1)
		val MEM  = OperandFlags(1 shl 2)
		val IMM  = OperandFlags(1 shl 3)
		val IMM8 = OperandFlags(1 shl 4)
		val A    = OperandFlags(1 shl 5)
		val AL   = OperandFlags(1 shl 6)
		val CL   = OperandFlags(1 shl 7)
		val R    = REG + A
		val R8   = REG8 + AL + CL
		val RM   = R + MEM
		val RM8  = R8 + MEM

	}

}



inline fun OperandFlags(block: OperandFlags.Companion.() -> OperandFlags) = OperandFlags.block()