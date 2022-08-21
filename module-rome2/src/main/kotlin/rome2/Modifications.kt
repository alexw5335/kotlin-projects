package rome2

import java.security.PublicKey



val modifiedWeapons = HashSet<Weapon>()

val modifiedShields = HashSet<Shield>()

val modifiedArmours = HashSet<Armour>()

val modifiedUnits = HashSet<Unit>()

val modifiedBuildings = HashSet<Building>()

val modifiedBuildingEffects = HashSet<BuildingEffect>()



private fun Weapon.mod(block: Weapon.() -> kotlin.Unit) = also(block).let(modifiedWeapons::add)

private fun Shield.mod(block: Shield.() -> kotlin.Unit) = also(block).let(modifiedShields::add)

private fun Armour.mod(block: Armour.() -> kotlin.Unit) = also(block).let(modifiedArmours::add)

private fun Unit.mod(block: Unit.() -> kotlin.Unit) = also(block).let(modifiedUnits::add)

private fun Building.mod(block: Building.() -> kotlin.Unit) = also(block).let(modifiedBuildings::add)

private fun Building.effect(name: String, value: Int) {
	val effect = effects.first { it.effect == name }
	effect.value = value
	modifiedBuildingEffects.add(effect)
}



fun modifyWeapons() {
	weapon("rome_gladius").mod           { damage = 30 }
	weapon("rome_gladius_marian").mod    { damage = 35 }
	weapon("rome_gladius_imperial").mod  { damage = 40 }
	weapon("rome_gladius_elite").mod     { damage = 40; apDamage = 10 }
	weapon("rome_spear_cav_elite").mod   { damage = 30; apDamage = 10 }
}



fun modifyShields() {
	shield("scutum").mod           { defence = 30; armour = 35; block = 50 }
	shield("scutum_marian").mod    { defence = 35; armour = 40; block = 55 }
	shield("scutum_imperial").mod  { defence = 40; armour = 45; block = 60 }
}



fun modifyArmours() {
	armour("cloth").mod           { armour = 15 }
	armour("mail").mod            { armour = 40 }
	armour("mail_improved").mod   { armour = 50 }
	armour("segmentata").mod      { armour = 60 }
	armour("segmentata_arm").mod  { armour = 70 }
}



fun modifyUnits() {
	RORARII.mod {
		attack = 15
		defence = 30
		morale = 40
		bonusHp = 10
		charge = 10
	}

	VIGILES.mod {
		attack = 25
		defence = 40
		morale = 50
		bonusHp = 20
		charge = 15
		armour = armour("chest")
		shield = shield("scutum_marian")
		cost = 400
		upkeep = 100
	}


	HASTATI.mod {
		attack = 35
		defence = 20
		morale = 45
		bonusHp = 10
		charge = 15
		cost = 400
		upkeep = 100
	}

	PRINCIPES.mod {
		attack = 50
		defence = 25
		morale = 55
		bonusHp = 15
		charge = 20
		cost = 700
		upkeep = 120
	}

	LEGIONARIES.mod {
		attack = 55
		defence = 30
		morale = 65
		bonusHp = 20
		charge = 20
		cost = 850
		upkeep = 150
	}

	LEGIONARY_COHORT.mod {
		attack = 60
		defence = 35
		morale = 75
		bonusHp = 25
		charge = 25
		cost = 1000
		upkeep = 200
	}

	ARMOURED_LEGIONARIES.mod {
		attack = 60
		defence = 40
		morale = 80
		bonusHp = 30
		charge = 25
		cost = 1200
		upkeep = 220
	}

	TRIARII.mod {
		attack = 35
		defence = 40
		morale = 65
		bonusHp = 20
		charge = 20
		cost = 800
		upkeep = 150
	}

	VETERAN_LEGIONARIES.mod {
		attack = 55
		defence = 45
		morale = 70
		bonusHp = 25
		charge = 20
		cost = 1000
		upkeep = 200
	}

	EVOCATI_COHORT.mod {
		attack = 60
		defence = 50
		morale = 75
		bonusHp = 30
		charge = 25
		cost = 1500
		upkeep = 250
	}

	FIRST_COHORT.mod {
		attack = 65
		defence = 35
		morale = 70
		bonusHp = 20
		charge = 25
		cost = 1200
		upkeep = 220
	}

	EAGLE_COHORT.mod {
		attack = 75
		defence = 40
		morale = 75
		bonusHp = 25
		charge = 30
		cost = 1750
		upkeep = 275
	}

	PRAETORIANS.mod {
		attack = 70
		defence = 50
		morale = 80
		bonusHp = 25
		charge = 30
		cost = 2000
		upkeep = 400
	}

	PRAETORIAN_GUARD.mod {
		attack = 85
		defence = 60
		morale = 100
		bonusHp = 30
		charge = 35
		shield = shield("scutum_imperial")
		cost = 4000
		upkeep = 500
	}

	EQUITES.mod {
		attack = 40
		defence = 15
		morale = 45
		bonusHp = 10
		charge = 30
		cost = 500
		upkeep = 100
	}

	LEGIONARY_CAVALRY.mod {
		attack = 50
		defence = 25
		morale = 55
		bonusHp = 15
		charge = 35
		cost = 1000
		upkeep = 150
	}

	PRAETORIAN_CAVALRY.mod {
		attack = 64
		defence = 40
		morale = 70
		bonusHp = 25
		charge = 45
		cost = 3000
		upkeep = 400
	}
}



