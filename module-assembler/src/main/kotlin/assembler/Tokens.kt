package assembler



val Token.printableString get() = when(this) {
	is SymbolToken   -> "SYMBOL    $string"
	is IdToken       -> "ID        $value"
	is IntToken      -> "INT       $value"
	is MnemonicToken -> "MNEMONIC  ${value.string}"
	is RegisterToken -> "REGISTER  ${value.string}"
	is KeywordToken  -> "KEYWORD   $string"
}



sealed interface Token



data class IntToken(val value: Long) : Token



data class IdToken(val value: String) : Token



data class RegisterToken(val value: Register) : Token



data class MnemonicToken(val value: Mnemonic) : Token



enum class KeywordToken(val width: Width? = null) : Token {

	BYTE(width = Width.BIT8),
	WORD(width = Width.BIT16),
	DWORD(width = Width.BIT32),
	QWORD(width = Width.BIT64),
	CONST;

	val string = name.lowercase()

}



enum class SymbolToken(
	val string   : String,
	val binaryOp : BinaryOp? = null,
	val unaryOp  : UnaryOp? = null
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
	LEFT_SHIFT    ("<<", binaryOp = BinaryOp.SHL),
	RIGHT_SHIFT   (">>", binaryOp = BinaryOp.SHR),
	LEFT_BRACKET  ("["),
	RIGHT_BRACKET ("]"),
	NEWLINE       ("\\n");

}