package rome2

enum class BuildingEffectType(val string: String) {

	GROWTH("rom_building_growth_all"),
	SANITATION("rom_building_public_order_happiness_sanitation"),
	FOOD_CONSUMPTION("rom_building_food_consumption"),
	GDP_CULTURE_ENTERTAINMENT("rom_building_gdp_culture_entertainment"),
	RESEARCH_RATE("rom_building_research_points"),
	GDP_CULTURE_LEARNING("rom_building_gdp_culture_learning"),
	GDP_INDUSTRY_MANUFACTURING("rom_building_gdp_industry_manufacture"),
	GDP_INDUSTRY_MINING("rom_building_gdp_industry_mining"),
	LATIN_INFLUENCE("rom_building_culture_conversion_latin"),
	HAPPINESS("rom_building_public_order_happiness"),
	GDP_MOD_SLAVES("rom_building_slave_gpd_mod"),
	LAND_MORALE("rom_building_unit_training_morale_all"),
	GDP_MOD_CULTURE("rom_building_gdp_mod_culture_all"),
	GDP_MOD_MARITIME_TRADE("rom_building_gdp_mod_trade_sea"),
	FOOD_PRODUCTION("rom_building_food_farming_grain"),
	GDP_AGRICULTURE_FARMING("rom_building_gdp_agriculture_farming"),
	SQUALOR("rom_building_public_order_attitude_squalor"),
	UNIT_REPLENISHMENT("rom_building_replenishment_land"),
	GDP_AGRICULTURE_HERDING("rom_building_gdp_agriculture_animal_husbandry"),
	GDP_LOCAL_TRADE("rom_building_gdp_trade_local"),
	ATTACK_BUFF("rom_building_unit_training_attack_melee_inf"),
	DEFENCE_BUFF("rom_building_unit_training_defence_melee_inf"),
	HORSE_BUFF("rom_building_unit_training_attack_melee_cavalry"),
	RANGE_BUFF("rom_building_unit_training_accuracy_ranged_inf"),
	GDP_MOD_ALL("rom_building_gdp_mod_all"),
	GDP_SUBSISTENCE("rom_building_gdp_subsistence"),
	ARMY_RECRUITMENT_SLOT("rom_building_recruitment_points"),
	GDP_MOD_AGRICULTURE("rom_building_gdp_mod_agriculture_all"),
	GDP_MOD_TRADE("rom_building_gdp_mod_trade_all"),
	SECURITY("rom_building_province_attribute_all");

}