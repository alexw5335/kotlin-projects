package rol

class UnitMod(
	val unit               : String? = null,
	val units              : (Unit) -> Boolean = { false },
	val block              : (Unit) -> kotlin.Unit = { },
	val moveSpeed          : Int? = null,
	val acceleration       : Int? = null,
	val cost               : String? = null,
	val rampCost           : String? = null,
	val time               : String? = null,
	val rampTime           : String? = null,
	val hits               : Int? = null,
	val pop                : Int? = null,
	val groundMeleeAttack  : String? = null,
	val groundRangedAttack : String? = null,
	val airMeleeAttack     : String? = null,
	val airRangedAttack    : String? = null
)