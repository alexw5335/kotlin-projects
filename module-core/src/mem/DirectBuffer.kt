package core.mem

/**
 * Represents a contiguous array that is stored in native memory.
 */
interface DirectBuffer : Addressable {


	/**
	 * The size in bytes of the element type that can be stored in this buffer.
	 */
	val elementSize: Int

	/**
	 * The number of elements that can be stored in this buffer.
	 */
	val capacity: Int

	/**
	 * The total number of bytes that can be stored in this buffer.
	 */
	val byteSize: Int get() = capacity * elementSize

	/**
	 * The memory address of the element at the given [index] in this buffer.
	 */
	fun offset(index: Int): Long = address + index * elementSize


}