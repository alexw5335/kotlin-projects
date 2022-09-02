package rome2

import core.Core
import core.associateFlatMap
import core.binary.BinaryReader
import java.nio.file.Paths

fun main() {
	//writeSchema(Paths.get("schema.bin"), readRome2Schemas(masterSchemaPath = Paths.get("master_schema.xml")))
	val packs = PackReader(BinaryReader(ROME_2_DATA_PACK_PATH)).read()
}



/*
Modifications
 */



val tableMap = mapOf(
	Shield::class.java to Tables.SHIELDS,
	Armour::class.java to Tables.ARMOURS,
	Weapon::class.java to Tables.WEAPONS,
	MainUnitData::class.java to Tables.MAIN_UNITS,
	LandUnitData::class.java to Tables.LAND_UNITS,
	BuildingEffect::class.java to Tables.BUILDING_EFFECTS,
	BuildingData::class.java to Tables.BUILDING_LEVELS,
	Garrison::class.java to Tables.GARRISONS,
	TechData::class.java to Tables.TECHS,
	TechEffect::class.java to Tables.TECH_EFFECTS,
	MissileWeapon::class.java to Tables.MISSILE_WEAPONS,
	Projectile::class.java to Tables.PROJECTILES,
	BuildingUnit::class.java to Tables.BUILDING_UNITS,
	GarrisonGroupData::class.java to Tables.GARRISON_GROUPS,
	GarrisonGroupEntry::class.java to Tables.GARRISON_GROUP_UNITS,
	TechUnitUpgrade::class.java to Tables.TECH_UNIT_UPGRADES,
	ExperienceTier::class.java to Tables.EXPERIENCE_TIERS,
	SkillData::class.java to Tables.SKILLS,
	SkillLevelData::class.java to Tables.SKILL_LEVELS,
	SkillEffect::class.java to Tables.SKILL_EFFECTS,
)

fun getTable(c: Class<*>) = tableMap[c] ?: error("Missing table mapping for '$c'")

inline fun <reified T : Rome2Object> T.mod(block: T.() -> Unit): T {
	block(this)
	modifiedObjects.getOrPut(T::class.java, ::HashSet).add(this)
	return this
}

inline fun <reified T : Rome2Object> T.addMod(): T {
	modifiedObjects.getOrPut(T::class.java, ::HashSet).add(this)
	return this
}

fun <T : Rome2CompoundObject> T.mod(block: T.() -> Unit): T {
	block()
	addMod()
	return this
}

val modifiedObjects = HashMap<Class<*>, HashSet<Rome2Object>>()

fun Building.effect(name: String, value: Int) = effects.first { it.effect == name }.mod { this.value = value }

fun Building.effect(effect: BuildingEffectType, value: Int) = effect(effect.string, value)

fun Building.addEffect(effect: BuildingEffectType, scope: EffectScope, value: Int) = BuildingEffect(this, effect, scope, value).addMod()

fun Tech.effect(name: String, value: Int) = effects.first { it.effect == name }.mod { this.value = value }

fun Tech.effect(effect: TechEffectType, value: Int) = effect(effect.string, value)

fun Tech.addEffect(name: String, scope: String, value: Int) = TechEffect(this.name, name, scope, value).addMod()

fun Building.garrison(prev: String, new: String) = garrisons.first { it.group == prev }.mod { group = new }

fun Building.garrison(prev: GarrisonGroup, new: GarrisonGroup) = garrison(prev.name, new.name)

var garrisonId = 100_000

var buildingUnitId = 300_000

var garrisonUnitId = 400_000

fun Building.garrison(group: String) = Garrison(arrayOf(garrisonId++, name, group).joinToString("\t")).addMod()

fun Building.garrison(group: GarrisonGroup) = garrison(group.name)

fun Building.setGarrison(vararg garrisons: Pair<GarrisonGroup, Int>) {
	val size = garrisons.sumOf { it.second }
	val prev = this.garrisons.filter { it.group.startsWith("rom_") }
	if(size < prev.size) error("$name, new garrison count: $size. Prev garrison count: ${prev.size}")
	var index = 0
	for(g in garrisons) {
		var count = g.second
		while(index < prev.size && count > 0) {
			prev[index++].mod { group = g.first.name }
			count--
		}
		for(i in 0 until count)
			Garrison(garrisonId++, name, g.first.name).addMod()
	}
}

fun Building.unit(unit: LandUnit) = BuildingUnit(buildingUnitId++, name, unit.name).addMod()

fun GarrisonGroup.unit(prev: LandUnit, new: LandUnit) = entries.first { it.unit == prev.name }.mod { unit = new.name }

fun GarrisonGroup.unit(unit: LandUnit, priority: Int = 100) = GarrisonGroupEntry(garrisonUnitId++, priority, unit, name)

fun Tech.unitUpgrade(prev: LandUnit, new: LandUnit, cost: Int? = null) = unitUpgrades.first { it.prev == prev.name }.mod {
	this.new = new.name
	cost?.let { this.cost = it }
}

