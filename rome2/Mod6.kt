package rome2

import rome2.BuildingEffectType.*
import rome2.EffectScope.PROVINCE
import rome2.TrainingLevel.*

object Mod6 {


	fun mod() {
		val romanMediumArtillery = newGarrisonGroup("rom_roman_medium_artillery", Units.POLYBOLOS)
		val romanEliteArtillery = newGarrisonGroup("rom_roman_elite_artillery", Units.SCORPION)
		val romanEliteRanged = newGarrisonGroup("rom_roman_elite_ranged", Units.AUX_PELTASTS)

		Buildings.ROMAN_CITY_CIVIL_3.apply {
			garrison(romanMediumArtillery)
		}
		Buildings.ROMAN_CITY_CIVIL_4.apply {
			garrison(romanEliteArtillery)
			garrison(romanEliteArtillery)
			garrison(romanEliteRanged)
		}
		Units.AUX_PELTASTS.newProjectile {
			damage = 10
			effectiveRange = 200
		}

		Units.POLYBOLOS.printFormatted()
		Units.SCORPION.printFormatted()

/*		val polybolosProjectile = Projectile(Units.POLYBOLOS.siegeWeapon!!.projectile.assembleLine).mod {
			name           = "rome_polybolos_projectile"
			damage         = 35  // 36 - 1
			apDamage       = 5   // 4 + 1
			effectiveRange = 100 // 150 + 25
		}

		val polybolosWeapon = MissileWeapon(Units.POLYBOLOS.siegeWeapon!!.assembleLine).mod {
			name = "rome_polybolos2"
			projectile = polybolosProjectile
		}

		Units.POLYBOLOS.*/

	}



