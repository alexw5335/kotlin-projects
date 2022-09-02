package rome2



interface EntryType {
	val entry: PackEntry
	fun string(index: Int) = entry.string(index)
	fun int(index: Int) = entry.int(index)
	fun float(index: Int) = entry.float(index)
	fun boolean(index: Int) = entry.boolean(index)
	fun<T> any(index: Int, getter: (String) -> T, setter: (T) -> String) = entry.any(index, getter, setter)
	fun <T : NamedType> any(index: Int, getter: (String) -> T) = entry.any(index, getter)
	fun booleanString(index: Int) = entry.booleanString(index)
}



interface NamedType : EntryType {
	var name: String
}



interface CompoundType {
	fun addMod()
}



/*
Units
 */



class LandUnitData(override val entry: PackEntry) : NamedType {
	override var name by entry.string(11)
}



class MainUnitData(override val entry: PackEntry) : NamedType {
	override var name by entry.string(17)
}



class LandUnit(val landUnitData: LandUnitData, val mainUnitData: MainUnitData) : CompoundType {

	var name
		get() = landUnitData.name
		set(value) { landUnitData.name = value; mainUnitData.name = value }

	var accuracy      by landUnitData.int(0)
	var ammo          by landUnitData.int(1)
	var armour        by landUnitData.any(2, { armour(it) }, { it.name })
	var movement      by landUnitData.int(3)
	var category      by landUnitData.string(4)
	var charge        by landUnitData.int(5)
	var attack        by landUnitData.int(14)
	var defence       by landUnitData.int(15)
	var morale        by landUnitData.int(16)
	var bonusHp       by landUnitData.int(17)
	var weapon        by landUnitData.any(22, { weapon(it) }, { it.name })
	var missileWeapon by landUnitData.any(23, { missileWeapons[it] }, { it?.name ?: "" })
	var shield        by landUnitData.any(25, { shield(it) }, { it.name })
	var siegeEngine   by landUnitData.any(34, { siegeEngines[it] }, { it?.name ?: "" })
	var reload        by landUnitData.int(44)
	var cost          by mainUnitData.int(14)
	var upkeep        by mainUnitData.int(18)

	val totalArmour get() = armour.armour + shield.armour
	val totalDefence get() = defence + shield.defence

	override fun addMod() { landUnitData.addMod(); mainUnitData.addMod() }

}



val LandUnit.formattedString get() = buildString {
	appendLine(name)
	appendLine("\tattack:   $attack")
	appendLine("\tdefence:  $totalDefence = $defence (base) + ${shield.defence} (${shield.name})")
	appendLine("\tmorale:   $morale")
	appendLine("\tbonusHp:  $bonusHp")
	appendLine("\tcharge:   $charge")
	appendLine("\tarmour:   $totalArmour = ${armour.armour} (${armour.name}) + ${shield.defence} (${shield.name})")
	appendLine("\tdamage:   ${weapon.damage}/${weapon.apDamage} (${weapon.name})")
	appendLine("\tcost:     $cost")
	appendLine("\tupkeep:   $upkeep")

	val rangedWeapon = missileWeapon ?: siegeEngine?.weapon ?: return@buildString

	if(missileWeapon != null)
		appendLine("\tranged (${rangedWeapon.name}, ${rangedWeapon.projectile.name})")
	else
		appendLine("\tsiege (${siegeEngine!!.name}, ${rangedWeapon.name}, ${rangedWeapon.projectile.name})")

	appendLine("\t\tdamage:    ${rangedWeapon.projectile.damage}")
	appendLine("\t\tapDamage:  ${rangedWeapon.projectile.apDamage}")
	appendLine("\t\trange:     ${rangedWeapon.projectile.range}")
	appendLine("\t\treload:    $reload")
	appendLine("\t\taccuracy:  $accuracy")
	appendLine("\t\tammo:      $ammo")

}



/*
Equipment
 */



class Shield(override val entry: PackEntry) : NamedType {
	override var name by string(0)
	var defence       by int(1)
	var armour        by int(2)
	var blockChance   by int(4)
}



class Armour(override val entry: PackEntry) : NamedType {
	override var name   by string(2)
	var armour          by int(0)
	var bonusVsMissiles by booleanString(1)
	var weakVsMissiles  by booleanString(3)
}



class Weapon(override val entry: PackEntry) : NamedType {
	override var name     by string(5)
	var cavalryBonus      by int(2)
	var largeBonus        by int(3)
	var infantryBonus     by int(4)
	var damage            by int(6)
	var apDamage          by int(7)
	var firstStrike       by int(8)
	var shieldPiercing    by boolean(9)
	var armourPenetrating by boolean(0)
	var armourPiercing    by boolean(1)
}



class Projectile(override val entry: PackEntry) : NamedType {
	override
	var name     by entry.string(0)
	var range    by entry.int(7)
	var damage   by entry.int(13)
	var apDamage by entry.int(14)
}



class MissileWeapon(override val entry: PackEntry) : NamedType {
	override
	var name       by entry.string(0)
	var projectile by entry.any(2, { projectile(it) }, { it.name })
}



