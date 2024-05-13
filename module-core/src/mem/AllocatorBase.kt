package core.mem

import java.nio.charset.Charset


interface AllocatorBase {

	fun malloc(size: Int): Long
	fun calloc(size: Int): Long

	fun mallocByte() = BytePtr(malloc(1))
	fun mallocShort() = ShortPtr(malloc(2))
	fun mallocInt() = IntPtr(malloc(4))
	fun mallocLong() = LongPtr(malloc(8))
	fun mallocFloat() = FloatPtr(malloc(4))
	fun mallocDouble() = DoublePtr(malloc(8))

	fun mallocByte(count: Int) = BytePtr(malloc(count * 1))
	fun mallocShort(count: Int) = ShortPtr(malloc(count * 2))
	fun mallocInt(count: Int) = IntPtr(malloc(count * 4))
	fun mallocLong(count: Int) = LongPtr(malloc(count * 8))
	fun mallocFloat(count: Int) = FloatPtr(malloc(count * 4))
	fun mallocDouble(count: Int) = DoublePtr(malloc(count * 8))

	fun wrapByte(value: Int) = mallocByte().also { it.value = value }
	fun wrapShort(value: Int) = mallocShort().also { it.value = value }
	fun wrapInt(value: Int) = mallocInt().also { it.value = value }
	fun wrapFloat(value: Float) = mallocFloat().also { it.value = value }
	fun wrapLong(value: Long) = mallocLong().also { it.value = value }
	fun wrapDouble(value: Double) = mallocDouble().also { it.value = value }

	fun wrapBytes(array: ByteArray) = mallocByte(array.size).also { Unsafe.setBytes(it.address, array) }
	fun wrapShorts(array: ShortArray) = mallocShort(array.size).also { Unsafe.setShorts(it.address, array) }
	fun wrapInts(array: IntArray) = mallocInt(array.size).also { Unsafe.setInts(it.address, array) }
	fun wrapFloats(array: FloatArray) = mallocFloat(array.size).also { Unsafe.setFloats(it.address, array) }
	fun wrapLongs(array: LongArray) = mallocLong(array.size).also { Unsafe.setLongs(it.address, array) }
	fun wrapDoubles(array: DoubleArray) = mallocDouble(array.size).also { Unsafe.setDoubles(it.address, array) }

	fun wrapPtr(value: Addressable) = mallocLong().also { it.value = value.address }
	fun<T: Addressable> wrapPointers(list: List<T>) = mallocLong(list.size).also {
		for(i in list.indices)
			it[i] = list[i].address
	}

	fun encodeString(string: String, charset: Charset): BytePtr {
		val bytes = string.toByteArray(charset)
		val buffer = mallocByte(bytes.size + 4)
		Unsafe.setBytes(buffer.address, bytes)
		buffer.setInt(bytes.size, 0)
		return buffer
	}

	fun encodeStringList(strings: Collection<String>, charset: Charset): LongPtr {
		val buffer = mallocLong(strings.size)
		for((i, s) in strings.withIndex())
			buffer[i] = encodeString(s, charset).address
		return buffer
	}

	fun encodeAscii(string: String) = encodeString(string, Charsets.US_ASCII)
	fun encodeUtf8(string: String) = encodeString(string, Charsets.UTF_8)
	fun encodeUtf16(string: String) = encodeString(string, Charsets.UTF_16LE)
	fun encodeUtf32(string: String) = encodeString(string, Charsets.UTF_32)

	fun encodeAsciiList(strings: Collection<String>) = encodeStringList(strings, Charsets.US_ASCII)
	fun encodeUtf8List(strings: Collection<String>) = encodeStringList(strings, Charsets.UTF_8)
	fun encodeUtf16List(strings: Collection<String>) = encodeStringList(strings, Charsets.UTF_16LE)
	fun encodeUtf32List(strings: Collection<String>) = encodeStringList(strings, Charsets.UTF_32LE)

}