	private fun modifyUnits() {
		Weapons.GLADIUS.mod           { damage = 30 }                // 30       5
		Weapons.GLADIUS_MARIAN.mod    { damage = 35 }                // 30 + 5   5
		Weapons.GLADIUS_IMPERIAL.mod  { damage = 40 }                // 30 + 10  5
		Weapons.GLADIUS_ELITE.mod     { damage = 45; apDamage = 10 } // 34 + 11  5 + 5
		Weapons.SPEAR_CAV_ELITE.mod   { damage = 35; apDamage = 10 } // 24 + 12  5 + 5

		Shields.SCUTUM.mod           { defence = 30; armour = 35; block = 50 } // 30       35       50
		Shields.SCUTUM_MARIAN.mod    { defence = 40; armour = 45; block = 60 } // 25 + 15  40 + 5   50 + 5
		Shields.SCUTUM_IMPERIAL.mod  { defence = 50; armour = 55; block = 70 } // 25 + 25  40 + 15  50 + 10

		Armours.MAIL.mod            { armour = 40 } // 40
		Armours.MAIL_IMPROVED.mod   { armour = 50 } // 45 + 5
		Armours.SEGMENTATA.mod      { armour = 65 } // 50 + 15
		Armours.SEGMENTATA_ARM.mod  { armour = 80 } // 55 + 25

		val cretanArrow = Projectile(projectile("arrow_composite").assembleLine).mod {
			name           = "cretan_arrow"
			damage         = 35  // 36 - 1
			apDamage       = 5   // 4 + 1
			effectiveRange = 175 // 150 + 25
		}

		val peltastJavelin = Projectile(projectile("javelin_normal").assembleLine).mod {
			name           = "peltast_javelin"
			damage         = 25  // 20 + 5
			apDamage       = 15  // 12 + 3
			effectiveRange = 100 // 80 + 20
		}

		val cretanBow = MissileWeapon(missileWeapon("rome_composite_bow_elite").assembleLine).mod {
			name = "cretan_bow"
			projectile = cretanArrow
		}

		val peltastJavelinWeapon = MissileWeapon(missileWeapon("rome_javelin").assembleLine).mod {
			name = "peltast_javelin_weapon"
			projectile = peltastJavelin
		}

		Units.LEVES.mod {
			reload   = 10  // 8 + 2
			accuracy = 5   // 5
			ammo     = 10  // 7 + 3
		}

		Units.VELITES.mod {
			reload   = 15  // 13 + 2
			accuracy = 8   // 5 + 3
			ammo     = 12  // 7 + 5
			cost     = 500 // 340 + 160
			level    = TRAINED
		}

		Units.AUX_PELTASTS.mod {
			attack   = 15   // 12 + 3
			defence  = 15   // 14 + 1
			morale   = 55   // 55
			bonusHp  = 15   // 15
			charge   = 5    // 6 - 1
			cost     = 1000 // 600 + 400
			upkeep   = 150  // 130 + 20
			reload   = 30   // 28 + 2
			accuracy = 15   // 5 + 10
			ammo     = 15   // 7 + 8
			cost     = 1000 // 420 + 580
			upkeep   = 100  // 90 + 10
			level    = ELITE // poorly_trained
			missileWeapon = peltastJavelinWeapon
		}

		Units.AUX_CRETAN_ARCHERS.mod {
			reload   = 30    // 28 + 2
			ammo     = 25    // 15 + 10
			accuracy = 10    // 5 + 5
			cost     = 2000  // 600 + 1400
			upkeep   = 200   // 130 + 70
			level    = ELITE // poorly_trained
			missileWeapon = cretanBow
		}

		Units.RORARII.mod {
			attack  = 20  // 13 + 7
			defence = 30  // 24 + 6
			morale  = 35  // 30 + 5
			bonusHp = 10  // 5 + 5
			charge  = 10  // 11 - 1
			cost    = 250 // 200 + 50
			upkeep  = 60  // 60
			armour  = armour("mail") // cloth
			level   = POORLY_TRAINED // poorly_trained
		}

		Units.VIGILES.mod {
			attack  = 25  // 13 + 12
			defence = 35  // 24 + 11
			morale  = 45  // 30 + 15
			bonusHp = 15  // 5 + 10
			charge  = 10  // 11 - 1
			cost    = 250 // 200 + 50
			upkeep  = 60  // 60
			armour  = armour("mail_improved") // cloth
			shield  = shield("scutum_marian") // scutum
			level   = TRAINED // poorly_trained
		}

		Units.HASTATI.mod {
			attack  = 35  // 35
			defence = 25  // 18 + 7
			morale  = 45  // 45
			bonusHp = 10  // 10
			charge  = 15  // 12 + 3
			cost    = 400 // 350 + 50
			upkeep  = 100 // 90 + 10
			level   = TRAINED // trained
		}

		Units.PRINCIPES.mod {
			attack  = 50  // 47 + 3
			defence = 30  // 23 + 7
			morale  = 55  // 55
			bonusHp = 15  // 15
			charge  = 20  // 14 + 6
			cost    = 700 // 680 + 20
			upkeep  = 140 // 120 + 20
			level   = WELL_TRAINED // trained
		}

		Units.TRIARII.mod {
			attack  = 50   // 31 + 19
			defence = 50   // 34 + 16
			morale  = 65   // 65
			bonusHp = 25   // 20 + 5
			charge  = 25   // 24 + 1
			cost    = 1500 // 800 + 400
			upkeep  = 200  // 140 + 60
			level   = ELITE // elite
		}

		Units.LEGIONARIES.mod {
			attack  = 50   // 47 + 3    + 15
			defence = 30   // 23 + 7    + 5
			morale  = 55   // 55        + 10
			bonusHp = 20   // 15 + 5    + 5
			charge  = 15   // 15
			cost    = 1000 // 660 + 340
			upkeep  = 150  // 140 + 10
			level   = TRAINED // trained
		}

		Units.VETERAN_LEGIONARIES.mod {
			attack  = 60   // 59 + 1     + 10
			defence = 35   // 23 + 12    + 5
			morale  = 65   // 65         + 10
			bonusHp = 20   // 15 + 5     + 5
			charge  = 20   // 15 + 5
			cost    = 1500 // 850 + 650
			upkeep  = 200  // 160 + 40
			level   = WELL_TRAINED // trained
		}

		Units.FIRST_COHORT.mod {
			attack  = 55   // 47 + 8      + 5
			defence = 50   // 33 + 17
			morale  = 75   // 65 + 10     + 10
			bonusHp = 30   // 20 + 10     + 5
			charge  = 25   // 12 + 13
			cost    = 2000 // 910 + 1090
			upkeep  = 250  // 180 + 70
			level   = ELITE // trained
		}

		Units.PRAETORIANS.mod {
			attack  = 65   // 65
			defence = 50   // 30 + 20
			morale  = 85   // 70 + 15
			bonusHp = 30   // 20 + 10
			charge  = 25   // 19 + 6
			cost    = 4000 // 1280 + 2720
			upkeep  = 400  // 200 + 200
			level   = ELITE
		}

		Units.LEGIONARY_COHORT.mod {
			attack  = 60   // 47 + 13     + 10
			defence = 35   // 23 + 12     + 5
			morale  = 65   // 55 + 20     + 10
			bonusHp = 25   // 15 + 10     + 5
			charge  = 20   // 14 + 6      + 5
			cost    = 1500 // 700 + 800
			upkeep  = 150  // 140 + 10
			level   = TRAINED
		}

		Units.EVOCATI_COHORT.mod {
			attack  = 65   // 61 + 4      + 5
			defence = 40   // 23 + 17     + 5
			morale  = 75   // 65 + 10     + 10
			bonusHp = 25   // 15 + 10     + 5
			charge  = 25   // 15 + 10     + 5
			cost    = 2000 // 890 + 1110
			upkeep  = 200  // 160 + 40
			level   = WELL_TRAINED
		}

		Units.EAGLE_COHORT.mod {
			attack  = 60   // 47 + 13     + 5
			defence = 55   // 35 + 20     + 5
			morale  = 85   // 65 + 20     + 10
			bonusHp = 35   // 20 + 15     + 5
			charge  = 30   // 12 + 18     + 5
			cost    = 2500 // 930 + 2070
			upkeep  = 250  // 180 + 70
			level   = ELITE
		}

		Units.PRAETORIAN_GUARD.mod {
			attack  = 75   // 65 + 10     + 10
			defence = 55   // 30 + 25     + 5
			morale  = 95   // 70 + 30      + 10
			bonusHp = 35   // 20 + 15     + 5
			charge  = 30   // 19 + 11     + 5
			shield  = shield("scutum_imperial")
			cost    = 5000 // 1280 + 4720
			upkeep  = 400  // 200 + 400
			level   = ELITE
		}

		Units.EQUITES.mod {
			attack  = 40  // 33 + 7
			defence = 15  // 12 + 3
			morale  = 45  // 40 + 5
			bonusHp = 10  // 10
			charge  = 30  // 29 + 1
			cost    = 600 // 500 + 100
			upkeep  = 120 // 100 + 20
			level   = TRAINED
		}

		Units.LEGIONARY_CAVALRY.mod {
			attack  = 50   // 33 + 12    + 10
			defence = 25   // 10 + 15    + 10
			morale  = 65   // 45 + 10    + 20
			bonusHp = 20   // 15 + 5     + 10
			charge  = 35   // 29 + 6     + 5
			cost    = 2000 // 620 + 880
			upkeep  = 150  // 120 + 30
			level   = WELL_TRAINED
		}

		Units.PRAETORIAN_CAVALRY.mod {
			attack  = 60   // 49 + 11
			defence = 40   // 22 + 18
			morale  = 75   // 70 + 5
			bonusHp = 30   // 20 + 10
			charge  = 45   // 42 + 3
			cost    = 5000 // 1250 + 1750
			upkeep  = 400  // 200 + 100
			level   = ELITE
		}

		RomanTechs.COHORT_ORGANISATION.unitUpgrade(Units.TRIARII, Units.FIRST_COHORT)
		RomanTechs.PROFESSIONAL_SOLDIERY.unitUpgrade(Units.TRIARII, Units.EAGLE_COHORT)
		RomanTechs.COHORT_ORGANISATION.modifyUnitUpgradeCosts()
		RomanTechs.PROFESSIONAL_SOLDIERY.modifyUnitUpgradeCosts()
	}



