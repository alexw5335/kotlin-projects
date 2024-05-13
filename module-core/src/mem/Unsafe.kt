package core.mem

import sun.misc.Unsafe

@Suppress("Unused", "MemberVisibilityCanBePrivate")
object Unsafe : Allocator {


	val instance: Unsafe = Unsafe::class.java.getDeclaredField("theUnsafe").let {
		it.isAccessible = true
		it.get(null) as Unsafe
	}



	// ALLOCATION



	override fun malloc(size: Int) = instance.allocateMemory(size.toLong())
	override fun calloc(size: Int) = instance.allocateMemory(size.toLong()).also { set(it, size, 0) }
	override fun realloc(address: Long, newSize: Int) = instance.reallocateMemory(address, newSize.toLong())
	override fun free(address: Long) = instance.freeMemory(address)



	// BULK SETTING



	fun set(addr: Long, size: Long, value: Byte) = instance.setMemory(addr, size, value)
	fun set(addr: Long, size: Int, value: Byte) = instance.setMemory(addr, size.toLong(), value)
	fun zero(addr: Long, size: Long) = instance.setMemory(addr, size, 0)
	fun zero(addr: Long, size: Int) = instance.setMemory(addr, size.toLong(), 0)
	
	
	
	// COPYING
	
	
	
	fun copy(src: Long, dst: Long, size: Long) = 
		instance.copyMemory(src, dst, size)
	
	fun copyArray(src: Long, dst: Any, size: Int) = 
		instance.copyMemory(null, src, dst, 16L, size.toLong())
	
	fun copyArray(src: Any, offset: Int, dst: Long, size: Int) = 
		instance.copyMemory(src, 16L + offset, null, dst, size.toLong())


	
	// READING



	fun getByte(address: Long)   = instance.getByte(address)
	fun getChar(address: Long)   = instance.getChar(address)
	fun getShort(address: Long)  = instance.getShort(address)
	fun getInt(address: Long)    = instance.getInt(address)
	fun getFloat(address: Long)  = instance.getFloat(address)
	fun getLong(address: Long)   = instance.getLong(address)
	fun getDouble(address: Long) = instance.getDouble(address)



	// WRITING



	fun setByte(address: Long, value: Byte)     = instance.putByte(address, value)
	fun setChar(address: Long, value: Char)     = instance.putChar(address, value)
	fun setShort(address: Long, value: Short)   = instance.putShort(address, value)
	fun setInt(address: Long, value: Int)       = instance.putInt(address, value)
	fun setFloat(address: Long, value: Float)   = instance.putFloat(address, value)
	fun setLong(address: Long, value: Long)     = instance.putLong(address, value)
	fun setDouble(address: Long, value: Double) = instance.putDouble(address, value)



	// ARRAY READING



	fun getBytes(src: Long, size: Int) = ByteArray(size).also { copyArray(src, it, size) }
	fun getChars(src: Long, size: Int) = CharArray(size).also { copyArray(src, it, size * 2) }
	fun getShorts(src: Long, size: Int) = ShortArray(size).also { copyArray(src, it, size * 2) }
	fun getInts(src: Long, size: Int) = IntArray(size).also { copyArray(src, it, size * 4) }
	fun getLongs(src: Long, size: Int) = LongArray(size).also { copyArray(src, it, size * 8) }
	fun getFloats(src: Long, size: Int) = FloatArray(size).also { copyArray(src, it, size * 4) }
	fun getDoubles(src: Long, size: Int) = DoubleArray(size).also { copyArray(src, it, size * 8) }



	// ARRAY WRITING



	fun setBytes  (dst: Long, src: ByteArray,   offset: Int, length: Int) = copyArray(src, offset, dst, length * 1)
	fun setChars  (dst: Long, src: CharArray,   offset: Int, length: Int) = copyArray(src, offset, dst, length * 2)
	fun setShorts (dst: Long, src: ShortArray,  offset: Int, length: Int) = copyArray(src, offset, dst, length * 2)
	fun setInts   (dst: Long, src: IntArray,    offset: Int, length: Int) = copyArray(src, offset, dst, length * 4)
	fun setLongs  (dst: Long, src: LongArray,   offset: Int, length: Int) = copyArray(src, offset, dst, length * 8)
	fun setFloats (dst: Long, src: FloatArray,  offset: Int, length: Int) = copyArray(src, offset, dst, length * 4)
	fun setDoubles(dst: Long, src: DoubleArray, offset: Int, length: Int) = copyArray(src, offset, dst, length * 8)

	fun setBytes  (dst: Long, src: ByteArray)   = copyArray(src, 0, dst, src.size * 2)
	fun setChars  (dst: Long, src: CharArray)   = copyArray(src, 0, dst, src.size * 2)
	fun setShorts (dst: Long, src: ShortArray)  = copyArray(src, 0, dst, src.size * 2)
	fun setInts   (dst: Long, src: IntArray)    = copyArray(src, 0, dst, src.size * 4)
	fun setLongs  (dst: Long, src: LongArray)   = copyArray(src, 0, dst, src.size * 8)
	fun setFloats (dst: Long, src: FloatArray)  = copyArray(src, 0, dst, src.size * 4)
	fun setDoubles(dst: Long, src: DoubleArray) = copyArray(src, 0, dst, src.size * 8)



	// DECODING




	fun strlen(address: Long, maxLength: Int = Integer.MAX_VALUE): Int {
		for(i in 0 ..< maxLength)
			if(getByte(address + i) == 0.toByte())
				return i
		return maxLength
	}

	fun wstrlen(address: Long, maxLength: Int = Integer.MAX_VALUE): Int {
		for(i in 0 ..< maxLength)
			if(getShort(address + i) == 0.toShort())
				return i
		return maxLength
	}

	fun decodeAscii(address: Long, length: Int) =
		String(getBytes(address, length), Charsets.US_ASCII)

	fun decodeUtf8(address: Long, length: Int)  =
		String(getBytes(address, length), Charsets.UTF_8)

	fun decodeUtf16(address: Long, length: Int) =
		String(getBytes(address, length), Charsets.UTF_16LE)

	fun decodeAsciiNT(address: Long, maxLength: Int = Int.MAX_VALUE) =
		String(getBytes(address, strlen(address, maxLength)), Charsets.US_ASCII)

	fun decodeUtf8NT(address: Long, maxLength: Int = Int.MAX_VALUE) =
		String(getBytes(address, strlen(address, maxLength)), Charsets.UTF_8)

	fun decodeUtf16NT(address: Long, maxLength: Int = Int.MAX_VALUE) =
		String(getBytes(address, wstrlen(address, maxLength)), Charsets.UTF_16BE)


}