package rome2.mods

/*import rome2.GarrisonUnit.*
import rome2.BuildingEffectType.*
import rome2.TechEffectType.*

object Mod3 {


	fun mod() {
		modifyUnits()
		modifyGarrisons()
		modifyBuildings()
		modifyTechs()
	}



	private fun modifyUnits() {
		weapon("rome_gladius").mod           { damage = 30 }
		weapon("rome_gladius_marian").mod    { damage = 35 }
		weapon("rome_gladius_imperial").mod  { damage = 40 }
		weapon("rome_gladius_elite").mod     { damage = 45; apDamage = 10 }
		weapon("rome_spear_cav_elite").mod   { damage = 35; apDamage = 10 }

		shield("scutum").mod           { defence = 30; armour = 35; block = 50 }
		shield("scutum_marian").mod    { defence = 40; armour = 40; block = 60 }
		shield("scutum_imperial").mod  { defence = 50; armour = 50; block = 70 }

		armour("cloth").mod           { armour = 20 }
		armour("mail").mod            { armour = 40 }
		armour("mail_improved").mod   { armour = 50 }
		armour("segmentata").mod      { armour = 65 }
		armour("segmentata_arm").mod  { armour = 75 }

		val cretanArrow = Projectile(projectile("arrow_composite").assembleLine).mod {
			name = "cretan_arrow"
			damage = 60 // 45 + 15
			apDamage = 15 // 4 + 11
			effectiveRange = 200 // 150 + 50
		}

		val cretanBow = MissileWeapon(missileWeapon("rome_composite_bow_elite").assembleLine).mod {
			name = "cretan_bow"
			projectile = cretanArrow
		}

		val peltastJavelin = Projectile(projectile("javelin_normal").assembleLine).mod {
			name = "peltast_javelin"
			damage = 30 // 20 + 10
			apDamage = 20 // 12 + 8
			effectiveRange = 100 // 80 + 20
		}

		val peltastJavelinWeapon = MissileWeapon(missileWeapon("rome_javelin").assembleLine).mod {
			//name = "peltast_javelin_weapon"
			//projectile = peltastJavelin
		}

		Units.LEVES.mod {
			reload = 15 // 8 + 7
			accuracy = 10 // 5 + 5
			ammo = 12 // 7 + 5
		}

		Units.VELITES.mod {
			reload = 20 // 13 + 7
			accuracy = 15 // 5 + 10
			ammo = 15 // 7 + 8
			level = TrainingLevel.TRAINED
			cost = 500 // 340 + 160
		}

		Units.AUX_CRETAN_ARCHERS.mod {
			level    = TrainingLevel.ELITE
			reload   = 45 // 28 + 17
			ammo     = 30 // 15 + 15
			accuracy = 15 // 5 + 10
			cost     = 1000 // 600 + 400
			missileWeapon = cretanBow
		}

		Units.AUX_PELTASTS.mod {
			attack   = 20 // 12 + 8
			defence  = 20 // 14 + 6
			morale   = 55 // 55
			bonusHp  = 20 // 15 + 5
			charge   = 5 // 6 - 1
			cost     = 1000 // 600 + 400
			upkeep   = 150 // 130 + 20
			level    = TrainingLevel.ELITE // poorly_trained
			reload   = 45 // 28 + 27
			accuracy = 15 // 5 + 10
			ammo     = 20 // 7 + 13
			cost     = 1000 // 420 + 580
			upkeep   = 100 // 90 + 10
			missileWeapon = peltastJavelinWeapon
		}

		Units.RORARII.mod {
			attack  = 20
			defence = 40
			morale  = 40
			bonusHp = 15
			charge  = 10
			armour  = armour("mail")
			shield  = shield("scutum")
		}

		Units.VIGILES.mod {
			attack  = 30
			defence = 55
			morale  = 50
			bonusHp = 20
			charge  = 15
			armour  = armour("mail_improved")
			shield  = shield("scutum_marian")
			cost    = 400
			upkeep  = 100
		}

		Units.HASTATI.mod {
			attack  = 35  // 35
			defence = 20  // 18 + 2
			morale  = 45  // 45
			bonusHp = 10  // 10
			charge  = 15  // 12 + 3
			cost    = 400 // 350 + 50
			upkeep  = 100 // 90 + 10
		}

		Units.PRINCIPES.mod {
			attack  = 50  // 47 + 3
			defence = 25  // 23 + 2
			morale  = 55  // 55
			bonusHp = 15  // 15
			charge  = 20  // 14 + 6
			cost    = 700 // 680 + 20
			upkeep  = 120 // 120
		}

		Units.LEGIONARIES.mod {
			attack  = 55   // 47 + 8
			defence = 30   // 23 + 7
			morale  = 65   // 55 + 10
			bonusHp = 20   // 15 + 5
			charge  = 20   // 14 + 6
			cost    = 1000 // 660 + 340
			upkeep  = 175  // 140 + 35
		}

		Units.LEGIONARY_COHORT.mod {
			attack  = 60   // 47 + 13
			defence = 35   // 23 + 12
			morale  = 75   // 55 + 20
			bonusHp = 25   // 15 + 10
			charge  = 25   // 14 + 11
			cost    = 1500 // 700 + 800
			upkeep  = 200  // 140 + 60
		}

		Units.ARMOURED_LEGIONARIES.mod {
			attack  = 60    // 58 + 2
			defence = 45    // 26 + 19
			morale  = 75    // 65 + 10
			bonusHp = 30    // 15 + 15
			charge  = 25    // 15 + 10
			cost    = 1750  // 930 + 820
			upkeep  = 220   // 160 + 60
		}

		Units.TRIARII.mod {
			attack  = 35  // 31 + 4
			defence = 40  // 34 + 6
			morale  = 65  // 65
			bonusHp = 20  // 20
			charge  = 20  // 24 - 4
			cost    = 800 // 790 + 10
			upkeep  = 150 // 140 + 10
		}

		Units.VETERAN_LEGIONARIES.mod {
			attack  = 60   // 59 + 1
			defence = 45   // 23 + 22
			morale  = 65   // 65
			bonusHp = 25   // 15 + 10
			charge  = 20   // 15 + 5
			cost    = 1500 // 850 + 650
			upkeep  = 220  // 160 + 60
		}

		Units.EVOCATI_COHORT.mod {
			attack  = 65   // 61 + 4
			defence = 50   // 23 + 22
			morale  = 75   // 65 + 10
			bonusHp = 30   // 15 + 15
			charge  = 25   // 15 + 10
			cost    = 2000 // 890 + 1110
			upkeep  = 250  // 160 + 90
		}

		Units.FIRST_COHORT.mod {
			attack  = 75   // 47 + 28
			defence = 35   // 33 + 2
			morale  = 65   // 65
			bonusHp = 20   // 20
			charge  = 30   // 12 + 13
			cost    = 2300 // 910 + 1390
			upkeep  = 250  // 180 + 70
		}

		Units.EAGLE_COHORT.mod {
			attack  = 85   // 47 + 38
			defence = 40   // 35 + 5
			morale  = 75   // 65 + 10
			bonusHp = 25   // 20 + 5
			charge  = 30   // 12 + 28
			cost    = 3000 // 930 + 2070
			upkeep  = 300  // 180 + 120
		}

		Units.PRAETORIANS.mod {
			attack  = 75   // 65 + 10
			defence = 55   // 30 + 15
			morale  = 80   // 70 + 10
			bonusHp = 40   // 20 + 5
			charge  = 30   // 19 + 11
			cost    = 4000 // 1280 + 2720
			upkeep  = 400  // 200 + 200
		}

		Units.PRAETORIAN_GUARD.mod {
			attack  = 85   // 65 + 20
			defence = 70   // 30 + 30
			morale  = 100  // 70 + 30
			bonusHp = 40   // 20 + 10
			charge  = 35   // 19 + 16
			shield  = shield("scutum_imperial")
			cost    = 5000 // 1280 + 3720
			upkeep  = 500  // 200 + 300
		}

		Units.EQUITES.mod {
			attack  = 40  // 33 + 7
			defence = 15  // 12 + 3
			morale  = 45  // 40 + 5
			bonusHp = 10  // 10
			charge  = 30  // 29 + 1
			cost    = 500 // 500
			upkeep  = 100 // 100
		}

		Units.LEGIONARY_CAVALRY.mod {
			attack  = 50   // 33 + 12
			defence = 25   // 10 + 15
			morale  = 55   // 45 + 10
			bonusHp = 15   // 15
			charge  = 35   // 29 + 6
			cost    = 1500 // 620 + 880
			upkeep  = 150  // 120 + 30
		}

		Units.PRAETORIAN_CAVALRY.mod {
			attack  = 70   // 49 + 21
			defence = 40   // 22 + 18
			morale  = 70   // 70
			bonusHp = 30   // 20 + 10
			charge  = 50   // 42 + 8
			cost    = 3000
			upkeep  = 300
		}
	}



	private fun modifyTechs() {
		for(t in RomanTechs.ALL) t.mod {
			t.cost = t.cost / 3
		}

		RomanTechs.TRAINING_REFORMS.apply {
			effect(ROME_UNIT_UPKEEP_MOD, -10)
		}
		RomanTechs.REMUNERATION_REFORMS.apply {
			effect(ROME_UNIT_UPKEEP_MOD, -10)
		}
		RomanTechs.COHORT_ORGANISATION.apply {
			effect(ROME_UNIT_UPKEEP_MOD, -10)
		}
		RomanTechs.PROFESSIONAL_SOLDIERY.apply {
			effect(ROME_UNIT_UPKEEP_MOD, -20)
		}

		RomanTechs.IRON_TOOLS.apply {
			effect(ROME_AGRI_GDP_MOD, 10)
			effect(ROME_AGRI_BUILDING_COST_MOD, -5)
		}
		
		RomanTechs.DOUBLE_CROPPING.apply {
			effect(ROME_AGRI_GDP_MOD, 10)
			effect(ROME_AGRI_BUILDING_COST_MOD, -5)
		}
		RomanTechs.IMPROVED_IRRIGATION.apply {
			effect(ROME_AGRI_GDP_MOD, 10)
			effect(ROME_AGRI_BUILDING_COST_MOD, -5)
		}
		RomanTechs.LAND_RECLAMATION.apply {
			effect(ROME_AGRI_GDP_MOD, 15)
			effect(ROME_AGRI_BUILDING_COST_MOD, -5)
		}
		RomanTechs.SEED_SELECTION.apply {
			effect(ROME_AGRI_GDP_MOD, 15)
			effect(ROME_AGRI_BUILDING_COST_MOD, -10)
		}

		RomanTechs.COMMON_WEIGHTS_AND_MEASURES.apply {
			effect("rom_tech_civil_economy_trade_gdp_mod", 10)
			effect("rom_tech_civil_economy_trade_mod", 10)
		}
		RomanTechs.COMMON_CURRENCY.apply {
			effect("rom_tech_civil_economy_trade_gdp_mod", 10)
			effect("rom_tech_civil_economy_trade_mod", 10)
		}
		RomanTechs.DENOMINATIONAL_SYSTEM.apply {
			effect("rom_tech_civil_economy_trade_gdp_mod", 15)
			effect("rom_tech_civil_economy_trade_mod", 15)
		}
		RomanTechs.PRODUCTION_LINES.apply {
			effect("rom_tech_civil_economy_trade_gdp_mod", 15)
			effect("rom_tech_civil_economy_trade_mod", 15)
		}

		RomanTechs.PHILOSOPHERS.apply {
			effect("rom_tech_civil_economy_culture_gdp_mod", 10)
		}
		RomanTechs.ASTRONOMY.apply {
			effect("rom_tech_civil_economy_culture_gdp_mod", 15)
		}
		RomanTechs.NATURAL_PHILOSOPHY.apply {
			effect("rom_tech_civil_economy_culture_gdp_mod", 20)
		}
		RomanTechs.CULTISM.apply {
			effect("rom_tech_civil_economy_culture_gdp_mod", 25)
		}

		RomanTechs.LEGAL_DOCUMENTATION.apply {
			effect("rom_tech_agent_action_cost_mod", -10)
			effect("rom_tech_civil_economy_tax_mod", 4)
		}
		RomanTechs.LABOUR_ORGANISATION.apply {
			effect("rom_tech_civil_economy_tax_mod", 6)
		}
		RomanTechs.LEGAL_INSTITUTIONS.apply {
			effect("rom_tech_civil_economy_tax_mod", 8)
			effect("rom_tech_module_civil_philosophy", -10) // corruption
		}
		RomanTechs.CONSENSUAL_CONTRACTS.apply {
			effect("rom_tech_agent_action_cost_mod", -20)
			effect("rom_tech_civil_economy_tax_mod", 10)
			effect("rom_tech_module_civil_philosophy", -15) // corruption
		}

		RomanTechs.TAX_LABOUR.apply {
			effect("rom_tech_civil_economy_growth_mod", 3)
		}
		RomanTechs.FIRED_BRICK.apply {
			effect("rom_tech_civil_economy_industry_gdp_mod", 15)
		}
		RomanTechs.CRANE.apply {
			effect("rom_tech_civil_economy_growth_region_mod", 4)
		}
		RomanTechs.MOULDED_ARCHITECTURE.apply {
			effect("rom_tech_civil_economy_industry_gdp_mod", 25)
		}
	}



	private fun modifyBuildings() {
		for(b in Buildings.ALL) b.mod {
			b.turns = 1
		}
		
		Buildings.ROMAN_TOWN_TRADE_2.apply {
			effect(GDP_LOCAL_TRADE, 100) // 100
			effect(GDP_MOD_TRADE, 0)
		}
		Buildings.ROMAN_TOWN_TRADE_3.apply {
			effect(GDP_LOCAL_TRADE, 200) // 150 + 50
			effect(GDP_MOD_TRADE, 0)
		}
		Buildings.ROMAN_TOWN_TRADE_4.apply {
			effect(GDP_LOCAL_TRADE, 300) // 200 + 100
			effect(GDP_MOD_TRADE, 0)
		}

		Buildings.ROMAN_TOWN_FARM_2.apply {
			effect(GDP_AGRICULTURE_FARMING, 100) // 100
			effect(GDP_MOD_AGRICULTURE, 10) // 3 + 7
		}
		Buildings.ROMAN_TOWN_FARM_3.apply {
			effect(GDP_AGRICULTURE_FARMING, 200) // 150 + 50
			effect(GDP_MOD_AGRICULTURE, 15) // 6 + 9
		}
		Buildings.ROMAN_TOWN_FARM_4.apply {
			effect(GDP_AGRICULTURE_FARMING, 300) // 200 + 100
			effect(GDP_MOD_AGRICULTURE, 25) // 9 + 16
		}

		Buildings.ROMAN_TOWN_CIVIL_2.apply {
			effect(GDP_SUBSISTENCE, 100) // 100
			effect(GDP_MOD_ALL, 4) // 2 + 2
		}
		Buildings.ROMAN_TOWN_CIVIL_3.apply {
			effect(GDP_SUBSISTENCE, 200) // 150 + 50
			effect(GDP_MOD_ALL, 8) // 4 + 4
		}
		Buildings.ROMAN_TOWN_CIVIL_4.apply {
			effect(GDP_SUBSISTENCE, 300) // 200 + 100
			effect(GDP_MOD_ALL, 12) // 6 + 6
		}

		Buildings.ROMAN_CITY_CIVIL_2.apply {
			effect(GDP_SUBSISTENCE, 350) // 300 + 50
			effect(GDP_MOD_ALL, 10) // 8 + 2
		}
		Buildings.ROMAN_CITY_CIVIL_3.apply {
			effect(GDP_SUBSISTENCE, 450) // 400 + 50
			effect(GDP_MOD_ALL, 15) // 10 + 5
		}
		Buildings.ROMAN_CITY_CIVIL_4.apply {
			effect(GDP_SUBSISTENCE, 600) // 500 + 100
			effect(GDP_MOD_ALL, 20) // 12 + 8
		}

		Buildings.ROMAN_CITY_GARRISON_2.apply {
			effect(GDP_MOD_ALL, 5) // 6 - 1
			effect(HAPPINESS, 4) // 2 + 2
			effect(GROWTH, 4) // 3 + 1
		}
		Buildings.ROMAN_CITY_GARRISON_3.apply {
			effect(GDP_MOD_ALL, 5) // 6 - 1
			effect(HAPPINESS, 8) // 4 + 4
			effect(GROWTH, 6) // 3 + 3
		}
		Buildings.ROMAN_CITY_GARRISON_4.apply {
			effect(GDP_MOD_ALL, 5) // 6 - 1
			effect(HAPPINESS, 16) // 6 + 10
			effect(GROWTH, 10) // 3 + 7
		}

		Buildings.ROMAN_TEMPLE_MARS_2.apply {
			effect(LATIN_INFLUENCE, 2)
		}
		Buildings.ROMAN_TEMPLE_MARS_3.apply {
			effect(LATIN_INFLUENCE, 3)
		}
		Buildings.ROMAN_TEMPLE_MARS_4.apply {
			effect(LATIN_INFLUENCE, 4)
		}

		Buildings.ROMAN_TEMPLE_VULCAN_2.apply {
			effect(LATIN_INFLUENCE, 2)
		}
		Buildings.ROMAN_TEMPLE_VULCAN_3.apply {
			effect(LATIN_INFLUENCE, 3)
		}
		Buildings.ROMAN_TEMPLE_VULCAN_4.apply {
			effect(LATIN_INFLUENCE, 4)
		}

		Buildings.ROMAN_TEMPLE_MINERVA_2.apply {
			effect(RESEARCH_RATE, 5)
			effect(GDP_MOD_CULTURE, 10)
			effect(LATIN_INFLUENCE, 2)
		}
		Buildings.ROMAN_TEMPLE_MINERVA_3.apply {
			effect(RESEARCH_RATE, 10)
			effect(GDP_MOD_CULTURE, 15)
			effect(LATIN_INFLUENCE, 3)
		}
		Buildings.ROMAN_TEMPLE_MINERVA_4.apply {
			effect(RESEARCH_RATE, 15)
			effect(GDP_MOD_CULTURE, 20)
			effect(LATIN_INFLUENCE, 4)
		}

		Buildings.ROMAN_LIBRARY_2.apply {
			effect(RESEARCH_RATE, 10)
			effect(GDP_CULTURE_LEARNING, 100)
		}
		Buildings.ROMAN_LIBRARY_3.apply {
			effect(RESEARCH_RATE, 20)
			effect(GDP_CULTURE_LEARNING, 150)
		}
		Buildings.ROMAN_LIBRARY_4.apply {
			effect(RESEARCH_RATE, 40)
			effect(GDP_CULTURE_LEARNING, 200)
		}

		Buildings.ROMAN_FARM_2.apply {
			effect(FOOD_PRODUCTION, 8)
			effect(GDP_AGRICULTURE_FARMING, 50)
		}
		Buildings.ROMAN_FARM_3.apply {
			effect(FOOD_PRODUCTION, 12)
			effect(GDP_AGRICULTURE_FARMING, 100)
		}
		Buildings.ROMAN_FARM_4.apply {
			effect(FOOD_PRODUCTION, 16)
			effect(GDP_AGRICULTURE_FARMING, 200)
		}
		Buildings.ROMAN_GRANARY_2.apply {
			effect(FOOD_PRODUCTION, 4)
			effect(GDP_AGRICULTURE_FARMING, 150)
			effect(UNIT_REPLENISHMENT, 10)
		}
		Buildings.ROMAN_GRANARY_3.apply {
			effect(FOOD_PRODUCTION, 5)
			effect(GDP_AGRICULTURE_FARMING, 200)
			effect(UNIT_REPLENISHMENT, 20)
		}
		Buildings.ROMAN_GRANARY_4.apply {
			effect(FOOD_PRODUCTION, 6)
			effect(GDP_AGRICULTURE_FARMING, 300)
			effect(UNIT_REPLENISHMENT, 40)
		}
		Buildings.ROMAN_HERD_2.apply {
			effect(FOOD_PRODUCTION, 6)
			effect(GDP_AGRICULTURE_HERDING, 150)
		}
		Buildings.ROMAN_HERD_3.apply {
			effect(FOOD_PRODUCTION, 8)
			effect(GDP_AGRICULTURE_HERDING, 200)
		}
		Buildings.ROMAN_HERD_4.apply {
			effect(FOOD_PRODUCTION, 10)
			effect(GDP_AGRICULTURE_HERDING, 300)
		}

		Buildings.ROMAN_TRADER_2.apply {
			effect(FOOD_CONSUMPTION, 1)
			effect(GDP_LOCAL_TRADE, 150)
		}
		Buildings.ROMAN_TRADER_3.apply {
			effect(FOOD_CONSUMPTION, 2)
			effect(GDP_LOCAL_TRADE, 300)
		}
		Buildings.ROMAN_TRADER_4.apply {
			effect(FOOD_CONSUMPTION, 4)
			effect(GDP_LOCAL_TRADE, 500)
		}

		Buildings.ROMAN_THEATRE_2.apply {
			effect(HAPPINESS, 4)
		}
		Buildings.ROMAN_THEATRE_3.apply {
			effect(HAPPINESS, 6)
		}
		Buildings.ROMAN_THEATRE_4.apply {
			effect(HAPPINESS, 8)
		}

		Buildings.ROMAN_BUFF_ATTACK_2.apply {
			effect(FOOD_CONSUMPTION, 4)
			effect(ATTACK_BUFF, 10)
		}
		Buildings.ROMAN_BUFF_ATTACK_3.apply {
			effect(FOOD_CONSUMPTION, 8)
			effect(ATTACK_BUFF, 20)
		}
		Buildings.ROMAN_BUFF_DEFENCE_2.apply {
			effect(FOOD_CONSUMPTION, 4)
			effect(DEFENCE_BUFF, 10)
		}
		Buildings.ROMAN_BUFF_DEFENCE_3.apply {
			effect(FOOD_CONSUMPTION, 8)
			effect(DEFENCE_BUFF, 20)
		}
		Buildings.ROMAN_BUFF_HORSE_2.apply {
			effect(FOOD_CONSUMPTION, 4)
			effect(HORSE_BUFF, 10)
		}
		Buildings.ROMAN_BUFF_HORSE_3.apply {
			effect(FOOD_CONSUMPTION, 8)
			effect(HORSE_BUFF, 20)
		}
		Buildings.ROMAN_BUFF_RANGE_2.apply {
			effect(FOOD_CONSUMPTION, 4)
			effect(RANGE_BUFF, 10)
		}
		Buildings.ROMAN_BUFF_RANGE_3.apply {
			effect(FOOD_CONSUMPTION, 8)
			effect(RANGE_BUFF, 20)
		}
	}



	private fun modifyGarrisons() {
		Buildings.ROMAN_BARRACKS_MAIN_2.setGarrison(
			ROMAN_MEDIUM_MELEE to 2,
			ROMAN_STRONG_MELEE to 2,
		)

		Buildings.ROMAN_BARRACKS_MAIN_3.setGarrison(
			ROMAN_MEDIUM_MELEE to 3,
			ROMAN_STRONG_MELEE to 3,
		)

		Buildings.ROMAN_BARRACKS_MAIN_4.setGarrison(
			ROMAN_STRONG_MELEE to 4,
			ROMAN_ELITE_MELEE to 4
		)

		Buildings.ROMAN_BARRACKS_AUX_2.setGarrison(
			ROMAN_MEDIUM_MELEE to 2,
			ROMAN_MEDIUM_RANGED to 2
		)

		Buildings.ROMAN_BARRACKS_AUX_3.setGarrison(
			ROMAN_MEDIUM_MELEE to 4,
			ROMAN_MEDIUM_RANGED to 2
		)

		Buildings.ROMAN_BARRACKS_AUX_4.setGarrison(
			ROMAN_ELITE_MELEE to 3,
			ROMAN_MEDIUM_RANGED to 3
		)

		Buildings.ROMAN_CITY_1.setGarrison(
			ROMAN_LEVY_MELEE to 6,
			ROMAN_BASIC_RANGED to 3
		)

		Buildings.ROMAN_CITY_CIVIL_2.setGarrison(
			ROMAN_MEDIUM_MELEE to 4,
			ROMAN_LEVY_MELEE to 4,
			ROMAN_BASIC_RANGED to 3
		)

		Buildings.ROMAN_CITY_CIVIL_3.setGarrison(
			ROMAN_MEDIUM_MELEE to 4,
			ROMAN_STRONG_MELEE to 4,
			ROMAN_LEVY_MELEE to 4,
			ROMAN_BASIC_RANGED to 3
		)

		Buildings.ROMAN_CITY_CIVIL_4.setGarrison(
			ROMAN_ELITE_MELEE to 2,
			ROMAN_STRONG_MELEE to 10,
			ROMAN_MEDIUM_RANGED to 4
		)

		Buildings.ROMAN_CITY_GARRISON_2.setGarrison(
			ROMAN_STRONG_MELEE to 4,
			ROMAN_MEDIUM_MELEE to 4,
			ROMAN_MEDIUM_RANGED to 3
		)

		Buildings.ROMAN_CITY_GARRISON_3.setGarrison(
			ROMAN_STRONG_MELEE to 12,
			ROMAN_MEDIUM_RANGED to 4
		)

		Buildings.ROMAN_CITY_GARRISON_4.setGarrison(
			ROMAN_STRONG_MELEE to 10,
			ROMAN_ELITE_MELEE to 6,
			ROMAN_MEDIUM_RANGED to 4
		)

		for(building in Buildings.ROMAN_TEMPLES) {
			if(building.isLevel2) building.garrison(ROMAN_MEDIUM_MELEE)
			if(building.isLevel3) building.garrison(ROMAN_STRONG_MELEE)
			if(building.isLevel4) building.garrison(ROMAN_ELITE_MELEE)
			if(building.isLevel5) building.garrison(ROMAN_ELITE_MELEE)
		}

		Buildings.ROMAN_TOWN_TRADE_2.setGarrison(
			ROMAN_MEDIUM_MELEE to 6,
			ROMAN_MEDIUM_RANGED to 3
		)

		Buildings.ROMAN_TOWN_TRADE_3.setGarrison(
			ROMAN_STRONG_MELEE to 8,
			ROMAN_MEDIUM_RANGED to 4
		)

		Buildings.ROMAN_TOWN_TRADE_4.setGarrison(
			ROMAN_STRONG_MELEE to 8,
			ROMAN_ELITE_MELEE to 4,
			ROMAN_MEDIUM_RANGED to 4,
		)

		for(building in Buildings.ROMAN_TOWNS) {
			if(building.name.startsWith("rome_town_trade_")) continue

			if(building.isLevel1) building.setGarrison(
				ROMAN_LEVY_MELEE to 4,
				ROMAN_BASIC_RANGED to 2
			)

			if(building.isLevel2) building.setGarrison(
				ROMAN_MEDIUM_MELEE to 2,
				ROMAN_LEVY_MELEE to 4,
				ROMAN_BASIC_RANGED to 2
			)

			if(building.isLevel3) building.setGarrison(
				ROMAN_MEDIUM_MELEE to 4,
				ROMAN_LEVY_MELEE to 4,
				ROMAN_BASIC_RANGED to 3
			)

			if(building.isLevel4) building.setGarrison(
				ROMAN_ELITE_MELEE to 2,
				ROMAN_STRONG_MELEE to 6,
				ROMAN_MEDIUM_RANGED to 4
			)
		}

		for(building in Buildings.NON_ROMAN_TOWNS) {
			val culture = building.culture!!

			if(building.isLevel2) {
				building.garrison(culture.basicMeleeGarrison)
			}

			if(building.isLevel3) {
				building.garrison(culture.basicMeleeGarrison)
				building.garrison(culture.basicMeleeGarrison)
				building.garrison(culture.strongMeleeGarrison)
			}

			if(building.isLevel4) {
				building.garrison(culture.basicMeleeGarrison)
				building.garrison(culture.basicMeleeGarrison)
				building.garrison(culture.basicMeleeGarrison)
				building.garrison(culture.strongMeleeGarrison)
				building.garrison(culture.strongMeleeGarrison)
			}
		}

		for(building in Buildings.NON_ROMAN_CITIES) {
			val culture = building.culture!!

			if(building.isLevel2) {
				building.garrison(culture.basicMeleeGarrison)
				building.garrison(culture.basicMeleeGarrison)
			}

			if(building.isLevel3) {
				building.garrison(culture.basicMeleeGarrison)
				building.garrison(culture.basicMeleeGarrison)
				building.garrison(culture.strongMeleeGarrison)
				building.garrison(culture.strongMeleeGarrison)
			}

			if(building.isLevel4) {
				building.garrison(culture.eliteMeleeGarrison)
				building.garrison(culture.basicMeleeGarrison)
				building.garrison(culture.basicMeleeGarrison)
				building.garrison(culture.basicMeleeGarrison)
				building.garrison(culture.strongMeleeGarrison)
				building.garrison(culture.strongMeleeGarrison)
				building.garrison(culture.strongMeleeGarrison)
			}
		}
	}


}*/