	private fun modifyBuildings() {
		for(b in Buildings.ALL) b.mod {
			b.turns = 1
		}

		/*
		TRADE TOWN: Better garrisons, better public order, weaker gdp, 4/6/8 happiness
		FARMING TOWN: Better growth, agriculture gdp mod for whole province, no happiness
		CIVIL TOWN: all gdp mod for whole province, 1/2/3 happiness
		 */
		Buildings.ROMAN_TOWN_TRADE_2.apply {
			effect(GDP_LOCAL_TRADE, 100)
			effect(GDP_MOD_TRADE, 5) // 3 + 2
			addEffect(HAPPINESS, PROVINCE, 4)
		}
		Buildings.ROMAN_TOWN_TRADE_3.apply {
			effect(GDP_LOCAL_TRADE, 200)
			effect(GDP_MOD_TRADE, 10) // 6 + 4
			addEffect(HAPPINESS, PROVINCE, 6)
		}
		Buildings.ROMAN_TOWN_TRADE_4.apply {
			effect(GDP_LOCAL_TRADE, 300)
			effect(GDP_MOD_TRADE, 15) // 9 + 6
			addEffect(HAPPINESS, PROVINCE, 8)
		}
		Buildings.ROMAN_TOWN_FARM_2.apply {
			effect(GDP_AGRICULTURE_FARMING, 150) // 100
			effect(GDP_MOD_AGRICULTURE, 10) // 3 + 7
			effect(GROWTH, 2) // 2
		}
		Buildings.ROMAN_TOWN_FARM_3.apply {
			effect(GDP_AGRICULTURE_FARMING, 300) // 150
			effect(GDP_MOD_AGRICULTURE, 20) // 6 + 14
			effect(GROWTH, 4) // 3 + 1
		}
		Buildings.ROMAN_TOWN_FARM_4.apply {
			effect(GDP_AGRICULTURE_FARMING, 450) // 200
			effect(GDP_MOD_AGRICULTURE, 40) // 9 + 31
			effect(GROWTH, 8) // 4 + 4
		}
		Buildings.ROMAN_TOWN_CIVIL_2.apply {
			effect(GDP_SUBSISTENCE, 150) // 100
			effect(GDP_MOD_ALL, 4) // 2
		}
		Buildings.ROMAN_TOWN_CIVIL_3.apply {
			effect(GDP_SUBSISTENCE, 300) // 150
			effect(GDP_MOD_ALL, 8) // 4
		}
		Buildings.ROMAN_TOWN_CIVIL_4.apply {
			effect(GDP_SUBSISTENCE, 450) // 200
			effect(GDP_MOD_ALL, 16) // 6
		}

		/*
		CIVIL CITY: Better GDP and GDP mod
		GARRISON CITY: Better happiness and much better garrison
		ALL: 4/6/8 growth
		 */
		Buildings.ROMAN_CITY_CIVIL_2.apply {
			effect(GDP_SUBSISTENCE, 300) // 300
			effect(GDP_MOD_ALL, 10) // 8 + 2
			effect(GROWTH, 4) // 5
		}
		Buildings.ROMAN_CITY_CIVIL_3.apply {
			effect(GDP_SUBSISTENCE, 450) // 400
			effect(GDP_MOD_ALL, 15) // 10
			effect(GROWTH, 6) // 7
		}
		Buildings.ROMAN_CITY_CIVIL_4.apply {
			effect(GDP_SUBSISTENCE, 600) // 500
			effect(GDP_MOD_ALL, 30) // 12
			effect(GROWTH, 8) // 9
		}
		Buildings.ROMAN_CITY_GARRISON_2.apply {
			effect(GDP_SUBSISTENCE, 250) // 250
			effect(GDP_MOD_ALL, 5) // 6 - 1
			effect(HAPPINESS, 4) // 2 + 2
			effect(GROWTH, 4) // 3 + 1
		}
		Buildings.ROMAN_CITY_GARRISON_3.apply {
			effect(GDP_SUBSISTENCE, 300) // 300
			effect(GDP_MOD_ALL, 5) // 6 - 1
			effect(HAPPINESS, 6) // 4 + 2
			effect(GROWTH, 6) // 3 + 3
		}
		Buildings.ROMAN_CITY_GARRISON_4.apply {
			effect(GDP_SUBSISTENCE, 350) // 350
			effect(GDP_MOD_ALL, 5) // 6 - 1
			effect(HAPPINESS, 8) // 6 + 2
			effect(GROWTH, 8) // 3 + 5
		}

		/*
		TEMPLES
		All have at least 2/3/4 cultural influence
		 */
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

		/*
		CITY CENTRE
		 */
		Buildings.ROMAN_LIBRARY_2.apply {
			effect(RESEARCH_RATE, 10) // 6 + 4
			effect(GDP_CULTURE_LEARNING, 100) // 70 + 30
		}
		Buildings.ROMAN_LIBRARY_3.apply {
			effect(RESEARCH_RATE, 20) // 12 + 8
			effect(GDP_CULTURE_LEARNING, 150) // 100 + 50
		}
		Buildings.ROMAN_LIBRARY_4.apply {
			effect(RESEARCH_RATE, 30) // 24 + 6
			effect(GDP_CULTURE_LEARNING, 200) // 150 + 50
		}

		/*
		FARM: high food, low gdp, high squalor
		GRANARY: low food, medium gdp, army replenishment, provides some happiness
		HERD: medium food, high gdp, high squalor
		 */
		Buildings.ROMAN_FARM_2.apply {
			effect(FOOD_PRODUCTION, 8) // 7 + 1
			effect(GDP_AGRICULTURE_FARMING, 60) // 60
		}
		Buildings.ROMAN_FARM_3.apply {
			effect(FOOD_PRODUCTION, 12) // 11 + 1
			effect(GDP_AGRICULTURE_FARMING, 80) // 80
		}
		Buildings.ROMAN_FARM_4.apply {
			effect(FOOD_PRODUCTION, 16) // 15 + 1
			effect(GDP_AGRICULTURE_FARMING, 100) // 100
		}
		Buildings.ROMAN_GRANARY_2.apply {
			effect(FOOD_PRODUCTION, 4) // 4
			effect(GDP_AGRICULTURE_FARMING, 100) // 100
			effect(UNIT_REPLENISHMENT, 10) // 3 + 7
			addEffect(SANITATION, PROVINCE, 2)
		}
		Buildings.ROMAN_GRANARY_3.apply {
			effect(FOOD_PRODUCTION, 5) // 4
			effect(GDP_AGRICULTURE_FARMING, 150) // 150
			effect(UNIT_REPLENISHMENT, 20) // 6 + 14
			addEffect(SANITATION, PROVINCE, 3)
		}
		Buildings.ROMAN_GRANARY_4.apply {
			effect(FOOD_PRODUCTION, 6) // 4
			effect(GDP_AGRICULTURE_FARMING, 200) // 200
			effect(UNIT_REPLENISHMENT, 30) // 9 + 21
			addEffect(SANITATION, PROVINCE, 4)
		}
		Buildings.ROMAN_HERD_2.apply {
			effect(FOOD_PRODUCTION, 5) // 5
			effect(GDP_AGRICULTURE_HERDING, 100) // 100
		}
		Buildings.ROMAN_HERD_3.apply {
			effect(FOOD_PRODUCTION, 7) // 7
			effect(GDP_AGRICULTURE_HERDING, 175) // 175
		}
		Buildings.ROMAN_HERD_4.apply {
			effect(FOOD_PRODUCTION, 9) // 9
			effect(GDP_AGRICULTURE_HERDING, 250) // 250
		}

		/*
		TOWN CENTRE
		TRADER: Low food consumption, high GDP
		THEATRE: high happiness, medium GDP
		 */
		Buildings.ROMAN_TRADER_2.apply {
			effect(FOOD_CONSUMPTION, 1)
			effect(GDP_LOCAL_TRADE, 150)
		}
		Buildings.ROMAN_TRADER_3.apply {
			effect(FOOD_CONSUMPTION, 2)
			effect(GDP_LOCAL_TRADE, 250)
		}
		Buildings.ROMAN_TRADER_4.apply {
			effect(FOOD_CONSUMPTION, 4)
			effect(GDP_LOCAL_TRADE, 400)
		}
		Buildings.ROMAN_THEATRE_2.apply {
			effect(HAPPINESS, 4) // 2 + 2
			effect(GDP_CULTURE_ENTERTAINMENT, 100) // 80 + 20
		}
		Buildings.ROMAN_THEATRE_3.apply {
			effect(HAPPINESS, 8) // 4 + 4
			effect(GDP_CULTURE_ENTERTAINMENT, 150) // 160 - 10
		}
		Buildings.ROMAN_THEATRE_4.apply {
			effect(HAPPINESS, 12) // 6 + 6
			effect(GDP_CULTURE_ENTERTAINMENT, 200) // 240 - 40
		}

		/*
		MILITARY BUFFS: higher buffs, higher food consumption
		food consumption: 0/1/4 -> 0/4/8
		buffs: 5/10 -> 10/15
		 */
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
	}



