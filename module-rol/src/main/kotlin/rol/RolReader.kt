package rol

import core.Core
import core.xml.XmlParser
import core.xml.XmlWriter

class RolReader(resourcePath: String) {


	val root = XmlParser(Core.readResourceChars(resourcePath)).parse()

	val units = root.children("UNIT").map(::Unit)

	val buildings = root.children("BUILDING").map(::Building)



	fun applyMod(mod: UnitMod) {
		var found = false
		for(u in units) {
			if(u.name == mod.unit || mod.units(u)) {
				found = true
				mod.moveSpeed?.let { u.moveSpeed = it }
				mod.acceleration?.let { u.acceleration = it }
				mod.cost?.let { u.cost = it }
				mod.rampCost?.let { u.rampCost = it }
				mod.time?.let { u.time = it }
				mod.rampTime?.let { u.rampTime = it }
				mod.hits?.let { u.hits = it }
				mod.pop?.let { u.pop = it }
				mod.groundMeleeAttack?.let { u.groundMeleeAttack = it }
				mod.groundRangedAttack?.let { u.groundRangedAttack = it }
				mod.airMeleeAttack?.let { u.airMeleeAttack = it }
				mod.airRangedAttack?.let { u.airRangedAttack = it }
				mod.block(u)
			}
		}
		if(!found) throw IllegalArgumentException("Unit '${mod.unit}' not found.")
	}



	fun applyMod(mod: BuildingMod) {
		var found = false
		for(b in buildings) {
			if(b.name == mod.building || mod.buildings(b)) {
				found = true
				mod.cost?.let { b.cost = it }
				mod.rampCost?.let { b.rampCost = it }
				mod.time?.let { b.time = it }
				mod.groundAttack?.let { b.groundAttack = it }
				mod.airAttack?.let { b.airAttack = it }
				mod.lineOfSight?.let { b.lineOfSight = it }
				mod.hits?.let { b.hits = it }
				mod.block(b)
			}
		}
		if(!found) throw IllegalArgumentException("Building '${mod.building}' not found.")
	}



	fun write(path: String) = XmlWriter(path).use {
		it.writeProlog()
		it.writeElement(root)
	}


}