package eyre

enum class Visibility {

	/**
	 * Visible only in the scope in which the symbol is defined.
	 */
	PRIVATE,

	/**
	 * Visible everywhere.
	 */
	PUBLIC,

	/**
	 * Visible only in the module in which the symbol is defined.
	 */
	INTERNAL;

	val string = name.lowercase()

	companion object {
		val values = values()
	}

}