class SiegeEngine(override val entry: PackEntry) : NamedType {
	override
	var name   by entry.string(4)
	var type   by entry.string(2)
	var weapon by entry.any(5, ::missileWeapon)
}



/*
Garrisons
 */



class GarrisonGroupUnit(override val entry: PackEntry) : EntryType {
	var id       by int(0)
	var priority by int(2)
	var unit     by string(3)
	var group    by string(4)
}



class GarrisonGroupData(override val entry: PackEntry) : NamedType {
	override var name by string(0)
}



class GarrisonGroup(val data: GarrisonGroupData, val units: List<GarrisonGroupUnit>) : CompoundType {
	var name by data::name
	override fun addMod() { data.addMod() }
}



val GarrisonGroup.formattedString get() = buildString {
	appendLine(name)
	for(u in units)
		appendLine("\t${u.unit} : ${u.priority} : ${u.id}")
}



/*
Buildings
 */



class Garrison(override val entry: PackEntry) : EntryType {
	var building by string(0)
	var id       by int(1)
	var group    by string(3)
}



class BuildingData(override val entry: PackEntry) : NamedType {
	override var name by string(0)
}



class BuildingEffect(override val entry: PackEntry) : EntryType {
	var building     by string(0)
	var effect       by string(1)
	var scope        by string(2)
	var value        by float(3)
	var damagedValue by float(4)
	var ruinedValue  by float(5)

	constructor(building: Building, effect: BuildingEffectType, scope: EffectScope, value: Int) : this(PackEntry(listOf(
		PackFieldString(building.name),
		PackFieldString(effect.string),
		PackFieldString(scope.string),
		PackFieldFloat(value.toFloat()),
		PackFieldFloat(value.toFloat()),
		PackFieldFloat(value.toFloat())
	)))
}



class Building(val data: BuildingData, val effects: List<BuildingEffect>, val garrisons: List<Garrison>) : CompoundType {
	var name  by data::name
	var level by data.int(2)
	var turns by data.int(4)
	var cost  by data.int(5)
	override fun addMod() { data.addMod() }
}



val Building.formattedString get() = buildString {
	appendLine(name)
	appendLine("\tcost:   $cost")
	appendLine("\tturns:  $turns")

	appendLine("\teffects:")
	for(effect in effects)
		appendLine("\t\t${effect.effect} : ${effect.scope} : ${effect.value.toString().dropLast(2)}")

	appendLine("\tgarrisons:")
	for(garrison in garrisons)
		appendLine("\t\t${garrison.group}")
}



/*
Techs
 */



class TechData(override val entry: PackEntry) : NamedType {
	override var name by string(0)
}



class TechEffect(override val entry: PackEntry) : EntryType {
	var tech   by string(0)
	var effect by string(1)
	var scope  by string(2)
	var value  by float(3)

	constructor(tech: Tech, effect: TechEffectType, scope: EffectScope, value: Int) : this(PackEntry(listOf(
		PackFieldString(tech.name),
		PackFieldString(effect.string),
		PackFieldString(scope.string),
		PackFieldFloat(value.toFloat())
	)))
}



class Tech(val data: TechData, val effects: List<TechEffect>) : CompoundType {
	var name by data::name
	var cost by data.int(3)
	override fun addMod() { data.addMod() }
}



val Tech.formattedString get() = buildString {
	appendLine(name)
	appendLine("\tcost: $cost")
	appendLine("\teffects:")
	for(effect in effects)
		appendLine("\t\t${effect.effect} :: ${effect.scope} :: ${effect.value}")
}



/*
Skills
 */



class ExperienceTier(override val entry: PackEntry) : EntryType {
	var agent       by string(0)
	var experience  by int(1)
	var skillPoints by int(2)
	var rank        by int(3)
	var forArmy     by boolean(5)
	var forNavy     by boolean(6)
}



class SkillData(override val entry: PackEntry) : NamedType {
	override var name by string(1)
	var unlockRank by int(5)
}



class SkillLevelData(override val entry: PackEntry) : EntryType {
	var skill by string(4)
	var level by int(3)
	var unlockRank by int(6)
}



class SkillEffect(override val entry: PackEntry) : EntryType {
	var skill by string(0)
	var effect by string(1)
	var scope by string(2)
	var level by int(3)
	var value by float(4)
}



class SkillLevel(val data: SkillLevelData, val effects: List<SkillEffect>) : CompoundType {
	var level by data::level
	var unlockRank by data::unlockRank
	override fun addMod() { data.addMod() }
}



class Skill(val data: SkillData, val level1: SkillLevel, val level2: SkillLevel, val level3: SkillLevel) : CompoundType {
	var name by data::name
	var unlockRank by data::unlockRank
	override fun addMod() { data.addMod() }
}



val Skill.formattedString get() = buildString {
	appendLine(name)
	appendLine("\tunlockRank: $unlockRank")
	for(level in listOf(level1, level2, level3)) {
		appendLine("\tlevel ${level.level} (${level.unlockRank}):")
		for(effect in level.effects)
			appendLine("\t\t${effect.effect} :: ${effect.scope} :: ${effect.value.toString().dropLast(2)}")
	}
}
