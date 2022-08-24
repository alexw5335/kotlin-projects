package rome2

enum class GarrisonUnit(val string: String) {

	ROMAN_BASIC_RANGED("rom_roman_basic_ranged"),
	ROMAN_MEDIUM_RANGED("rom_roman_medium_ranged"),
	ROMAN_BASIC_MELEE("rom_roman_basic_melee"),
	ROMAN_MEDIUM_MELEE("rom_roman_medium_melee"),
	ROMAN_STRONG_MELEE("rom_roman_strong_melee"),
	ROMAN_ELITE_MELEE("rom_roman_elite_melee"),
	ROMAN_LEVY_MELEE("rom_roman_vigiles_garrison"),

	EASTERN_BASIC_MELEE("rom_eastern_basic_melee"),
	EASTERN_STRONG_MELEE("rom_eastern_strong_melee"),
	EASTERN_ELITE_MELEE("rom_eastern_elite_melee"),

	GREEK_BASIC_MELEE("rom_hellenic_basic_melee"),
	GREEK_STRONG_MELEE("rom_hellenic_strong_melee"),
	GREEK_ELITE_MELEE("rom_hellenic_elite_melee"),

	BARB_TRIBESMEN("rom_barbarian_tribesmen"),
	BARB_MEDIUM_MELEE("rom_barbarian_medium_melee"),
	BARB_ELITE_MELEE("rom_barbarian_elite_melee");

}