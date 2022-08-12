package assembler

@JvmInline
value class SIB(val value: Int) {

	val scale get() = (value and 0b11_000_000) shr 6
	val index get() = (value and 0b00_111_000) shr 3
	val base  get() = (value and 0b00_000_111) shr 0

	fun withScale(scale: Int) = SIB(value or (scale shl 6))
	fun withIndex(index: Int) = SIB(value or (index shl 3))
	fun withBase(base: Int) = SIB(value or (base shl 0))

	val actualScale get() = when(scale) {
		0b00 -> 1
		0b01 -> 2
		0b10 -> 4
		0b11 -> 8
		else -> error("Invalid scale")
	}

}