fun Tech.modifyUnitUpgradeCosts() {
	for(upgrade in unitUpgrades) {
		val prev = units[upgrade.prev] ?: continue
		val new = units[upgrade.new] ?: continue
		val diff = new.cost - prev.cost
		if(upgrade.cost != diff && diff > 0) {
			upgrade.mod {
				// cost = diff * 1.2, with the 0.2 being rounded up to 100
				upgrade.cost = diff + (diff / 5) + (100 - (diff / 5) % 100)
			}
		}
	}
}

fun newGarrisonGroup(name: String, vararg units: LandUnit): GarrisonGroup {
	val entries = units.map { GarrisonGroupEntry(garrisonUnitId++, 100, it, name) }
	val group = GarrisonGroup(GarrisonGroupData(name), entries)
	group.addMod()
	return group
}



fun LandUnit.newProjectile(block: Projectile.() -> Unit) {
	val prevRangedWeapon = missileWeapon ?: siegeWeapon ?: error("No ranged weapon for unit $name")
	val projectile = Projectile(prevRangedWeapon.projectile.assembleLine)
	block(projectile)
	projectile.name += "_new"
	projectile.addMod()
	val rangedWeapon = MissileWeapon(prevRangedWeapon.assembleLine)
	rangedWeapon.name += "_new"
	rangedWeapon.projectile = projectile
	rangedWeapon.addMod()
	if(missileWeapon != null) missileWeapon = rangedWeapon
	else if(siegeWeapon != null) siegeWeapon = rangedWeapon
	addMod()
}




fun applyMods() {
	for((type, map) in modifiedObjects)
		applyMods("R1V1", getTable(type), map)
}



fun applyMods(prefix: String, table: Table, mods: Collection<Rome2Object>) {
	if(mods.isEmpty()) return
	val lines = ArrayList<String>()
	lines.add(table.firstLine)
	lines.add(table.secondLine)
	for(m in mods) lines.add(m.assembleLine)
	Core.writeLines(table.outputPath(prefix), lines)
}



/*
Info
 */



private inline fun<reified T : Rome2Object> map(block: (String) -> T) = getTable(T::class.java).lines.map(block)

val armours = map(::Armour).associateBy { it.name }

val shields = map(::Shield).associateBy { it.name }

val weapons = map(::Weapon).associateBy { it.name }

val projectiles = map(::Projectile).associateBy { it.name }

val missileWeapons = map(::MissileWeapon).associateBy { it.name }

val mainUnits = map(::MainUnitData).associateBy { it.name }

val landUnits = map(::LandUnitData).associateBy { it.name }

val units = landUnits.filter { mainUnits.containsKey(it.key) }.mapValues { LandUnit(it.value, mainUnits[it.key]!!) }

val buildingEffects = map(::BuildingEffect).associateFlatMap { it.building }

val garrisons = map(::Garrison).associateFlatMap { it.building }

val buildingUnits = map(::BuildingUnit).associateFlatMap { it.building }

val garrisonGroupEntries = map(::GarrisonGroupEntry).associateFlatMap { it.group }

val garrisonGroups = map(::GarrisonGroupData)
	.map { GarrisonGroup(it, garrisonGroupEntries[it.name] ?: emptyList()) }
	.associateBy { it.name }

val buildings = map(::BuildingData).associate {
	it.name to Building(it, buildingEffects[it.name] ?: emptyList(), garrisons[it.name] ?: emptyList(), buildingUnits[it.name] ?: emptyList())
}

val techEffects = map(::TechEffect).associateFlatMap { it.tech }

val techUnitUpgrades = map(::TechUnitUpgrade).associateFlatMap { it.tech }

val techs = map(::TechData).associate {
	it.name to Tech(it, techEffects[it.name] ?: emptyList(), techUnitUpgrades[it.name] ?: emptyList())
}



val experienceTiers = map(::ExperienceTier)

val skillEffects = map(::SkillEffect).associateFlatMap { it.skill }

val skillLevelData = map(::SkillLevelData).associateFlatMap { it.skill }

val skillData = map(::SkillData).filter { skillLevelData.containsKey(it.name) }.associateBy { it.name }

val skills = skillData.mapValues { (name, data) ->
	val effects = skillEffects[name] ?: emptyList()

	val levels = skillLevelData[name]!!.map { levelData ->
		SkillLevel(levelData, effects.filter { it.level == levelData.level })
	}

	Skill(
		data,
		levels.first { it.level == 1 },
		levels.first { it.level == 2 },
		levels.first { it.level == 3 }
	)
}



fun armour(name: String) = armours[name] ?: error("Armour not present: $name")

fun shield(name: String) = shields[name] ?: error("Shield not present: $name")

fun weapon(name: String) = weapons[name] ?: error("Weapon not present: $name")

fun projectile(name: String) = projectiles[name] ?: error("Projectile not present: $name")

fun missileWeapon(name: String) = missileWeapons[name] ?: error("Missile weapon not present: $name")

fun unit(name: String) = units[name] ?: error("Unit not present: $name")

fun building(name: String) = buildings[name] ?: error("Building not present: $name")

fun tech(name: String) = techs[name] ?: error("Tech not present: $name")

fun garrisonGroup(name: String) = garrisonGroups[name] ?: error("Garrison group not present: $name")

fun skill(name: String) = skills[name] ?: error("Skill not present: $name")