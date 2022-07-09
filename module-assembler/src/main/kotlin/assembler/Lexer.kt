package assembler

class Lexer(private val chars: CharArray) {


	private var pos = 0

	private val tokens = ArrayList<Token>()

	private val newlines = NewlineList()



	/*
	Lexing
	 */



	fun lex(): LexResult {
		while(pos < chars.size) {
			val char = chars[pos++]

			if(char == ' ' || char == '\t' || char == '\r')
				continue

			if(char == '\n') {
				newlines.set(tokens.size)
				continue
			}

			val symbol = symbol(char)
			if(symbol != null) {
				tokens.add(symbol)
				continue
			}

			pos--

			if(char.isDigit()) {
				val value = if(char == '0' && pos < chars.size)
					when(chars[pos + 1]) {
						'b'  -> { pos += 2; parseBinary() }
						else -> parseDecimal()
					}
				else
					parseDecimal()

				tokens.add(IntLiteral(value))
				continue
			}

			if(!char.isIdStartChar)
				throw RuntimeException("Invalid char: $char")

			val string = buildString {
				while(pos < chars.size && chars[pos].isIdChar)
					append(chars[pos++])
			}

			val keyword = keywordMap[string]
			if(keyword != null) {
				tokens.add(keyword)
				continue
			}

			tokens.add(Identifier(string))
		}

		for(i in 0 until 4) tokens.add(EndToken)
		newlines.ensureBitCapacity(tokens.size)
		return LexResult(tokens, newlines)
	}



	/*
	Symbols
	 */



	private fun checkCompoundSymbol(single: Symbol, char: Char, double: Symbol) =
		if(pos < chars.size && chars[pos] == char)
			double.also { pos++ }
		else
			single



	private fun symbol(char: Char) = when(char) {
		'+' -> Symbol.PLUS
		'-' -> Symbol.MINUS
		'*' -> Symbol.ASTERISK
		'/' -> Symbol.SLASH
		'(' -> Symbol.LEFT_PAREN
		')' -> Symbol.RIGHT_PAREN
		'=' -> Symbol.EQUALS
		',' -> Symbol.COMMA
		';' -> Symbol.SEMICOLON
		':' -> Symbol.COLON
		'|' -> Symbol.PIPE
		'&' -> Symbol.AMPERSAND
		'~' -> Symbol.TILDE
		'^' -> Symbol.CARET
		'[' -> Symbol.LEFT_BRACKET
		']' -> Symbol.RIGHT_BRACKET
		'<' -> checkCompoundSymbol(Symbol.LEFT_ANGLE, '<', Symbol.LEFT_SHIFT)
		'>' -> checkCompoundSymbol(Symbol.RIGHT_ANGLE, '>', Symbol.RIGHT_SHIFT)
		else -> null
	}



	/*
	Number literal parsing
	 */



	private fun parseDecimal(): Long {
		if(chars[pos] < '0' || chars[pos] > '9') error("Invalid decimal literal")
		var value = 0L
		while(pos < chars.size) {
			val char = chars[pos++]
			if(char == '_') continue
			if(char.isIdStartChar) error("Unsupported decimal literal postfix '$char'")
			if(char < '0' || char > '9') { pos--; break }
			val digit = char - '0'
			value = value * 10 + digit
		}
		return value
	}



	private fun parseBinary(): Long {
		if(chars[pos] < '0' || chars[pos] > '1') error("Invalid binary literal")
		var value = 0L
		while(pos < chars.size) {
			val char = chars[pos++]
			if(char == '_') continue
			if(char.isIdStartChar) error("Unsupported binary literal postfix: '$char'")
			if(char < '0' || char > '1') { pos--; break }
			val digit = char - '0'
			value = (value shl 1) or digit.toLong()
		}
		return value
	}



	/*
	Utils
	 */



	companion object {

		private val keywordMap = HashMap<String, Token>()

		init {
			for(r in Register.values())
				keywordMap[r.name.lowercase()] = RegisterToken(r)
			for(m in Mnemonic.values())
				keywordMap[m.name.lowercase()] = MnemonicToken(m)
			for(k in Keyword.values())
				keywordMap[k.string] = KeywordToken(k)
		}

		private val Char.isIdChar get() = isLetterOrDigit() || this == '_'

		private val Char.isIdStartChar get() = isLetter() || this == '_'

	}


}




