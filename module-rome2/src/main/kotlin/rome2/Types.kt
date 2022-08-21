package rome2

import kotlin.reflect.KProperty



fun printUnits(vararg units: Unit) = units.forEach { it.printFormatted() }



fun Unit.printFormatted() {
	println("""
		$name:
			attack:   $attack
			defence:  $totalDefence = $defence base + ${shield.defence} ${shield.name}
			morale:   $morale
			bonusHp:  $bonusHp
			charge:   $charge
			armour:   $totalArmour = ${armour.value} ${armour.name} + ${shield.armour} ${shield.name}
			damage:   ${weapon.damage}  ${weapon.name}
			bonus:    ${weapon.cavalryBonus}/${weapon.largeBonus}/${weapon.infantryBonus}
			cost:     $cost
			upkeep:   $upkeep
	""".trimIndent())
	println()
}



data class UnitInfo(
	val name: String,
	val cost: Int,
	val upkeep: Int
)



data class Unit(
	val name: String,
	val attack: Int,
	val defence: Int,
	val morale: Int,
	val bonusHp: Int,
	val charge: Int,
	val armour: Armour,
	val weapon: Weapon,
	val shield: Shield,
	val cost: Int,
	val upkeep: Int,
) {
	val totalArmour = armour.value + shield.armour
	val totalDefence = defence + shield.defence
	override fun hashCode() = name.hashCode()
	override fun equals(other: Any?) = other is Unit && other.name == name
}



class IntProperty(private val parts: MutableList<String>, private val index: Int) {
	private var value = parts[index].toInt()

	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value


	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
		parts[index] = value.toString()
		this.value = value
	}
}



class Armour2(line: String) {
	private val parts = line.split('\t').toMutableList()
	var armour by IntProperty(parts, 0)
}



data class Armour(
	val name            : String,
	val value           : Int,
	val missileBonus    : Int,
	val weakVsMissiles  : Boolean
)



data class Shield(
	val name               : String,
	val defence            : Int,
	val armour             : Int,
	val missileBlockChance : Int
)



data class Weapon(
	val name              : String,
	val damage            : Int,
	val apDamage          : Int,
	val cavalryBonus      : Int,
	val largeBonus        : Int,
	val infantryBonus     : Int,
	val isArmourPiercing  : Boolean
)



data class BuildingEffect(
	val effect       : String,
	val scope        : String,
	val value        : Int,
	val valueDamaged : Int,
	val valueRuined  : Int
)



data class BuildingInfo(
	val name      : String,
	val level     : Int,
	val buildTime : Int,
	val cost      : Int
)



data class Building(
	val name     : String,
	val level    : Int,
	val turns    : Int,
	val cost     : Int,
	val effects  : List<BuildingEffect>
)



fun Building.printFormatted() {
	println(buildString {
		append("$name\n")
		append("\tcost:  $cost\n")
		append("\tturns: $turns\n")
		for(effect in effects)
			append("\t${effect.effect} ${effect.scope} ${effect.value}\n")
	})
}