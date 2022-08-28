package rome2

import binary.BinaryReader
import core.Core



class Table(val name: String, lines: List<String>) {
	val firstLine = lines[0]
	val secondLine = lines[1]
	val lines = lines.drop(2)
	fun secondLine(prefix: String) = secondLine.replaceAfterLast('/', "${prefix}_$name")
	fun outputPath(prefix: String) = "db/${name}_tables/${prefix}_$name.tsv"
}



object Tables {

	val SCHEMA = readSchema(BinaryReader(Core.readResourceBytes("/schema.bin")))

	private fun table(name: String) = Table(name, Core.readResourceLines("/db/$name.tsv"))
	
	val SHIELDS = table("unit_shield_types")
	val ARMOURS = table("unit_armour_types")
	val WEAPONS = table("melee_weapons")
	val LAND_UNITS = table("land_units")
	val MAIN_UNITS = table("main_units")
	val BUILDING_EFFECTS = table("building_effects_junction")
	val BUILDING_LEVELS = table("building_levels")
	val GARRISONS = table("building_level_armed_citizenry_junctions")
	val TECHS = table("technologies")
	val TECH_EFFECTS = table("technology_effects_junction")
	val MISSILE_WEAPONS = table("missile_weapons")
	val PROJECTILES = table("projectiles")
	val BUILDING_UNITS = table("building_units_allowed")
	val GARRISON_GROUPS = table("armed_citizenry_units_to_unit_groups_junctions")
	val TECH_UNIT_UPGRADES = table("technology_unit_upgrades")

}