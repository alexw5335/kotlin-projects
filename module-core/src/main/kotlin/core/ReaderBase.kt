package core

/**
 * Not intended as a general-purpose lexer superclass. Contains convenience methods for simple lexers/parsers. The
 * [chars] array is expected to provide a buffer of at 8 null chars at the end of the array.
 */
abstract class ReaderBase(val chars: CharArray) {


	var pos = 0

	val atNewline get() = chars[pos] == '\n' || chars[pos] == '\r'



	/*
	Skipping
	 */



	inline fun skipUntil(block: (Char) -> Boolean) {
		while(pos < chars.size && !block(chars[pos]))
			pos++
	}

	inline fun skipWhile(block: (Char) -> Boolean) {
		while(pos < chars.size && block(chars[pos]))
			pos++
	}

	inline fun skipTo(block: (Char) -> Boolean) {
		while(pos < chars.size && !block(chars[pos++])) Unit
	}

	fun skipWhitespace() = skipWhile { it.isWhitespace() }

	fun skipLine() = skipTo { it == '\n' }

	fun skipSpaces() = skipWhile { it.isWhitespace() && it != '\n' }

	fun skipLines() = skipWhile { it == '\n' }

	protected fun<T> T.adv(): T { pos++; return this }

	fun advanceIfAt(char: Char) = if(chars[pos] == char) { pos++; true; } else false



	/*
	String reading
	 */



	fun read(length: Int) = String(CharArray(length) { chars[pos++ ]})

	inline fun readUntil(block: (Char) -> Boolean) = buildString {
		while(pos < chars.size && !block(chars[pos]))
			append(chars[pos++])
	}

	inline fun readWhile(block: (Char) -> Boolean) = buildString {
		while(pos < chars.size && block(chars[pos]))
			append(chars[pos++])
	}

	fun readUntilWhitespace() = readUntil { it.isWhitespace() }



	/*
	Number reading
	 */



	fun readNumber(char: Char) = when {
		pos == chars.size - 1 -> readIntegerDefaultBase()
		char != '0'           -> readIntegerDefaultBase()
		chars[pos + 1] == 'b' -> { pos += 2; readBinary() }
		chars[pos + 1] == 'x' -> { pos += 2; readHex() }
		else                  -> readIntegerDefaultBase()
	}



	fun readDecimal(): Long {
		if(chars[pos] < '0' || chars[pos] > '9') error("Invalid decimal literal")
		var value = 0L
		while(pos < chars.size) {
			val char = chars[pos++]
			if(char == '_') continue
			if(char < '0' || char > '9') { pos--; break }
			val digit = char - '0'
			value = value * 10 + digit
		}
		return value
	}



	fun readBinary(): Long {
		if(chars[pos] < '0' || chars[pos] > '1') error("Invalid binary literal")
		var value = 0L
		while(pos < chars.size) {
			val char = chars[pos++]
			if(char == '_') continue
			if(char < '0' || char > '1') { pos--; break }
			val digit = char - '0'
			value = (value shl 1) or digit.toLong()
		}
		return value
	}



	fun readHex(): Long {
		val first = chars[pos]
		if(first !in '0'..'9' && first !in 'a'..'f' && first !in 'A'..'F') error("Invalid hex literal")
		var value = 0L
		while(pos < chars.size) {
			val digit = when(val char = chars[pos++]) {
				in '0'..'9' -> char - '0'
				in 'a'..'f' -> char - 'a' + 10
				in 'A'..'F' -> char - 'A' + 10
				'_'         -> continue
				else        -> { pos--; break }
			}
			value = (value shl 4) or digit.toLong()
		}
		return value
	}



	fun readHex2() = read(2).toInt(16)



	/*
	Open
	 */



	open fun readIntegerDefaultBase() = readDecimal()


}