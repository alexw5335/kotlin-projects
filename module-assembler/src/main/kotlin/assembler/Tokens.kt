package assembler

sealed interface Token



object EndToken : Token

data class IntToken(val value: Long) : Token

data class CharToken(val value: Char) : Token

data class StringToken(val value: String) : Token

data class IdToken(val value: String) : Token

data class RegToken(val value: Register) : Token



enum class SymbolToken(
	val string      : String,
	val binaryOp    : BinaryOp? = null,
	val unaryOp     : UnaryOp? = null,
	val firstSymbol : SymbolToken? = null
) : Token {

	LEFT_PAREN    ("("),
	RIGHT_PAREN   (")"),
	PLUS          ("+", binaryOp = BinaryOp.ADD, unaryOp = UnaryOp.POS),
	MINUS         ("-", binaryOp = BinaryOp.SUB, unaryOp = UnaryOp.NEG),
	ASTERISK      ("*", binaryOp = BinaryOp.MUL),
	SLASH         ("/", binaryOp = BinaryOp.DIV),
	EQUALS        ("="),
	COMMA         (","),
	SEMICOLON     (";"),
	COLON         (":"),
	PIPE          ("|", binaryOp = BinaryOp.OR),
	AMPERSAND     ("&", binaryOp = BinaryOp.AND),
	TILDE         ("~", unaryOp = UnaryOp.NOT),
	CARET         ("^", binaryOp = BinaryOp.XOR),
	LEFT_ANGLE    ("<"),
	RIGHT_ANGLE   (">"),
	LEFT_SHIFT    ("<<", binaryOp = BinaryOp.SHL, firstSymbol = SymbolToken.LEFT_ANGLE),
	RIGHT_SHIFT   (">>", binaryOp = BinaryOp.SHR, firstSymbol = SymbolToken.RIGHT_ANGLE),
	LEFT_BRACKET  ("["),
	RIGHT_BRACKET ("]"),
	LEFT_BRACE    ("{"),
	RIGHT_BRACE   ("}"),
	PERIOD        (".");

}



internal val registerMap = HashMap<String, RegToken>().also { map ->
	for(r in Register.values()) {
		val token = RegToken(r)
		map[r.string] = token
		map[r.name] = token
	}
}