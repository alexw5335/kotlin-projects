package rome2

import core.Core



fun main() {
	Units.LEVES.printFormatted()
	Units.VELITES.printFormatted()
	Units.AUX_CRETAN_ARCHERS.printFormatted()
	Units.AUX_PELTASTS.printFormatted()
	Mod3.mod()
	applyMods()
}



/*
Modifications
 */



val modifiedWeapons = HashSet<Weapon>()

val modifiedShields = HashSet<Shield>()

val modifiedArmours = HashSet<Armour>()

val modifiedProjectiles = HashSet<Projectile>()

val modifiedUnits = HashSet<Unit>()

val modifiedBuildings = HashSet<Building>()

val modifiedBuildingEffects = HashSet<BuildingEffect>()

val modifiedTechs = HashSet<Tech>()

val modifiedTechEffects = HashSet<TechEffect>()

val modifiedGarrisons = HashSet<Garrison>()

val extraProjectiles = ArrayList<Projectile>()

val extraMissileWeapons = ArrayList<MissileWeapon>()

val extraBuildingUnits = ArrayList<BuildingUnit>()



fun Weapon.mod(block: Weapon.() -> kotlin.Unit) = also(block).let(modifiedWeapons::add)

fun Shield.mod(block: Shield.() -> kotlin.Unit) = also(block).let(modifiedShields::add)

fun Armour.mod(block: Armour.() -> kotlin.Unit) = also(block).let(modifiedArmours::add)

fun Projectile.mod(block: Projectile.() -> kotlin.Unit) = also(block).let(modifiedProjectiles::add)

fun Unit.mod(block: Unit.() -> kotlin.Unit) = also(block).let(modifiedUnits::add)

fun Building.mod(block: Building.() -> kotlin.Unit) = also(block).let(modifiedBuildings::add)

fun Building.effect(name: String, value: Int) {
	val effect = effects.first { it.effect == name }
	effect.value = value
	modifiedBuildingEffects.add(effect)
}

fun Building.effect(effect: BuildingEffectType, value: Int) = effect(effect.string, value)

fun Tech.mod(block: Tech.() -> kotlin.Unit) = also(block).let(modifiedTechs::add)

fun Tech.effect(name: String, value: Int) {
	val effect = effects.first { it.effect == name }
	effect.value = value
	modifiedTechEffects.add(effect)
}

fun Tech.effect(effect: TechEffectType, value: Int) = effect(effect.string, value)

fun Tech.addEffect(name: String, scope: String, value: Int) {
	extraEffects.add(TechEffect(this.name, name, scope, value))
}

fun Building.garrison(prev: String, new: String) {
	val garrison = garrisons.first { it.unit == prev }
	garrison.unit = new
	modifiedGarrisons.add(garrison)
}

fun Building.garrison(prev: GarrisonUnit, new: GarrisonUnit) = garrison(prev.string, new.string)

var garrisonId = 100_000

var buildingUnitId = 300_000

fun Building.garrison(unit: String) = extraGarrisons.add(Garrison(arrayOf(garrisonId++, name, unit).joinToString("\t")))

fun Building.garrison(unit: GarrisonUnit) = garrison(unit.string)

fun Building.setGarrison(vararg garrisons: Pair<GarrisonUnit, Int>) {
	val size = garrisons.sumOf { it.second }
	val prev = this.garrisons.filter { it.unit.startsWith("rom_") }
	if(size < prev.size) error("$name, new garrison count: $size. Prev garrison count: ${prev.size}")
	var index = 0
	for(g in garrisons) {
		var count = g.second
		while(index < prev.size && count > 0) {
			val garrison = prev[index++]
			garrison.unit = g.first.string
			modifiedGarrisons.add(garrison)
			count--
		}
		for(i in 0 until count)
			extraGarrisons.add(Garrison(garrisonId++, name, g.first.string))
	}
}



fun Building.unit(unit: Unit) = extraBuildingUnits.add(BuildingUnit(buildingUnitId++, name, unit.name))



