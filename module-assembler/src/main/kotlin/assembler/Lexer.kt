package assembler

import core.ReaderBase

class Lexer(chars: CharArray) : ReaderBase(chars) {


	private val tokens = ArrayList<Token>()



	fun lex(): LexResult {
		while(pos < chars.size) {
			val char = chars[pos++]

			if(char == '\n') {
				tokens.add(SymbolToken.NEWLINE)
				continue
			}

			if(char.isWhitespace()) continue

			val symbol = resolveSymbol(char)

			if(symbol != null) {
				tokens.add(symbol)
				continue
			}

			pos--

			if(char.isDigit()) {
				tokens.add(IntToken(readNumber(char)))
				continue
			}

			if(!char.isIdStartChar)
				error("Unexpected character '$char'")

			val string = readWhile { it.isIdChar }

			val keyword = keywordMap[string]

			if(keyword != null) {
				tokens.add(keyword)
				continue
			}

			tokens.add(IdToken(string))
		}

		return LexResult(tokens)
	}



	private fun resolveSymbol(char: Char) = when(char) {
		'+' -> SymbolToken.PLUS
		'-' -> SymbolToken.MINUS
		'*' -> SymbolToken.ASTERISK
		'/' -> SymbolToken.SLASH
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
		'<' -> {
			if(pos < chars.size && chars[pos] == '<') { pos++; SymbolToken.LEFT_SHIFT }
			else { SymbolToken.LEFT_ANGLE }
		}
		'>' -> {
			if(pos < chars.size && chars[pos] == '>') { pos++; SymbolToken.RIGHT_SHIFT }
			else { SymbolToken.RIGHT_ANGLE }
		}
		else -> null
	}



	companion object {

		private val Char.isIdStartChar get() = isLetter() || this == '_'

		private val Char.isIdChar get() = isLetterOrDigit() || this == '_'

		private val keywordMap = HashMap<String, Token>()

		init {
			for(r in Register.values())
				keywordMap[r.string] = RegisterToken(r)

			for(m in Mnemonic.values())
				keywordMap[m.string] = MnemonicToken(m)

			for(k in KeywordToken.values())
				keywordMap[k.string] = k
		}

	}


}




