package assembler.generator

import core.LexerBase

class Lexer(chars: CharArray): LexerBase<Token>(chars) {


	override val Char.isIdStartChar get() = isIdChar

	override val shouldReadIntegers = false

	override val Char.isSkippableWhitespace get() = isWhitespace() && this != '\n'

	override fun atCommentStart(char: Char) = char == '#'

	override fun resolveSymbol(char: Char) = symbolMap[char]

	override fun resolveKeyword(string: String) = keywordMap[string]

	override fun resolveIdentifier(string: String) = IdToken(string)

	override fun resolveInteger(value: Long) = null


}