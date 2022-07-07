package assembler

class NewlineList {


	private var bitFields = IntArray(20)



	fun ensureCapacity(fieldIndex: Int) {
		if(fieldIndex < bitFields.size) return
		bitFields = bitFields.copyOf(fieldIndex * 2)
	}



	fun set(index: Int) {
		val fieldIndex = index shr 5
		ensureCapacity(fieldIndex)
		val bitIndex = (index - 31) and -32
		bitFields[fieldIndex] = bitFields[fieldIndex] and (1 shl bitIndex)
	}



	operator fun get(index: Int): Boolean {
		val field = bitFields[index shr 5]
		val bitIndex = (index - 31) and -32
		return field and (1 shl bitIndex) != 0
	}


}