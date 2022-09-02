package core.binary

import java.nio.charset.Charset
import java.util.*

class BinaryWriter(var bytes: ByteArray, var endianness: Endianness) {


	constructor(bytes: ByteArray) : this(bytes, LittleEndian)

	constructor(initialSize: Int) : this(ByteArray(initialSize))

	constructor() : this(8192)



	var pos = 0



	private fun ensureCapacity() {
		if(pos >= bytes.size)
			bytes = bytes.copyOf(pos * 2)
	}



	private fun ensureCapacity(length: Int) {
		if(pos + length - 1 >= bytes.size)
			bytes = bytes.copyOf((pos + length - 1) * 2)
	}



	fun trimmedBytes() = bytes.copyOf(pos)



	/*
	Byte
	 */



	fun s8(value: Int) {
		ensureCapacity()
		bytes[pos++] = value.toByte()
	}

	fun s8(pos: Int, value: Int) {
		bytes[pos] = value.toByte()
	}

	fun u8(value: Int) {
		ensureCapacity()
		bytes[pos++] = value.toUByte().toByte()
	}

	fun u8(pos: Int, value: Int) {
		bytes[pos] = value.toUByte().toByte()
	}



	/*
	Byte array
	 */



	fun bytes(bytes: ByteArray, offset: Int, length: Int) {
		ensureCapacity(length)
		System.arraycopy(bytes, offset, this.bytes, pos, length)
		pos += length
	}

	fun bytes(bytes: ByteArray) = bytes(bytes, 0, bytes.size)

	fun bytes(pos: Int, bytes: ByteArray, offset: Int, length: Int) {
		System.arraycopy(bytes, offset, this.bytes, pos, length)
	}

	fun bytes(pos: Int, bytes: ByteArray) = bytes(pos, bytes, 0, bytes.size)



	/*
	Little-endian 16-bit integers
	 */



	fun s16LE(pos: Int, value: Int) {
		bytes[pos + 0] = value.toUByte().toByte()
		bytes[pos + 1] = (value shr 8).toByte()
	}

	fun s16LE(value: Int) {
		ensureCapacity(2)
		s16LE(pos, value)
		pos += 2
	}

	fun u16LE(pos: Int, value: Int) {
		bytes[pos + 0] = value.toUByte().toByte()
		bytes[pos + 1] = (value shr 8).toUByte().toByte()
	}

	fun u16LE(value: Int) {
		ensureCapacity(2)
		u16LE(pos, value)
		pos += 2
	}


	/*
	Little-endian 32-bit integers
	 */


	fun s32LE(pos: Int, value: Int) {
		bytes[pos + 0] = value.toUByte().toByte()
		bytes[pos + 1] = (value shr 8).toUByte().toByte()
		bytes[pos + 2] = (value shr 16).toUByte().toByte()
		bytes[pos + 3] = (value shr 24).toByte()
	}

	fun s32LE(value: Int) {
		ensureCapacity(4)
		s32LE(pos, value)
		pos += 4
	}

	fun u32LE(pos: Int, value: Int) {
		bytes[pos + 0] = value.toUByte().toByte()
		bytes[pos + 1] = (value shr 8).toUByte().toByte()
		bytes[pos + 2] = (value shr 16).toUByte().toByte()
		bytes[pos + 3] = (value shr 24).toUByte().toByte()
	}

	fun u32LE(value: Int) {
		ensureCapacity(4)
		u32LE(pos, value)
		pos += 4
	}



	/*
	Little-endian 64-bit integers
	 */



	fun s64LE(pos: Int, value: Long) {
		bytes[pos + 0] = value.toUByte().toByte()
		bytes[pos + 1] = (value shr 8).toUByte().toByte()
		bytes[pos + 2] = (value shr 16).toUByte().toByte()
		bytes[pos + 3] = (value shr 24).toUByte().toByte()
		bytes[pos + 4] = (value shr 32).toUByte().toByte()
		bytes[pos + 5] = (value shr 40).toUByte().toByte()
		bytes[pos + 6] = (value shr 48).toUByte().toByte()
		bytes[pos + 7] = (value shr 56).toByte()
	}

