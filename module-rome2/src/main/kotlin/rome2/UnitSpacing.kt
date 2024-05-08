package rome2

enum class UnitSpacing {
	MISSILE_CAV,
	CAV_ROMAN,
	SPEAR_INF,
	MELEE_INF_ROMAN_RECRUITS,
	FALX_INF,
	MISSILE_INF,
	MELEE_INF_BARB,
	CHARIOT,
	GIANT_ARTILLERY,
	CAV_MELEE,
	PIKEMEN,
	ANIMALS,
	ELEPHANT,
	ARTILLERY,
	MELEE_INF_ROMAN;
	val string = name.lowercase()
	companion object { val map = values().associateBy { it.string } }
}