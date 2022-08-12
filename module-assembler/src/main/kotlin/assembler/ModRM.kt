package assembler

@JvmInline
value class ModRM(val value: Int) {

	val mod get() = (value and 0b11_000_000) shr 6
	val reg get() = (value and 0b00_111_000) shr 3
	val rm get()  = (value and 0b00_000_111) shr 0

	fun withMod(mod: Int) = ModRM(value or (mod shl 6))
	fun withReg(reg: Int) = ModRM(value or (reg shl 3))
	fun withRm(rm: Int) = ModRM(value or (rm shl 0))

}