package rome2

import core.associateFlatMap
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.reflect.KClass



fun main() {
	CurrentMod.mod()
	applyMods()
}



/*
Mods
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

private const val MOD_NAME = "roman_improvements_v6"
private const val MOD_PREFIX = "R1V6_"

fun applyMods() {
	Files.write(Paths.get("$MOD_NAME.pack"), PackWriter(modMap, MOD_PREFIX).write())
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



/*
Compound types
 */



val landUnits = landUnitsData
	.filter { mainUnitsData.containsKey(it.key) }
	.mapValues { (name, data) -> LandUnit(data, mainUnitsData[name]!!) }

val garrisonGroups = garrisonGroupsData
	.mapValues { (name, data) -> GarrisonGroup(data, garrisonGroupUnits[name] ?: emptyList()) }

val buildings = buildingsData
	.mapValues { (name, data) -> Building(data, buildingEffects[name] ?: emptyList(), garrisons[name] ?: emptyList()) }

val techs = techsData
	.mapValues { (name, data) -> Tech(data, techEffects[name] ?: emptyList()) }

val skills = skillsData.filter { skillLevelsData.containsKey(it.key) }.mapValues { (name, data) ->
	val effects = skillEffects[name] ?: emptyList()
	val levelsData = skillLevelsData[name]!!
	fun level(level: Int) = SkillLevel(levelsData.first { it.level == level }, effects.filter { it.level == level })
	Skill(data, level(1), level(2), level(3))
}



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