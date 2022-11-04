package core.collection

class ByteList(private var array: ByteArray) {


	constructor(initialCapacity: Int = 512) : this(ByteArray(initialCapacity))



	var pos = 0



	fun ensureCapacity(capacity: Int) {
		if(capacity > array.size) array = array.copyOf(capacity shl 2)
	}



	private fun ensureCapacity() {
		if(pos >= array.size) array = array.copyOf(pos shl 2)
	}



	fun add(value: Int) {
		ensureCapacity()
		array[pos++] = value.toByte()
	}



	operator fun get(index: Int) = array[index].toInt()


}