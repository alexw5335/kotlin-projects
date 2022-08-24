package rome2

object Mod1 {


	fun mod() {
		modifyArmours()
		modifyWeapons()
		modifyShields()
		modifyUnits()
		modifyBuildings()
		modifyGarrisons()
		modifyTechs()
	}



	private fun modifyWeapons() {
		weapon("rome_gladius").mod           { damage = 30 }
		weapon("rome_gladius_marian").mod    { damage = 35 }
		weapon("rome_gladius_imperial").mod  { damage = 40 }
		weapon("rome_gladius_elite").mod     { damage = 40; apDamage = 10 }
		weapon("rome_spear_cav_elite").mod   { damage = 30; apDamage = 10 }
	}



	private fun modifyShields() {
		shield("scutum").mod           { defence = 30; armour = 35; block = 50 }
		shield("scutum_marian").mod    { defence = 35; armour = 40; block = 55 }
		shield("scutum_imperial").mod  { defence = 40; armour = 45; block = 60 }
	}



	private fun modifyArmours() {
		armour("cloth").mod           { armour = 15 }
		armour("mail").mod            { armour = 40 }
		armour("mail_improved").mod   { armour = 50 }
		armour("segmentata").mod      { armour = 60 }
		armour("segmentata_arm").mod  { armour = 70 }
	}