private const val GROWTH = "rom_building_growth_all"
private const val SANITATION = "rom_building_public_order_happiness_sanitation"
private const val FOOD_CONSUMPTION = "rom_building_food_consumption"
private const val GDP_CULTURE_ENTERTAINMENT = "rom_building_gdp_culture_entertainment"
private const val RESEARCH_RATE = "rom_building_research_points"
private const val GDP_CULTURE_LEARNING = "rom_building_gdp_culture_learning"
private const val GDP_INDUSTRY_MANUFACTURING = "rom_building_gdp_industry_manufacture"
private const val GDP_INDUSTRY_MINING = "rom_building_gdp_industry_mining"
private const val LATIN_INFLUENCE = "rom_building_culture_conversion_latin"
private const val HAPPINESS = "rom_building_public_order_happiness"
private const val GDP_MOD_SLAVES = "rom_building_slave_gpd_mod"
private const val LAND_MORALE = "rom_building_unit_training_morale_all"
private const val GDP_MOD_CULTURE = "rom_building_gdp_mod_culture_all"
private const val GDP_MOD_MARITIME_TRADE = "rom_building_gdp_mod_trade_sea"
private const val FOOD_PRODUCTION = "rom_building_food_farming_grain"
private const val GDP_AGRICULTURE_FARMING = "rom_building_gdp_agriculture_farming"
private const val SQUALOR = "rom_building_public_order_attitude_squalor"
private const val UNIT_REPLENISHMENT = "rom_building_replenishment_land"
private const val GDP_AGRICULTURE_HERDING = "rom_building_gdp_agriculture_animal_husbandry"
private const val GDP_LOCAL_TRADE = "rom_building_gdp_trade_local"
private const val ATTACK_BUFF = "rom_building_unit_training_attack_melee_inf"
private const val DEFENCE_BUFF = "rom_building_unit_training_defence_melee_inf"
private const val HORSE_BUFF = "rom_building_unit_training_attack_melee_cavalry"
private const val RANGE_BUFF = "rom_building_unit_training_accuracy_ranged_inf"
private const val GDP_MOD_ALL = "rom_building_gdp_mod_all"
private const val GDP_SUBSISTENCE = "rom_building_gdp_subsistence"
private const val ARMY_RECRUITMENT_SLOT = "rom_building_recruitment_points"
private const val GDP_MOD_AGRICULTURE = "rom_building_gdp_mod_agriculture_all"



