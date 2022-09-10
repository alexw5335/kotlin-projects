package rome2

import kotlin.math.roundToInt



interface EntryType {
	val entry: PackEntry
	fun string(index: Int) = entry.string(index)
	fun int(index: Int) = entry.int(index)
	fun float(index: Int) = entry.float(index)
	fun boolean(index: Int) = entry.boolean(index)
	fun<T> any(index: Int, getter: (String) -> T, setter: (T) -> String) = entry.any(index, getter, setter)
	fun <T : NamedType> any(index: Int, getter: (String) -> T) = entry.any(index, getter)
	fun booleanString(index: Int) = entry.booleanString(index)
	fun intFloat(index: Int) = entry.intFloat(index)
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
	override var name by string(11)
}



class NavalUnit(override val entry: PackEntry) : NamedType {
	override var name by string(4)
	var category by string(1)
	var type by string(2)
}



class MainUnitData(override val entry: PackEntry) : NamedType {
	override var name by string(17)
	var isNaval by boolean(4)
	var landUnit by string(5)
	var navalUnit by string(9)
	var worldLeaderOnly by boolean(22)
	var additionalBuildingRequirement by string(0)
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
	var level         by landUnitData.any(30, { TrainingLevel.get(it) }, { it.string })
	var reload        by landUnitData.int(44)
	var cost          by mainUnitData.int(14)
	var upkeep        by mainUnitData.int(18)
	var cap           by mainUnitData.int(1)
	var navalUnit     by mainUnitData.string(9)
	var spacing       by landUnitData.string(27)
	var numGuns       by landUnitData.int(31)

	val totalArmour get() = armour.armour + shield.armour
	val totalDefence get() = defence + shield.defence

	// reload is a percentage reduction of reloadTime
	val shotsPerMinute get() = (missileWeapon ?: siegeEngine?.weapon)?.let {
		(60F / (it.projectile.reloadTime * ((100F - reload) / 100F))).roundToInt()
	} ?: 0

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

	val proj = rangedWeapon.projectile
	appendLine("\t\tdamage:       ${proj.damage}")
	appendLine("\t\tapDamage:     ${proj.apDamage}")
	appendLine("\t\trange:        ${proj.range}")
	appendLine("\t\tfireRate:     $shotsPerMinute (baseReload: ${proj.reloadTime}s, reloadSkill: $reload%)")
	appendLine("\t\taccuracy:     $accuracy")
	appendLine("\t\tammo:         $ammo")
	appendLine("\t\tmarksmanship: ${proj.marksmanship}")
	appendLine("\t\tvelocity:     ${proj.velocity}")

	if(siegeEngine != null) {
		appendLine("\t\tpenetration:  ${proj.penetration}")
		appendLine("\t\tmarksmanship: ${proj.marksmanship}")
		appendLine("\t\tcollision:    ${proj.collision}")
		appendLine("\t\tshockwave:    ${proj.shockwave}")
		appendLine("\t\tvelocity:     ${proj.velocity}")
		appendLine("\t\tspread:       ${proj.spread}")
	}
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
	var name          by string(0)
	var range         by int(7)
	var damage        by int(13)
	var apDamage      by int(14)
	var reloadTime    by intFloat(20)
	var category      by string(1)
	var marksmanship  by float(11)
	var spread        by float(12)
	var velocity      by float(10)
	var penetration   by string(15)
	var collision     by float(19)
	var shockwave     by float(30)
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

	constructor(group: String, unit: LandUnit, id: Int, priority: Int) : this(PackEntry(listOf(
		PackFieldInt(id),
		PackFieldInt(0),
		PackFieldInt(priority),
		PackFieldString(unit.name),
		PackFieldString(group)
	)))
}



class GarrisonGroupData(override val entry: PackEntry) : NamedType {
	override var name by string(0)
	constructor(name: String) : this(PackEntry(listOf(PackFieldString(name))))
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

	constructor(building: Building, id: Int, group: GarrisonGroup) : this(PackEntry(listOf(
		PackFieldString(building.name),
		PackFieldInt(id),
		PackFieldInt(0),
		PackFieldString(group.name)
	)))
}



class BuildingUnit(override val entry: PackEntry) : EntryType {
	var building by string(0)
	var unit by string(1)
	var id by int(4)

