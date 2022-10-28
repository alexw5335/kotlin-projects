package assembler

import core.BitList
import core.ByteList
import core.ReaderBase

class Lexer(chars: CharArray) : ReaderBase(chars.copyOf(chars.size + 8)) {


	private val tokens = ArrayList<Token>()

	private val newlines = BitList()

	private val newlineCounts = ByteList()

	private var newlineCount = 0



	private fun addNewlines() {
		if(newlineCount == 0) return
		
		newlines.set(tokens.size)
		if(newlineCount > 255)
			error("Cannot have more than 255 consecutive newlines")
		newlineCounts.add(newlineCount)
		newlineCount = 0
	}
	
	
	
	private fun addToken(token: Token) {
		addNewlines()
		tokens.add(token)
	}
	
	
	
	fun lex(): LexerResult {
		while(true) {
			val char = chars[pos++]

			if(char == Char(0)) break

			if(char == '\n') {
				newlines.set(tokens.size)
				newlineCount++
				continue
			}

			if(char.isWhitespace()) continue

			if(char == '/') {
				resolveSlash()
				continue
			}

			if(char == '"') {
				resolveDoubleApostrophe()
				continue
			}

			if(char == '\'') {
				resolveSingleApostrophe()
				continue
			}

			val symbol = resolveSymbol(char)

			if(symbol != null) {
				addToken(symbol)
				continue
			}

			pos--

			if(char.isDigit()) {
				addToken(IntToken(readNumber(char)))
				continue
			}

			if(!char.isIdStartChar)
				error("Unexpected character '$char'")

			val string = readWhile { it.isIdChar }

			val keyword = keywordMap[string]

			if(keyword != null) {
				addToken(keyword)
				continue
			}

			addToken(IdToken(string))
		}
		
		addNewlines()

		for(i in 0 until 4) addToken(EndToken)
		newlines.ensureBitCapacity(tokens.size)
		return LexerResult(tokens, newlines, newlineCounts)
	}



	private fun resolveSymbol(char: Char) = when(char) {
		'+' -> SymbolToken.PLUS
		'-' -> SymbolToken.MINUS
		'*' -> SymbolToken.ASTERISK
		'(' -> SymbolToken.LEFT_PAREN
		')' -> SymbolToken.RIGHT_PAREN
		'=' -> SymbolToken.EQUALS
		',' -> SymbolToken.COMMA
		';' -> SymbolToken.SEMICOLON
		':' -> SymbolToken.COLON
		'|' -> SymbolToken.PIPE
		'&' -> SymbolToken.AMPERSAND
		'~' -> SymbolToken.TILDE
		'^' -> SymbolToken.CARET
		'[' -> SymbolToken.LEFT_BRACKET
		']' -> SymbolToken.RIGHT_BRACKET
		'{' -> SymbolToken.LEFT_BRACE
		'}' -> SymbolToken.RIGHT_BRACE
		'.' -> SymbolToken.PERIOD
		'<' -> if(chars[pos] == '<') SymbolToken.LEFT_SHIFT.adv() else SymbolToken.LEFT_ANGLE
		'>' -> if(chars[pos] == '>') SymbolToken.RIGHT_SHIFT.adv() else SymbolToken.RIGHT_ANGLE
		else -> null
	}



	private fun resolveSlash() {
		if(pos >= chars.size) {
			addToken(SymbolToken.SLASH)
			return
		}

		if(chars[pos] == '/') {
			pos++
			skipLine()
			return
		}

		if(chars[pos] != '*') {
			addToken(SymbolToken.SLASH)
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
			} else if(char == '\n') {
				newlineCount++
			}
		}
	}



	private fun resolveDoubleApostrophe() {
		val string = readUntil { it == '"' }
		if(chars[pos++] != '"') error("Unterminated string literal")
		addToken(StringToken(string))
	}



	private fun resolveSingleApostrophe() {
		val char = chars[pos++]
		if(chars[pos++] != '\'') error("Unterminated char literal")
		addToken(CharToken(char))
	}


}