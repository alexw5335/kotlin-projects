package core



private fun String.zeroPadded(paddedLength: Int): String {
	var s = this
	while(s.length < paddedLength) s = "0$s"
	return s
}



val Int.bin get() = Integer.toBinaryString(this)

val Int.bin8 get() = Integer.toBinaryString(this).zeroPadded(8)

val Int.bin16 get() = Integer.toBinaryString(this).zeroPadded(16)

val Int.bin32 get() = Integer.toBinaryString(this).zeroPadded(32)

val Int.binFull get() = "0b$bin"

val Int.bin8Full get() = "0b$bin8"

val Int.bin16Full get() = "0b$bin16"

val Int.bin32Full get() = "0b$bin32"



val Int.hex get() = Integer.toHexString(this)

val Int.hex8 get() = Integer.toHexString(this).zeroPadded(2)

val Int.hex16 get() = Integer.toHexString(this).zeroPadded(4)

val Int.hex32 get() = Integer.toHexString(this).zeroPadded(8)

val Int.hexFull get() = "0x$hex"

val Int.hex8Full get() = "0x$hex8"

val Int.hex16Full get() = "0x$hex16"

val Int.hex32Full get() = "0x$hex32"



val Int.bin233 get() = buildString {
	val bin8 = bin8
	append(bin8, 0, 2)
	append('_')
	append(bin8, 2, 5)
	append('_')
	append(bin8, 5, 8)
}



val Byte.bin get() = toUByte().toInt().bin

val Byte.bin8 get() = toUByte().toInt().bin8

val Byte.hex get() = toUByte().toInt().hex

val Byte.hex8 get() = toUByte().toInt().hex8

val Byte.bin233 get() = toUByte().toInt().bin233