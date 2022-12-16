package core.collection

class IntList(array: IntArray) {

	constructor(initialCapacity: Int = 512) : this(IntArray(initialCapacity))

	var array = array; private set

	var pos = 0

	private fun ensureCapacity(index: Int) {
		if(index >= array.size)
			array = array.copyOf(index shl 2)
	}

	private fun ensureCapacity() {
		if(pos >= array.size)
			array = array.copyOf(pos shl 2)
	}

	fun add(value: Int) {
		ensureCapacity()
		array[pos++] = value
	}

	operator fun set(index: Int, value: Int) {
		ensureCapacity(index)
		array[pos] = value
	}

	operator fun get(index: Int) = array[index]

	operator fun plusAssign(value: Int) = add(value)

	operator fun contains(value: Int) = array.contains(value)

	inline fun forEach(block: (Int) -> Unit) {
		for(i in 0 until pos)
			block(array[i])
	}

}