	private fun modifyTechs() {
		for(tech in RomanTechs.ALL)
			tech.cost = 0

		RomanTechs.TRAINING_REFORMS.apply {
			effect(TechEffectType.ROME_UNIT_UPKEEP_MOD, -10)
		}
		RomanTechs.REMUNERATION_REFORMS.apply {
			effect(TechEffectType.ROME_UNIT_UPKEEP_MOD, -15)
		}
		RomanTechs.COHORT_ORGANISATION.apply {
			effect(TechEffectType.ROME_UNIT_UPKEEP_MOD, -20)
		}
		RomanTechs.PROFESSIONAL_SOLDIERY.apply {
			effect(TechEffectType.ROME_UNIT_UPKEEP_MOD, -25)
		}

		RomanTechs.IRON_TOOLS.apply {
			effect(TechEffectType.ROME_AGRI_GDP_MOD, 5)
			effect(TechEffectType.ROME_AGRI_BUILDING_COST_MOD, -5)
		}
		RomanTechs.DOUBLE_CROPPING.apply {
			effect(TechEffectType.ROME_AGRI_GDP_MOD, 10)
			effect(TechEffectType.ROME_AGRI_BUILDING_COST_MOD, -5)
		}
		RomanTechs.IMPROVED_IRRIGATION.apply {
			effect(TechEffectType.ROME_AGRI_GDP_MOD, 15)
			effect(TechEffectType.ROME_AGRI_BUILDING_COST_MOD, -5)
		}
		RomanTechs.LAND_RECLAMATION.apply {
			effect(TechEffectType.ROME_AGRI_GDP_MOD, 20)
			effect(TechEffectType.ROME_AGRI_BUILDING_COST_MOD, -10)
		}
		RomanTechs.SEED_SELECTION.apply {
			effect(TechEffectType.ROME_AGRI_GDP_MOD, 25)
			effect(TechEffectType.ROME_AGRI_BUILDING_COST_MOD, -10)
		}

		RomanTechs.COMMON_WEIGHTS_AND_MEASURES.apply {
			effect(TechEffectType.ROME_TRADE_GDP_MOD, 5)
			effect(TechEffectType.ROME_TARIFF_MOD, 5)
		}
		RomanTechs.COMMON_CURRENCY.apply {
			effect(TechEffectType.ROME_TRADE_GDP_MOD, 10)
			effect(TechEffectType.ROME_TARIFF_MOD, 10)
		}
		RomanTechs.DENOMINATIONAL_SYSTEM.apply {
			effect(TechEffectType.ROME_TRADE_GDP_MOD, 15)
			effect(TechEffectType.ROME_TARIFF_MOD, 15)
		}
		RomanTechs.PRODUCTION_LINES.apply {
			effect(TechEffectType.ROME_TRADE_GDP_MOD, 20)
			effect(TechEffectType.ROME_TARIFF_MOD, 20)
		}

		RomanTechs.PHILOSOPHERS.apply {
			effect(TechEffectType.ROME_CULTURE_GDP_MOD, 10)
		}
		RomanTechs.ASTRONOMY.apply {
			effect(TechEffectType.ROME_CULTURE_GDP_MOD, 20)
		}
		RomanTechs.NATURAL_PHILOSOPHY.apply {
			effect(TechEffectType.ROME_CULTURE_GDP_MOD, 30)
		}
		RomanTechs.CULTISM.apply {
			effect(TechEffectType.ROME_CULTURE_GDP_MOD, 40)
		}

		RomanTechs.LEGAL_DOCUMENTATION.apply {
			effect(TechEffectType.ROME_AGENT_ACTION_COST_MOD, -10)
			effect(TechEffectType.ROME_TAX_MOD, 5)
		}
		RomanTechs.LABOUR_ORGANISATION.apply {
			effect(TechEffectType.ROME_TAX_MOD, 10)
		}
		RomanTechs.LEGAL_INSTITUTIONS.apply {
			effect(TechEffectType.ROME_TAX_MOD, 10)
			effect(TechEffectType.ROME_CORRUPTION_MOD, -10)
		}
		RomanTechs.CONSENSUAL_CONTRACTS.apply {
			effect(TechEffectType.ROME_TAX_MOD, 15)
			effect(TechEffectType.ROME_AGENT_ACTION_COST_MOD, -20)
			effect(TechEffectType.ROME_CORRUPTION_MOD, -15)
		}

		RomanTechs.TAX_LABOUR.apply {
			effect(TechEffectType.ROME_GROWTH_PROVINCE, 6)
		}
		RomanTechs.WATER_SLUICING.apply {
			effect(TechEffectType.ROME_GROWTH_PROVINCE, 6)
		}
		RomanTechs.FIRED_BRICK.apply {
			effect(TechEffectType.ROME_INDUSTRY_GDP_MOD, 15)
		}
		RomanTechs.MOULDED_ARCHITECTURE.apply {
			effect(TechEffectType.ROME_INDUSTRY_GDP_MOD, 25)
		}
	}



