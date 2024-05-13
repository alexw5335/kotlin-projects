package core.mem

interface Allocator : AllocatorBase {
	fun realloc(address: Long, newSize: Int): Long
	fun free(address: Long)
	fun free(addressable: Addressable) = free(addressable.address)
}
