package core.mem



@JvmInline
value class BytePtr(override val address: Long) : Addressable {

	companion object {
		private const val SIZE = 4
		fun get(addr: Long) = Unsafe.getByte(addr).toInt()
		fun set(addr: Long, value: Int) = Unsafe.setByte(addr, value.toByte())
	}

	var value get() = get(address); set(value) = set(address, value)
	fun set(value: Int) = set(address, value)
	operator fun set(index: Int, value: Int) = set(address + index * SIZE, value)
	operator fun get(index: Int) = get(address + index * SIZE)

	fun getShort(index: Int) = Unsafe.getShort(address + index).toInt()
	fun getInt(index: Int) = Unsafe.getInt(address + index)
	fun getLong(index: Int) = Unsafe.getLong(address + index)
	fun getFloat(index: Int) = Unsafe.getFloat(address + index)
	fun getDouble(index: Int) = Unsafe.getDouble(address + index)

	fun setShort(index: Int, value: Int) = Unsafe.setShort(address + index, value.toShort())
	fun setInt(index: Int, value: Int) = Unsafe.setInt(address + index, value)
	fun setLong(index: Int, value: Long) = Unsafe.setLong(address + index, value)
	fun setFloat(index: Int, value: Float) = Unsafe.setFloat(address + index, value)
	fun setDouble(index: Int, value: Double) = Unsafe.setDouble(address + index, value)

	fun getShorts(index: Int, size: Int) = Unsafe.getShorts(address + index, size)
	fun getInts(index: Int, size: Int) = Unsafe.getInts(address + index, size)
	fun getFloats(index: Int, size: Int) = Unsafe.getFloats(address + index, size)
	fun getLongs(index: Int, size: Int) = Unsafe.getLongs(address + index, size)
	fun getDoubles(index: Int, size: Int) = Unsafe.getDoubles(address + index, size)

	fun setShorts(index: Int, value: ShortArray) = Unsafe.setShorts(address + index, value)
	fun setInts(index: Int, value: IntArray) = Unsafe.setInts(address + index, value)
	fun setFloats(index: Int, value: FloatArray) = Unsafe.setFloats(address + index, value)
	fun setLongs(index: Int, value: LongArray) = Unsafe.setLongs(address + index, value)
	fun setDoubles(index: Int, value: DoubleArray) = Unsafe.setDoubles(address + index, value)

	fun decodeAscii() = Unsafe.decodeAsciiNT(address)
	fun decodeUtf8() = Unsafe.decodeUtf8NT(address)

}



@JvmInline
value class ShortPtr(override val address: Long) : Addressable {

	companion object {
		private const val SIZE = 4
		fun get(addr: Long) = Unsafe.getShort(addr).toInt()
		fun set(addr: Long, value: Int) = Unsafe.setShort(addr, value.toShort())
	}

	var value get() = get(address); set(value) = set(address, value)
	operator fun set(index: Int, value: Int) = set(address + index * SIZE, value)
	operator fun get(index: Int) = get(address + index * SIZE)

}



@JvmInline
value class IntPtr(override val address: Long) : Addressable {

	companion object {
		private const val SIZE = 4
		fun get(addr: Long) = Unsafe.getInt(addr)
		fun set(addr: Long, value: Int) = Unsafe.setInt(addr, value)
	}

	var value get() = get(address); set(value) = set(address, value)
	operator fun set(index: Int, value: Int) = set(address + index * SIZE, value)
	operator fun get(index: Int) = get(address + index * SIZE)

}



@JvmInline
value class LongPtr(override val address: Long) : Addressable {

	companion object {
		private const val SIZE = 8
		fun get(addr: Long) = Unsafe.getLong(addr)
		fun set(addr: Long, value: Long) = Unsafe.setLong(addr, value)
	}

	var value get() = get(address); set(value) = set(address, value)
	operator fun set(index: Int, value: Long) = set(address + index * SIZE, value)
	operator fun get(index: Int) = get(address + index * SIZE)

}



@JvmInline
value class FloatPtr(override val address: Long) : Addressable {

	companion object {
		private const val SIZE = 4
		fun get(addr: Long) = Unsafe.getFloat(addr)
		fun set(addr: Long, value: Float) = Unsafe.setFloat(addr, value)
	}

	var value get() = get(address); set(value) = set(address, value)
	operator fun set(index: Int, value: Float) = set(address + index * SIZE, value)
	operator fun get(index: Int) = get(address + index * SIZE)

}



@JvmInline
value class DoublePtr(override val address: Long) : Addressable {

	companion object {
		private const val SIZE = 8
		fun get(addr: Long) = Unsafe.getDouble(addr)
		fun set(addr: Long, value: Double) = Unsafe.setDouble(addr, value)
	}

	var value get() = get(address); set(value) = set(address, value)
	operator fun set(index: Int, value: Double) = set(address + index * SIZE, value)
	operator fun get(index: Int) = get(address + index * SIZE)

}