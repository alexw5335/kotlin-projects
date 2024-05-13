package core.mem

open class LinearAllocator(val address: Long, val size: Long) : AllocatorBase {

	var pointer = address
	val maxAddress = address + size
	val bytesUsed get() = pointer - address
	val bytesAvailable get() = size - pointer + address
	val usage get() = bytesUsed.toFloat() / size.toFloat() * 100F

	override fun malloc(size: Int): Long {
		val address = (pointer + 7) and -8
		pointer = address + size
		if(pointer > maxAddress)
			throw IllegalAccessException("Memory stack overflow for allocation of $size bytes")
		return address
	}

	override fun calloc(size: Int): Long {
		val address = malloc(size)
		Unsafe.set(address, size, 0)
		return address
	}

	fun reset() { pointer = address }

}