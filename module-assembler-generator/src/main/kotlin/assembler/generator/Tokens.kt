package assembler.generator



internal val symbolMap = SymbolToken.values().associateBy { it.value }

internal val keywordMap = KeywordToken.values().associateBy { it.value }



interface Token

data class IdToken(val value: String) : Token



enum class SymbolToken(val value: Char) : Token {

	LPAREN('('),
	RPAREN(')'),
	HASH('#'),
	SLASH('/'),
	NEWLINE('\n');

}



enum class KeywordToken(explicitValue: String? = null) : Token {
	REXW("REXW"),
	OSO("OSO"),

	A,
	AL,
	R,
	R8,
	R16,
	R32,
	R64,
	RM,
	RM8,
	RM16,
	RM32,
	RM64,
	IMM,
	IMM8,
	IMM16,
	IMM32,
	IMM64,
	REL32,
	M64,
	M128,
	M16_16,
	M16_32,
	M16_64;


	val value = explicitValue ?: name;
}