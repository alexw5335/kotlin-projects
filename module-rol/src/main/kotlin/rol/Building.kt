package rol

import core.xml.XmlElement

class Building(element: XmlElement) : RolObject(element) {


	var name by StringProperty("NAME")

	var cost by StringProperty("COST")

	var rampCost by StringProperty("RAMP_COST")

	var time by StringProperty("TIME")

	var groundAttack by StringProperty("GA")

	var airAttack by StringProperty("AA")

	var lineOfSight by IntProperty("LOS")

	var hits by IntProperty("HITS")



	override fun toString() = """
		$name
			cost:         $cost
			rampCost:     $rampCost
			time:         $time
			groundAttack: $groundAttack
			airAttack:    $airAttack
			lineOfSight:  $lineOfSight
			hits:         $hits
	""".trimIndent()

}