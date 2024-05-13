package core.mem

import core.swapEndian
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.math.max
import kotlin.math.min

@Suppress("Unused", "MemberVisibilityCanBePrivate")
class BinWriter(bytes: ByteArray) {


	constructor(initialSize: Int = 8192) : this(ByteArray(initialSize))



	companion object {
		fun write(path: String, block: (BinWriter) -> Unit) {
			val writer = BinWriter()
			block(writer)
			Files.write(Paths.get(path), writer.copy())
		}
	}



	var bytes = bytes; private set

	var pos = 0

	val isEmpty get() = pos == 0

	val isNotEmpty get() = pos > 0

	fun copy() = bytes.copyOf(pos)

	fun copy(count: Int) = bytes.copyOf(count)

	fun copy(pos: Int, count: Int) = bytes.copyOfRange(pos, pos + count)



	// POSITION



	fun reset() {
		pos = 0
	}
	
	fun clear() {
		pos = 0
		Arrays.fill(bytes, 0)
	}
	
	inline fun at(newPos: Int, block: () -> Unit) {
		val pos = pos
		this.pos = newPos
		block()
		this.pos = pos
	}
	
	fun ensureCapacity() {
		if(pos >= bytes.size)
			bytes = bytes.copyOf(pos shl 2)
	}

	fun ensureCapacityAt(pos: Int) {
		if(pos > bytes.size)
			bytes = bytes.copyOf(pos shl 2)
	}
	
	fun ensureCapacity(count: Int) {
		if(pos + count > bytes.size)
			bytes = bytes.copyOf((pos + count) shl 2)
	}

	inline fun ensure(size: Int, inc: Int, block: () -> Unit) {
		ensureCapacity(size)
		block()
		pos += inc
	}

	fun align2() = if(pos and 1 != 0) i8(0) else Unit

	fun align(alignment: Int) {
		pos = (pos + alignment - 1) and -alignment
		ensureCapacity()
	}
	
	fun varLengthInt(value: Int) {
		ensureCapacity(4)
		Unsafe.instance.putInt(bytes, pos + 16L, value)
		pos += ((39 - (value or 1).countLeadingZeroBits()) and -8) shr 3
	}
	
	fun seek(pos: Int) {
		this.pos = pos
		ensureCapacity()
	}



	// LITTLE ENDIAN



	fun i8(value: Int)     = ensure(1, 1) { bytes[pos++] = value.toByte() }
	fun i16(value: Int)    = ensure(2, 2) { Unsafe.instance.putShort(bytes, pos + 16L, value.toShort()) }
	fun i24(value: Int)    = ensure(4, 3) { Unsafe.instance.putInt(bytes, pos + 16L, value) }
	fun i32(value: Int)    = ensure(4, 4) { Unsafe.instance.putInt(bytes, pos + 16L, value) }
	fun i40(value: Long)   = ensure(8, 5) { Unsafe.instance.putLong(bytes, pos + 16L, value) }
	fun i48(value: Long)   = ensure(8, 6) { Unsafe.instance.putLong(bytes, pos + 16L, value) }
	fun i56(value: Long)   = ensure(8, 7) { Unsafe.instance.putLong(bytes, pos + 16L, value) }
	fun i64(value: Long)   = ensure(8, 8) { Unsafe.instance.putLong(bytes, pos + 16L, value) }
	fun f32(value: Float)  = ensure(4, 4) { Unsafe.instance.putFloat(bytes, pos + 16L, value) }
	fun f64(value: Double) = ensure(8, 8) { Unsafe.instance.putDouble(bytes, pos + 16L, value) }

	fun i8(pos: Int, value: Int)     = bytes.set(pos, value.toByte())
	fun i16(pos: Int, value: Int)    = Unsafe.instance.putShort(bytes, pos + 16L, value.toShort())
	fun i32(pos: Int, value: Int)    = Unsafe.instance.putInt(bytes, pos + 16L, value)
	fun i64(pos: Int, value: Long)   = Unsafe.instance.putLong(bytes, pos + 16L, value)
	fun f32(pos: Int, value: Float)  = Unsafe.instance.putFloat(bytes, pos + 16L, value)
	fun f64(pos: Int, value: Double) = Unsafe.instance.putDouble(bytes, pos + 16L, value)



	// BIG ENDIAN



	fun i16BE(value: Int) = ensure(2, 2) { Unsafe.instance.putShort(bytes, pos + 16L, value.toShort().swapEndian) }
	fun i32BE(value: Int) = ensure(4, 4) { Unsafe.instance.putInt(bytes, pos + 16L, value.swapEndian) }
	fun i64BE(value: Long) = ensure(8, 8) { Unsafe.instance.putLong(bytes, pos + 16L, value.swapEndian) }
	fun f32BE(value: Float) = ensure(4, 4) { Unsafe.instance.putInt(bytes, pos + 16L, value.toRawBits().swapEndian) }
	fun f64BE(value: Double) = ensure(8, 8) { Unsafe.instance.putLong(bytes, pos + 16L, value.toRawBits().swapEndian) }

	fun i16BE(pos: Int, value: Int) = Unsafe.instance.putShort(bytes, pos + 16L, value.toShort().swapEndian)
	fun i32BE(pos: Int, value: Int) = Unsafe.instance.putInt(bytes, pos + 16L, value.swapEndian)
	fun i64BE(pos: Int, value: Long) = Unsafe.instance.putLong(bytes, pos + 16L, value.swapEndian)
	fun f32BE(pos: Int, value: Float) = Unsafe.instance.putInt(bytes, pos + 16L, value.toRawBits().swapEndian)
	fun f64BE(pos: Int, value: Double) = Unsafe.instance.putLong(bytes, pos + 16L, value.toRawBits().swapEndian)



	// ARRAYS



	fun bytes(pos: Int, writer: BinWriter, srcPos: Int = 0, length: Int = writer.pos) {
		System.arraycopy(writer.bytes, srcPos, bytes, pos, length)
	}

	fun bytes(writer: BinWriter, srcPos: Int = 0, length: Int = writer.pos) {
		ensureCapacity(length)
		System.arraycopy(writer.bytes, srcPos, bytes, pos, length)
		pos += length
	}

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



	// STRINGS



	fun string(string: String, charset: Charset) {
		bytes(charset.encode(string).array())
	}

	fun ascii(string: String) {
		for(c in string) i8(c.code)
	}

	fun asciiNT(string: String) {
		for(c in string) i8(c.code)
		i8(0)
	}

	fun ascii64(string: String) {
		ensureCapacity(8)
		for(i in 0 ..< min(8, string.length))
			i8(string[i].code)
		for(i in 0 ..< max(0, 8 - string.length))
			i8(0)
	}



	// MISC.



	fun set(count: Int, value: Int) {
		if(count <= 0) return
		ensureCapacity(count)
		Unsafe.instance.setMemory(bytes, pos + 16L, count.toLong(), value.toByte())
		pos += count
	}
	
	fun setTo(pos: Int, value: Int) {
		if(pos <= this.pos) return
		ensureCapacity(pos - this.pos)
		Unsafe.instance.setMemory(bytes, this.pos + 16L, pos.toLong() - this.pos, value.toByte())
		this.pos = pos
	}
	
	fun zero(count: Int) = set(count, 0)
	
	fun zeroTo(pos: Int) = setTo(pos, 0)
	
	fun advance(count: Int) {
		if(count <= 0) return
		ensureCapacity(count)
		pos += count
	}
	
	fun advanceTo(pos: Int) {
		ensureCapacity(pos - this.pos)
		this.pos = pos
	}


}