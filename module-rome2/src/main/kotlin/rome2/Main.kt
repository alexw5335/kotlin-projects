package rome2

import core.Core
import core.associateFlatMap
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.reflect.KClass



fun main() {
	CurrentMod.mod(); applyMods(); runRome2()
}



/*
Mods
 */



private var garrisonId = 300_000

private var buildingUnitId = 300_000

private var garrisonUnitId = 300_000

private var newProjectileId = 0

fun Difficulty.effect(effect: String, value: Int) = effects
	.first { it.effect == effect }
	.takeIf { it.value != value.toFloat() }
	?.mod { this.value = value.toFloat() }

fun EffectBundle.effect(effect: String, value: Int) = effects
	.first { it.effect == effect }
	.takeIf { it.value != value.toFloat() }
	?.mod { this.value = value.toFloat() }

fun Tech.effect(effect: TechEffectType, value: Int) = effects
	.first { it.effect == effect.string }
	.takeIf { it.value != value.toFloat() }
	?.mod { this.value = value.toFloat() }

fun Building.effect(effect: BuildingEffectType, value: Int) = effects
	.first { it.effect == effect.string }
	.takeIf { it.value != value.toFloat() }
	?.mod { this.value = value.toFloat() }

fun Difficulty.addEffect(effect: String, scope: EffectScope, value: Float, isHuman: Boolean = false) =
	DifficultyEffect(level, isHuman, effect, scope.string, value)

fun Tech.addEffect(effect: TechEffectType, scope: EffectScope, value: Int) =
	TechEffect(this, effect, scope, value).addMod()

fun Building.addEffect(effect: BuildingEffectType, scope: EffectScope, value: Int) =
	BuildingEffect(this, effect, scope, value).addMod()

fun Building.garrison(prev: GarrisonGroup, new: GarrisonGroup) = garrisons
	.first { it.group == prev.name }
	.mod { group = new.name }

fun Building.garrison(group: GarrisonGroup) = Garrison(this, garrisonId++, group).addMod()

fun Building.addGarrison(vararg garrisons: Pair<GarrisonGroup, Int>) {
	for((garrison, count) in garrisons)
		for(i in 0 until count)
			garrison(garrison)
}

fun Building.setGarrison(vararg garrisons: Pair<GarrisonGroup, Int>, keepArtillery: Boolean = true) {
	val size = garrisons.sumOf { it.second }
	val prev = this.garrisons.filter { it.group.startsWith("rom_") && (!keepArtillery || !it.group.contains("fixed")) }
	if(size < prev.size) error("$name, new garrison count: $size. Prev garrison count: ${prev.size}")
	var index = 0
	for(g in garrisons) {
		var count = g.second
		while(index < prev.size && count > 0) {
			prev[index++].mod { group = g.first.name }
			count--
		}
		for(i in 0 until count)
			garrison(g.first)
	}
}

fun Tech.removeUnitUpgrade(prev: LandUnit) = unitUpgrades
	.first { it.prev == prev.name }
	.mod { new = prev.name }

fun Tech.unitUpgrade(prev: LandUnit, new: LandUnit, cost: Int? = null) = unitUpgrades
	.first { it.prev == prev.name }
	.mod { this.new = new.name; this.cost = cost ?: this.cost }

fun Tech.addUnitUpgrade(prev: LandUnit, new: LandUnit, cost: Int? = null) =
	TechUnitUpgrade(cost ?: 500, new.name, name, prev.name).addMod()

fun Tech.modifyUnitUpgradeCosts() {
	for(upgrade in unitUpgrades) {
		val prev = landUnits[upgrade.prev] ?: continue
		val new = landUnits[upgrade.new] ?: continue
		val diff = new.cost - prev.cost
		if(upgrade.cost != diff && diff > 0) {
			upgrade.mod {
				// cost = diff * 1.5, with the 0.5 being rounded up to 100
				upgrade.cost = diff + (diff shr 2) + (100 - (diff shr 2) % 100)
			}
		}
	}
}

fun newGarrisonGroup(name: String, priorities: Int, vararg units: LandUnit): GarrisonGroup {
	val entries = units.map { GarrisonGroupUnit(name, it, garrisonUnitId++, priorities) }
	val group = GarrisonGroup(GarrisonGroupData(name), entries)
	for(e in entries) e.addMod()
	group.addMod()
	return group
}

