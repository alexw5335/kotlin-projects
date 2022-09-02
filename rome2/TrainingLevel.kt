package rome2

enum class TrainingLevel {

	POORLY_TRAINED,
	TRAINED,
	WELL_TRAINED,
	ELITE,
	RABBLE;

	val string = name.lowercase()

	companion object {
		private val values = values()
		fun get(string: String) = values.first { it.string == string }
	}

}