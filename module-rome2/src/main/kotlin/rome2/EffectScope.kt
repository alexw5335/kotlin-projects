package rome2

enum class EffectScope(val string: String) {

	PROVINCE("this_province"),
	REGION("this_region"),
	REGIONS_IN_PROVINCE("regions_in_this_province"),
	BUILDING("this_building"),
	FACTION("this_faction")

}