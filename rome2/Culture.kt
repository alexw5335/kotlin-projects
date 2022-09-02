package rome2

enum class Culture(
	val prefix: String,
	val basicMeleeGarrison: GarrisonGroup,
	val strongMeleeGarrison: GarrisonGroup,
) {

	ROME(
		"rome",
		GarrisonGroups.ROMAN_BASIC_MELEE,
		GarrisonGroups.ROMAN_STRONG_MELEE
	),

	EAST(
		"east",
		GarrisonGroups.EASTERN_BASIC_MELEE,
		GarrisonGroups.EASTERN_STRONG_MELEE
	),

	GREEK(
		"greek",
		GarrisonGroups.GREEK_BASIC_MELEE,
		GarrisonGroups.GREEK_STRONG_MELEE
	),

	BARB(
		"barb",
		GarrisonGroups.BARB_TRIBESMEN,
		GarrisonGroups.BARB_MEDIUM_MELEE
	);

}