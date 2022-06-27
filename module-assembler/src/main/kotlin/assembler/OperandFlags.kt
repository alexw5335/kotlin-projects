package assembler

@JvmInline
value class OperandFlags(val value: Int) {


	val isR    get() = value and (1 shl 0) != 0
	val isMEM  get() = value and (1 shl 1) != 0
	val isR8   get() = value and (1 shl 2) != 0
	val isIMM  get() = value and (1 shl 3) != 0
	val isIMM8 get() = value and (1 shl 4) != 0
	val isAL   get() = value and (1 shl 5) != 0
	val isA    get() = value and (1 shl 6) != 0
	val isCL   get() = value and (1 shl 7) != 0


}