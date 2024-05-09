package core.binary

import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class BinaryReader(val bytes: ByteArray, var endianness: Endianness = LittleEndian) {


	constructor(path: Path) : this(Files.readAllBytes(path))

	constructor(path: String) : this(Paths.get(path))



	/*
	Position
	 */


	
	var pos = 0

	val atEos get() = pos <= bytes.size

	inline fun<T> retainPos(block: () -> T): T {
		val pos = this.pos
		val result = block()
		this.pos = pos
		return result
	}



	/*
	Byte
	 */



	fun bytes(length: Int) = bytes.copyOfRange(pos, pos + length).also { pos += length }

	fun s8() = bytes[pos++].toInt()

	fun u8() = bytes[pos++].toUByte().toInt()
	
	fun s8L() = bytes[pos++].toLong()

	fun u8L() = bytes[pos++].toUByte().toLong()



	/*
	Little-endian
	 */



	fun s16LE() = u8() or (s8() shl 8)

	fun u16LE() = u8() or (u8() shl 8)

	fun s32LE() = (u8() shl 0) or (u8() shl 8) or (u8() shl 16) or (s8() shl 24)

	fun u32LE() = (u8() shl 0) or (u8() shl 8) or (u8() shl 16) or (u8() shl 24)

	fun s64LE() = (u8L() shl 0) or (u8L() shl 8) or (u8L() shl 16) or (u8L() shl 24) or (u8L() shl 32) or (u8L() shl 40) or (u8L() shl 48) or (s8L() shl 56)

	fun u64LE() = (u8L() shl 0) or (u8L() shl 8) or (u8L() shl 16) or (u8L() shl 24) or (u8L() shl 32) or (u8L() shl 40) or (u8L() shl 48) or (u8L() shl 56)

	fun f32LE() = Float.fromBits(u32LE())

	fun f64LE() = Double.fromBits(u64LE())
	


	/*
	Big-endian
	 */



	fun s16BE() = (s8() shl 8) or u8()

	fun u16BE() = (u8() shl 8) or u8()
	
	fun s32BE() = (s8() shl 24) or (u8() shl 16) or (u8() shl 8) or u8()
	
	fun u32BE() = (u8() shl 24) or (u8() shl 16) or (u8() shl 8) or u8()
	
	fun s64BE() = (s8L() shl 56) or (u8L() shl 48) or (u8L() shl 40) or (u8L() shl 32) or (u8L() shl 24) or (u8L() shl 16) or (u8L() shl 8) or u8L()

	fun u64BE() = (u8L() shl 56) or (u8L() shl 48) or (u8L() shl 40) or (u8L() shl 32) or (u8L() shl 24) or (u8L() shl 16) or (u8L() shl 8) or u8L()

	fun f32BE() = Float.fromBits(u32BE())

	fun f64BE() = Double.fromBits(u64BE())



	/*
	Endianness
	 */



	fun s16() = endianness.s16(this)
	
	fun u16() = endianness.u16(this)
	
	fun s32() = endianness.s32(this)
	
	fun u32() = endianness.u32(this)
	
	fun s64() = endianness.s64(this)
	
	fun u64() = endianness.u64(this)
	
	fun f32() = endianness.f32(this)
	
	fun f64() = endianness.f64(this)

	fun u32Long() = u32().toUInt().toLong()



	/*
	Strings
	 */



	fun string(length: Int, encoding: Charset) = encoding.decode(ByteBuffer.wrap(bytes(length))).toString()

	fun ascii(length: Int) = String(CharArray(length) { Char(u8()) }).trimEnd { it == 0.toChar() }

	fun ntLength(): Int {
		val pos = this.pos
		while(u8() != 0) Unit
		val count = this.pos - pos - 1
		this.pos = pos
		return count
	}

	fun ntLength64(): Int {
		val pos = this.pos
		while(u64() != 0L) Unit
		val count = (this.pos - pos - 1) / 8
		this.pos = pos
		return count
	}

	fun asciiNt() = ascii(ntLength()).also { pos++ }

	fun alignEven() { if(pos and 1 != 0) pos++ }


}