fun LandUnit.newProjectile(block: Projectile.() -> Unit) {
	val prevRangedWeapon = missileWeapon ?: siegeEngine?.weapon ?: error("No ranged weapon for unit $name")
	val projectile = Projectile(prevRangedWeapon.projectile.entry.clone())
	block(projectile)
	projectile.name += "_new${newProjectileId}"
	projectile.addMod()
	val rangedWeapon = MissileWeapon(prevRangedWeapon.entry.clone())
	rangedWeapon.name += "_new${newProjectileId++}"
	rangedWeapon.projectile = projectile
	rangedWeapon.addMod()
	if(missileWeapon != null)
		this.missileWeapon = rangedWeapon
	else
		this.siegeEngine = SiegeEngine(siegeEngine!!.entry).mod { name += "_new"; weapon = rangedWeapon }
	addMod()
}

fun Building.removeUnit(unit: LandUnit) = units
	.first { it.unit == unit.name }
	.mod { this.unit = units.first { it.unit != unit.name }.unit }

fun Building.unit(unit: LandUnit) = BuildingUnit(name, unit.name, buildingUnitId++).addMod()

fun GarrisonGroup.unit(prev: LandUnit, new: LandUnit, priority: Int? = null) = units
	.first { it.unit == prev.name }
	.mod { unit = new.name; this.priority = priority ?: this.priority }

fun GarrisonGroup.unit(new: LandUnit, priority: Int? = null) = GarrisonGroupUnit(name, new, garrisonUnitId++, priority ?: 200).addMod()

fun GarrisonGroup.priority(unit: LandUnit, priority: Int) = units
	.first { it.unit == unit.name }
	.mod { this.priority = priority }



/*
Mod application
 */



val modMap = HashMap<KClass<*>, HashSet<EntryType>>()

inline fun <reified T : EntryType> T.addMod(): T {
	modMap.getOrPut(T::class, ::HashSet).add(this)
	return this
}

inline fun <reified T : EntryType> T.mod(block: T.() -> Unit): T {
	block(this)
	modMap.getOrPut(T::class, ::HashSet).add(this)
	return this
}

fun <T : CompoundType> T.mod(block: T.() -> Unit): T {
	block()
	addMod()
	return this
}

private const val ROME_2_PATH = "C:/Program Files (x86)/Steam/steamapps/common/Total War Rome II"

private const val ROME_2_DATA_PATH = "$ROME_2_PATH/data"

private const val MOD_NAME = "roman_improvements_v6"

private const val MOD_PREFIX = "R1V6_"


private fun applyMods() {
	Files.write(Paths.get("$ROME_2_DATA_PATH/$MOD_NAME.pack"), PackWriter(modMap, MOD_PREFIX).write())
}

private fun runRome2() {
	Core.run("$ROME_2_PATH/Rome2.exe", output = false)
}



/*
Types
 */



