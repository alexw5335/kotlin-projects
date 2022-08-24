package rome2

enum class Culture(
	val prefix: String,
	val basicMeleeGarrison: GarrisonUnit,
	val strongMeleeGarrison: GarrisonUnit,
	val eliteMeleeGarrison: GarrisonUnit,
) {

	ROME(
		"rome",
		GarrisonUnit.ROMAN_BASIC_MELEE,
		GarrisonUnit.ROMAN_STRONG_MELEE,
		GarrisonUnit.ROMAN_ELITE_MELEE,
	),

	EAST(
		"east",
		GarrisonUnit.EASTERN_BASIC_MELEE,
		GarrisonUnit.EASTERN_STRONG_MELEE,
		GarrisonUnit.EASTERN_ELITE_MELEE
	),

	GREEK(
		"greek",
		GarrisonUnit.GREEK_BASIC_MELEE,
		GarrisonUnit.GREEK_STRONG_MELEE,
		GarrisonUnit.GREEK_ELITE_MELEE
	),

	BARB(
		"barb",
		GarrisonUnit.BARB_TRIBESMEN,
		GarrisonUnit.BARB_MEDIUM_MELEE,
		GarrisonUnit.BARB_ELITE_MELEE
	);

}