	fun s64LE(value: Long) {
		ensureCapacity(8)
		s64LE(pos, value)
		pos += 8
	}

	fun u64LE(pos: Int, value: Long) {
		ensureCapacity(8)
		bytes[pos + 0] = value.toUByte().toByte()
		bytes[pos + 1] = (value shr 8).toUByte().toByte()
		bytes[pos + 2] = (value shr 16).toUByte().toByte()
		bytes[pos + 3] = (value shr 24).toUByte().toByte()
		bytes[pos + 4] = (value shr 32).toUByte().toByte()
		bytes[pos + 5] = (value shr 40).toUByte().toByte()
		bytes[pos + 6] = (value shr 48).toUByte().toByte()
		bytes[pos + 7] = (value shr 56).toUByte().toByte()
	}

	fun u64LE(value: Long) {
		ensureCapacity(8)
		u64LE(pos, value)
		pos += 8
	}



	/*
	Little-endian floats
	 */



	fun f32LE(pos: Int, value: Float) = u32LE(pos, value.toBits())

	fun f32LE(value: Float) = u32LE(value.toBits())

	fun f64LE(pos: Int, value: Double) = u64LE(pos, value.toBits())

	fun f64LE(value: Double) = u64LE(value.toBits())



	/*
	Big-Endian 16-bit integers
	 */



	fun s16BE(pos: Int, value: Int) {
		bytes[pos + 1] = value.toUByte().toByte()
		bytes[pos + 0] = (value shr 8).toByte()
	}

	fun s16BE(value: Int) {
		ensureCapacity(2)
		s16BE(pos, value)
		pos += 2
	}

	fun u16BE(pos: Int, value: Int) {
		bytes[pos + 1] = value.toUByte().toByte()
		bytes[pos + 0] = (value shr 8).toUByte().toByte()
	}

	fun u16BE(value: Int) {
		ensureCapacity(2)
		u16BE(pos, value)
		pos += 2
	}



	/*
	Big-Endian 32-bit integers
	 */



	fun s32BE(pos: Int, value: Int) {
		bytes[pos + 3] = value.toUByte().toByte()
		bytes[pos + 2] = (value shr 8).toUByte().toByte()
		bytes[pos + 1] = (value shr 16).toUByte().toByte()
		bytes[pos + 0] = (value shr 24).toByte()
	}

	fun s32BE(value: Int) {
		ensureCapacity(4)
		s32BE(pos, value)
		pos += 4
	}

	fun u32BE(pos: Int, value: Int) {
		bytes[pos + 3] = value.toUByte().toByte()
		bytes[pos + 2] = (value shr 8).toUByte().toByte()
		bytes[pos + 1] = (value shr 16).toUByte().toByte()
		bytes[pos + 0] = (value shr 24).toUByte().toByte()
	}

	fun u32BE(value: Int) {
		ensureCapacity(4)
		u32BE(pos, value)
		pos += 4
	}



	/*
	Big-Endian 64-bit integers
	 */



	fun s64BE(pos: Int, value: Long) {
		bytes[pos + 7] = value.toUByte().toByte()
		bytes[pos + 6] = (value shr 8).toUByte().toByte()
		bytes[pos + 5] = (value shr 16).toUByte().toByte()
		bytes[pos + 4] = (value shr 24).toUByte().toByte()
		bytes[pos + 3] = (value shr 32).toUByte().toByte()
		bytes[pos + 2] = (value shr 40).toUByte().toByte()
		bytes[pos + 1] = (value shr 48).toUByte().toByte()
		bytes[pos + 0] = (value shr 56).toByte()
	}

	fun s64BE(value: Long) {
		ensureCapacity(8)
		s64BE(pos, value)
		pos += 8
	}