val landUnitsData = Tables.mapNamed(::LandUnitData)
val mainUnitsData = Tables.mapNamed(::MainUnitData)
val projectiles = Tables.mapNamed(::Projectile)
val missileWeapons = Tables.mapNamed(::MissileWeapon)
val siegeEngines = Tables.mapNamed(::SiegeEngine)
val armours = Tables.mapNamed(::Armour)
val shields = Tables.mapNamed(::Shield)
val weapons = Tables.mapNamed(::Weapon)
val garrisonGroupUnits = Tables.map(::GarrisonGroupUnit).associateFlatMap { it.group }
val garrisonGroupsData = Tables.mapNamed(::GarrisonGroupData)
val garrisons = Tables.map(::Garrison).associateFlatMap { it.building }
val buildingsData = Tables.mapNamed(::BuildingData)
val buildingEffects = Tables.map(::BuildingEffect).associateFlatMap { it.building }
val techsData = Tables.mapNamed(::TechData)
val techEffects = Tables.map(::TechEffect).associateFlatMap { it.tech }
val skillsData = Tables.mapNamed(::SkillData)
val skillEffects = Tables.map(::SkillEffect).associateFlatMap { it.skill }
val skillLevelsData = Tables.map(::SkillLevelData).associateFlatMap { it.skill }
val experienceTiers = Tables.map(::ExperienceTier)
val techUnitUpgrades = Tables.map(::TechUnitUpgrade).associateFlatMap { it.tech }
val buildingUnits = Tables.map(::BuildingUnit).associateFlatMap { it.building }
val missions = Tables.mapNamed(::Mission)
val dilemmas = Tables.mapNamed(::Dilemma)
val incidents = Tables.mapNamed(::Incident)
val difficultyEffects = Tables.map(::DifficultyEffect).associateFlatMap { it.difficulty }
val campaignVariables = Tables.mapNamed(::CampaignVariable)
val messageEvents = Tables.mapNamed(::MessageEvent)
val budgetAllocations = Tables.mapNamed(::BudgetAllocation)
val effectBundleEffects = Tables.map(::EffectBundleEffect).associateFlatMap { it.bundle }
val effectBundlesData = Tables.mapNamed(::EffectBundleData)
val dealEvalComponents = Tables.map(::DealEvalComponent)
val dealGenPriorities = Tables.mapNamed(::DealGenPriority)
val occupationPriorities = Tables.mapNamed(::OccupationPriority)
val commanderUnits = Tables.map(::CommanderUnit)
val navalUnits = Tables.mapNamed(::NavalUnit)
val unitPermissions = Tables.map(::UnitPermission).associateFlatMap { it.unit }
val unitExclusives = Tables.map(::UnitExclusive)
val techBuildings = Tables.map(::TechBuilding).associateFlatMap { it.building }



/*
Compound types
 */



val landUnits = landUnitsData
	.filter { mainUnitsData.containsKey(it.key) }
	.mapValues { (name, data) -> LandUnit(data, mainUnitsData[name]!!) }

val garrisonGroups = garrisonGroupsData
	.mapValues { (name, data) -> GarrisonGroup(data, garrisonGroupUnits[name] ?: emptyList()) }

val buildings = buildingsData.mapValues { (name, data) ->
	Building(
		data,
		buildingEffects[name] ?: emptyList(),
		garrisons[name] ?: emptyList(),
		buildingUnits[name] ?: emptyList()
	)
}

val techs = techsData
	.mapValues { (name, data) -> Tech(data, techEffects[name] ?: emptyList(), techUnitUpgrades[name] ?: emptyList()) }

val skills = skillsData.filter { skillLevelsData.containsKey(it.key) }.mapValues { (name, data) ->
	val effects = skillEffects[name] ?: emptyList()
	val levelsData = skillLevelsData[name]!!
	fun level(level: Int) = SkillLevel(levelsData.first { it.level == level }, effects.filter { it.level == level })
	Skill(data, level(1), level(2), level(3))
}

val effectBundles = effectBundlesData
	.mapValues { (name, data) -> EffectBundle(data, effectBundleEffects[name] ?: emptyList()) }

val difficulties = difficultyEffects.mapValues { (level, effects) -> Difficulty(level, effects.toMutableList()) }



/*
Type getting
 */



fun projectile(name: String) = projectiles[name] ?: error("No projectile with name: $name")
fun missileWeapon(name: String) = missileWeapons[name] ?: error("No missile weapon with name: $name")
fun siegeEngine(name: String) = siegeEngines[name] ?: error("No siege weapon with name: $name")
fun armour(name: String) = armours[name] ?: error("No armour with name: $name")
fun shield(name: String) = shields[name] ?: error("No shield with name: $name")
fun weapon(name: String) = weapons[name] ?: error("No weapon with name: $name")
fun landUnit(name: String) = landUnits[name] ?: error("No land unit with name: $name")
fun garrisonGroup(name: String) = garrisonGroups[name] ?: error("No garrison group with name: $name")
fun building(name: String) = buildings[name] ?: error("No building with name: $name")
fun tech(name: String) = techs[name] ?: error("No tech with name: $name")
fun skill(name: String) = skills[name] ?: error("No skill with name: $name")
fun messageEvent(name: String) = messageEvents[name] ?: error("No message event with name: $name")
fun effectBundle(name: String) = effectBundles[name] ?: error("No effect bundle with name: $name")
fun navalUnit(name: String) = navalUnits[name] ?: error("No naval unit with name: $name")