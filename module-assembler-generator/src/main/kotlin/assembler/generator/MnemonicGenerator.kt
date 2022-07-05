package assembler.generator

class MnemonicGenerator {


	private val unsorted = Mnemonic.values()

	private val sorted = unsorted.sortedBy { it.name.lowercase().ascii8 }

	private val indexTable = sorted.map(unsorted::indexOf)

	private val reverseIndexTable = unsorted.map(sorted::indexOf)



	fun print() {
		println("; enum Mnemonic (2 bytes)")
		println("%define NUM_MNEMONICS ${unsorted.size}")
		for((i, mnemonic) in unsorted.withIndex())
			println("%define MNEMONIC_$mnemonic $i")

		println("\n\n\nmnemonicSearchTable:")
		for(mnemonic in sorted)
			println("\tdq \"${mnemonic.name.lowercase()}\"")


		println("\n\n\nmnemonicIndexTable:")
		for(index in indexTable)
			println("\tdw $index")

		println("\n\n\nmnemonicReverseIndexTable:")
		for(reversedIndex in reverseIndexTable)
			println("\tdw $reversedIndex")
	}



	private val String.ascii8: Long get() {
		var value = 0L
		for(i in length - 1 downTo 0)
			value = value shl 8 or (this[i].code.toLong())
		return value
	}



	private enum class Mnemonic {
		ADD,
		OR,
		ADC,
		SBB,
		AND,
		SUB,
		XOR,
		CMP;
	}


}