package rome2

import core.Core
import java.io.File



fun main() {
	for(dir in File("db").listFiles()!!) {
		val file = dir.listFiles()!![0]
		val firstLine = file.readLines()[0]
		if(file.name == "unit_shield_types")
			println(firstLine)
	}
}



/*
Modifications
 */



fun applyBuildingMods(mods: List<BuildingMod>) {

}



fun applyArmourMods(mods: List<ArmourMod>) {
	val lines = Core.readResourceLines("/unit_armour_types.txt")
	val outputLines = ArrayList<String>()

	outputLines.add("key\tarmour_value\tbonus_vs_missiles\tweak_vs_missiles\taudio_material")
	outputLines.add("#unit_armour_types_tables;1;db/unit_armour_types_tables/RAI_unit_armour_types\t")

	for(mod in mods) {
		val parts = lines.first { it.substringBefore('\t') == mod.name }.split('\t').toMutableList()
		mod.armour?.let { parts[1] = it.toString() }
		outputLines.add(parts.joinToString("\t"))
	}

	Core.writeLines("db/unit_armour_types_tables/RAI_unit_armour_types.tsv", outputLines)
}



fun applyShieldMods(mods: List<ShieldMod>) {
	val lines = Core.readResourceLines("/unit_shield_types.txt")
	val outputLines = ArrayList<String>()

	outputLines.add("key\tshield_defence_value\tshield_armour_value\taudio_material\tmissile_block_chance")
	outputLines.add("#unit_shield_types_tables;3;db/unit_shield_types_tables/RAI_unit_shield_types")

	for(mod in mods) {
		val parts = lines.first { it.substringBefore('\t') == mod.name }.split('\t').toMutableList()
		mod.defence?.let { parts[1] = it.toString() }
		mod.armour?.let { parts[2] = it.toString() }
		mod.missileBlockChance?.let { parts[4] = it.toString() }
		outputLines.add(parts.joinToString("\t"))
	}

	Core.writeLines("db/unit_shield_types_tables/RAI_unit_shield_types.tsv", outputLines)
}



fun applyWeaponMods(mods: List<WeaponMod>) {
	val lines = Core.readResourceLines("/melee_weapons.txt")
	val outputLines = ArrayList<String>()

	outputLines.add("key\tarmour_penetrating\tarmour_piercing\tbonus_v_cavalry\tbonus_v_elephants\tbonus_v_infantry\tdamage\tap_damage\tfirst_strike\tshield_piercing\tweapon_length\tmelee_weapon_type\taudio_material")
	outputLines.add("#melee_weapons_tables;6;db/melee_weapons_tables/RAI_melee_weapons")

	for(mod in mods) {
		val parts = lines.first { it.substringBefore('\t') == mod.name }.split('\t').toMutableList()
		mod.damage?.let { parts[6] = it.toString() }
		mod.apDamage?.let { parts[7] = it.toString() }
		mod.cavalryBonus?.let { parts[3] = it.toString() }
		mod.largeBonus?.let { parts[4] = it.toString() }
		mod.infantryBonus?.let { parts[5] = it.toString() }
		outputLines.add(parts.joinToString("\t"))
	}

	Core.writeLines("db/melee_weapons_tables/RAI_melee_weapons.tsv", outputLines)
}




fun applyUnitMods(mods: List<UnitMod>) {
	applyUnitMods1(mods)
	applyUnitMods2(mods)
}



fun applyUnitMods1(mods: List<UnitMod>) {
	val lines = Core.readResourceLines("/land_units.txt").filter { it.startsWith("Rom_") }
	val outputLines = ArrayList<String>()

	outputLines.add("key\taccuracy\tammo\tarmour\tcampaign_action_points\tcategory\tcharge_bonus\tclass\tdismounted_charge_bonus\tdismounted_melee_attack\tdismounted_melee_defense\thistorical_description_text\tman_animation\tman_entity\tmelee_attack\tmelee_defence\tmorale\tbonus_hit_points\tmount\tnum_animals\tanimal\tnum_mounts\tprimary_melee_weapon\tprimary_missile_weapon\trank_depth\tshield\tshort_description_text\tspacing\tstrengths_&_weaknesses_text\tsupports_first_person\ttraining_level\tnum_guns\tofficers\tarticulated_record\tengine\tis_male\tvisibility_spotting_range_min\tvisibility_spotting_range_max\tability_global_recharge\tattribute_group\tspot_dist_tree\tspot_dist_scrub\tchariot\tnum_chariots\treload\tloose_spacing\tspotting_and_hiding\tselection_vo\tselected_vo_secondary\tselected_vo_tertiary\thiding_scalar")
	outputLines.add("#land_units_tables;27;db/land_units_tables/RAI_land_units")

	for(mod in mods) {
		val parts = lines.first { it.substringBefore('\t') == mod.unit.name }.split('\t').toMutableList()
		mod.attack?.let { parts[14] = it.toString(); parts[9] = it.toString() }
		mod.defence?.let { parts[15] = it.toString(); parts[10] = it.toString() }
		mod.morale?.let { parts[16] = it.toString() }
		mod.bonusHp?.let { parts[17] = it.toString() }
		mod.charge?.let { parts[6] = it.toString(); parts[8] = it.toString() }
		mod.armour?.let { parts[3] = it }
		mod.weapon?.let { parts[22] = it }
		mod.shield?.let { parts[25] = it }
		outputLines.add(parts.joinToString("\t"))
	}

	Core.writeLines("db/land_units_tables/RAI_land_units.tsv", outputLines)
}



