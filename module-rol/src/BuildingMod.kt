package rol

import kotlin.Unit

class BuildingMod(
	val building     : String? = null,
	val buildings    : (Building) -> Boolean = { false },
	val block        : (Building) -> Unit = { },
	val cost         : String? = null,
	val rampCost     : String? = null,
	val time         : String? = null,
	val hits         : Int? = null,
	val groundAttack : String? = null,
	val airAttack    : String? = null,
	val lineOfSight  : Int? = null,
)