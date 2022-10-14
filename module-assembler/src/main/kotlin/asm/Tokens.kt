package asm



val Token.printableString get() = when(this) {
	is SymbolToken    -> "SYMBOL    $string"
	is IdToken        -> "ID        $value"
	is IntToken       -> "INT       $value"
	is MnemonicToken  -> "MNEMONIC  ${value.string}"
	is RegToken       -> "REGISTER  ${value.string}"
	is KeywordToken   -> "KEYWORD   $string"
	is EndToken       -> "END OF STREAM"
	is CharToken      -> "CHAR      $value"
	is StringToken    -> "STRING    $value"
	is SRegToken      -> "SREG:     $value"
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



enum class KeywordToken(
	vararg val strings: String,
	val width: Width? = null,
	val isModifier: Boolean = false
) : Token {

	BYTE("byte", "BYTE", width = Width.BIT8),
	WORD("word", "WORD", width = Width.BIT16),
	DWORD("dword", "DWORD", width = Width.BIT32),
	QWORD("qword", "QWORD", width = Width.BIT64),
	TWORD("tword", "TWORD", width = Width.BIT80),
	XWORD("xword", "XWORD", width = Width.BIT128),
	CONST("const"),
	ENUM("enum"),
	DB("db", "DB"),
	DW("dw", "DW"),
	DD("dd", "DD"),
	DQ("dq", "DQ"),
	IMPORT("import"),
	VAL("val"),
	VAR("var"),
	REP("rep", "repe", "repz", "REP", "REPE", "REPZ", isModifier = true),
	REPNE("repne", "repnz", "REPNE", "REPNZ", isModifier = true),
	SHORT("short", "SHORT"),
	LOCK("lock", "LOCK", isModifier = true);

	val string = strings[0]

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



internal val keywordMap = HashMap<String, Token>().also { map ->
	for(r in Register.values()) {
		val token = RegToken(r)
		map[r.string] = token
		map[r.name] = token
	}

	for(m in Mnemonic.values()) {
		val token = MnemonicToken(m)
		map[m.string] = token
		map[m.name] = token
	}

	for(k in KeywordToken.values()) {
		for(s in k.strings)
			map[s] = k
	}

	for(r in SRegister.values()) {
		val token = SRegToken(r)
		map[r.string] = token
		map[r.name] = token
	}

	for(r in STRegister.values()) {
		val token = STRegToken(r)
		map[r.string] = token
		map[r.name] = token
	}
}