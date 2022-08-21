package rome2

import core.Core



fun main() {
	modifyArmours()
	modifyShields()
	modifyWeapons()
	modifyUnits()
	applyMods("RAI", Tables.ARMOURS, armours.values)
	applyMods("RAI", Tables.SHIELDS, shields.values)
	applyMods("RAI", Tables.WEAPONS, weapons.values)
	applyMods("RAI", Tables.LAND_UNITS, Tables.MAIN_UNITS, units.values)
}



/*
Modifications
 */



fun applyMods(prefix: String, table: Table, mods: Collection<Rome2Object>) {
	val lines = ArrayList<String>()
	lines.add(table.keys)
	for(mod in mods) lines.add(mod.assembleLine)
	Core.writeLines(table.outputPath(prefix), lines)
}



fun applyMods(prefix: String, table1: Table, table2: Table, mods: Collection<Rome2Object2>) {
	val lines1 = ArrayList<String>()
	val lines2 = ArrayList<String>()
	lines1.add(table1.keys)
	lines2.add(table2.keys)
	for(mod in mods) {
		lines1.add(mod.assembleLine1)
		lines2.add(mod.assembleLine2)
	}
	Core.writeLines(table1.outputPath(prefix), lines1)
	Core.writeLines(table2.outputPath(prefix), lines2)
}



/*
Info
 */



private fun tableLines(table: Table) = Core.readResourceLines("/${table.name}.txt")



val armours = tableLines(Tables.ARMOURS).map(::Armour).associateBy { it.name }

val shields = tableLines(Tables.SHIELDS).map(::Shield).associateBy { it.name }

val weapons = tableLines(Tables.WEAPONS).map(::Weapon).associateBy { it.name }

val unitLines1 = tableLines(Tables.LAND_UNITS)

val unitLines2 = tableLines(Tables.MAIN_UNITS).associateBy { it.substringBefore('\t') }

val units = unitLines1.filter { unitLines2.contains(it.substringBefore('\t')) }.map { Unit(it, unitLines2[it.substringBefore('\t')] ?: error(it)) }.associateBy { it.name }



/*
val buildingInfos = map("building_levels") {
	BuildingInfo(
		name      = it[0],
		level     = it[2].toInt(),
		buildTime = it[4].toInt(),
		cost      = it[5].toInt()
	)
}



val buildingEffects = HashMap<String, ArrayList<BuildingEffect>>().also { map ->
	val lines = Core.readResourceLines("/building_effects_junction.txt")

	for(line in lines) {
		val parts = line.split('\t')
		val name = parts[0]
		map.getOrPut(name, ::ArrayList).add(BuildingEffect(
			effect = parts[1],
			scope = parts[2],
			value = parts[3].toInt(),
			valueDamaged = parts[4].toInt(),
			valueRuined = parts[5].toInt()
		))
	}
}



val buildings = buildingInfos.map {
	Building(it.name, it.level, it.buildTime, it.cost, buildingEffects[it.name] ?: emptyList())
}.associateBy { it.name }
*/




/*
Units
 */



private val String.unit get() = units["Rom_$this"]!!



val ARMOURED_LEGIONARIES = "Armour_Legionaries".unit
val EAGLE_COHORT = "Eagle_Cohort".unit
val EQUITES = "Equites".unit
val EVOCATI_COHORT = "Evocati_Cohort".unit
val FIRST_COHORT = "First_Cohort".unit
val HASTATI = "Hastati".unit
val LEGIONARIES = "Legionaries".unit
val LEGIONARY_CAVALRY = "Legionary_Cav".unit
val LEGIONARY_COHORT = "Legionary_Cohort".unit
val PRAETORIAN_CAVALRY = "Praetorian_Cav".unit
val PRAETORIAN_GUARD = "Praetorian_Guard".unit
val PRAETORIANS = "Praetorians".unit
val PRINCIPES = "Principes".unit
val RORARII = "Rorarii".unit
val TRIARII = "Triarii".unit
val VETERAN_LEGIONARIES = "Vet_Legionaries".unit
val VIGILES = "Vigiles".unit