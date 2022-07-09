package assembler

@JvmInline
value class OperandFlags(val value: Int) {


	operator fun contains(other: OperandFlags) = other.value and value == other.value

	infix fun shl(count: Int) = OperandFlags(value shl count)

	infix fun or(other: OperandFlags) = OperandFlags(value or other.value)

	infix fun and(other: OperandFlags) = OperandFlags(value and other.value)

	val type get() = OperandFlags(value and 0b00011111)

	val flags get() = OperandFlags(value and 0b11100000)



	companion object {

		// Bits 0-4: type. Bits 5-7: flags.
		val NONE  = OperandFlags(0)
		val R8    = OperandFlags(1)
		val R16   = OperandFlags(2)
		val R32   = OperandFlags(3)
		val R64   = OperandFlags(4)
		val IMM   = OperandFlags(5)
		val MEM   = OperandFlags(6)
		val XMM   = OperandFlags(7)
		val YMM   = OperandFlags(8)

		val AL = OperandFlags(0b001) or R8
		val CL = OperandFlags(0b010) or R8
		val R8REX = OperandFlags(0b100) or R8

		val AX = OperandFlags(0b001) or R16
		val DX = OperandFlags(0b010) or R16

		val EAX = OperandFlags(0b001) or R32

		val RAX = OperandFlags(0b001) or R64

		val IMM8 = OperandFlags(0b001) or IMM

		val MEM_EXPLICIT = OperandFlags(0b001) or MEM

	}


}