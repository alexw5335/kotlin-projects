package asm

import core.BitList
import core.ReaderBase
import java.nio.file.Path
import java.nio.file.Files

class Lexer(chars: CharArray) : ReaderBase(chars) {


	companion object {


		fun lex(string: String) = Lexer(CharArray(string.length + 8).also(string::toCharArray)).lex()

		fun lex(path: Path) = lex(Files.readString(path))



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



	private val tokens = ArrayList<Token>()

	private val newlines = BitList()



	fun lex(): LexResult {
		while(true) {
			val char = chars[pos++]

			if(char == Char(0)) break

			if(char == '\n') {
				newlines.set(tokens.size)
				while(chars[pos] == '\n') pos++
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

		for(i in 0 until 4) tokens.add(EndToken)
		newlines.ensureBitCapacity(tokens.size)
		return LexResult(tokens, newlines)
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
			tokens.add(SymbolToken.SLASH)
			return
		}

		if(chars[pos] == '/') {
			pos++
			skipLine()
			return
		}

		if(chars[pos] != '*') {
			tokens.add(SymbolToken.SLASH)
			return
		}

		pos++
		var count = 1

		while(count > 0) {
			if(pos >= chars.size) error("Unterminated comment")
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
		val string = readUntil { it == '"' }
		if(chars[pos++] != '"') error("Unterminated string literal")
		tokens.add(StringToken(string))
	}



	private fun resolveSingleApostrophe() {
		val char = chars[pos++]
		if(chars[pos++] != '\'') error("Unterminated char literal")
		tokens.add(CharToken(char))
	}


}