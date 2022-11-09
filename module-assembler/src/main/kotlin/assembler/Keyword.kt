package assembler

enum class Keyword {
	CONST,
	VAR,
	IMPORT,
	ENUM;

	val string = name.lowercase()
}