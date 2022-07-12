package assembler

import core.LexerBase

class Lexer(chars: CharArray) : LexerBase<Token>(chars) {


	override val Char.isSkippableWhitespace get() = this.isWhitespace()

	override fun resolveKeyword(string: String) = keywordMap[string]

	override fun resolveIdentifier(string: String) = IdToken(string)

	override fun resolveInteger(value: Long) = IntToken(value)



	override fun resolveSymbol(char: Char) = when(char) {
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
		'<' -> {
			if(pos < chars.size && chars[pos] == '<') { pos++; Symbol.LEFT_SHIFT }
			else { Symbol.LEFT_ANGLE }
		}
		'>' -> {
			if(pos < chars.size && chars[pos] == '>') { pos++; Symbol.RIGHT_SHIFT }
			else { Symbol.RIGHT_ANGLE }
		}
		else -> null
	}



	/*
	Utils
	 */



	companion object {

		private val keywordMap = HashMap<String, Token>().apply {
			for(r in Register.values())
				this[r.string] = RegisterToken(r)

			for(m in Mnemonic.values())
				this[m.string] = MnemonicToken(m)

			for(k in Keyword.values())
				this[k.string] = k
		}

	}


}




