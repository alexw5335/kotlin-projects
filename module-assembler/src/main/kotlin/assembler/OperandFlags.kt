package assembler

@JvmInline
value class OperandFlags(val value: Int) {

	operator fun plus(other: OperandFlags) = OperandFlags(value or other.value)

	operator fun contains(other: OperandFlags) = other.value and value == other.value

	companion object {

		// Group occupies first byte
		// '+' operator must only be used within groups
		val R     = OperandFlags(1)
		val IMM   = OperandFlags(2)
		val R8    = OperandFlags(3)

		val R16   = OperandFlags(0b0000001) + R
		val R32   = OperandFlags(0b0000010) + R
		val R64   = OperandFlags(0b0000100) + R
		val AX    = OperandFlags(0b0001000) + R
		val EAX   = OperandFlags(0b0010000) + R
		val RAX   = OperandFlags(0b0100000) + R
		val DX    = OperandFlags(0b1000000) + R

		val AL    = OperandFlags(0b0000001) + R8
		val CL    = OperandFlags(0b0000010) + R8
		val R8REX = OperandFlags(0b0000100) + R8

		val IMM8  = OperandFlags(0b0000001) + IMM
		val IMM16 = OperandFlags(0b0000010) + IMM

		val NONE  = OperandFlags(0)
		val M     = OperandFlags(0b1000_0000_0000_0000)
		val RM    = R + M
		val RM8   = R8 + M
		val R1664 = R16 + R64
		val R3264 = R32 + R64
		val A     = AX + EAX + RAX

	}

}



inline fun OperandFlags(block: OperandFlags.Companion.() -> OperandFlags) = block(OperandFlags)