/*private fun<T> applyMods(input: String, columns: List<String>, block: (List<String>) ->) {
	val lines = Core.readResourceLines("/$input.txt")
	val outputLines = ArrayList<String>()

	outputLines.add(columns.joinToString("\t"))
	outputLines.add("#${input}_tables;${columns.size};db/${input}_tables/RAI_$input")

	for(mod in mods) outputLines.add(block(mod))

	Core.writeLines("db/${input}_tables/RAI_$input.tsv", outputLines)
}*/



fun applyUnitMods2(mods: List<UnitMod>) {
	val lines = Core.readResourceLines("/main_units.txt").filter { it.startsWith("Rom_") }
	val outputLines = ArrayList<String>()

	outputLines.add("unit\tadditional_building_requirement\tcampaign_cap\tcaste\tcreate_time\tis_naval\tland_unit\tnum_men\tmultiplayer_cap\tmultiplayer_cost\tnaval_unit\tnum_ships\tmin_men_per_ship\tmax_men_per_ship\tprestige\trecruitment_cost\trecruitment_movie\treligion_requirement\tupkeep_cost\tweight\tcampaign_total_cap\tresource_requirement\tworld_leader_only\tcan_trade\tspecial_edition_mask\tunique_index\tin_encyclopedia\tregion_unit_resource_requirement\taudio_language\taudio_vo_actor_group")
	outputLines.add("#main_units_tables;14;db/main_units_tables/RAI_main_units")

	for(mod in mods) {
		val parts = lines.first { it.substringBefore('\t') == mod.unit.name}.split('\t').toMutableList()
		mod.cost?.let { parts[15] = it.toString(); parts[9] = it.toString() }
		mod.upkeep?.let { parts[18] = it.toString() }
		outputLines.add(parts.joinToString("\t"))
	}

	Core.writeLines("db/main_units_tables/RAI_main_units.tsv", outputLines)
}



/*
Info
 */



private fun<T> map(table: String, block: (List<String>) -> T?) = buildList {
	for(l in Core.readResourceLines("/$table.txt"))
		block(l.split('\t'))?.let(::add)
}



private fun String.toBoolean() = this == "1" || this == "true"



val armours = map("unit_armour_types") {
	Armour(it[0], it[1].toInt(), it[2].toInt(), it[3].toBoolean())
}.associateBy { it.name }



val shields = map("unit_shield_types") {
	Shield(
		name               = it[0],
		defence            = it[1].toInt(),
		armour             = it[2].toInt(),
		missileBlockChance = it[4].toInt())
}.associateBy { it.name }



val weapons = map("melee_weapons") {
	Weapon(
		name             = it[0],
		damage           = it[6].toInt(),
		apDamage         = it[7].toInt(),
		cavalryBonus     = it[3].toInt(),
		largeBonus       = it[4].toInt(),
		infantryBonus    = it[5].toInt(),
		isArmourPiercing = it[2].toBoolean())
}.associateBy { it.name }



val unitInfos = map("main_units") {
	UnitInfo(it[0], it[15].toInt(), it[18].toInt())
}.associateBy { it.name }



val units = map("land_units") {
	Unit(
		name    = it[0],
		attack  = it[14].toInt(),
		defence = it[15].toInt(),
		morale  = it[16].toInt(),
		bonusHp = it[17].toInt(),
		charge  = it[6].toInt(),
		armour  = armours[it[3]]!!,
		weapon  = weapons[it[22]]!!,
		shield  = shields[it[25]]!!,
		cost    = unitInfos[it[0]]?.cost ?: -1,
		upkeep  = unitInfos[it[0]]?.upkeep ?: -1
	)
}.filter { it.name.startsWith("Rom_") }.associateBy { it.name }



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