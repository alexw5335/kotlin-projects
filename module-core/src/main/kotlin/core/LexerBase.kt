package core

abstract class LexerBase<T>(chars: CharArray) : ReaderBase(chars) {


	private val tokens = ArrayList<T>()



	fun lex(): List<T> {
		while(pos < chars.size) {
			val char = chars[pos++]

			if(char.isSkippableWhitespace) continue

			if(atCommentStart(char)) {
				skipTo { it == '\n' }
				continue
			}

			val symbol = resolveSymbol(char)
			if(symbol != null) {
				tokens.add(symbol)
				continue
			}

			pos--

			if(shouldReadIntegers && char.isDigit()) {
				tokens.add(resolveInteger(readNumber(char))!!)
				continue
			}

			if(!char.isIdStartChar)
				error("Unexpected character '$char' at pos $pos")

			val string = readIdString()

			val keyword = resolveKeyword(string)
			if(keyword != null) {
				tokens.add(keyword)
				continue
			}

			tokens.add(resolveIdentifier(string))
		}

		return tokens
	}



	protected open val shouldReadIntegers = true

	protected open fun atCommentStart(char: Char) = false

	protected open val Char.isIdStartChar get() = isLetter() || this == '_'

	protected open val Char.isIdChar get() = isLetterOrDigit() || this == '_'

	protected open fun readIdString() = readWhile { it.isIdChar }



	protected abstract val Char.isSkippableWhitespace: Boolean

	protected abstract fun resolveSymbol(char: Char): T?

	protected abstract fun resolveKeyword(string: String): T?

	protected abstract fun resolveIdentifier(string: String): T

	protected abstract fun resolveInteger(value: Long): T?


}