package rome2

enum class TechEffectType(val string: String) {

	ROME_UNIT_COST_MOD("rom_tech_military_management_unit_cost_mod"),
	ROME_UNIT_UPKEEP_MOD("rom_tech_military_management_upkeep_mod"),
	ROME_RECRUITMENT_SLOTS("rom_tech_military_management_recruitment_mod"),
	ROME_UNIT_MORALE_MOD("rom_tech_military_tactics_morale_mod")
}