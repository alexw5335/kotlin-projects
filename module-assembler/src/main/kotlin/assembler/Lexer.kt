package assembler

import core.LexerBase

class Lexer(chars: CharArray) : LexerBase<Token>(chars) {


	override val Char.isSkippableWhitespace get() = this.isWhitespace()

	override fun resolveKeyword(string: String) = keywordMap[string]

	override fun resolveIdentifier(string: String) = IdToken(string)

	override fun resolveInteger(value: Long) = IntToken(value)



	override fun resolveSymbol(char: Char) = when(char) {
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



	/*
	Utils
	 */



	companion object {

		private val keywordMap = HashMap<String, Token>().apply {
			for(r in Register.values())
				this[r.string] = RegisterToken(r)

			for(m in Mnemonic.values())
				this[m.string] = MnemonicToken(m)

			for(k in KeywordToken.values())
				this[k.string] = k
		}

	}


}




