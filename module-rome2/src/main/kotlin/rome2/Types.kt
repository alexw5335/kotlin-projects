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
	private var value = parts[index].toIntOrNull() ?: parts[index].toFloat().toInt()
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
		parts[index] = value.toString()
		this.value = value
	}
}



class FloatProperty(
	private val parts: MutableList<String>,
	private val index: Int
) {
	private var value = parts[index].toFloat()
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) {
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
	protected fun FloatProperty(index: Int) = FloatProperty(parts, index)
	protected fun StringProperty(index: Int) = StringProperty(parts, index)
	protected fun BooleanProperty(index: Int) = BooleanProperty(parts, index)
	protected fun<T> ObjectProperty(index: Int, getter: (String) -> T, setter: (T) -> String) = ObjectProperty(parts, index, getter, setter)
}



abstract class Rome2Object2(line1: String, line2: String) : Rome2Object(line1) {
	private val parts2 = line2.split('\t').toMutableList()
	val assembleLine2 get() = parts2.joinToString("\t")
	protected fun IntProperty2(index: Int) = IntProperty(parts2, index)
	protected fun FloatProperty2(index: Int) = FloatProperty(parts2, index)
	protected fun StringProperty2(index: Int) = StringProperty(parts2, index)
	protected fun BooleanProperty2(index: Int) = BooleanProperty(parts2, index)
	protected fun<T> ObjectProperty2(index: Int, getter: (String) -> T, setter: (T) -> String) = ObjectProperty(parts2, index, getter, setter)
}



/*
Printing
 */



fun printUnits(vararg units: Unit) = units.forEach { it.printFormatted() }



fun Unit.printFormatted() {
	var string = """
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
			level:    ${level.string}
	""".trimIndent()

	missileWeapon?.let {
		string += """
	ranged:
		damage:    ${it.projectile.damage} (${it.name}, ${it.projectile.name})
		apDamage:  ${it.projectile.apDamage}
		range:     ${it.projectile.effectiveRange} 
		reload:    $reload
		accuracy:  $accuracy
		ammo:      $ammo"""
	}

	println(string)
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
	var name     by StringProperty(0)
	var attack   by IntProperty(14)
	var defence  by IntProperty(15)
	var morale   by IntProperty(16)
	var bonusHp  by IntProperty(17)
	var charge   by IntProperty(6)
	var level    by ObjectProperty(30, TrainingLevel::get, TrainingLevel::string)
	var armour   by ObjectProperty(3, { armour(it) }, Armour::name)
	var weapon   by ObjectProperty(22, { weapon(it) }, Weapon::name)
	var shield   by ObjectProperty(25, { shield(it) }, Shield::name)
	var cost     by IntProperty2(15)
	var upkeep   by IntProperty2(18)

	var missileWeapon by ObjectProperty(23, { missileWeapons[it] }, { it?.name ?: "" })
	var accuracy      by IntProperty(1)
	var reload        by IntProperty(44)
	var ammo          by IntProperty(2)

	val totalDefence get() = defence + shield.defence
	val totalArmour get() = armour.armour + shield.armour
}



/**
 * unit_armour_types
 */
class Armour(line: String) : Rome2Object(line) {
	var name            by StringProperty(0)
	var armour          by IntProperty(1)
	var bonusVsMissiles by IntProperty(2)
	var weakVsMissiles  by BooleanProperty(3)
}



/**
 * unit_shield_types
 */
class Shield(line: String) : Rome2Object(line) {
	var name     by StringProperty(0)
	var defence  by IntProperty(1)
	var armour   by IntProperty(2)
	var block    by IntProperty(4)
}



/**
 * melee_weapons
 */
class Weapon(line: String) : Rome2Object(line) {
	var name           by StringProperty(0)
	var armourPiercing by BooleanProperty(2)
	var cavalryBonus   by IntProperty(3)
	var largeBonus     by IntProperty(4)
	var infantryBonus  by IntProperty(5)
	var damage         by IntProperty(6)
	var apDamage       by IntProperty(7)
}



/**
 * missile_weapons
 */
class MissileWeapon(line: String) : Rome2Object(line) {
	var name         by StringProperty(0)
	var projectile   by ObjectProperty(2, { projectile(it) }, Projectile::name)
}



/**
 * projectiles
 */
class Projectile(line: String) : Rome2Object(line) {
	var name             by StringProperty(0)
	var effectiveRange   by IntProperty(7)
	var minimumRange     by IntProperty(8)
	var damage           by IntProperty(13)
	var apDamage         by IntProperty(14)
	var penetration      by StringProperty(15)
	var bonus            by StringProperty(11)
	var muzzleVelocity   by IntProperty(10)
	var collisionRadius  by FloatProperty(19)
	var baseReloadTime   by IntProperty(20)
	var infantryBonus    by IntProperty(24)
	var cavarryBonus     by IntProperty(25)
	var largeBonus       by IntProperty(26)
	
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
	var name  by StringProperty(0)
	var level by IntProperty(2)
	var turns by IntProperty(4)
	var cost  by IntProperty(5)

	val effects = ArrayList<BuildingEffect>()
	val garrisons = ArrayList<Garrison>()
	val extraGarrisons = ArrayList<Garrison>()
	val extraEffects = ArrayList<BuildingEffect>()
	val units = ArrayList<BuildingUnit>()
	val extraUnits = ArrayList<BuildingUnit>()

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
 * building_units_allowed
 */
class BuildingUnit(line: String) : Rome2Object(line) {
	var id       by IntProperty(0)
	var building by StringProperty(1)
	var unit     by StringProperty(2)
	constructor(id: Int, building: String, unit: String) : this(assemble(id, building, unit, 0, "", "", "false"))
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
	var name by StringProperty(0)
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