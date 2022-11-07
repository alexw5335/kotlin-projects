package assembler

enum class Keyword {
	CONST,
	VAR,
	IMPORT;

	val string = name.lowercase()
}