	private fun modifyExperienceTiers() {
		for(tier in experienceTiers) tier.mod {
			if(agent.isNotEmpty()) {
				experience = when(rank) {
					0 -> 100
					1 -> 200
					2 -> 300
					3 -> 400
					4 -> 500
					5 -> 600
					6 -> 800
					7 -> 1000
					8 -> 1200
					else -> error("Invalid rank")
				}

				skillPoints = when(rank) {
					0 -> 3
					1 -> 3
					2 -> 3
					3 -> 4
					4 -> 4
					5 -> 4
					6 -> 4
					7 -> 4
					8 -> 4
					else -> error("Invalid rank")
				}
			} else {
				experience = when(rank) {
					0 -> 100
					1 -> 200
					2 -> 300
					3 -> 400
					4 -> 500
					5 -> 600
					6 -> 800
					7 -> 1000
					8 -> 1200
					else -> error("Invalid rank")
				}

				skillPoints = when(rank) {
					0 -> 3
					1 -> 3
					2 -> 3
					3 -> 4
					4 -> 4
					5 -> 4
					6 -> 4
					7 -> 4
					8 -> 4
					else -> error("Invalid rank")
				}
			}
		}
	}



	private fun modifySkills() {
		for(skill in Skills.ALL) {
			skill.level1.mod { unlockRank = 1 }
			skill.level2.mod { unlockRank = 1 }
			skill.level3.mod { unlockRank = 1 }
		}
	}

}