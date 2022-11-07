package assembler

import core.collection.BitList

class Lexer(chars: CharArray) {


	private val chars = chars.copyOf(chars.size + 8)

	private var pos = 0

	private val tokens = ArrayList<Token>()

	private val newlines = BitList()

	private val charMap = arrayOfNulls<() -> Unit>(255)

	private operator fun<T> Array<T>.set(char: Char, value: T) = set(char.code, value)

	private val stringBuilder = StringBuilder()

	private val Char.isIdentifierPart get() = isLetterOrDigit() || this == '_'



	init {
		populateCharMap()
	}



	fun lex(): LexerResult {
		while(true) {
			val char = chars[pos++]
			if(char.code == 0) break
			charMap[char.code]!!()
		}

		for(i in 0 until 4) tokens.add(EndToken)
		newlines.ensureBitCapacity(tokens.size)
		return LexerResult(tokens, newlines)
	}



	private fun populateCharMap() {
		charMap['\n'] = { newlines.set(tokens.size) }
		charMap[' ']  = { }
		charMap['\t'] = { }
		charMap['\r'] = { }

		for(s in SymbolToken.values()) {
			val firstChar = s.string[0]

			if(s.string.length == 1) {
				charMap[firstChar] = { tokens.add(s)}
				continue
			}

			val firstSymbol = s.firstSymbol ?: error("Invalid symbol")
			val secondChar = s.string[1]

			charMap[firstChar] = {
				if(chars[pos] == secondChar) {
					tokens.add(s)
					pos++
				} else
					tokens.add(firstSymbol)
			}
		}

		charMap['"'] = ::resolveDoubleApostrophe
		charMap['\''] = ::resolveSingleApostrophe
		charMap['/'] = ::resolveSlash

		for(i in 65..90)
			charMap[i] = ::idStart

		for(i in 97..122)
			charMap[i] = ::idStart

		charMap['_'] = ::idStart

		charMap['0'] = ::zero

		for(i in 49..57)
			charMap[i] = ::digit

		for(i in charMap.indices)
			if(charMap[i] == null)
				charMap[i] = { error("Invalid char code: $i") }
	}



	private fun readBinary(): Long {
		var value = 0L

		while(true) {
			val mask = when(chars[pos++]) {
				'0'  -> 0L
				'1'  -> 1L
				'_'  -> continue
				else -> break
			}

			if(value and (1L shl 63) != 0L)
				error("Integer literal out of range")
			value = (value shl 1) or mask
		}

		pos--
		return value
	}



	private fun readDecimal(): Long {
		var value = 0L

		while(true) {
			val mask = when(val char = chars[pos++]) {
				in '0'..'9' -> char.code - 48L
				'_'         -> continue
				else        -> break
			}

			if(value and (0xFFL shl 56) != 0L)
				error("Integer literal out of range")
			value = (value * 10) + mask
		}

		pos--
		return value
	}



	private fun readHex(): Long {
		var value = 0L

		while(true) {
			val mask = when(val char = chars[pos++]) {
				'_'         -> continue
				in '0'..'9' -> char.code - 48L
				in 'a'..'z' -> char.code - 75L
				in 'A'..'Z' -> char.code - 107L
				else        -> break
			}

			if(value and (0b1111L shl 60) != 0L)
				error("Integer literal out of range")
			value = (value shl 4) or mask
		}

		pos--
		return value
	}



	private fun digit() {
		pos--
		tokens.add(IntToken(readDecimal()))
	}



	private fun zero() {
		if(chars[pos].isDigit()) {
			tokens.add(IntToken(readDecimal()))
			return
		}

		if(!chars[pos].isLetter()) {
			tokens.add(IntToken(0))
			return
		}

		when(val base = chars[pos++]) {
			'x'  -> tokens.add(IntToken(readHex()))
			'b'  -> tokens.add(IntToken(readBinary()))
			else -> error("Invalid integer base: $base")
		}
	}



	private fun idStart() {
		pos--

		val startPos = pos

		while(true) {
			val char = chars[pos]
			if(!char.isIdentifierPart) break
			pos++
		}

		tokens.add(IdToken(Intern.addAndGet(String(chars, startPos, pos - startPos))))
	}



	private fun resolveSlash() {
		if(chars[pos] == '/') {
			pos++
			while(chars[pos] != '\n') pos++
			return
		}

		if(chars[pos] != '*') {
			tokens.add(SymbolToken.SLASH)
			return
		}

		pos++
		var count = 1

		while(count > 0) {
			if(pos >= chars.size)
				error("Unterminated multiline comment")

			val char = chars[pos++]

			if(char == '/' && chars[pos] == '*') {
				count++
				pos++
			} else if(char == '*' && chars[pos] == '/') {
				count--
				pos++
			}
		}
	}



	private fun resolveDoubleApostrophe() {
		stringBuilder.clear()

		while(true) {
			val char = chars[pos++]
			if(char.code == 0) error("Unterminated string literal")
			if(char == '\n') error("Newline not allowed in string literal")
			if(char == '"') break
			stringBuilder.append(char)
		}

		tokens.add(StringToken(stringBuilder.toString()))
	}



	private fun resolveSingleApostrophe() {
		val char = chars[pos++]
		if(chars[pos++] != '\'') error("Unterminated char literal")
		tokens.add(CharToken(char))
	}


}