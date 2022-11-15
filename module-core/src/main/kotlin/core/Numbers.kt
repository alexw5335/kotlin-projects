package core



val Short.swapEndian get() = java.lang.Short.reverseBytes(this)

val Int.swapEndian get() = Integer.reverseBytes(this)

val Long.swapEndian get() = java.lang.Long.reverseBytes(this)

val Float.swapEndian get() = java.lang.Float.intBitsToFloat(java.lang.Float.floatToRawIntBits(this).swapEndian)

val Double.swapEndian get() = java.lang.Double.longBitsToDouble(java.lang.Double.doubleToRawLongBits(this).swapEndian)



/*
val Int.swapEndian16 get() =
	((this and 0xFF) shl 8) or
	((this shr 8) and 0xFF)

val Int.swapEndian32 get() =
	((this and 0xFF) shl 24) or
	((this and 0xFF00) shl 8) or
	((this shr 24) and 0xFF) or
	((this shr 8) and 0xFF00)

val Long.swapEndian64 get() =
	((this and 0xFF) shl 56) or
	((this and 0xFF00) shl 40) or
	((this and 0xFF0000) shl 24) or
	((this and 0xFF000000) shl 8) or
	((this shr 56) and 0xFF) or
	((this shr 40) and 0xFF00) or
	((this shr 24) and 0xFF0000) or
	((this shr 8) and 0xFF000000)
*/
