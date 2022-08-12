package assembler

@JvmInline
value class REX(val value: Int) {

	val w get() = (value shr 3) and 1
	val r get() = (value shr 2) and 1
	val x get() = (value shr 1) and 1
	val b get() = (value shr 0) and 1

	val hasW get() = (value and 0b01001000) != 0
	val hasR get() = (value and 0b01000100) != 0
	val hasX get() = (value and 0b01000010) != 0
	val hasB get() = (value and 0b01000001) != 0

	val withW get() = REX(value or 0b01001000)
	val withR get() = REX(value or 0b01000100)
	val withX get() = REX(value or 0b01000010)
	val withB get() = REX(value or 0b01000001)

}