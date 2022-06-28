package assembler



sealed interface Token



data class IntLiteral(val value: Long) : Token

data class Identifier(val value: String) : Token

data class RegisterToken(val value: Register) : Token

data class MnemonicToken(val value: Mnemonic) : Token



enum class Symbol(
	val string: String,
	val binaryOp: BinaryOp? = null,
	val unaryOp: UnaryOp? = null
) : Token {

	LEFT_PAREN("("),
	RIGHT_PAREN(")"),
	PLUS("+", binaryOp = BinaryOp.ADD, unaryOp = UnaryOp.POS),
	MINUS("-", binaryOp = BinaryOp.SUB, unaryOp = UnaryOp.NEG),
	ASTERISK("*", binaryOp = BinaryOp.MUL),
	SLASH("/", binaryOp = BinaryOp.DIV),
	EQUALS("="),
	COMMA(","),
	NEWLINE("\\n"),
	SEMICOLON(";"),
	COLON(":"),
	PIPE("|", binaryOp = BinaryOp.OR),
	AMPERSAND("&", binaryOp = BinaryOp.AND),
	TILDE("~", unaryOp = UnaryOp.NOT),
	CARET("^", binaryOp = BinaryOp.XOR),
	LEFT_ANGLE("<"),
	RIGHT_ANGLE(">"),
	LEFT_SHIFT("<<", binaryOp = BinaryOp.SHL),
	RIGHT_SHIFT(">>", binaryOp = BinaryOp.SHR);
}