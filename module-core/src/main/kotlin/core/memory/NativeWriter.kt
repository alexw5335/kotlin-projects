package core.memory

import core.swapEndian16
import core.swapEndian32
import core.swapEndian64
import java.nio.charset.Charset

@Suppress("Unused")
class NativeWriter {


	var bytes = ByteArray(8192); private set

	var pos = 0

	fun trimmedBytes() = bytes.copyOf(pos)



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



	fun i8(value: Int) {
		ensureCapacity(1)
		Unsafe.instance.putByte(bytes, pos + 16L, value.toByte())
	}

	fun i16LE(value: Int) {
		ensureCapacity(2)
		Unsafe.instance.putShort(bytes, pos + 16L, value.toShort())
	}

	fun i32LE(value: Int) {
		ensureCapacity(4)
		Unsafe.instance.putInt(bytes, pos + 16L, value)
	}

	fun i64LE(value: Long) {
		ensureCapacity(8)
		Unsafe.instance.putLong(bytes, pos + 16L, value)
	}

	fun i16BE(value: Int) {
		ensureCapacity(2)
		Unsafe.instance.putShort(bytes, pos + 16L, value.swapEndian16.toShort())
	}

	fun i32BE(value: Int) {
		ensureCapacity(4)
		Unsafe.instance.putInt(bytes, pos + 16L, value.swapEndian32)
	}

	fun i64BE(value: Long) {
		ensureCapacity(8)
		Unsafe.instance.putLong(bytes, pos + 16L, value.swapEndian64)
	}



	fun f32LE(value: Float) {
		ensureCapacity(4)
		Unsafe.instance.putFloat(bytes, pos + 16L, value)
	}

	fun f64LE(value: Double) {
		ensureCapacity(8)
		Unsafe.instance.putDouble(bytes, pos + 16L, value)
	}

	fun f32BE(value: Float) {
		ensureCapacity(4)
		Unsafe.instance.putInt(bytes, pos + 16L, value.toRawBits().swapEndian32)
	}

	fun f64BE(value: Double) {
		ensureCapacity(8)
		Unsafe.instance.putLong(bytes, pos + 16L, value.toRawBits().swapEndian64)
	}



	fun bytes(bytes: ByteArray) {
		ensureCapacity(bytes.size)
		Unsafe.instance.copyMemory(bytes, 16L, this.bytes, pos + 16L, bytes.size.toLong())
	}



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