package eyre

enum class Keyword {

	CONST,
	VAR,
	IMPORT,
	ENUM,
	NAMESPACE,
	FLAGS,
	STRUCT,
	PROC;

	val string = name.lowercase()

	companion object {
		val values = values()
	}

}