	constructor(building: String, unit: String, id: Int) : this(PackEntry(listOf(
		PackFieldString(building),
		PackFieldString(unit),
		PackFieldInt(0),
		PackFieldString(""),
		PackFieldInt(id),
		PackFieldInt(0),
		PackFieldString(""),
		PackFieldBoolean(false)
	)))
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



class Building(
	val data      : BuildingData,
	val effects   : List<BuildingEffect>,
	val garrisons : List<Garrison>,
	val units     : List<BuildingUnit>
) : CompoundType {
	var name  by data::name
	var level by data.int(2)
	var turns by data.int(4)
	var cost  by data.int(5)

	val adjustedLevel get() = level + 1

	val culture get() = when {
		name.startsWith("rome_") -> Culture.ROME
		name.startsWith("greek_") -> Culture.GREEK
		name.startsWith("east_") -> Culture.EAST
		name.startsWith("barb_") -> Culture.BARB
		else -> null
	}

	override fun addMod() { data.addMod() }
}



val Building.formattedString get() = buildString {
	appendLine(name)
	appendLine("\tcost:   $cost")
	appendLine("\tturns:  $turns")

	if(effects.isNotEmpty()) {
		appendLine("\teffects:")
		for(effect in effects)
			appendLine("\t\t${effect.effect} : ${effect.scope} : ${effect.value.toString().dropLast(2)}")
	}

	if(garrisons.isNotEmpty()) {
		appendLine("\tgarrisons:")
		for(garrison in garrisons)
			appendLine("\t\t${garrison.group}")
	}

	if(units.isNotEmpty()) {
		appendLine("\tunits:")
		for(unit in units)
			appendLine("\t\t${unit.unit} :: ${unit.id}")
	}
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



class TechUnitUpgrade(override val entry: PackEntry) : EntryType {
	var cost by int(0)
	var new  by string(1)
	var tech by string(2)
	var prev by string(3)

	constructor(cost: Int, new: String, tech: String, prev: String) : this(PackEntry(listOf(
		PackFieldInt(cost),
		PackFieldString(new),
		PackFieldString(tech),
		PackFieldString(prev)
	)))
}



class Tech(val data: TechData, val effects: List<TechEffect>, val unitUpgrades: List<TechUnitUpgrade>) : CompoundType {
	var name by data::name
	var cost by data.int(3)
	override fun addMod() { data.addMod() }
}



val Tech.formattedString get() = buildString {
	appendLine(name)
	appendLine("\tcost: $cost")

	if(effects.isNotEmpty()) {
		appendLine("\teffects:")
		for(effect in effects)
			appendLine("\t\t${effect.effect} :: ${effect.scope} :: ${effect.value}")
	}

	if(unitUpgrades.isNotEmpty()) {
		appendLine("\tunitUpgrades:")
		for(u in unitUpgrades)
			appendLine("\t\t${u.prev} -> ${u.new} ($cost)")
	}
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

	constructor(agent: String, experience: Int, skillPoints: Int, rank: Int, forArmy: Boolean, forNavy: Boolean) : this(PackEntry(listOf(
		PackFieldString(agent),
		PackFieldInt(experience),
		PackFieldInt(skillPoints),
		PackFieldInt(rank),
		PackFieldString(""),
		PackFieldBoolean(forArmy),
		PackFieldBoolean(forNavy)
	)))
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



/*
Events
 */



class Mission(override val entry: PackEntry) : NamedType {
	override var name by string(1)
	var enabled by boolean(0)
	var description by string(2)
	var title by string(3)
	var type by string(4)
	var prioritised by boolean(7)
}



class Dilemma(override var entry: PackEntry) : NamedType {
	override var name by string(1)
	var enabled by boolean(0)
	var description by string(2)
	var title by string(3)
	var prioritised by boolean(6)
}



class Incident(override var entry: PackEntry) : NamedType {
	override var name by string(1)
	var enabled by boolean(0)
	var prioritised by boolean(4)
}



/*
Difficulty
 */



class DifficultyEffect(override val entry: PackEntry) : EntryType {
	var difficulty by int(0)
	var isHuman by boolean(1)
	var effect by string(2)
	var scope by string(3)
	var value by float(4)

	constructor(difficulty: Int, isHuman: Boolean, effect: String, scope: String, value: Float) : this(PackEntry(listOf(
		PackFieldInt(difficulty),
		PackFieldBoolean(isHuman),
		PackFieldString(effect),
		PackFieldString(scope),
		PackFieldFloat(value)
	)))
}



class Difficulty(val level: Int, val effects: MutableList<DifficultyEffect>)



val Difficulty.formattedString get() = buildString {
	val name = when(level) {
		-1   -> "easy (-1)"
		0    -> "normal (0)"
		1    -> "hard (1)"
		2    -> "very hard (2)"
		3    -> "legendary (3)"
		else -> "other (${level})"
	}

	appendLine(name)
	for(e in effects) {
		append("\t${e.effect}  ::  ${e.scope}  ::  ${e.value}")
		if(e.isHuman) append("  (human)")
		append('\n')
	}
}



	/*
	Other
	 */
class CampaignVariable(override val entry: PackEntry) : NamedType {
	override var name by string(0)
	var value by float(1)
}



class MessageEvent(override val entry: PackEntry) : NamedType {
	override var name    by string(0)
	var instantOpen      by boolean(1)
	var layout           by string(2)
	var requiresResponse by boolean(3)
	var priority         by int(4)
}



class BudgetAllocation(override val entry: PackEntry) : NamedType {
	override var name by string(16)
	var agentFundingCap by int(0)
	var agentFundingAllocationPercentage by int (1)
	var agentPercentageOfPoolToSaveOnFail by int(2)
	var agentTurnOfInactivityUntilCap by int(3)
	var armyFundingCap by int(4)
	var armyFundsAllocationPercentage by int(5)
	var armyPercentageOfPoolToSaveOnFail by int(6)
	var armyTurnsOfInactivityUntilCap by int(7)
	var constructionFundingCap by int(8)
	var constructionFundsAllocationPercentage by int(9)
	var constructionPercentageOfPoolToSaveOnFail by int(10)
	var constructionTurnsOfInactivityUntilCap by int(11)
	var diplomacyFundingCap by int(12)
	var diplomacyFundsAllocationPercentage by int(13)
	var diplomacyPercentageOfPoolToSaveOnFail by int(14)
	var diplomacyTurnsOfInactivityUntilCap by int(15)
	var navyFundingCap by int(17)
	var navyFundsAllocationPercentage by int(18)
	var navyPercentageOfPoolToSaveOnFail by int(19)
	var navyTurnsOfInactivityUntilCap by int(20)
	var minimumSettableTaxLevel by string(21)
	var maximumSettableTaxLevel by string(22)
	var technologyFundsAllocationPercentage by int(23)
	var technologyTurnsOfInactivityUntilCap by int(24)
	var technologyFundingCap by int(25)
	var technologyPercentageOfPoolToSaveOnFail by int(26)
}



class DealEvalComponent(override val entry: PackEntry) : EntryType {
	var deal by string(2)
	var personality by string(5)
	var bestFriendsValue by float(0)
	var bitterEnemiesValue by float(1)
	var friendlyValue by float(3)
	var neutralValue by float(4)
	var totalWarFactor by float(6)
	var lastStandFactor by float(7)
	var unfriendlyValue by float(8)
	var veryFriendlyValue by float(9)
	var veryUnfriendlyValue by float(10)
	var warFactor by float(11)
	var tensionFactor by float(12)
	var peaceFactor by float(13)
	var treacheryValue by float(14)
}



class DealGenPriority(override val entry: PackEntry) : NamedType {
	override var name by string(1)
	var lastStandPriority by float(2)
	var peacePriority by float(7)
	var tensionPriority by float(8)
	var totalWarPriority by float(9)
	var warPriority by float(10)
	var failureTimeout by int(11)
	var minPaymentCap by int(12)
	var maxPaymentCap by int(13)
	var maxPaymentPercent by float(14)
}



class OccupationPriority(override val entry: PackEntry) : NamedType {
	override var name by string(2)
	var lastStandPriority by intFloat(1)
	var peacePriority by intFloat(3)
	var tensionPriority by intFloat(4)
	var totalWarPriority by intFloat(5)
	var warPriority by intFloat(6)
}



class CommanderUnit(override val entry: PackEntry) : EntryType {
	var culture by string(0)
	var faction by string(1)
	var subculture by string(2)
	var unit by string(3)

	constructor(faction: String, unit: String) : this(PackEntry(listOf(
		PackFieldString(""),
		PackFieldString(faction),
		PackFieldString(""),
		PackFieldString(unit)
	)))
}



class UnitPermission(override val entry: PackEntry) : EntryType {
	var unit by string(0)
	var group by string(1)
}



class UnitExclusive(override val entry: PackEntry) : EntryType {
	var unit by string(0)
	var faction by string(1)
	var allowed by boolean(2)

	constructor(unit: String, faction: String, allowed: Boolean) : this(PackEntry(listOf(
		PackFieldString(unit),
		PackFieldString(faction),
		PackFieldBoolean(allowed)
	)))
}



class TechBuilding(override val entry: PackEntry) : EntryType {
	var building by string(0)
	var tech by string(1)
}



/*
EffectBundle
 */



class EffectBundleEffect(override val entry: PackEntry) : EntryType {
	var bundle by string(0)
	var effect by string(1)
	var scope by string(2)
	var value by float(3)

	constructor(bundle: String, effect: String, scope: String, value: Float) : this(PackEntry(listOf(
		PackFieldString(bundle),
		PackFieldString(effect),
		PackFieldString(scope),
		PackFieldFloat(value),
		PackFieldString("")
	)))
}



class EffectBundleData(override val entry: PackEntry) : NamedType {
	override var name by string(0)
}



class EffectBundle(val data: EffectBundleData, val effects: List<EffectBundleEffect>) : CompoundType {
	var name by data::name
	override fun addMod() { data.addMod() }
}



val EffectBundle.formattedString get() = buildString {
	appendLine(name)
	for(effect in effects)
		appendLine("\t${effect.effect}  ::  ${effect.scope}  ::  ${effect.value}")
}