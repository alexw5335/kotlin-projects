package rome2

import core.binary.BinaryReader
import kotlin.reflect.KClass

object Tables {

	private val map = PackReader(BinaryReader(ROME_2_DATA_PACK_PATH)).read().associateBy { it.schema.name }

	fun get(string: String) = map[string + "_tables"] ?: error("Table not present: $string")

	fun get(c: KClass<*>) = classMap[c] ?: error("No table mapping for class: ${c.simpleName}")

	inline fun<reified T : EntryType> map(block: (PackEntry) -> T) = get(T::class).entries.map(block)

	inline fun<reified T : NamedType> mapNamed(block: (PackEntry) -> T) = get(T::class).entries.map(block).associateBy { it.name }

	fun printTable(table: PackTable) {
		println("table ${table.schema.name}")
		table.schema.fields.forEachIndexed { i, f ->
			println("\t$i: ${f.type} ${f.name}" + if(f.isKey) "  (KEY)" else "")
		}
	}

	private val classMap = mapOf(
		Projectile::class        to get("projectiles"),
		MissileWeapon::class     to get("missile_weapons"),
		SiegeEngine::class       to get("battlefield_engines"),
		LandUnitData::class      to get("land_units"),
		MainUnitData::class      to get("main_units"),
		Armour::class            to get("unit_armour_types"),
		Shield::class            to get("unit_shield_types"),
		Weapon::class            to get("melee_weapons"),
		GarrisonGroupUnit::class to get("armed_citizenry_units_to_unit_groups_junctions"),
		GarrisonGroupData::class to get("armed_citizenry_unit_groups"),
		Garrison::class          to get("building_level_armed_citizenry_junctions"),
		BuildingData::class      to get("building_levels"),
		BuildingEffect::class    to get("building_effects_junction"),
		TechData::class          to get("technologies"),
		TechEffect::class        to get("technology_effects_junction"),
		SkillData::class         to get("character_skills"),
		SkillLevelData::class    to get("character_skill_level_details"),
		SkillEffect::class       to get("character_skill_level_to_effects_junctions"),
		ExperienceTier::class    to get("character_experience_skill_tiers")
	)
}