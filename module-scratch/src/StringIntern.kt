package scratch



class StringHolder(val mask: Int, val stringSize: Int) {

	var pos = 0
	var longs = LongArray(8)

	fun add(string: String): Int {
		if(pos + stringSize > longs.size)
			longs = longs.copyOf(pos + stringSize)

		for(i in 0 until stringSize)
			longs[pos++] = string.toString6(i * 10)

		return mask or (pos shl 16)
	}

}



private fun String.toString6(start: Int): Long {
	var value = 0L
	var shift = 0

	for(i in start until length.coerceAtMost(start + 10)) {
		val mask = when(val code = this[i].code) {
			in 48..57   -> code - 48L
			in 65..90   -> code - 55L
			in 97..122  -> code - 61L
			95          -> 63L
			else        -> 0L
		}

		value = value or (mask shl shift)
		shift += 6
	}

	return value
}