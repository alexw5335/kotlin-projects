package core

class BitList(private var fields: IntArray) {


	constructor(initialCapacity: Int = 512) : this(IntArray(initialCapacity shr 5))



	fun ensureFieldCapacity(fieldIndex: Int) {
		if(fieldIndex < fields.size) return
		fields = fields.copyOf(fieldIndex * 2)
	}



	fun ensureBitCapacity(bitIndex: Int) {
		ensureFieldCapacity(bitIndex shr 5)
	}



	fun set(index: Int) {
		val fieldIndex = index shr 5
		ensureFieldCapacity(fieldIndex)
		val bitIndex = index and 31
		fields[fieldIndex] = fields[fieldIndex] or (1 shl bitIndex)
	}



	fun clear(index: Int) {
		val fieldIndex = index shr 5
		ensureFieldCapacity(fieldIndex)
		val bitIndex = index and 31
		fields[fieldIndex] = fields[fieldIndex] and (1 shl bitIndex).inv()
	}



	operator fun get(index: Int): Boolean {
		val field = fields[index shr 5]
		val bitIndex = index and 31
		return field and (1 shl bitIndex) != 0
	}


}