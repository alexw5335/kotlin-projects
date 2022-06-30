package assembler

@JvmInline
value class OperandFlags2(val value: Int) {

	operator fun contains(other: OperandFlags2) = value and other.value == other.value

	operator fun plus(other: OperandFlags2) = OperandFlags2(value or other.value)

	val isGroupREG get() = value and 0b111 == GROUP_REG
	val isGroupREG8 get() = value and 0b111 == GROUP_REG8
	val isGroupIMM get() = value and 0b111 == GROUP_IMM
	val isGroupXMM get() = value and 0b111 == GROUP_XMM

	companion object {

		// Flags within groups must be mutually exclusive
		const val GROUP_REG = 1
		const val GROUP_REG8 = 2
		const val GROUP_IMM = 3
		const val GROUP_XMM = 4
		const val GROUP_YMM = 5
		const val GROUP_ZMM = 6

		val NONE  = OperandFlags2(0)
		val MEM   = OperandFlags2(0b10000_000)

		val REG8   = OperandFlags2(GROUP_REG8)
		val REG8AL = OperandFlags2(0b0001 and GROUP_REG8)
		val REG8CL = OperandFlags2(0b0010 and GROUP_REG8)
		val REG8DL = OperandFlags2(0b0100 and GROUP_REG8)

		val REG  = OperandFlags2(GROUP_REG)
		val REGA = OperandFlags2(0b0001 and GROUP_REG)
		val REGC = OperandFlags2(0b0010 and GROUP_REG)
		val REGD = OperandFlags2(0b0100 and GROUP_REG)

		val IMM  = OperandFlags2(GROUP_IMM)
		val IMM8 = OperandFlags2(0b0001 and GROUP_IMM)

		val XMM     = OperandFlags2(GROUP_XMM)
		val XMMREG0 = OperandFlags2(0b0001 and GROUP_XMM)
		val XMMREG  = OperandFlags2(0b0000 and GROUP_XMM)
	}

}