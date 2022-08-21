package rome2

import kotlin.reflect.KProperty



/*
Utils
 */



class BooleanProperty(private val parts: MutableList<String>, private val index: Int) {
	private var value = parts[index] == "1" || parts[index] == "true"
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
		parts[index] = "1"
		this.value = value
	}
}



class IntProperty(private val parts: MutableList<String>, private val index: Int) {
	private var value = parts[index].toInt()
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
		parts[index] = value.toString()
		this.value = value
	}
}



class StringProperty(private val parts: MutableList<String>, private val index: Int) {
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = parts[index]
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
		parts[index] = value
	}
}



class ObjectProperty<T>(private val parts: MutableList<String>, private val index: Int, val getter: (String) -> T, val setter: (T) -> String) {
	private var value = getter(parts[index])
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
		parts[index] = setter(value)
		this.value = value
	}
}



abstract class Rome2Object(line: String) {
	protected val parts = line.split('\t').toMutableList()
	val assembleLine get() = parts.joinToString("\t")
	protected fun IntProperty(index: Int) = IntProperty(parts, index)
	protected fun StringProperty(index: Int) = StringProperty(parts, index)
	protected fun BooleanProperty(index: Int) = BooleanProperty(parts, index)
	protected fun<T> ObjectProperty(index: Int, getter: (String) -> T, setter: (T) -> String) = ObjectProperty(parts, index, getter, setter)
}



abstract class Rome2Object2(line1: String, line2: String) {
	protected val parts1 = line1.split('\t').toMutableList()
	protected val parts2 = line2.split('\t').toMutableList()
	val assembleLine1 get() = parts1.joinToString("\t")
	val assembleLine2 get() = parts2.joinToString("\t")
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
		for(effect in effects)
			append("\t${effect.effect} ${effect.scope} ${effect.value}\n")
	})
}



/*
Types
 */



/**
 * land_units, main_units
 */
class Unit(line1: String, line2: String) : Rome2Object2(line1, line2) {
	val name    by StringProperty(parts1, 0)
	var attack  by IntProperty(parts1, 14)
	var defence by IntProperty(parts1, 15)
	var morale  by IntProperty(parts1, 16)
	var bonusHp by IntProperty(parts1, 17)
	var charge  by IntProperty(parts1, 6)
	var armour  by ObjectProperty(parts1, 3, { armours[it]!! }, { it.name })
	var weapon  by ObjectProperty(parts1, 22, { weapons[it]!! }, { it.name })
	var shield  by ObjectProperty(parts1, 25, { shields[it]!! }, { it.name })
	var cost    by IntProperty(parts2, 15)
	var upkeep  by IntProperty(parts2, 18)

	val totalDefence get() = defence + shield.defence
	val totalArmour get() = armour.armour + shield.armour
}



/**
 * unit_armour_types
 */
class Armour(line: String) : Rome2Object(line) {
	val name            by StringProperty(parts, 0)
	var armour          by IntProperty(parts, 1)
	var bonusVsMissiles by IntProperty(parts, 2)
	var weakVsMissiles  by BooleanProperty(parts, 3)
}



/**
 * unit_shield_types
 */
class Shield(line: String) : Rome2Object(line) {
	val name     by StringProperty(parts, 0)
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
	val effect       by StringProperty(1)
	var scope        by StringProperty(2)
	var value        by IntProperty(3)
	var valueDamaged by IntProperty(4)
	var valueRuined  by IntProperty(5)
}



/**
 * building_levels
 * building_effects_junction
 */
class Building(line: String, val effects: MutableList<BuildingEffect>) : Rome2Object(line) {
	val name  by StringProperty(0)
	val level by IntProperty(2)
	val turns by IntProperty(4)
	val cost  by IntProperty(5)
}