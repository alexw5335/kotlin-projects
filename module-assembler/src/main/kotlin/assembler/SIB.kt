package assembler

@JvmInline
value class SIB(val value: Int) {

	constructor(scale: Int, index: Int, base: Int) : this((scale shl 6) or (index shl 3) or base)
	constructor(scale: Int, index: Register, base: Register) : this(scale, index.value, base.value)

	val scale get() = (value and 0b11_000_000) shr 6
	val index get() = (value and 0b00_111_000) shr 3
	val base  get() = (value and 0b00_000_111) shr 0

	fun withScale(scale: Int) = SIB(value or (scale shl 6))
	fun withIndex(index: Int) = SIB(value or (index shl 3))
	fun withBase(base: Int) = SIB(value or (base shl 0))

	val actualScale get() = 1 shl scale

}