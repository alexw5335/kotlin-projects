package eyre



sealed interface Token

object EndToken : Token { override fun toString() = "EndToken" }

data class IntToken(val value: Long) : Token

data class CharToken(val value: Char) : Token

data class StringToken(val value: String) : Token

data class IdToken(val value: Intern) : Token



enum class SymToken(
	val string      : String,
	val binaryOp    : BinaryOp? = null,
	val unaryOp     : UnaryOp? = null,
	val firstSymbol : SymToken? = null
) : Token {

	LEFT_PAREN    ("(", binaryOp = BinaryOp.FUN),
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
	LEFT_SHIFT    ("<<", binaryOp = BinaryOp.SHL, firstSymbol = LEFT_ANGLE),
	RIGHT_SHIFT   (">>", binaryOp = BinaryOp.SHR, firstSymbol = RIGHT_ANGLE),
	LEFT_BRACKET  ("["),
	RIGHT_BRACKET ("]"),
	LEFT_BRACE    ("{"),
	RIGHT_BRACE   ("}"),
	PERIOD        (".", binaryOp = BinaryOp.DOT),
	REFERENCE     ("::", firstSymbol = COLON);

}