package asm

enum class Modifier(vararg val strings: String) {

	REP("rep", "repe", "repz", "REP", "REPE", "REPZ"),
	REPNE("repne", "repnz", "REPNE", "REPNZ"),
	SHORT("short", "SHORT"),
	FAR("far", "FAR"),
	O16("o16", "O16");

	companion object {

		val map: Map<String, Modifier> = HashMap<String, Modifier>().also {
			for(modifier in values())
				for(string in modifier.strings)
					it[string] = modifier
		}

	}

}