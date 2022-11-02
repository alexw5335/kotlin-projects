package core

class IntList(private var array: IntArray) {


	constructor(initialCapacity: Int = 512) : this(IntArray(initialCapacity))



	var pos = 0



	fun ensureCapacity(capacity: Int) {
		if(capacity > array.size) array = array.copyOf(capacity shl 2)
	}



	private fun ensureCapacity() {
		if(pos >= array.size) array = array.copyOf(pos shl 2)
	}



	fun add(value: Int) {
		ensureCapacity()
		array[pos++] = value
	}



	operator fun get(index: Int) = array[index]


}