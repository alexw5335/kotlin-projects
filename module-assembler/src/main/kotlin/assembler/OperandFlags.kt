package assembler

@JvmInline
value class OperandFlags(val value: Int) {


	infix fun or(other: OperandFlags) = OperandFlags(value or other.value)

	operator fun contains(other: OperandFlags) = value and other.value == other.value



	companion object {

		val R8    = OperandFlags(1 shl 0)
		val R16   = OperandFlags(1 shl 1)
		val R32   = OperandFlags(1 shl 2)
		val R64   = OperandFlags(1 shl 3)
		val MEMN  = OperandFlags(1 shl 4)
		val MEM8  = OperandFlags(1 shl 5)
		val MEM16 = OperandFlags(1 shl 6)
		val MEM32 = OperandFlags(1 shl 7)
		val MEM64 = OperandFlags(1 shl 8)
		val IMMN  = OperandFlags(1 shl 9)
		val IMM8  = OperandFlags(1 shl 10)
		val IMM   = OperandFlags(1 shl 11)

	}


}