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

	fun printTable(name: String) = printTable(get(name))

	private val classMap = mapOf(
		Projectile::class         to get("projectiles"),
		MissileWeapon::class      to get("missile_weapons"),
		SiegeEngine::class        to get("battlefield_engines"),
		LandUnitData::class       to get("land_units"),
		MainUnitData::class       to get("main_units"),
		Armour::class             to get("unit_armour_types"),
		Shield::class             to get("unit_shield_types"),
		Weapon::class             to get("melee_weapons"),
		GarrisonGroupUnit::class  to get("armed_citizenry_units_to_unit_groups_junctions"),
		GarrisonGroupData::class  to get("armed_citizenry_unit_groups"),
		Garrison::class           to get("building_level_armed_citizenry_junctions"),
		BuildingData::class       to get("building_levels"),
		BuildingEffect::class     to get("building_effects_junction"),
		TechData::class           to get("technologies"),
		TechEffect::class         to get("technology_effects_junction"),
		SkillData::class          to get("character_skills"),
		SkillLevelData::class     to get("character_skill_level_details"),
		SkillEffect::class        to get("character_skill_level_to_effects_junctions"),
		ExperienceTier::class     to get("character_experience_skill_tiers"),
		TechUnitUpgrade::class    to get("technology_unit_upgrades"),
		BuildingUnit::class       to get("building_units_allowed"),
		Mission::class            to get("missions"),
		Dilemma::class            to get("dilemmas"),
		Incident::class           to get("incidents"),
		DifficultyEffect::class   to get("campaign_difficulty_handicap_effects"),
		CampaignVariable::class   to get("campaign_variables"),
		MessageEvent::class       to get("message_events"),
		BudgetAllocation::class   to get("cai_personalities_budget_allocations"),
		EffectBundleEffect::class to get("effect_bundles_to_effects_junctions"),
		EffectBundleData::class   to get("effect_bundles"),
		DealEvalComponent::class  to get("cai_personality_deal_evaluation_deal_component_values"),
		DealGenPriority::class    to get("cai_personality_deal_generation_generator_priorities")
	)

}