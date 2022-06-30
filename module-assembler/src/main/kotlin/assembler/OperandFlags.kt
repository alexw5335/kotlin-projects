package assembler

/**
 * Bits 0-5: group, bits 6-14: type, bit 15: MEM
 */
@JvmInline
value class OperandFlags(val value: Int) {

	operator fun plus(other: OperandFlags) = OperandFlags(value or other.value)

	operator fun contains(other: OperandFlags) = other.value and value == other.value

	companion object {

		val GROUP_REG = OperandFlags(1)
		val GROUP_IMM = OperandFlags(2)
		val GROUP_REG8 = OperandFlags(3)

		val NONE = OperandFlags(0)
		val MEM = OperandFlags(0b1000_0000_0000_0000)

		val REG16 = OperandFlags(0b0000000001) + GROUP_REG
		val REG32 = OperandFlags(0b0000000010) + GROUP_REG
		val REG64 = OperandFlags(0b0000000100) + GROUP_REG
		val AX    = OperandFlags(0b0000001000) + GROUP_REG
		val EAX   = OperandFlags(0b0000010000) + GROUP_REG
		val RAX   = OperandFlags(0b0000100000) + GROUP_REG
		val DX    = OperandFlags(0b0001000000) + GROUP_REG

		val AL    = OperandFlags(0b0000000001) + GROUP_REG8
		val CL    = OperandFlags(0b0000000010) + GROUP_REG8

		val IMM8  = OperandFlags(0b0000000001) + GROUP_IMM
		val IMM16 = OperandFlags(0b0000000010) + GROUP_IMM


	}

}