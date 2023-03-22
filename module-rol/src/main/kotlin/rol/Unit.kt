package rol

import xml.XmlElement

class Unit(element: XmlElement) : RolObject(element) {


	val name by StringProperty("NAME")

	val category by StringProperty("CAT")

	var moveSpeed by IntProperty("MOVES")

	var acceleration by IntProperty("ACCEL")

	var cost by StringProperty("COST")

	var rampCost by StringProperty("RAMP_COST")

	var time by StringProperty("TIME")

	var rampTime by StringProperty("RAMP_TIME")

	var hits by IntProperty("HITS")

	var pop by IntProperty("POP")

	/** Melee attack against ground units. */
	var groundMeleeAttack by StringProperty("GM")

	/** Ranged attack against ground units. */
	var groundRangedAttack by StringProperty("GA")

	/** Melee attack against air units. */
	var airMeleeAttack by StringProperty("AM")

	/** Ranged attack against air units. */
	var airRangedAttack by StringProperty("AA")

	/** Ranged attack against buildings. */
	var buildingRangedAttack by StringProperty("BA")



	override fun toString() = """
		$name
			category:           $category
			moveSpeed:          $moveSpeed
			cost:               $cost
			rampCost:           $rampCost
			time:				$time
			rampTime:			$rampTime
			hits:               $hits
			pop:                $pop
			groundMeleeAttack:  $groundMeleeAttack
			groundRangedAttack: $groundRangedAttack
			airMeleeAttack:     $airMeleeAttack
			airRangedAttack:    $airRangedAttack
	""".trimIndent()


}