package assembler

@JvmInline
value class EncodingFlags(val value: Long) {


	companion object {

		private var count = 0
		private fun new() = EncodingFlags(1L shl count++)

		// No operands

		val NONE = new()

		// One operand

		val R8 = new()
		val R16 = new()
		val R32 = new()
		val R64 = new()
		val M8 = new()
		val M16 = new()
		val M32 = new()
		val M64 = new()
		val M = new()


		// Two operands
		val R8_R8 = new()
		val R8_M8 = new()
		val R8_IMM8 = new()
		val AL_IMM8 = new()

		val R16_R16 = new()
		val R16_M16 = new()
		val R16_IMM8 = new()
		val R16_IMM16 = new()
		val AX_IMM16 = new()

		val R32_R32 = new()
		val R32_M32 = new()
		val R32_IMM8 = new()
		val R32_IMM32 = new()
		val EAX_IMM32 = new()

		val R64_R64 = new()
		val R64_M64 = new()
		val R64_IMM8 = new()
		val R64_IMM32 = new()
		val RAX_IMM32 = new()

		val M8_R8 = new()
		val M8_IMM8 = new()

		val M16_R16 = new()
		val M16_IMM8 = new()
		val M16_IMM16 = new()

		val M32_R32 = new()
		val M32_IMM8 = new()
		val M32_IMM32 = new()

		val M64_R64 = new()
		val M64_IMM8 = new()
		val M64_IMM32 = new()

	}


}