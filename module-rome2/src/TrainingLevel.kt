package rome2

enum class TrainingLevel {

	POORLY_TRAINED,
	TRAINED,
	WELL_TRAINED,
	ELITE,
	RABBLE;

	val string = name.lowercase()

	companion object {
		fun get(string: String) = entries.first { it.string == string }
	}

}