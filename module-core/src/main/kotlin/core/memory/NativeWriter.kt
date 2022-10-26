package core.memory

import core.swapEndian16
import core.swapEndian32
import core.swapEndian64
import java.nio.charset.Charset

@Suppress("Unused", "MemberVisibilityCanBePrivate")
class NativeWriter {


	var bytes = ByteArray(8192); private set

	var pos = 0

	fun trimmedBytes() = bytes.copyOf(pos)



	/*
	Position
	 */



	inline fun retainPos(newPos: Int, block: () -> Unit) {
		val pos = pos
		this.pos = newPos
		block()
		this.pos = pos
	}



	fun ensureCapacity() {
		if(pos >= bytes.size)
			bytes = bytes.copyOf(pos shl 2)
	}



	fun ensureCapacity(count: Int) {
		if(pos + count > bytes.size)
			bytes = bytes.copyOf((pos + count) shl 2)
	}



	fun advance(count: Int) {
		pos += count
		if(pos > bytes.size)
			bytes = bytes.copyOf(pos shl 2)
	}



	/*
	Little-endian primitives
	 */



	fun i8(pos: Int, value: Int) {
		bytes[pos] = value.toByte()
	}

	fun i8(value: Int) {
		ensureCapacity(1)
		bytes[pos++] = value.toByte()
	}

	fun i16(pos: Int, value: Int) {
		Unsafe.instance.putShort(bytes, pos + 16L, value.toShort())
	}

	fun i16(value: Int) {
		ensureCapacity(2)
		Unsafe.instance.putShort(bytes, pos + 16L, value.toShort())
		pos += 2
	}

	fun i32(pos: Int, value: Int) {
		Unsafe.instance.putInt(bytes, pos + 16L, value)
	}

	fun i32(value: Int) {
		ensureCapacity(4)
		Unsafe.instance.putInt(bytes, pos + 16L, value)
		pos += 4
	}

	fun i64(pos: Int, value: Long) {
		Unsafe.instance.putLong(bytes, pos + 16L, value)
	}

	fun i64(value: Long) {
		ensureCapacity(8)
		Unsafe.instance.putLong(bytes, pos + 16L, value)
		pos += 8
	}

	fun f32(pos: Int, value: Float) {
		Unsafe.instance.putFloat(bytes, pos + 16L, value)
	}

	fun f32(value: Float) {
		ensureCapacity(4)
		Unsafe.instance.putFloat(bytes, pos + 16L, value)
		pos += 4
	}

	fun f64(pos: Int, value: Double) {
		Unsafe.instance.putDouble(bytes, pos + 16L, value)
	}

	fun f64(value: Double) {
		ensureCapacity(8)
		Unsafe.instance.putDouble(bytes, pos + 16L, value)
		pos += 8
	}



	/*
	Big-endian primitives
	 */



	fun i16BE(pos: Int, value: Int) {
		Unsafe.instance.putShort(bytes, pos + 16L, value.swapEndian16.toShort())
	}

	fun i16BE(value: Int) {
		ensureCapacity(2)
		Unsafe.instance.putShort(bytes, pos + 16L, value.swapEndian16.toShort())
		pos += 2
	}

	fun i32BE(pos: Int, value: Int) {
		Unsafe.instance.putInt(bytes, pos + 16L, value.swapEndian32)
	}

	fun i32BE(value: Int) {
		ensureCapacity(4)
		Unsafe.instance.putInt(bytes, pos + 16L, value.swapEndian32)
		pos += 4
	}

	fun i64BE(pos: Int, value: Long) {
		Unsafe.instance.putLong(bytes, pos + 16L, value.swapEndian64)
	}

	fun i64BE(value: Long) {
		ensureCapacity(8)
		Unsafe.instance.putLong(bytes, pos + 16L, value.swapEndian64)
		pos += 8
	}

	fun f32BE(pos: Int, value: Float) {
		Unsafe.instance.putInt(bytes, pos + 16L, value.toRawBits().swapEndian32)
	}

	fun f32BE(value: Float) {
		ensureCapacity(4)
		Unsafe.instance.putInt(bytes, pos + 16L, value.toRawBits().swapEndian32)
		pos += 4
	}

	fun f64BE(pos: Int, value: Double) {
		Unsafe.instance.putLong(bytes, pos + 16L, value.toRawBits().swapEndian64)
	}

	fun f64BE(value: Double) {
		ensureCapacity(8)
		Unsafe.instance.putLong(bytes, pos + 16L, value.toRawBits().swapEndian64)
		pos += 8
	}



	/*
	Arrays
	 */



	fun bytes(pos: Int, array: ByteArray, srcPos: Int = 0, length: Int = array.size) {
		System.arraycopy(array, srcPos, bytes, pos, length)
	}

	fun bytes(array: ByteArray, srcPos: Int = 0, length: Int = array.size) {
		ensureCapacity(length)
		System.arraycopy(array, srcPos, bytes, pos, length)
		pos += length
	}

	fun ints(pos: Int, array: IntArray, srcPos: Int = 0, length: Int = array.size) {
		Unsafe.instance.copyMemory(array, 16L + srcPos, bytes, 16L + pos, length.toLong())
	}

	fun ints(array: IntArray, srcPos: Int = 0, length: Int = array.size) {
		ensureCapacity(length * 4)
		Unsafe.instance.copyMemory(array, 16L + srcPos, bytes, 16L + pos, length.toLong())
		pos += length * 4
	}



	/*
	Misc
	 */



	fun string(string: String, charset: Charset) {
		bytes(charset.encode(string).array())
	}



	fun asciiNT(string: String) {
		for(c in string)
			i8(c.code)
		i8(0)
	}



	fun set(count: Int, value: Int) {
		ensureCapacity(count)
		Unsafe.instance.setMemory(bytes, pos + 16L, count.toLong(), value.toByte())
	}



	fun zero(count: Int) {
		ensureCapacity(count)
		Unsafe.instance.setMemory(bytes, pos + 16L, count.toLong(), 0)
	}


}