	fun u64BE(pos: Int, value: Long) {
		ensureCapacity(8)
		bytes[pos + 7] = value.toUByte().toByte()
		bytes[pos + 6] = (value shr 8).toUByte().toByte()
		bytes[pos + 5] = (value shr 16).toUByte().toByte()
		bytes[pos + 4] = (value shr 24).toUByte().toByte()
		bytes[pos + 3] = (value shr 32).toUByte().toByte()
		bytes[pos + 2] = (value shr 40).toUByte().toByte()
		bytes[pos + 1] = (value shr 48).toUByte().toByte()
		bytes[pos + 0] = (value shr 56).toUByte().toByte()
	}

	fun u64BE(value: Long) {
		ensureCapacity(8)
		u64BE(pos, value)
		pos += 8
	}



	/*
	Big-endian floats
	 */



	fun f32BE(pos: Int, value: Float) = u32BE(pos, value.toBits())

	fun f32BE(value: Float) = u32BE(value.toBits())

	fun f64BE(pos: Int, value: Double) = u64BE(pos, value.toBits())

	fun f64BE(value: Double) = u64BE(value.toBits())



	/*
	Endianness
	 */



	fun s16(value: Int) = endianness.s16(this, value)

	fun u16(value: Int) = endianness.u16(this, value)

	fun s32(value: Int) = endianness.s32(this, value)

	fun u32(value: Int) = endianness.u32(this, value)

	fun s64(value: Long) = endianness.s64(this, value)

	fun u64(value: Long) = endianness.u64(this, value)

	fun f32(value: Float) = endianness.f32(this, value)

	fun f64(value: Double) = endianness.f64(this, value)



	fun s16(pos: Int, value: Int) = endianness.s16(this, pos, value)

	fun u16(pos: Int, value: Int) = endianness.u16(this, pos, value)

	fun s32(pos: Int, value: Int) = endianness.s32(this, pos, value)

	fun u32(pos: Int, value: Int) = endianness.u32(this, pos, value)

	fun s64(pos: Int, value: Long) = endianness.s64(this, pos, value)

	fun u64(pos: Int, value: Long) = endianness.u64(this, pos, value)

	fun f32(pos: Int, value: Float) = endianness.f32(this, pos, value)

	fun f64(pos: Int, value: Double) = endianness.f64(this, pos, value)



	/*
	Strings
	 */



	fun stringu8(string: String) {
		ensureCapacity(string.length)
		for(i in string.indices)
			u8(pos++, string[i].code)
	}



	fun stringu16(string: String) {
		ensureCapacity(string.length * 2)
		for(i in string.indices) {
			u16(pos, string[i].code)
			pos += 2
		}
	}



	fun ascii(string: String) {
		ensureCapacity(string.length)
		for(i in string.indices)
			u8(pos++, string[i].code)
	}



	fun ascii8(pos: Int, string: String) {
		for(i in string.indices)
			u8(pos + i, string[i].code)
		for(i in 0 until 8 - string.length)
			u8(pos + string.length + i, 0)
	}

	fun ascii8(string: String) {
		ensureCapacity(8)
		ascii8(pos, string)
		pos += 8
	}



	fun fill(pos: Int, length: Int, value: Byte) {
		if(length <= 0) return
		Arrays.fill(bytes, pos, pos + length, value)
	}

	fun fill(length: Int, value: Byte) {
		if(length <= 0) return
		ensureCapacity(length)
		fill(pos, length, value)
		pos += length
	}

	fun fillTo(endPos: Int, value: Byte) = fill(endPos - pos, value)



	fun zero(pos: Int, length: Int) = fill(pos, length, 0)

	fun zero(length: Int) = fill(length, 0)

	fun zeroTo(endPos: Int) = fillTo(endPos, 0)



	fun alignEven() { if(pos and 1 != 0) u8(0) }


}