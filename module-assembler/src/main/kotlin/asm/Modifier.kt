package asm

enum class Modifier(vararg val strings: String) {

	REP("rep", "repe", "repz", "REP", "REPE", "REPZ"),
	REPNE("repne", "repnz", "REPNE", "REPNZ"),
	REL8("rel8", "REL8"),
	REL32("rel32", "REL32"),
	FAR("far", "FAR");

	companion object {

		val map: Map<String, Modifier> = HashMap<String, Modifier>().also {
			for(modifier in values())
				for(string in modifier.strings)
					it[string] = modifier
		}

	}

}