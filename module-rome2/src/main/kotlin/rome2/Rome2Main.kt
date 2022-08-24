package rome2

import core.Core



fun main() {
	applyMods()
}



/*
Modifications
 */



fun applyMods() {
	modifyArmours()
	modifyShields()
	modifyWeapons()
	modifyUnits()
	modifyBuildings()
	modifyTechs()
	modifyGarrisons()

	applyMods("RAI", Tables.ARMOURS, modifiedArmours)
	applyMods("RAI", Tables.SHIELDS, modifiedShields)
	applyMods("RAI", Tables.WEAPONS, modifiedWeapons)
	applyMods("RAI", Tables.LAND_UNITS, Tables.MAIN_UNITS, modifiedUnits)
	applyMods("RAI", Tables.BUILDING_LEVELS, modifiedBuildings)
	applyMods("RAI", Tables.BUILDING_EFFECTS, modifiedBuildingEffects)
	applyMods("RAI", Tables.TECHS, modifiedTechs)
	applyMods("RAI", Tables.TECH_EFFECTS, modifiedTechEffects)
	applyMods("RAI", Tables.GARRISONS, modifiedGarrisons, buildings.values.flatMap { it.extraGarrisons }.map { it.assembleLine })
}



fun applyMods(
	prefix: String,
	table: Table,
	mods: Collection<Rome2Object>,
	additionalLines: List<String> = emptyList()
) {
	val lines = ArrayList<String>()
	lines.add(table.keys)
	lines.add(table.secondLine(prefix))
	for(mod in mods)
		lines.add(mod.assembleLine)
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
	val lines1 = ArrayList<String>()
	val lines2 = ArrayList<String>()
	lines1.add(table1.keys)
	lines1.add(table1.secondLine(prefix))
	lines2.add(table2.keys)
	lines2.add(table2.secondLine(prefix))
	for(mod in mods) {
		lines1.add(mod.assembleLine)
		lines2.add(mod.assembleLine2)
	}
	lines1.addAll(additionalLines1)
	lines2.addAll(additionalLines2)
	Core.writeLines(table1.outputPath(prefix), lines1)
	Core.writeLines(table2.outputPath(prefix), lines2)
}



/*
Info
 */



private fun table(table: Table) = Core.readResourceLines("/${table.name}.txt")



val armours = table(Tables.ARMOURS).map(::Armour).associateBy { it.name }

val shields = table(Tables.SHIELDS).map(::Shield).associateBy { it.name }

val weapons = table(Tables.WEAPONS).map(::Weapon).associateBy { it.name }

val unitLines1 = table(Tables.LAND_UNITS)

val unitLines2 = table(Tables.MAIN_UNITS).associateBy { it.substringBefore('\t') }

val units = unitLines1.filter { unitLines2.contains(it.substringBefore('\t')) }.map { Unit(it, unitLines2[it.substringBefore('\t')] ?: error(it)) }.associateBy { it.name }

val buildings = table(Tables.BUILDING_LEVELS).map(::Building).associateBy { it.name }.also { map ->
	table(Tables.BUILDING_EFFECTS).map(::BuildingEffect).forEach { map[it.building]?.effects?.add(it) }
	table(Tables.GARRISONS).map(::Garrison).forEach { map[it.building]?.garrisons?.add(it) }
}

val techs = table(Tables.TECHS).map(::Tech).associateBy { it.name }.also { map ->
	table(Tables.TECH_EFFECTS).map(::TechEffect).forEach { map[it.tech]?.effects?.add(it) }
}



fun armour(name: String) = armours[name] ?: error("Armour not present: $name")

fun shield(name: String) = shields[name] ?: error("Shield not present: $name")

fun weapon(name: String) = weapons[name] ?: error("Weapon not present: $name")

fun unit(name: String) = units[name] ?: error("Unit not present: $name")

fun building(name: String) = buildings[name] ?: error("Building not present: $name")

fun tech(name: String) = techs[name] ?: error("Tech not present: $name")



/*
Units
 */



val ARMOURED_LEGIONARIES = unit("Rom_Armour_Legionaries")
val EAGLE_COHORT = unit("Rom_Eagle_Cohort")
val EQUITES = unit("Rom_Equites")
val EVOCATI_COHORT = unit("Rom_Evocati_Cohort")
val FIRST_COHORT = unit("Rom_First_Cohort")
val HASTATI = unit("Rom_Hastati")
val LEGIONARIES = unit("Rom_Legionaries")
val LEGIONARY_CAVALRY = unit("Rom_Legionary_Cav")
val LEGIONARY_COHORT = unit("Rom_Legionary_Cohort")
val PRAETORIAN_CAVALRY = unit("Rom_Praetorian_Cav")
val PRAETORIAN_GUARD = unit("Rom_Praetorian_Guard")
val PRAETORIANS = unit("Rom_Praetorians")
val PRINCIPES = unit("Rom_Principes")
val RORARII = unit("Rom_Rorarii")
val TRIARII = unit("Rom_Triarii")
val VETERAN_LEGIONARIES = unit("Rom_Vet_Legionaries")
val VIGILES = unit("Rom_Vigiles")



