package scratch



fun main() {
	println(alignStack(48, 16))
}



fun alignStack(rsp: Int, alignment: Int) = rsp and -alignment



fun intStringSize(input: Int): Int {
	var size = 0
	var comparison = 1
	do {
		comparison *= 10
		size++
	} while(comparison <= input)
	return size
}



fun intToString(input: Int): String {
	val size = intStringSize(input)
	val chars = CharArray(size)

	var value = input

	for(i in 0 until size) {
		chars[chars.size - i - 1] = '0' + value % 10
		value /= 10
	}

	return String(chars)
}