fun modifyBuildings() {
	for(b in buildings.values) {
		if(!b.name.startsWith("rome_") && !b.name.startsWith("all_")) continue

		b.mod {
			turns = when(level) {
				0 -> 2
				1 -> 3
				2 -> 4
				3 -> 6
				4 -> 8
				else -> error("")
			}
		}
	}

	// CIVIL TOWN
	building("rome_town_civil")
	// FARMING TOWN
	building("rome_town_farm_2").mod {
		effect(GROWTH, 4)
		effect(GDP_AGRICULTURE_FARMING, 100)
		effect(GDP_MOD_AGRICULTURE, 5)
	}
	building("rome_town_farm_3").mod {
		effect(GROWTH, 6)
		effect(GDP_AGRICULTURE_FARMING, 200)
		effect(GDP_MOD_AGRICULTURE, 10)
	}
	building("rome_town_farm_4").mod {
		effect(GROWTH, 8)
		effect(GDP_AGRICULTURE_FARMING, 300)
		effect(GDP_MOD_AGRICULTURE, 20)
	}

	// COLOSSEUM
	building("rome_city_centre_gladiator_5").mod {
		effect(GDP_CULTURE_ENTERTAINMENT, 1000)
	}

	// CIRCUS MAXIMUS
	building("rome_city_centre_circus_5").mod {
		effect(HAPPINESS, 40)
		effect(GDP_CULTURE_ENTERTAINMENT, 500)
	}

	// CIRCUS
	building("rome_city_centre_gladiator_2").mod {
		effect(HAPPINESS, 8)
		effect(GDP_CULTURE_ENTERTAINMENT, 100)
	}
	building("rome_city_centre_gladiator_3").mod {
		effect(HAPPINESS, 16)
		effect(GDP_CULTURE_ENTERTAINMENT, 200)
	}
	building("rome_city_centre_gladiator_4").mod {
		effect(HAPPINESS, 24)
		effect(GDP_CULTURE_ENTERTAINMENT, 400)
	}

	// BARRACKS
	building("rome_military_main_4").mod {
		effect(ARMY_RECRUITMENT_SLOT, 2)
	}
	building("rome_military_aux_4").mod {
		effect(ARMY_RECRUITMENT_SLOT, 2)
	}

	// GARRISON CITY
	building("rome_city_garrison_2").mod {
		effect(HAPPINESS, 4)
		effect(GDP_SUBSISTENCE, 300)
		effect(GDP_MOD_ALL, 6)
	}
	building("rome_city_garrison_3").mod {
		effect(HAPPINESS, 8)
		effect(GDP_SUBSISTENCE, 400)
		effect(GDP_MOD_ALL, 8)
	}
	building("rome_city_garrison_4").mod {
		effect(HAPPINESS, 16)
		effect(GDP_SUBSISTENCE, 500)
		effect(GDP_MOD_ALL, 10)
	}

	// CIVIL CITY
	building("rome_city_civil_2").mod {
		effect(GROWTH, 6)
		effect(GDP_SUBSISTENCE, 350)
		effect(GDP_MOD_ALL, 10)
	}
	building("rome_city_civil_3").mod {
		effect(GROWTH, 8)
		effect(GDP_SUBSISTENCE, 500)
		effect(GDP_MOD_ALL, 15)
	}
	building("rome_city_civil_4").mod {
		effect(GROWTH, 12)
		effect(GDP_SUBSISTENCE, 650)
		effect(GDP_MOD_ALL, 20)
	}

	// MILITARY TRAINING
	building("rome_military_buff_attack_2").mod {
		effect(FOOD_CONSUMPTION, 4)
		effect(ATTACK_BUFF, 10)
	}
	building("rome_military_buff_attack_3").mod {
		effect(FOOD_CONSUMPTION, 8)
		effect(ATTACK_BUFF, 20)
	}
	building("rome_military_buff_defence_2").mod {
		effect(FOOD_CONSUMPTION, 4)
		effect(DEFENCE_BUFF, 10)
	}
	building("rome_military_buff_defence_3").mod {
		effect(FOOD_CONSUMPTION, 8)
		effect(DEFENCE_BUFF, 20)
	}
	building("rome_military_buff_horse_2").mod {
		effect(FOOD_CONSUMPTION, 4)
		effect(HORSE_BUFF, 10)
	}
	building("rome_military_buff_horse_3").mod {
		effect(FOOD_CONSUMPTION, 8)
		effect(HORSE_BUFF, 20)
	}
	building("rome_military_buff_range_2").mod {
		effect(FOOD_CONSUMPTION, 4)
		effect(RANGE_BUFF, 10)
	}
	building("rome_military_buff_range_3").mod {
		effect(FOOD_CONSUMPTION, 8)
		effect(RANGE_BUFF, 20)
	}

	// TRADER
	building("rome_town_centre_trade_2").mod {
		effect(FOOD_CONSUMPTION, 1)
		effect(GDP_LOCAL_TRADE, 150)
	}
	building("rome_town_centre_trade_3").mod {
		effect(FOOD_CONSUMPTION, 2)
		effect(GDP_LOCAL_TRADE, 300)
	}
	building("rome_town_centre_trade_4").mod {
		effect(FOOD_CONSUMPTION, 3)
		effect(GDP_LOCAL_TRADE, 450)
	}

	// THEATRE
	building("rome_town_centre_theatre_2").mod {
		effect(HAPPINESS, 4)
	}
	building("rome_town_centre_theatre_3").mod {
		effect(HAPPINESS, 8)
	}
	building("rome_town_centre_theatre_4").mod {
		effect(HAPPINESS, 12)
	}

	// FARM
	building("rome_agriculture_1").mod {
		effect(GDP_AGRICULTURE_FARMING, 50)
	}
	building("rome_agriculture_farm_2").mod {
		effect(FOOD_PRODUCTION, 8)
		effect(GDP_AGRICULTURE_FARMING, 50)
	}
	building("rome_agriculture_farm_3").mod {
		effect(FOOD_PRODUCTION, 12)
		effect(GDP_AGRICULTURE_FARMING, 100)
	}
	building("rome_agriculture_farm_4").mod {
		effect(FOOD_PRODUCTION, 16)
		effect(GDP_AGRICULTURE_FARMING, 200)
	}
	building("rome_agriculture_granary_2").mod {
		effect(FOOD_PRODUCTION, 4)
		effect(GDP_AGRICULTURE_FARMING, 150)
		effect(UNIT_REPLENISHMENT, 10)
	}
	building("rome_agriculture_granary_3").mod {
		effect(FOOD_PRODUCTION, 5)
		effect(GDP_AGRICULTURE_FARMING, 200)
		effect(UNIT_REPLENISHMENT, 20)
	}
	building("rome_agriculture_granary_4").mod {
		effect(FOOD_PRODUCTION, 6)
		effect(GDP_AGRICULTURE_FARMING, 300)
		effect(UNIT_REPLENISHMENT, 40)
	}
	building("rome_agriculture_herd_2").mod {
		effect(FOOD_PRODUCTION, 6)
		effect(GDP_AGRICULTURE_HERDING, 150)
	}
	building("rome_agriculture_herd_3").mod {
		effect(FOOD_PRODUCTION, 8)
		effect(GDP_AGRICULTURE_HERDING, 200)
	}
	building("rome_agriculture_herd_4").mod {
		effect(FOOD_PRODUCTION, 10)
		effect(GDP_AGRICULTURE_HERDING, 300)
	}

	// TEMPLE OF VULCAN
	building("rome_religious_vulcan_2").mod {
		effect(LATIN_INFLUENCE, 2)
	}
	building("rome_religious_vulcan_3").mod {
		effect(LATIN_INFLUENCE, 3)
	}
	building("rome_religious_vulcan_4").mod {
		effect(LATIN_INFLUENCE, 4)
	}

	// TEMPLE OF NEPTUNE
	building("rome_religious_neptune_2").mod {
		effect(GDP_MOD_MARITIME_TRADE, 10)
	}
	building("rome_religious_neptune_3").mod {
		effect(GDP_MOD_MARITIME_TRADE, 20)
	}
	building("rome_religious_neptune_4").mod {
		effect(GDP_MOD_MARITIME_TRADE, 40)
	}

	// TEMPLE OF MINERVA
	building("rome_religious_minerva_2").mod {
		effect(GDP_MOD_CULTURE, 10)
		effect(LATIN_INFLUENCE, 2)
		effect(RESEARCH_RATE, 5)
	}
	building("rome_religious_minerva_3").mod {
		effect(GDP_MOD_CULTURE, 20)
		effect(LATIN_INFLUENCE, 3)
		effect(RESEARCH_RATE, 10)
	}
	building("rome_religious_minerva_4").mod {
		effect(GDP_MOD_CULTURE, 40)
		effect(LATIN_INFLUENCE, 4)
		effect(RESEARCH_RATE, 20)
	}

	// TEMPLE OF MARS
	building("rome_religious_mars_2").mod {
		effect(GDP_MOD_SLAVES, 5)
		effect(LATIN_INFLUENCE, 2)
	}
	building("rome_religious_mars_3").mod {
		effect(GDP_MOD_SLAVES, 10)
		effect(LATIN_INFLUENCE, 3)
	}
	building("rome_religious_mars_4").mod {
		effect(GDP_MOD_SLAVES, 20)
		effect(LATIN_INFLUENCE, 4)
	}

	// LIBRARY
	building("rome_city_centre_library_2").mod {
		effect(RESEARCH_RATE, 10)
		effect(GDP_CULTURE_LEARNING, 100)
	}
	building("rome_city_centre_library_3").mod {
		effect(RESEARCH_RATE, 20)
		effect(GDP_CULTURE_LEARNING, 150)
	}
	building("rome_city_centre_library_4").mod {
		effect(RESEARCH_RATE, 30)
		effect(GDP_CULTURE_LEARNING, 200)
	}

	// MINE
	building("all_mine_1").mod {
		effect(GDP_INDUSTRY_MINING, 100)
	}
	building("all_mine_2").mod {
		effect(GDP_INDUSTRY_MINING, 200)
	}
	building("all_mine_3").mod {
		effect(GDP_INDUSTRY_MINING, 400)
	}
	building("all_mine_4").mod {
		effect(GDP_INDUSTRY_MINING, 600)
	}
}