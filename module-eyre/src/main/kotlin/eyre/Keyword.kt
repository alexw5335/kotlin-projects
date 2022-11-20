package eyre

enum class Keyword {

	CONST,
	VAR,
	IMPORT,
	ENUM,
	NAMESPACE;

	val string = name.lowercase()

	companion object {
		val values = values()
	}

}