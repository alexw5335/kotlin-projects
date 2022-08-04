package assembler

@JvmInline
value class SpecificOperands(val value: Int) {


	companion object {

		val A_IMM = SpecificOperands(1 shl 0)
	}
}