fun applyMods() {
	val prefix = "RIV1"
	applyMods(prefix, Tables.ARMOURS, modifiedArmours)
	applyMods(prefix, Tables.SHIELDS, modifiedShields)
	applyMods(prefix, Tables.WEAPONS, modifiedWeapons)
	applyMods(prefix, Tables.LAND_UNITS, Tables.MAIN_UNITS, modifiedUnits)
	applyMods(prefix, Tables.BUILDING_LEVELS, modifiedBuildings)
	applyMods(prefix, Tables.BUILDING_EFFECTS, modifiedBuildingEffects, buildings.values.flatMap { it.extraEffects }.map { it.assembleLine })
	applyMods(prefix, Tables.TECHS, modifiedTechs)
	applyMods(prefix, Tables.TECH_EFFECTS, modifiedTechEffects, techs.values.flatMap { it.extraEffects }.map { it.assembleLine })
	applyMods(prefix, Tables.GARRISONS, modifiedGarrisons, buildings.values.flatMap { it.extraGarrisons }.map { it.assembleLine })
	applyMods(prefix, Tables.BUILDING_UNITS, extraBuildingUnits)
	applyMods(prefix, Tables.PROJECTILES, extraProjectiles)
	applyMods(prefix, Tables.MISSILE_WEAPONS, extraMissileWeapons)
	println(extraProjectiles.size)
}



fun applyMods(
	prefix: String,
	table: Table,
	mods: Collection<Rome2Object>,
	additionalLines: List<String> = emptyList()
) {
	if(mods.isEmpty() && additionalLines.isEmpty()) return
	val lines = ArrayList<String>()
	lines.add(table.firstLine)
	lines.add(table.secondLine(prefix))
	for(mod in mods) lines.add(mod.assembleLine)
	lines.addAll(additionalLines)
	Core.writeLines(table.outputPath(prefix), lines)
}



fun applyMods(
	prefix: String,
	table1: Table,
	table2: Table,
	mods: Collection<Rome2Object2>,
	additionalLines1: List<String> = emptyList(),
	additionalLines2: List<String> = emptyList()
) {
	if(mods.isNotEmpty() || additionalLines1.isNotEmpty()) {
		val lines1 = ArrayList<String>()
		lines1.add(table1.firstLine)
		lines1.add(table1.secondLine(prefix))
		for(mod in mods) lines1.add(mod.assembleLine)
		lines1.addAll(additionalLines1)
		Core.writeLines(table1.outputPath(prefix), lines1)
	}
	if(mods.isNotEmpty() || additionalLines2.isNotEmpty()) {
		val lines2 = ArrayList<String>()
		lines2.add(table2.firstLine)
		lines2.add(table2.secondLine(prefix))
		for(mod in mods) lines2.add(mod.assembleLine2)
		lines2.addAll(additionalLines2)
		Core.writeLines(table2.outputPath(prefix), lines2)
	}
}



/*
Info
 */



val armours = Tables.ARMOURS.lines.map(::Armour).associateBy { it.name }

val shields = Tables.SHIELDS.lines.map(::Shield).associateBy { it.name }

val weapons = Tables.WEAPONS.lines.map(::Weapon).associateBy { it.name }

val projectiles = Tables.PROJECTILES.lines.map(::Projectile).associateBy { it.name }

val missileWeapons = Tables.MISSILE_WEAPONS.lines.map(::MissileWeapon).associateBy { it.name }

val unitLines2 = Tables.MAIN_UNITS.lines.associateBy { it.substringBefore('\t') }

val units = Tables.LAND_UNITS.lines
	.filter { unitLines2.contains(it.substringBefore('\t')) }
	.map { Unit(it, unitLines2[it.substringBefore('\t')] ?: error(it)) }
	.associateBy { it.name }

val buildings = Tables.BUILDING_LEVELS.lines.map(::Building).associateBy { it.name }.also { map ->
	Tables.BUILDING_EFFECTS.lines.map(::BuildingEffect).forEach { map[it.building]?.effects?.add(it) }
	Tables.GARRISONS.lines.map(::Garrison).forEach { map[it.building]?.garrisons?.add(it) }
	Tables.BUILDING_UNITS.lines.map(::BuildingUnit).forEach { map[it.building]?.units?.add(it) }
}

val techs = Tables.TECHS.lines.map(::Tech).associateBy { it.name }.also { map ->
	Tables.TECH_EFFECTS.lines.map(::TechEffect).forEach { map[it.tech]?.effects?.add(it) }
}



fun armour(name: String) = armours[name] ?: error("Armour not present: $name")

fun shield(name: String) = shields[name] ?: error("Shield not present: $name")

fun weapon(name: String) = weapons[name] ?: error("Weapon not present: $name")

fun projectile(name: String) = projectiles[name] ?: error("Projectile not present: $name")

fun missileWeapon(name: String) = missileWeapons[name] ?: error("Missile weapon not present: $name")

fun unit(name: String) = units[name] ?: error("Unit not present: $name")

fun building(name: String) = buildings[name] ?: error("Building not present: $name")

fun tech(name: String) = techs[name] ?: error("Tech not present: $name")