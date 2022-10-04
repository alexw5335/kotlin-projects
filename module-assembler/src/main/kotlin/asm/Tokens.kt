package asm



val Token.printableString get() = when(this) {
	is SymbolToken    -> "SYMBOL    $string"
	is IdToken        -> "ID        $value"
	is IntToken       -> "INT       $value"
	is MnemonicToken  -> "MNEMONIC  ${value.string}"
	is RegToken  -> "REGISTER  ${value.string}"
	is KeywordToken   -> "KEYWORD   $string"
	is EndToken       -> "END OF STREAM"
	is CharToken      -> "CHAR      $value"
	is StringToken    -> "STRING    $value"
	is SRegToken -> "SREG:     $value"
	is STRegToken     -> "STREG:    $value"
}



sealed interface Token



object EndToken : Token



data class IntToken(val value: Long) : Token



data class CharToken(val value: Char) : Token



data class StringToken(val value: String) : Token



data class IdToken(val value: String) : Token



data class RegToken(val value: Register) : Token



data class SRegToken(val value: SRegister) : Token



data class STRegToken(val value: STRegister): Token



data class MnemonicToken(val value: Mnemonic) : Token



enum class KeywordToken(val width: Width? = null) : Token {

	BYTE(width = Width.BIT8),
	WORD(width = Width.BIT16),
	DWORD(width = Width.BIT32),
	QWORD(width = Width.BIT64),
	TWORD(width = Width.BIT80),
	CONST,
	ENUM,
	DB,
	EXTERN;

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
	LEFT_BRACE    ("{"),
	RIGHT_BRACE   ("}"),
	PERIOD        (".", binaryOp = BinaryOp.DOT);

}