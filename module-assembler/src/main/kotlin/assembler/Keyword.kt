package assembler

enum class Keyword {
	CONST,
	VAR,
	IMPORT,
	ENUM,
	NAMESPACE;

	val string = name.lowercase()
}