package eyre

import core.collection.IntList

class PosLexer(private val srcFile: SrcFile) {


	private val chars = srcFile.contents

	private val lineNumbers = IntList()

	private var pos = 0

	private var lineNumber = 1



	fun lex() {
		if(chars.size < 2 || chars[chars.size - 1] != Char(0) || chars[chars.size - 2] != Char(0))
			error("File must end with at least 2 null characters")

		while(true) {
			val char = chars[pos++]
			if(char.code == 0) break
			charMap[char.code]!!()
		}

		srcFile.lineNumbers = lineNumbers
	}



	private fun addToken() {
		lineNumbers.add(lineNumber)
	}



	private fun letterOrDigit() {
		while(true) {
			val char = chars[pos]
			if(!char.isLetterOrDigit() && char != '_') break
			pos++
		}
		addToken()
	}


	private fun resolveDoubleApostrophe() {
		while(true) {
			val char = chars[pos++]
			if(char == '/') pos++
			else if(char == '"') break
		}
		addToken()
	}


	private fun resolveSingleApostrophe() {
		val char = chars[pos++]
		if(char == '/') pos++
		pos++
		addToken()
	}



	private fun resolveSlash() {
		if(chars[pos] == '/') {
			while(chars[pos] != '\n' && chars[pos].code != 0) pos++
			return
		}

		if(chars[pos] != '*') {
			addToken()
			return
		}

		var count = 1

		while(count > 0) {
			val char = chars[pos++]

			if(char == '/' && chars[pos] == '*') {
				count++
				pos++
			} else if(char == '*' && chars[pos] == '/') {
				count--
				pos++
			} else if(char == '\n') {
				lineNumber++
				pos++
			}
		}
	}



	companion object {

		private val charMap = arrayOfNulls<PosLexer.() -> Unit>(255)

		private operator fun<T> Array<T>.set(char: Char, value: T) = set(char.code, value)

		init {
			charMap['\n'] = { lineNumber++ }
			charMap[' ']  = { }
			charMap['\t'] = { }
			charMap['\r'] = { }

			for(s in SymToken.values()) {
				val firstChar = s.string[0]

				if(s.string.length == 1) {
					charMap[firstChar] = { addToken() }
					continue
				}

				charMap[firstChar] = {
					addToken()
					if(chars[pos] == s.string[1])
						pos++
				}
			}

			charMap['"']  = PosLexer::resolveDoubleApostrophe
			charMap['\''] = PosLexer::resolveSingleApostrophe
			charMap['/']  = PosLexer::resolveSlash
			charMap['_']  = PosLexer::letterOrDigit
			charMap['0']  = PosLexer::letterOrDigit
			for(i in 65..90)  charMap[i] = PosLexer::letterOrDigit
			for(i in 97..122) charMap[i] = PosLexer::letterOrDigit
			for(i in 49..57)  charMap[i] = PosLexer::letterOrDigit

			for(i in charMap.indices)
				if(charMap[i] == null)
					charMap[i] = { error("Invalid char code: $i") }
		}
	}


}