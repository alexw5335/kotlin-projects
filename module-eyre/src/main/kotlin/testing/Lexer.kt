package testing

import core.collection.BitList
import core.collection.IntList

class Lexer(private val srcFile: SrcFile) {


	private val chars = srcFile.contents

	private var pos = 0

	private val tokens = ArrayList<Token>()

	private val newlines = BitList()

	private val terminators = BitList() // newline, end-of-file, or any symbol

	private val stringBuilder = StringBuilder()

	private val Char.isIdentifierPart get() = isLetterOrDigit() || this == '_'

	private var lineNumber = 1

	private val lineNumbers = IntList()

	

	private fun error(message: String): Nothing {
		System.err.println("Error on line $lineNumber of ${srcFile.path}:\n\t$message\n")
		kotlin.error("Lexing error")
	}



	fun lex() {
		if(chars.size < 2 || chars[chars.size - 1] != Char(0) || chars[chars.size - 2] != Char(0))
			kotlin.error("File must end with at least 2 null characters")

		while(true) {
			val char = chars[pos++]
			if(char.code == 0) break
			charMap[char.code]!!()
		}

		for(i in 0 until 4) {
			terminators.set(tokens.size)
			tokens.add(EndToken)
		}

		newlines.ensureBitCapacity(tokens.size)
		srcFile.tokens = tokens
		srcFile.newlines = newlines
		srcFile.terminators = terminators
	}



	private fun onNewline() {
		lineNumber++
		newlines.set(tokens.size)
		terminators.set(tokens.size)
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
		if(chars[pos].isLetterOrDigit()) error("Invalid char in number literal: '${chars[pos]}'")
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

		tokens.add(IdToken(Interner.add(String(chars, startPos, pos - startPos))))
	}



	private fun resolveSlash() {
		if(chars[pos] == '/') {
			while(chars[pos] != '\n' && chars[pos].code != 0) pos++
			return
		}

		if(chars[pos] != '*') {
			tokens.add(SymToken.SLASH)
			return
		}

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
			} else if(char == '\n') {
				onNewline()
			}
		}
	}



	private val Char.escape get() = when(this) {
		't'  -> '\t'
		'n'  -> '\n'
		'\\' -> '\\'
		'r'  -> '\r'
		'b'  -> '\b'
		'"'  -> '"'
		'\'' -> '\''
		'0'  -> Char(0)
		else -> error("Invalid escape char: $this")
	}



	private fun resolveDoubleApostrophe() {
		stringBuilder.clear()

		while(true) {
			when(val char = chars[pos++]) {
				Char(0) -> error("Unterminated string literal")
				'\n'    -> error("Newline not allowed in string literal")
				'"'     -> break
				'\\'    -> stringBuilder.append(chars[pos++].escape)
				else    -> stringBuilder.append(char)
			}
		}

		tokens.add(StringToken(stringBuilder.toString()))
	}



	private fun resolveSingleApostrophe() {
		var char = chars[pos++]

		if(char == '\\')
			char = chars[pos++].escape

		tokens.add(CharToken(char))

		if(chars[pos++] != '\'')
			error("Unterminated char literal")
	}



	companion object {

		private val charMap = arrayOfNulls<Lexer.() -> Unit>(255)

		private operator fun<T> Array<T>.set(char: Char, value: T) = set(char.code, value)

		init {
			charMap['\n'] = Lexer::onNewline
			charMap[' ']  = { }
			charMap['\t'] = { }
			charMap['\r'] = { }

			for(s in SymToken.values()) {
				val firstChar = s.string[0]

				if(s.string.length == 1) {
					charMap[firstChar] = {
						terminators.set(tokens.size)
						tokens.add(s)
					}
					continue
				}

				val firstSymbol = s.firstSymbol ?: error("Invalid symbol")
				val secondChar = s.string[1]

				charMap[firstChar] = {
					terminators.set(tokens.size)
					if(chars[pos] == secondChar) {
						tokens.add(s)
						pos++
					} else
						tokens.add(firstSymbol)
				}
			}

			charMap['"']  = Lexer::resolveDoubleApostrophe
			charMap['\''] = Lexer::resolveSingleApostrophe
			charMap['/']  = Lexer::resolveSlash
			charMap['_']  = Lexer::idStart
			charMap['0']  = Lexer::zero
			for(i in 65..90)  charMap[i] = Lexer::idStart
			for(i in 97..122) charMap[i] = Lexer::idStart
			for(i in 49..57)  charMap[i] = Lexer::digit

			for(i in charMap.indices)
				if(charMap[i] == null)
					charMap[i] = { error("Invalid char code: $i") }
		}
	}


}