package assembler

class NewlineList(initialCapacity: Int = 20){


	private var bitFields = IntArray(initialCapacity)



	fun ensureFieldCapacity(fieldIndex: Int) {
		if(fieldIndex < bitFields.size) return
		bitFields = bitFields.copyOf(fieldIndex * 2)
	}



	fun ensureBitCapacity(bitIndex: Int) {
		ensureFieldCapacity(bitIndex shr 5)
	}



	fun set(index: Int) {
		val fieldIndex = index shr 5
		ensureFieldCapacity(fieldIndex)
		val bitIndex = index and 31
		bitFields[fieldIndex] = bitFields[fieldIndex] or (1 shl bitIndex)
	}



	fun clear(index: Int) {
		val fieldIndex = index shr 5
		ensureFieldCapacity(fieldIndex)
		val bitIndex = index and 31
		bitFields[fieldIndex] = bitFields[fieldIndex] and (1 shl bitIndex).inv()
	}



	operator fun get(index: Int): Boolean {
		val field = bitFields[index shr 5]
		val bitIndex = index and 31
		return field and (1 shl bitIndex) != 0
	}


}