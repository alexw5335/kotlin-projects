package assembler.generator

class RegisterGenerator {


	private val unsorted = Register.values()

	private val sorted = unsorted.sortedBy { it.name.lowercase().ascii8 }

	private val byType = RegType.values().map { type -> unsorted.filter { it.type == type } }



	fun print() {
		println("\n\n\n;enum RegType (1 byte)")
		for((i, type) in RegType.values().withIndex()) {
			println("%define REGTYPE_$type $i")
		}

		println("\n\n\n;enum Register (2 bytes)")
		println("%define NUM_REGISTERS ${unsorted.size}")
		for(register in unsorted) {
			println("%define REGISTER_$register ${register.fullValue}")
		}

		println("\n\n\nregisterSearchTable:")
		for(register in sorted) {
			println("\tdq \"${register.name.lowercase()}\"")
		}

		println("\n\n\nregTypeNames:")
		for(type in RegType.values())
			println("\tdq \"$type\"")

		println("\n\n\nregisterValueTable:")
		for(register in sorted)
			println("\tdw ${register.fullValue}")

		// index into this table is 32 * register.type + register.value
		println("\n\n\nalign 32")
		println("registerReverseIndexTable:")
		for(regList in byType) {
			println("\talign 32")
			for(register in regList)
				println("\tdb ${sorted.indexOf(register)}")
		}
	}



	private val String.ascii8: Long get() {
		var value = 0L
		for(i in length - 1 downTo 0)
			value = value shl 8 or (this[i].code.toLong())
		return value
	}



	private enum class RegType {
		GP8,
		GP16,
		GP32,
		GP64;
	}

	
	
	private enum class Register(val value: Int, val type: RegType) {
		RAX(0, RegType.GP64),
		RCX(1, RegType.GP64),
		RDX(2, RegType.GP64),
		RBX(3, RegType.GP64),
		RSP(4, RegType.GP64),
		RBP(5, RegType.GP64),
		RSI(6, RegType.GP64),
		RDI(7, RegType.GP64),
		R8(8, RegType.GP64),
		R9(9, RegType.GP64),
		R10(10, RegType.GP64),
		R11(11, RegType.GP64),
		R12(12, RegType.GP64),
		R13(13, RegType.GP64),
		R14(14, RegType.GP64),
		R15(15, RegType.GP64),
		EAX(0, RegType.GP32),
		ECX(1, RegType.GP32),
		EDX(2, RegType.GP32),
		EBX(3, RegType.GP32),
		ESP(4, RegType.GP32),
		EBP(5, RegType.GP32),
		ESI(6, RegType.GP32),
		EDI(7, RegType.GP32),
		R8D(8, RegType.GP32),
		R9D(9, RegType.GP32),
		R10D(10, RegType.GP32),
		R11D(11, RegType.GP32),
		R12D(12, RegType.GP32),
		R13D(13, RegType.GP32),
		R14D(14, RegType.GP32),
		R15D(15, RegType.GP32),
		AX(0, RegType.GP16),
		CX(1, RegType.GP16),
		DX(2, RegType.GP16),
		BX(3, RegType.GP16),
		SP(4, RegType.GP16),
		BP(5, RegType.GP16),
		SI(6, RegType.GP16),
		DI(7, RegType.GP16),
		R8W(8, RegType.GP16),
		R9W(9, RegType.GP16),
		R10W(10, RegType.GP16),
		R11W(11, RegType.GP16),
		R12W(12, RegType.GP16),
		R13W(13, RegType.GP16),
		R14W(14, RegType.GP16),
		R15W(15, RegType.GP16),
		AL(0, RegType.GP8),
		CL(1, RegType.GP8),
		DL(2, RegType.GP8),
		BL(3, RegType.GP8),
		AH(4, RegType.GP8),
		CH(5, RegType.GP8),
		DH(6, RegType.GP8),
		BH(7, RegType.GP8),
		R8B(8, RegType.GP8),
		R9B(9, RegType.GP8),
		R10B(10, RegType.GP8),
		R11B(11, RegType.GP8),
		R12B(12, RegType.GP8),
		R13B(13, RegType.GP8),
		R14B(14, RegType.GP8),
		R15B(15, RegType.GP8);

		val fullValue = value or (type.ordinal shl 8)

	}
	
	
}