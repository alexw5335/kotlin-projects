package core.mem

/**
 * A resizable array of native types.
 */
class DirectList<T : DirectBuffer>(
	private val allocator: Allocator,
	initialCapacity: Int = 5,
	private val create: Allocator.(Int) -> T
) : Addressable {


	override val address get() = buffer.address

	var size = 0

	var buffer = allocator.create(initialCapacity)

	val isEmpty get() = size == 0

	val isNotEmpty get() = size > 0



	fun reset() {
		size = 0
	}

	fun clear() {
		size = 0
		Unsafe.set(buffer.address, buffer.byteSize, 0)
	}

	val next: Int get() {
		ensureCapacity()
		return size++
	}

	private fun ensureCapacity() {
		if(size < buffer.capacity) return
		val previous = buffer
		buffer = allocator.create(size * 2)
		Unsafe.copy(previous.address, buffer.address, buffer.byteSize.toLong())
	}


}