	private fun modifyUnits() {
		Units.RORARII.mod {
			attack = 15
			defence = 30
			morale = 40
			bonusHp = 10
			charge = 10
		}

		Units.VIGILES.mod {
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


		Units.HASTATI.mod {
			attack = 35
			defence = 20
			morale = 45
			bonusHp = 10
			charge = 15
			cost = 400
			upkeep = 100
		}

		Units.PRINCIPES.mod {
			attack = 50
			defence = 25
			morale = 55
			bonusHp = 15
			charge = 20
			cost = 700
			upkeep = 120
		}

		Units.LEGIONARIES.mod {
			attack = 55
			defence = 30
			morale = 65
			bonusHp = 20
			charge = 20
			cost = 850
			upkeep = 150
		}

		Units.LEGIONARY_COHORT.mod {
			attack = 60
			defence = 35
			morale = 75
			bonusHp = 25
			charge = 25
			cost = 1000
			upkeep = 200
		}

		Units.ARMOURED_LEGIONARIES.mod {
			attack = 60
			defence = 40
			morale = 80
			bonusHp = 30
			charge = 25
			cost = 1200
			upkeep = 220
		}

		Units.TRIARII.mod {
			attack = 35
			defence = 40
			morale = 65
			bonusHp = 20
			charge = 20
			cost = 800
			upkeep = 150
		}

		Units.VETERAN_LEGIONARIES.mod {
			attack = 55
			defence = 45
			morale = 70
			bonusHp = 25
			charge = 20
			cost = 1000
			upkeep = 200
		}

		Units.EVOCATI_COHORT.mod {
			attack = 60
			defence = 50
			morale = 75
			bonusHp = 30
			charge = 25
			cost = 1500
			upkeep = 250
		}

		Units.FIRST_COHORT.mod {
			attack = 65
			defence = 35
			morale = 70
			bonusHp = 20
			charge = 25
			cost = 1200
			upkeep = 220
		}

		Units.EAGLE_COHORT.mod {
			attack = 75
			defence = 40
			morale = 75
			bonusHp = 25
			charge = 30
			cost = 1750
			upkeep = 275
		}

		Units.PRAETORIANS.mod {
			attack = 70
			defence = 50
			morale = 80
			bonusHp = 25
			charge = 30
			cost = 2000
			upkeep = 400
		}

		Units.PRAETORIAN_GUARD.mod {
			attack = 85
			defence = 60
			morale = 100
			bonusHp = 30
			charge = 35
			shield = shield("scutum_imperial")
			cost = 4000
			upkeep = 500
		}

		Units.EQUITES.mod {
			attack = 40
			defence = 15
			morale = 45
			bonusHp = 10
			charge = 30
			cost = 500
			upkeep = 100
		}

		Units.LEGIONARY_CAVALRY.mod {
			attack = 50
			defence = 25
			morale = 55
			bonusHp = 15
			charge = 35
			cost = 1000
			upkeep = 150
		}

		Units.PRAETORIAN_CAVALRY.mod {
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
	private const val GDP_MOD_TRADE = "rom_building_gdp_mod_trade_all"
	private const val SECURITY = "rom_building_province_attribute_all"

	private const val ROMAN_BASIC_RANGED = "rom_roman_basic_ranged"
	private const val ROMAN_MEDIUM_RANGED = "rom_roman_medium_ranged"
	private const val ROMAN_BASIC_MELEE = "rom_roman_basic_melee"
	private const val ROMAN_MEDIUM_MELEE = "rom_roman_medium_melee"
	private const val ROMAN_STRONG_MELEE = "rom_roman_strong_melee"
	private const val ROMAN_ELITE_MELEE = "rom_roman_elite_melee"
	private const val ROMAN_LEVY_MELEE = "rom_roman_vigiles_garrison"
	private const val ROMAN_MOBS = "rom_roman_mobs"



	private fun basicMelee(prefix: String) = when(prefix) {
		"east" -> "rom_eastern_basic_melee"
		"greek" -> "rom_hellenic_basic_melee"
		"barb" -> "rom_barbarian_tribesmen"
		else -> error(prefix)
	}

	private fun strongMelee(prefix: String) = when(prefix) {
		"east"  -> "rom_eastern_strong_melee"
		"greek" -> "rom_hellenic_strong_melee"
		"barb"  -> "rom_barbarian_elite_melee"
		else    -> error(prefix)
	}

	private fun eliteMelee(prefix: String) = when(prefix) {
		"east"  -> "rom_eastern_elite_melee"
		"greek" -> "rom_hellenic_elite_infantry"
		"barb"  -> "rom_barbarian_elite_melee"
		else    -> error(prefix)
	}



	private fun modifyGarrisons() {
		for(b in Buildings.NON_ROMAN_TOWNS) {
			val prefix = b.name.substringBefore('_')
			val basicMelee = basicMelee(prefix)
			val strongMelee = strongMelee(prefix)
			val eliteMelee = eliteMelee(prefix)

			if(b.isLevel1) b.apply {
				garrison(basicMelee)
				garrison(basicMelee)
			}

			if(b.isLevel2) b.apply {
				garrison(basicMelee)
				garrison(basicMelee)
				garrison(strongMelee)
			}

			if(b.isLevel3) b.apply {
				garrison(basicMelee)
				garrison(basicMelee)
				garrison(basicMelee)
				garrison(strongMelee)
				garrison(strongMelee)
			}

			if(b.isLevel4) b.apply {
				garrison(basicMelee)
				garrison(basicMelee)
				garrison(basicMelee)
				garrison(strongMelee)
				garrison(strongMelee)
				garrison(strongMelee)
				garrison(eliteMelee)
			}
		}

		for(b in Buildings.NON_ROMAN_CITIES) {
			val prefix = b.name.substringBefore('_')
			val basicMelee = basicMelee(prefix)
			val strongMelee = strongMelee(prefix)
			val eliteMelee = eliteMelee(prefix)

			if(b.isLevel1) b.apply {
				garrison(basicMelee)
				garrison(basicMelee)
				garrison(basicMelee)
			}

			if(b.isLevel2) b.apply {
				garrison(basicMelee)
				garrison(basicMelee)
				garrison(strongMelee)
				garrison(strongMelee)
			}

			if(b.isLevel3) b.apply {
				garrison(basicMelee)
				garrison(basicMelee)
				garrison(basicMelee)
				garrison(strongMelee)
				garrison(strongMelee)
				garrison(strongMelee)
			}

			if(b.isLevel4) b.apply {
				garrison(basicMelee)
				garrison(basicMelee)
				garrison(basicMelee)
				garrison(basicMelee)
				garrison(strongMelee)
				garrison(strongMelee)
				garrison(strongMelee)
				garrison(strongMelee)
				garrison(eliteMelee)
				garrison(eliteMelee)
			}
		}

		for(b in Buildings.ROMAN_TOWNS) {
			// 1 hastati, 4 rorarii, 2 leves
			if(b.isLevel1) b.apply {
				garrison(ROMAN_MOBS, ROMAN_LEVY_MELEE)
				garrison(ROMAN_MOBS, ROMAN_LEVY_MELEE)
				garrison(ROMAN_LEVY_MELEE)
				garrison(ROMAN_STRONG_MELEE, ROMAN_MEDIUM_MELEE)
			}

			// 2 hastati, 5 rorarii, 2 leves
			if(b.isLevel2) b.apply {
				garrison(ROMAN_MOBS, ROMAN_LEVY_MELEE)
				garrison(ROMAN_MOBS, ROMAN_LEVY_MELEE)
				garrison(ROMAN_LEVY_MELEE)
				garrison(ROMAN_LEVY_MELEE)
				garrison(ROMAN_STRONG_MELEE, ROMAN_MEDIUM_MELEE)
				garrison(ROMAN_MEDIUM_MELEE)
			}

			// 2 strong, 2 medium, 6 rorarii, 3 leves
			if(b.isLevel3) b.apply {
				garrison(ROMAN_MOBS, ROMAN_LEVY_MELEE)
				garrison(ROMAN_MOBS, ROMAN_LEVY_MELEE)
				garrison(ROMAN_LEVY_MELEE)
				garrison(ROMAN_LEVY_MELEE)
				garrison(ROMAN_LEVY_MELEE)
				garrison(ROMAN_STRONG_MELEE)
				garrison(ROMAN_MEDIUM_MELEE)
				garrison(ROMAN_MEDIUM_MELEE)
				garrison(ROMAN_BASIC_RANGED)
			}

			// 3 strong, 3 medium, 6 rorarii, 3 velites
			if(b.isLevel4) b.apply {
				garrison(ROMAN_MOBS, ROMAN_LEVY_MELEE)
				garrison(ROMAN_MOBS, ROMAN_LEVY_MELEE)
				garrison(ROMAN_LEVY_MELEE)
				garrison(ROMAN_LEVY_MELEE)
				garrison(ROMAN_LEVY_MELEE)
				garrison(ROMAN_STRONG_MELEE)
				garrison(ROMAN_STRONG_MELEE)
				garrison(ROMAN_MEDIUM_MELEE)
				garrison(ROMAN_MEDIUM_MELEE)
				garrison(ROMAN_MEDIUM_MELEE)
				garrison(ROMAN_BASIC_RANGED, ROMAN_MEDIUM_RANGED)
				garrison(ROMAN_BASIC_RANGED, ROMAN_MEDIUM_RANGED)
				garrison(ROMAN_MEDIUM_RANGED)
			}
		}

		// 2 hastati, 4 rorarii, 2 leves
		building("rome_city_1").apply {
			garrison(ROMAN_MOBS, ROMAN_LEVY_MELEE)
			garrison(ROMAN_MOBS, ROMAN_LEVY_MELEE)
			garrison(ROMAN_STRONG_MELEE, ROMAN_MEDIUM_MELEE)
			garrison(ROMAN_MEDIUM_MELEE)
		}

		// 3 hastati, 6 rorarii, 3 leves
		building("rome_city_civil_2").apply {
			garrison(ROMAN_MOBS, ROMAN_LEVY_MELEE)
			garrison(ROMAN_MOBS, ROMAN_LEVY_MELEE)
			garrison(ROMAN_STRONG_MELEE, ROMAN_MEDIUM_MELEE)
			garrison(ROMAN_MEDIUM_MELEE)
			garrison(ROMAN_LEVY_MELEE)
			garrison(ROMAN_LEVY_MELEE)
			garrison(ROMAN_BASIC_RANGED)
			garrison(ROMAN_MEDIUM_MELEE)
		}

		// 4 principes, 6 rorarii, 4 leves
		building("rome_city_civil_3").apply {
			garrison(ROMAN_MOBS, ROMAN_LEVY_MELEE)
			garrison(ROMAN_MOBS, ROMAN_LEVY_MELEE)
			garrison(ROMAN_STRONG_MELEE, ROMAN_MEDIUM_MELEE)
			garrison(ROMAN_LEVY_MELEE)
			garrison(ROMAN_LEVY_MELEE)
			garrison(ROMAN_BASIC_RANGED)
			garrison(ROMAN_BASIC_RANGED)
			garrison(ROMAN_MEDIUM_MELEE)
			garrison(ROMAN_MEDIUM_MELEE)
			garrison(ROMAN_MEDIUM_MELEE)
		}

		// 4 hastati, 4 principes, 6 rorarii, 4 velites
		building("rome_city_civil_4").apply {
			garrison(ROMAN_MOBS, ROMAN_LEVY_MELEE)
			garrison(ROMAN_MOBS, ROMAN_LEVY_MELEE)
			garrison(ROMAN_LEVY_MELEE)
			garrison(ROMAN_LEVY_MELEE)
			garrison(ROMAN_BASIC_RANGED, ROMAN_MEDIUM_RANGED)
			garrison(ROMAN_BASIC_RANGED, ROMAN_MEDIUM_RANGED)
			garrison(ROMAN_MEDIUM_RANGED)
			garrison(ROMAN_MEDIUM_RANGED)
			garrison(ROMAN_STRONG_MELEE)
			garrison(ROMAN_STRONG_MELEE)
			garrison(ROMAN_STRONG_MELEE)
			garrison(ROMAN_MEDIUM_MELEE)
			garrison(ROMAN_MEDIUM_MELEE)
			garrison(ROMAN_MEDIUM_MELEE)
			garrison(ROMAN_MEDIUM_MELEE)
		}

		// 3 principes, 3 hastati, 3 rorarii, 2 velites
		building("rome_city_garrison_2").apply {
			garrison(ROMAN_MOBS, ROMAN_LEVY_MELEE)
			garrison(ROMAN_STRONG_MELEE)
			garrison(ROMAN_STRONG_MELEE)
			garrison(ROMAN_MEDIUM_MELEE)
		}

		// 4 strong, 4 medium, 4 levy, 3 velites
		building("rome_city_garrison_3").apply {
			garrison(ROMAN_MOBS, ROMAN_LEVY_MELEE)
			garrison(ROMAN_LEVY_MELEE)
			garrison(ROMAN_MEDIUM_MELEE)
			garrison(ROMAN_MEDIUM_MELEE)
			garrison(ROMAN_STRONG_MELEE)
			garrison(ROMAN_STRONG_MELEE)
			garrison(ROMAN_STRONG_MELEE)
			garrison(ROMAN_MEDIUM_RANGED)
		}

		// 2 elite, 6 strong, 6 medium, 4 velites
		building("rome_city_garrison_4").apply {
			garrison(ROMAN_MOBS, ROMAN_MEDIUM_RANGED)
			garrison(ROMAN_MEDIUM_RANGED)
			garrison(ROMAN_STRONG_MELEE)
			garrison(ROMAN_STRONG_MELEE)
			garrison(ROMAN_STRONG_MELEE)
			garrison(ROMAN_STRONG_MELEE)
			garrison(ROMAN_STRONG_MELEE)
			garrison(ROMAN_LEVY_MELEE, ROMAN_MEDIUM_MELEE)
			garrison(ROMAN_LEVY_MELEE, ROMAN_MEDIUM_MELEE)
			garrison(ROMAN_MEDIUM_MELEE)
			garrison(ROMAN_MEDIUM_MELEE)
			garrison(ROMAN_ELITE_MELEE)
			garrison(ROMAN_ELITE_MELEE)
		}

		// 4 hastati
		building("rome_military_main_2").apply {

		}

		// 2 principes, 4 hastati
		building("rome_military_main_3").apply {
			garrison(ROMAN_MEDIUM_MELEE)
			garrison(ROMAN_MEDIUM_MELEE)
		}

		// 1 elite, 3 strong, 4 medium
		building("rome_military_main_4").apply {
			garrison(ROMAN_MEDIUM_MELEE)
			garrison(ROMAN_MEDIUM_MELEE)
			garrison(ROMAN_STRONG_MELEE)
		}

		// 2 hastati, 2 velites
		building("rome_military_aux_2").apply {
			garrison(ROMAN_BASIC_RANGED, ROMAN_MEDIUM_RANGED)
			garrison(ROMAN_BASIC_RANGED, ROMAN_MEDIUM_RANGED)
		}

		// 3 principes, 3 velites
		building("rome_military_aux_3").apply {
			garrison(ROMAN_MEDIUM_MELEE, ROMAN_STRONG_MELEE)
			garrison(ROMAN_MEDIUM_MELEE, ROMAN_STRONG_MELEE)
			garrison(ROMAN_STRONG_MELEE)
			garrison(ROMAN_MEDIUM_RANGED)
		}

		// 1 elite, 4 strong, 4 velites
		building("rome_military_aux_4").apply {
			garrison(ROMAN_MEDIUM_MELEE, ROMAN_STRONG_MELEE)
			garrison(ROMAN_MEDIUM_MELEE, ROMAN_STRONG_MELEE)
			garrison(ROMAN_STRONG_MELEE)
			garrison(ROMAN_STRONG_MELEE)
			garrison(ROMAN_ELITE_MELEE)
			garrison(ROMAN_MEDIUM_RANGED)
			garrison(ROMAN_MEDIUM_RANGED)
		}
	}



	private fun modifyBuildings() {
		for(b in buildings.values) {
			if(!b.name.startsWith("rome_") && !b.name.startsWith("all_")) continue

			b.mod {
				turns = when(level) {
					0 -> 2
					1 -> 3
					2 -> 4
					3 -> 5
					4 -> 8
					else -> error("")
				}
			}
		}

		// BATHS
		building("rome_water_baths_2").apply {
			effect(SANITATION, 4)
			effect(GROWTH, 4)
			effect(FOOD_CONSUMPTION, 1)
			effect(GDP_CULTURE_ENTERTAINMENT, 100)
			effect(LATIN_INFLUENCE, 2)
		}
		building("rome_water_baths_3").apply {
			effect(SANITATION, 8)
			effect(GROWTH, 6)
			effect(FOOD_CONSUMPTION, 3)
			effect(GDP_CULTURE_ENTERTAINMENT, 200)
			effect(LATIN_INFLUENCE, 4)
		}
		building("rome_water_baths_4").apply {
			effect(SANITATION, 16)
			effect(GROWTH, 8)
			effect(FOOD_CONSUMPTION, 6)
			effect(GDP_CULTURE_ENTERTAINMENT, 400)
			effect(LATIN_INFLUENCE, 6)
		}

		// SEWER
		building("rome_water_sewer_2").apply {
			effect(SANITATION, 4)
			effect(GROWTH, 4)
			effect(LATIN_INFLUENCE, 4)
		}
		building("rome_water_sewer_3").apply {
			effect(SANITATION, 6)
			effect(GROWTH, 8)
			effect(LATIN_INFLUENCE, 6)
		}
		building("rome_water_sewer_4").apply {
			effect(SANITATION, 8)
			effect(GROWTH, 16)
			effect(LATIN_INFLUENCE, 8)
		}
		building("rome_water_sewer_5").apply {
			effect(SANITATION, 16)
			effect(GROWTH, 32)
			effect(LATIN_INFLUENCE, 16)
		}

		// WATER TANK
		building("rome_water_tank_2").apply {
			effect(SANITATION, 3)
			effect(GROWTH, 3)
			effect(GDP_MOD_AGRICULTURE, 10)
			effect(SECURITY, 4)
		}
		building("rome_water_tank_3").apply {
			effect(SANITATION, 4)
			effect(GROWTH, 4)
			effect(GDP_MOD_AGRICULTURE, 20)
			effect(SECURITY, 8)
		}
		building("rome_water_tank_4").apply {
			effect(SANITATION, 5)
			effect(GROWTH, 5)
			effect(GDP_MOD_AGRICULTURE, 30)
			effect(SECURITY, 16)
		}

		// MARKET TOWN
		building("rome_town_trade_2").apply {
			effect(GDP_LOCAL_TRADE, 150)
			effect(GDP_MOD_TRADE, 10)
		}
		building("rome_town_trade_3").apply {
			effect(GDP_LOCAL_TRADE, 250)
			effect(GDP_MOD_TRADE, 20)
		}
		building("rome_town_trade_4").apply {
			effect(GDP_LOCAL_TRADE, 350)
			effect(GDP_MOD_TRADE, 30)
		}

		// CIVIL TOWN
		building("rome_town_civil_2").apply {
			effect(GDP_MOD_ALL, 5)
		}
		building("rome_town_civil_3").apply {
			effect(GDP_MOD_ALL, 10)
		}
		building("rome_town_civil_4").apply {
			effect(GDP_MOD_ALL, 15)
		}

		// FARMING TOWN
		building("rome_town_farm_2").apply {
			effect(GROWTH, 4)
			effect(GDP_AGRICULTURE_FARMING, 100)
			effect(GDP_MOD_AGRICULTURE, 10)
		}
		building("rome_town_farm_3").apply {
			effect(GROWTH, 6)
			effect(GDP_AGRICULTURE_FARMING, 200)
			effect(GDP_MOD_AGRICULTURE, 20)
		}
		building("rome_town_farm_4").apply {
			effect(GROWTH, 8)
			effect(GDP_AGRICULTURE_FARMING, 300)
			effect(GDP_MOD_AGRICULTURE, 30)
		}

		// COLOSSEUM
		building("rome_city_centre_gladiator_5").apply {
			effect(GDP_CULTURE_ENTERTAINMENT, 1000)
		}

		// CIRCUS MAXIMUS
		building("rome_city_centre_circus_5").apply {
			effect(HAPPINESS, 40)
			effect(GDP_CULTURE_ENTERTAINMENT, 500)
		}

		// CIRCUS
		building("rome_city_centre_gladiator_2").apply {
			effect(HAPPINESS, 8)
			effect(GDP_CULTURE_ENTERTAINMENT, 100)
		}
		building("rome_city_centre_gladiator_3").apply {
			effect(HAPPINESS, 16)
			effect(GDP_CULTURE_ENTERTAINMENT, 200)
		}
		building("rome_city_centre_gladiator_4").apply {
			effect(HAPPINESS, 24)
			effect(GDP_CULTURE_ENTERTAINMENT, 400)
		}

		// BARRACKS
		building("rome_military_main_4").apply {
			effect(ARMY_RECRUITMENT_SLOT, 2)
		}
		building("rome_military_aux_4").apply {
			effect(ARMY_RECRUITMENT_SLOT, 2)
		}

		// GARRISON CITY
		building("rome_city_garrison_2").apply {
			effect(HAPPINESS, 4)
			effect(GDP_SUBSISTENCE, 300)
			effect(GDP_MOD_ALL, 6)
		}
		building("rome_city_garrison_3").apply {
			effect(HAPPINESS, 8)
			effect(GDP_SUBSISTENCE, 400)
			effect(GDP_MOD_ALL, 8)
		}
		building("rome_city_garrison_4").apply {
			effect(HAPPINESS, 16)
			effect(GDP_SUBSISTENCE, 500)
			effect(GDP_MOD_ALL, 10)
		}

		// CIVIL CITY
		building("rome_city_civil_2").apply {
			effect(GROWTH, 6)
			effect(GDP_SUBSISTENCE, 350)
			effect(GDP_MOD_ALL, 10)
		}
		building("rome_city_civil_3").apply {
			effect(GROWTH, 8)
			effect(GDP_SUBSISTENCE, 500)
			effect(GDP_MOD_ALL, 15)
		}
		building("rome_city_civil_4").apply {
			effect(GROWTH, 12)
			effect(GDP_SUBSISTENCE, 650)
			effect(GDP_MOD_ALL, 20)
		}

		// MILITARY TRAINING
		building("rome_military_buff_attack_2").apply {
			effect(FOOD_CONSUMPTION, 4)
			effect(ATTACK_BUFF, 10)
		}
		building("rome_military_buff_attack_3").apply {
			effect(FOOD_CONSUMPTION, 8)
			effect(ATTACK_BUFF, 20)
		}
		building("rome_military_buff_defence_2").apply {
			effect(FOOD_CONSUMPTION, 4)
			effect(DEFENCE_BUFF, 10)
		}
		building("rome_military_buff_defence_3").apply {
			effect(FOOD_CONSUMPTION, 8)
			effect(DEFENCE_BUFF, 20)
		}
		building("rome_military_buff_horse_2").apply {
			effect(FOOD_CONSUMPTION, 4)
			effect(HORSE_BUFF, 10)
		}
		building("rome_military_buff_horse_3").apply {
			effect(FOOD_CONSUMPTION, 8)
			effect(HORSE_BUFF, 20)
		}
		building("rome_military_buff_range_2").apply {
			effect(FOOD_CONSUMPTION, 4)
			effect(RANGE_BUFF, 10)
		}
		building("rome_military_buff_range_3").apply {
			effect(FOOD_CONSUMPTION, 8)
			effect(RANGE_BUFF, 20)
		}

		// TRADER
		building("rome_town_centre_trade_2").apply {
			effect(FOOD_CONSUMPTION, 1)
			effect(GDP_LOCAL_TRADE, 150)
		}
		building("rome_town_centre_trade_3").apply {
			effect(FOOD_CONSUMPTION, 2)
			effect(GDP_LOCAL_TRADE, 300)
		}
		building("rome_town_centre_trade_4").apply {
			effect(FOOD_CONSUMPTION, 3)
			effect(GDP_LOCAL_TRADE, 450)
		}

		// THEATRE
		building("rome_town_centre_theatre_2").apply {
			effect(HAPPINESS, 4)
		}
		building("rome_town_centre_theatre_3").apply {
			effect(HAPPINESS, 8)
		}
		building("rome_town_centre_theatre_4").apply {
			effect(HAPPINESS, 12)
		}

		// FARM
		building("rome_agriculture_1").apply {
			effect(GDP_AGRICULTURE_FARMING, 50)
		}
		building("rome_agriculture_farm_2").apply {
			effect(FOOD_PRODUCTION, 8)
			effect(GDP_AGRICULTURE_FARMING, 50)
		}
		building("rome_agriculture_farm_3").apply {
			effect(FOOD_PRODUCTION, 12)
			effect(GDP_AGRICULTURE_FARMING, 100)
		}
		building("rome_agriculture_farm_4").apply {
			effect(FOOD_PRODUCTION, 16)
			effect(GDP_AGRICULTURE_FARMING, 200)
		}
		building("rome_agriculture_granary_2").apply {
			effect(FOOD_PRODUCTION, 4)
			effect(GDP_AGRICULTURE_FARMING, 150)
			effect(UNIT_REPLENISHMENT, 10)
		}
		building("rome_agriculture_granary_3").apply {
			effect(FOOD_PRODUCTION, 5)
			effect(GDP_AGRICULTURE_FARMING, 200)
			effect(UNIT_REPLENISHMENT, 20)
		}
		building("rome_agriculture_granary_4").apply {
			effect(FOOD_PRODUCTION, 6)
			effect(GDP_AGRICULTURE_FARMING, 300)
			effect(UNIT_REPLENISHMENT, 40)
		}
		building("rome_agriculture_herd_2").apply {
			effect(FOOD_PRODUCTION, 6)
			effect(GDP_AGRICULTURE_HERDING, 150)
		}
		building("rome_agriculture_herd_3").apply {
			effect(FOOD_PRODUCTION, 8)
			effect(GDP_AGRICULTURE_HERDING, 200)
		}
		building("rome_agriculture_herd_4").apply {
			effect(FOOD_PRODUCTION, 10)
			effect(GDP_AGRICULTURE_HERDING, 300)
		}

		// TEMPLE OF VULCAN
		building("rome_religious_vulcan_2").apply {
			effect(LATIN_INFLUENCE, 2)
		}
		building("rome_religious_vulcan_3").apply {
			effect(LATIN_INFLUENCE, 3)
		}
		building("rome_religious_vulcan_4").apply {
			effect(LATIN_INFLUENCE, 4)
		}

		// TEMPLE OF NEPTUNE
		building("rome_religious_neptune_2").apply {
			effect(GDP_MOD_MARITIME_TRADE, 10)
		}
		building("rome_religious_neptune_3").apply {
			effect(GDP_MOD_MARITIME_TRADE, 20)
		}
		building("rome_religious_neptune_4").apply {
			effect(GDP_MOD_MARITIME_TRADE, 40)
		}

		// TEMPLE OF MINERVA
		building("rome_religious_minerva_2").apply {
			effect(GDP_MOD_CULTURE, 10)
			effect(LATIN_INFLUENCE, 2)
			effect(RESEARCH_RATE, 5)
		}
		building("rome_religious_minerva_3").apply {
			effect(GDP_MOD_CULTURE, 20)
			effect(LATIN_INFLUENCE, 3)
			effect(RESEARCH_RATE, 10)
		}
		building("rome_religious_minerva_4").apply {
			effect(GDP_MOD_CULTURE, 40)
			effect(LATIN_INFLUENCE, 4)
			effect(RESEARCH_RATE, 20)
		}

		// TEMPLE OF MARS
		building("rome_religious_mars_2").apply {
			effect(GDP_MOD_SLAVES, 5)
			effect(LATIN_INFLUENCE, 2)
		}
		building("rome_religious_mars_3").apply {
			effect(GDP_MOD_SLAVES, 10)
			effect(LATIN_INFLUENCE, 3)
		}
		building("rome_religious_mars_4").apply {
			effect(GDP_MOD_SLAVES, 20)
			effect(LATIN_INFLUENCE, 4)
		}

		// LIBRARY
		building("rome_city_centre_library_2").apply {
			effect(RESEARCH_RATE, 10)
			effect(GDP_CULTURE_LEARNING, 100)
		}
		building("rome_city_centre_library_3").apply {
			effect(RESEARCH_RATE, 20)
			effect(GDP_CULTURE_LEARNING, 150)
		}
		building("rome_city_centre_library_4").apply {
			effect(RESEARCH_RATE, 30)
			effect(GDP_CULTURE_LEARNING, 200)
		}

		// MINE
		building("all_mine_1").apply {
			effect(GDP_INDUSTRY_MINING, 100)
		}
		building("all_mine_2").apply {
			effect(GDP_INDUSTRY_MINING, 200)
		}
		building("all_mine_3").apply {
			effect(GDP_INDUSTRY_MINING, 400)
		}
		building("all_mine_4").apply {
			effect(GDP_INDUSTRY_MINING, 600)
		}
	}



	private fun civilTech(name: String, block: Tech.() -> kotlin.Unit) = tech("rom_roman_civil_$name").let(block)

	private fun militaryTech(name: String, block: Tech.() -> kotlin.Unit) = tech("rom_roman_military_$name").let(block)

	private fun engineeringTech(name: String, block: Tech.() -> kotlin.Unit) = tech("rom_roman_engineering_$name").let(block)



	private fun modifyTechs() {
		civilTech("agriculture_iron_tools") {
			effect("rom_tech_civil_economy_agriculture_gdp_mod", 10)
			effect("rom_tech_civil_economy_agriculture_building_cost_mod", -5)
		}
		civilTech("agriculture_double_cropping") {
			effect("rom_tech_civil_economy_agriculture_gdp_mod", 10)
			effect("rom_tech_civil_economy_agriculture_building_cost_mod", -5)
		}
		civilTech("agriculture_improved_irrigation") {
			effect("rom_tech_civil_economy_agriculture_gdp_mod", 10)
			effect("rom_tech_civil_economy_agriculture_building_cost_mod", -5)
		}
		civilTech("agriculture_land_reclamation") {
			effect("rom_tech_civil_economy_agriculture_gdp_mod", 15)
			effect("rom_tech_civil_economy_agriculture_building_cost_mod", -5)
		}
		civilTech("agriculture_seed_selection") {
			effect("rom_tech_civil_economy_agriculture_gdp_mod", 15)
			effect("rom_tech_civil_economy_agriculture_building_cost_mod", -10)
		}

		civilTech("economy_common_weights_and_measures") {
			effect("rom_tech_civil_economy_trade_gdp_mod", 10)
			effect("rom_tech_civil_economy_trade_mod", 10)
		}
		civilTech("economy_common_currency") {
			effect("rom_tech_civil_economy_trade_gdp_mod", 10)
			effect("rom_tech_civil_economy_trade_mod", 10)
		}
		civilTech("economy_denominational_system") {
			effect("rom_tech_civil_economy_trade_gdp_mod", 15)
			effect("rom_tech_civil_economy_trade_mod", 15)
		}
		civilTech("economy_production_lines") {
			effect("rom_tech_civil_economy_trade_gdp_mod", 15)
			effect("rom_tech_civil_economy_trade_mod", 15)
		}

		civilTech("philosophy_philosophers") {
			effect("rom_tech_civil_economy_culture_gdp_mod", 10)
		}
		civilTech("philosophy_astronomy") {
			effect("rom_tech_civil_economy_culture_gdp_mod", 15)
		}
		civilTech("philosophy_natural_philosophy") {
			effect("rom_tech_civil_economy_culture_gdp_mod", 20)
		}
		civilTech("philosophy_mysticism") { // AKA cultism
			effect("rom_tech_civil_economy_culture_gdp_mod", 25)
		}

		civilTech("economy_legal_documentation") {
			effect("rom_tech_agent_action_cost_mod", -10)
			effect("rom_tech_civil_economy_tax_mod", 4)
		}
		civilTech("economy_labour_organisation") {
			effect("rom_tech_civil_economy_tax_mod", 6)
		}
		civilTech("economy_legal_institutions") {
			effect("rom_tech_civil_economy_tax_mod", 8)
			effect("rom_tech_module_civil_philosophy", -10) // corruption
		}
		civilTech("economy_consensual_contracts") {
			effect("rom_tech_agent_action_cost_mod", -20)
			effect("rom_tech_civil_economy_tax_mod", 10)
			effect("rom_tech_module_civil_philosophy", -15) // corruption
		}

		engineeringTech("construction_tax_labour") {
			effect("rom_tech_civil_economy_growth_mod", 3)
		}
		engineeringTech("construction_fired_brick") {
			effect("rom_tech_civil_economy_industry_gdp_mod", 15)
		}
		engineeringTech("construction_crane") {
			effect("rom_tech_civil_economy_growth_region_mod", 4)
		}
		engineeringTech("construction_moulded_architecture") {
			effect("rom_tech_civil_economy_industry_gdp_mod", 25)
		}

		militaryTech("management_training_reforms") {
			effect("rom_tech_military_management_unit_cost_mod", -5)
			effect("rom_tech_military_management_upkeep_mod", -5)
		}
		militaryTech("management_remuneration_reforms") {
			effect("rom_tech_military_management_unit_cost_mod", -5)
			effect("rom_tech_military_management_upkeep_mod", -5)
		}
		militaryTech("management_cohort_organisation") {
			effect("rom_tech_military_management_unit_cost_mod", -5)
			effect("rom_tech_military_management_upkeep_mod", -5)
		}
		militaryTech("management_professional_soldiery") {
			effect("rom_tech_military_management_unit_cost_mod", -5)
			effect("rom_tech_military_management_upkeep_mod", -5)
		}
		militaryTech("navy_naval_manoeuvres") {
			effect("rom_tech_military_management_ship_cost_mod", -5)
			effect("rom_tech_military_navy_upkeep_mod", -5)
		}
		militaryTech("navy_fore_and_aft_rigging") {
			effect("rom_tech_military_management_ship_cost_mod", -10)
			effect("rom_tech_military_navy_upkeep_mod", -10)
		}
	}
	
	
}