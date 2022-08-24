package rome2

import kotlin.reflect.KProperty



/*
Utils
 */



class BooleanProperty(
	private val parts: MutableList<String>,
	private val index: Int
) {
	private var value = parts[index] == "1" || parts[index] == "true"
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
		parts[index] = "1"
		this.value = value
	}
}



class IntProperty(
	private val parts: MutableList<String>,
	private val index: Int
) {
	private var value = parts[index].toInt()
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
		parts[index] = value.toString()
		this.value = value
	}
}



class StringProperty(
	private val parts: MutableList<String>,
	private val index: Int
) {
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = parts[index]
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
		parts[index] = value
	}
}



class ObjectProperty<T>(
	private val parts: MutableList<String>,
	private val index: Int,
	private val getter: (String) -> T,
	private val setter: (T) -> String
) {
	private var value = getter(parts[index])
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
		parts[index] = setter(value)
		this.value = value
	}
}



abstract class Rome2Object(line: String) {
	private val parts = line.split('\t').toMutableList()
	val assembleLine get() = parts.joinToString("\t")
	protected fun IntProperty(index: Int) = IntProperty(parts, index)
	protected fun StringProperty(index: Int) = StringProperty(parts, index)
	protected fun BooleanProperty(index: Int) = BooleanProperty(parts, index)
	protected fun<T> ObjectProperty(index: Int, getter: (String) -> T, setter: (T) -> String) = ObjectProperty(parts, index, getter, setter)
}



abstract class Rome2Object2(line1: String, line2: String) : Rome2Object(line1) {
	private val parts2 = line2.split('\t').toMutableList()
	val assembleLine2 get() = parts2.joinToString("\t")
	protected fun IntProperty2(index: Int) = IntProperty(parts2, index)
	protected fun StringProperty2(index: Int) = StringProperty(parts2, index)
	protected fun BooleanProperty2(index: Int) = BooleanProperty(parts2, index)
	protected fun<T> ObjectProperty2(index: Int, getter: (String) -> T, setter: (T) -> String) = ObjectProperty(parts2, index, getter, setter)
}



/*
Printing
 */



fun printUnits(vararg units: Unit) = units.forEach { it.printFormatted() }



fun Unit.printFormatted() {
	println("""
		$name:
			attack:   $attack
			defence:  $totalDefence = $defence base + ${shield.defence} ${shield.name}
			morale:   $morale
			bonusHp:  $bonusHp
			charge:   $charge
			armour:   $totalArmour = ${armour.armour} ${armour.name} + ${shield.armour} ${shield.name}
			damage:   ${weapon.damage}  ${weapon.name}
			bonus:    ${weapon.cavalryBonus}/${weapon.largeBonus}/${weapon.infantryBonus}
			cost:     $cost
			upkeep:   $upkeep
	""".trimIndent())
	println()
}



fun Building.printFormatted() {
	println(buildString {
		append("$name\n")
		append("\tcost:  $cost\n")
		append("\tturns: $turns\n")
		append("\teffects:\n")
		for(effect in effects)
			append("\t\t${effect.effect} :: ${effect.scope} :: ${effect.value}\n")
		if(garrisons.isNotEmpty()) {
			append("\tgarrison:\n")
			for(garrison in garrisons)
				append("\t\t${garrison.unit}\n")
		}
	})
}



fun Tech.printFormatted() {
	println(buildString {
		append("$name\n")
		append("\tcost: $cost\n")
		append("\teffects:\n")
		for(effect in effects)
			append("\t\t${effect.effect} :: ${effect.scope} :: ${effect.value}\n")
	})
}



/*
Types
 */



/**
 * land_units, main_units
 */
class Unit(line1: String, line2: String) : Rome2Object2(line1, line2) {
	val name    by StringProperty(0)
	var attack  by IntProperty(14)
	var defence by IntProperty(15)
	var morale  by IntProperty(16)
	var bonusHp by IntProperty(17)
	var charge  by IntProperty(6)
	var armour  by ObjectProperty(3, { armour(it) }, Armour::name)
	var weapon  by ObjectProperty(22, { weapon(it) }, Weapon::name)
	var shield  by ObjectProperty(25, { shield(it) }, Shield::name)
	var cost    by IntProperty2(15)
	var upkeep  by IntProperty2(18)

	val totalDefence get() = defence + shield.defence
	val totalArmour get() = armour.armour + shield.armour
}



/**
 * unit_armour_types
 */
class Armour(line: String) : Rome2Object(line) {
	val name            by StringProperty(0)
	var armour          by IntProperty(1)
	var bonusVsMissiles by IntProperty(2)
	var weakVsMissiles  by BooleanProperty(3)
}



/**
 * unit_shield_types
 */
class Shield(line: String) : Rome2Object(line) {
	val name     by StringProperty(0)
	var defence  by IntProperty(1)
	var armour   by IntProperty(2)
	var block    by IntProperty(4)
}



/**
 * melee_weapons
 */
class Weapon(line: String) : Rome2Object(line) {
	val name           by StringProperty(0)
	var armourPiercing by BooleanProperty(2)
	var cavalryBonus   by IntProperty(3)
	var largeBonus     by IntProperty(4)
	var infantryBonus  by IntProperty(5)
	var damage         by IntProperty(6)
	var apDamage       by IntProperty(7)
}



/**
 * building_effects_junction
 */
class BuildingEffect(line: String) : Rome2Object(line) {
	val building     by StringProperty(0)
	val effect       by StringProperty(1)
	var scope        by StringProperty(2)
	var value        by IntProperty(3)
	var valueDamaged by IntProperty(4)
	var valueRuined  by IntProperty(5)
}



/**
 * building_levels
 */
class Building(line: String) : Rome2Object(line) {
	val name  by StringProperty(0)
	var level by IntProperty(2)
	var turns by IntProperty(4)
	var cost  by IntProperty(5)

	val effects = ArrayList<BuildingEffect>()
	val garrisons = ArrayList<Garrison>()
	val extraGarrisons = ArrayList<Garrison>()
	val extraEffects = ArrayList<BuildingEffect>()

	val adjustedLevel = level + 1

	val isLevel1 get() = level == 0
	val isLevel2 get() = level == 1
	val isLevel3 get() = level == 2
	val isLevel4 get() = level == 3
	val isLevel5 get() = level == 4

	val culture = when {
		name.startsWith("rome_")  -> Culture.ROME
		name.startsWith("east_")  -> Culture.EAST
		name.startsWith("greek_") -> Culture.GREEK
		name.startsWith("barb_")  -> Culture.BARB
		else                      -> null
	}
}



/**
 * building_level_armed_citizenry_junctions
 */
class Garrison(line: String) : Rome2Object(line) {
	val id       by IntProperty(0)
	val building by StringProperty(1)
	var unit     by StringProperty(2)
	constructor(id: Int, building: String, unit: String) : this(assemble(id, building, unit))
}



/**
 * technologies
 */
class Tech(line: String) : Rome2Object(line) {
	val name by StringProperty(0)
	var cost by IntProperty(3)

	val effects = ArrayList<TechEffect>()
	val extraEffects = ArrayList<TechEffect>()
}



/**
 * technology_effects_junction
 */
class TechEffect(line: String) : Rome2Object(line) {
	val tech  by StringProperty(0)
	var effect by StringProperty(1)
	var scope  by StringProperty(2)
	var value  by IntProperty(3)
	constructor(tech: String, effect: String, scope: String, value: Int) : this(assemble(tech, effect, scope, value.toString()))
}



private fun assemble(vararg parts: Any) = parts.joinToString("\t")