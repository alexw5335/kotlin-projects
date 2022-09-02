package rome2.mods

/*import rome2.BuildingEffectType.*
import rome2.EffectScope.PROVINCE
import rome2.GarrisonUnit.*
import rome2.TrainingLevel.*

object Mod4 {


	fun mod() {
		modifyUnits()
		modifyGarrisons()
		modifyBuildings()
		modifyTechs()
		//modifyExperienceTiers()
	}



	/*
	RORARII   -> VIGILES                                    levy     (poor attack, good defence)
	HASTATI   -> LEGIONARIES         -> LEGIONARY_COHORT    basic    (basic attack, basic defence)
	PRINCIPES -> VETERAN_LEGIONARIES -> EVOCATI_COHORT      medium   (good attack, good defence, good charge)
	TRIARII   -> FIRST_COHORT        -> EAGLE_COHORT        strong   (good attack, great defence, great hp)
	          -> PRAETORIANS         -> PRAETORIAN_GUARD    elite    (great everything, extremely expensive)
	EQUITES   -> LEGIONARY_CAVALRY                          cavalry
	                                 -> PRAETORIAN_CAVALRY  elite cavalry
	LEVES                                                   basic ranged
	VELITES                                                 medium ranged
	 */



	private fun modifyUnits() {
		Weapons.GLADIUS.mod           { damage = 30 }                // 30       5
		Weapons.GLADIUS_MARIAN.mod    { damage = 35 }                // 30 + 5   5
		Weapons.GLADIUS_IMPERIAL.mod  { damage = 40 }                // 30 + 10  5
		Weapons.GLADIUS_ELITE.mod     { damage = 45; apDamage = 10 } // 34 + 11  5 + 5
		Weapons.SPEAR_CAV_ELITE.mod   { damage = 35; apDamage = 10 } // 24 + 12  5 + 5

		Shields.SCUTUM.mod           { defence = 30; armour = 35; block = 50 } // 30       35       50
		Shields.SCUTUM_MARIAN.mod    { defence = 40; armour = 40; block = 60 } // 25 + 15  40       50 + 10
		Shields.SCUTUM_IMPERIAL.mod  { defence = 50; armour = 50; block = 70 } // 25 + 25  40 + 10  50 + 20

		Armours.MAIL.mod            { armour = 40 } // 40
		Armours.MAIL_IMPROVED.mod   { armour = 50 } // 45 + 5
		Armours.SEGMENTATA.mod      { armour = 65 } // 50 + 15
		Armours.SEGMENTATA_ARM.mod  { armour = 75 } // 55 + 20

		val cretanArrow = Projectile(projectile("arrow_composite").assembleLine).mod {
			name           = "cretan_arrow"
			damage         = 55  // 45 + 10
			apDamage       = 10  // 4 + 6
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
			ammo     = 12  // 7 + 5
		}

		Units.VELITES.mod {
			reload   = 15  // 13 + 2
			accuracy = 10  // 5 + 5
			ammo     = 15  // 7 + 8
			cost     = 500 // 340 + 160
			level    = TRAINED
		}

		Units.AUX_PELTASTS.mod {
			attack   = 20   // 12 + 8
			defence  = 20   // 14 + 6
			morale   = 55   // 55
			bonusHp  = 20   // 15 + 5
			charge   = 5    // 6 - 1
			cost     = 1000 // 600 + 400
			upkeep   = 150  // 130 + 20
			reload   = 35   // 28 + 7
			accuracy = 15   // 5 + 10
			ammo     = 15   // 7 + 8
			cost     = 1000 // 420 + 580
			upkeep   = 100  // 90 + 10
			level    = ELITE // poorly_trained
			missileWeapon = peltastJavelinWeapon
		}

		Units.AUX_CRETAN_ARCHERS.mod {
			reload   = 35   // 28 + 7
			ammo     = 25   // 15 + 10
			accuracy = 15   // 5 + 10
			cost     = 1000 // 600 + 400
			level    = ELITE
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
			level   = TRAINED // poorly_trained
		}

		Units.HASTATI.mod {
			attack  = 40  // 35 + 5
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
			defence = 35  // 23 + 12
			morale  = 55  // 55
			bonusHp = 20  // 15 + 5
			charge  = 20  // 14 + 6
			cost    = 700 // 680 + 20
			upkeep  = 140 // 120 + 20
			level   = WELL_TRAINED // trained
		}

		Units.TRIARII.mod {
			attack  = 50   // 31 + 19
			defence = 50   // 34 + 16
			morale  = 65   // 65
			bonusHp = 30   // 20 + 10
			charge  = 25   // 24 + 1
			cost    = 1500 // 800 + 700
			upkeep  = 200  // 140 + 60
			level   = ELITE // elite
		}

		Units.LEGIONARIES.mod {
			attack  = 50   // 47 + 3     + 10
			defence = 35   // 23 + 12    + 10
			morale  = 55   // 55         + 10
			bonusHp = 20   // 15 + 10    + 10
			charge  = 20   // 14 + 6     + 5
			cost    = 1000 // 660 + 340
			upkeep  = 150  // 140 + 10
			level   = TRAINED // trained
		}

		Units.VETERAN_LEGIONARIES.mod {
			attack  = 60   // 59 + 1      + 10
			defence = 45   // 23 + 22     + 10
			morale  = 65   // 65          + 10
			bonusHp = 25   // 15 + 10     + 5
			charge  = 20   // 15 + 5
			cost    = 1500 // 850 + 650
			upkeep  = 200  // 160 + 40
			level   = WELL_TRAINED // trained
		}

		Units.FIRST_COHORT.mod {
			attack  = 60   // 47 + 13     + 10
			defence = 60   // 33 + 27     + 10
			morale  = 75   // 65 + 10     + 10
			bonusHp = 40   // 20 + 20     + 10
			charge  = 25   // 12 + 13
			cost    = 2000 // 910 + 1090
			upkeep  = 250  // 180 + 70
			level   = ELITE // trained
		}

		Units.PRAETORIANS.mod {
			attack  = 75   // 65 + 10
			defence = 65   // 30 + 35
			morale  = 85   // 70 + 15
			bonusHp = 40   // 20 + 20
			charge  = 30   // 19 + 11
			cost    = 4000 // 1280 + 2720
			upkeep  = 400  // 200 + 200
		}

		Units.LEGIONARY_COHORT.mod {
			attack  = 60   // 47 + 13     + 10
			defence = 45   // 23 + 12     + 10
			morale  = 65   // 55 + 20     + 10
			bonusHp = 25   // 15 + 10     + 5
			charge  = 25   // 14 + 11     + 5
			cost    = 1500 // 700 + 800
			upkeep  = 200  // 140 + 60
		}

		Units.EVOCATI_COHORT.mod {
			attack  = 70   // 61 + 9      + 10
			defence = 55   // 23 + 22     + 10
			morale  = 75   // 65 + 10     + 10
			bonusHp = 30   // 15 + 15     + 5
			charge  = 25   // 15 + 10     + 5
			cost    = 2000 // 890 + 1110
			upkeep  = 250  // 160 + 90
		}

		Units.EAGLE_COHORT.mod {
			attack  = 70   // 47 + 23     + 10
			defence = 70   // 35 + 35     + 10
			morale  = 85   // 65 + 20     + 10
			bonusHp = 50   // 20 + 30     + 10
			charge  = 30   // 12 + 18     + 5
			cost    = 3000 // 930 + 2070
			upkeep  = 300  // 180 + 120
		}

		Units.PRAETORIAN_GUARD.mod {
			attack  = 90   // 65 + 25     + 15
			defence = 80   // 30 + 50     + 15
			morale  = 100  // 70 + 30     + 15
			bonusHp = 50   // 20 + 30     + 10
			charge  = 35   // 19 + 16     + 5
			shield  = shield("scutum_imperial")
			cost    = 6000 // 1280 + 4720
			upkeep  = 600  // 200 + 400
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
			cost    = 3000 // 1250 + 1750
			upkeep  = 300  // 200 + 100
		}

		RomanTechs.COHORT_ORGANISATION.unitUpgrade(Units.TRIARII, Units.FIRST_COHORT)
		RomanTechs.PROFESSIONAL_SOLDIERY.unitUpgrade(Units.TRIARII, Units.EAGLE_COHORT)
		RomanTechs.COHORT_ORGANISATION.modifyUnitUpgradeCosts()
		RomanTechs.PROFESSIONAL_SOLDIERY.modifyUnitUpgradeCosts()
	}



	private fun modifyBuildings() {
		for(b in Buildings.ALL) b.mod {
			b.turns = b.adjustedLevel
		}

		/*
		TRADE TOWN: Better garrisons, better public order, weaker gdp, 4/6/8 happiness
		FARMING TOWN: Better growth, agriculture gdp mod for whole province, no happiness
		CIVIL TOWN: all gdp mod for whole province, 1/2/3 happiness
		 */
		Buildings.ROMAN_TOWN_TRADE_2.apply {
			effect(GDP_LOCAL_TRADE, 50) // 50
			effect(GDP_MOD_TRADE, 5) // 3 + 2
			addEffect(HAPPINESS, PROVINCE, 4)
		}
		Buildings.ROMAN_TOWN_TRADE_3.apply {
			effect(GDP_LOCAL_TRADE, 100) // 100
			effect(GDP_MOD_TRADE, 10) // 6 + 4
			addEffect(HAPPINESS, PROVINCE, 6)
		}
		Buildings.ROMAN_TOWN_TRADE_4.apply {
			effect(GDP_LOCAL_TRADE, 150) // 150
			effect(GDP_MOD_TRADE, 15) // 9 + 6
			addEffect(HAPPINESS, PROVINCE, 8)
		}
		Buildings.ROMAN_TOWN_FARM_2.apply {
			effect(GDP_AGRICULTURE_FARMING, 100) // 100
			effect(GDP_MOD_AGRICULTURE, 10) // 3 + 7
			effect(GROWTH, 2) // 2
		}
		Buildings.ROMAN_TOWN_FARM_3.apply {
			effect(GDP_AGRICULTURE_FARMING, 150) // 150
			effect(GDP_MOD_AGRICULTURE, 20) // 6 + 9
			effect(GROWTH, 4) // 3 + 1
		}
		Buildings.ROMAN_TOWN_FARM_4.apply {
			effect(GDP_AGRICULTURE_FARMING, 200) // 200
			effect(GDP_MOD_AGRICULTURE, 30) // 9 + 16
			effect(GROWTH, 6) // 4 + 2
		}
		Buildings.ROMAN_TOWN_CIVIL_2.apply {
			effect(GDP_SUBSISTENCE, 100) // 100
			effect(GDP_MOD_ALL, 4) // 2 + 2
		}
		Buildings.ROMAN_TOWN_CIVIL_3.apply {
			effect(GDP_SUBSISTENCE, 150) // 150
			effect(GDP_MOD_ALL, 8) // 4 + 4
		}
		Buildings.ROMAN_TOWN_CIVIL_4.apply {
			effect(GDP_SUBSISTENCE, 200) // 200
			effect(GDP_MOD_ALL, 12) // 6 + 6
		}

		/*
		CIVIL CITY: Better GDP and GDP mod
		GARRISON CITY: Better happiness and much better garrison
		ALL: 4/6/8 growth
		 */
		Buildings.ROMAN_CITY_CIVIL_2.apply {
			effect(GDP_SUBSISTENCE, 250) // 300 - 50
			effect(GDP_MOD_ALL, 10) // 8 + 2
			effect(GROWTH, 4) // 5 - 1
		}
		Buildings.ROMAN_CITY_CIVIL_3.apply {
			effect(GDP_SUBSISTENCE, 300) // 400 - 50
			effect(GDP_MOD_ALL, 15) // 10 + 5
			effect(GROWTH, 6) // 7 - 1
		}
		Buildings.ROMAN_CITY_CIVIL_4.apply {
			effect(GDP_SUBSISTENCE, 350) // 500 - 150
			effect(GDP_MOD_ALL, 20) // 12 + 8
			effect(GROWTH, 8) // 9 - 1
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
			effect(RESEARCH_RATE, 10)
		}
		Buildings.ROMAN_LIBRARY_3.apply {
			effect(RESEARCH_RATE, 20)
		}
		Buildings.ROMAN_LIBRARY_4.apply {
			effect(RESEARCH_RATE, 30)
		}

		/*
		FARM: high food, low gdp, high squalor
		GRANARY: low food, medium gdp, army replenishment, provides some happiness
		HERD: medium food, high gdp, high squalor
		 */
		Buildings.ROMAN_FARM_2.apply {
			effect(FOOD_PRODUCTION, 8) // 7 + 1
			effect(GDP_AGRICULTURE_FARMING, 60) // 60
			effect(SQUALOR, -2) // -1 - 1
		}
		Buildings.ROMAN_FARM_3.apply {
			effect(FOOD_PRODUCTION, 12) // 11 + 1
			effect(GDP_AGRICULTURE_FARMING, 70) // 80 - 10
			effect(SQUALOR, -4) // -3 - 1
		}
		Buildings.ROMAN_FARM_4.apply {
			effect(FOOD_PRODUCTION, 16) // 15 + 1
			effect(GDP_AGRICULTURE_FARMING, 80) // 100 - 20
			effect(SQUALOR, -8) // -6 - 2
		}
		Buildings.ROMAN_GRANARY_2.apply {
			effect(FOOD_PRODUCTION, 4) // 4
			effect(GDP_AGRICULTURE_FARMING, 100) // 100
			effect(UNIT_REPLENISHMENT, 10) // 3 + 7
			addEffect(HAPPINESS, PROVINCE, 2)
		}
		Buildings.ROMAN_GRANARY_3.apply {
			effect(FOOD_PRODUCTION, 5) // 4
			effect(GDP_AGRICULTURE_FARMING, 150) // 150
			effect(UNIT_REPLENISHMENT, 20) // 6 + 14
			addEffect(HAPPINESS, PROVINCE, 3)
		}
		Buildings.ROMAN_GRANARY_4.apply {
			effect(FOOD_PRODUCTION, 6) // 4
			effect(GDP_AGRICULTURE_FARMING, 200) // 200
			effect(UNIT_REPLENISHMENT, 30) // 9 + 21
			addEffect(HAPPINESS, PROVINCE, 4)
		}
		Buildings.ROMAN_HERD_2.apply {
			effect(FOOD_PRODUCTION, 5) // 5
			effect(GDP_AGRICULTURE_HERDING, 100) // 100
			effect(SQUALOR, -2) // -1 - 1
		}
		Buildings.ROMAN_HERD_3.apply {
			effect(FOOD_PRODUCTION, 7) // 7
			effect(GDP_AGRICULTURE_HERDING, 150) // 175 - 25
			effect(SQUALOR, -4) // -3 - 1
		}
		Buildings.ROMAN_HERD_4.apply {
			effect(FOOD_PRODUCTION, 9) // 9
			effect(GDP_AGRICULTURE_HERDING, 200) // 250 - 50
			effect(SQUALOR, -8) // -6 - 2
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
			effect(ATTACK_BUFF, 15)
		}
		Buildings.ROMAN_BUFF_DEFENCE_2.apply {
			effect(FOOD_CONSUMPTION, 4)
			effect(DEFENCE_BUFF, 10)
		}
		Buildings.ROMAN_BUFF_DEFENCE_3.apply {
			effect(FOOD_CONSUMPTION, 8)
			effect(DEFENCE_BUFF, 15)
		}
		Buildings.ROMAN_BUFF_HORSE_2.apply {
			effect(FOOD_CONSUMPTION, 4)
			effect(HORSE_BUFF, 10)
		}
		Buildings.ROMAN_BUFF_HORSE_3.apply {
			effect(FOOD_CONSUMPTION, 8)
			effect(HORSE_BUFF, 15)
		}
		Buildings.ROMAN_BUFF_RANGE_2.apply {
			effect(FOOD_CONSUMPTION, 4)
			effect(RANGE_BUFF, 10)
		}
		Buildings.ROMAN_BUFF_RANGE_3.apply {
			effect(FOOD_CONSUMPTION, 8)
			effect(RANGE_BUFF, 15)
		}
	}



	private fun modifyGarrisons() {
		GarrisonGroups.ROMAN_BASIC_MELEE.unit(Units.RORARII, Units.HASTATI)
		GarrisonGroups.ROMAN_BASIC_MELEE.unit(Units.VIGILES, Units.LEGIONARIES)
		GarrisonGroups.ROMAN_BASIC_MELEE.unit(Units.LEGIONARY_COHORT)

		GarrisonGroups.ROMAN_MEDIUM_MELEE.unit(Units.HASTATI, Units.PRINCIPES)
		GarrisonGroups.ROMAN_MEDIUM_MELEE.unit(Units.LEGIONARIES, Units.VETERAN_LEGIONARIES)
		GarrisonGroups.ROMAN_MEDIUM_MELEE.unit(Units.LEGIONARY_COHORT, Units.EVOCATI_COHORT)

		GarrisonGroups.ROMAN_STRONG_MELEE.unit(Units.PRINCIPES, Units.TRIARII)
		GarrisonGroups.ROMAN_STRONG_MELEE.unit(Units.LEGIONARIES, Units.FIRST_COHORT)
		GarrisonGroups.ROMAN_STRONG_MELEE.unit(Units.LEGIONARY_COHORT, Units.EAGLE_COHORT)

		Buildings.ROMAN_BARRACKS_MAIN_2.setGarrison(
			ROMAN_MEDIUM_MELEE to 2,
			ROMAN_STRONG_MELEE to 2,
		)
		Buildings.ROMAN_BARRACKS_MAIN_3.setGarrison(
			ROMAN_MEDIUM_MELEE to 3,
			ROMAN_STRONG_MELEE to 3,
		)
		Buildings.ROMAN_BARRACKS_MAIN_4.setGarrison(
			ROMAN_STRONG_MELEE to 3,
			ROMAN_ELITE_MELEE to 2
		)

		Buildings.ROMAN_BARRACKS_AUX_2.setGarrison(
			ROMAN_MEDIUM_MELEE to 3,
			ROMAN_MEDIUM_RANGED to 1
		)
		Buildings.ROMAN_BARRACKS_AUX_3.setGarrison(
			ROMAN_STRONG_MELEE to 3,
			ROMAN_MEDIUM_RANGED to 2
		)
		Buildings.ROMAN_BARRACKS_AUX_4.setGarrison(
			ROMAN_ELITE_MELEE to 2,
			ROMAN_MEDIUM_RANGED to 3
		)

		Buildings.ROMAN_CITY_1.setGarrison(
			ROMAN_LEVY_MELEE to 6,
			ROMAN_BASIC_RANGED to 3
		)
		Buildings.ROMAN_CITY_CIVIL_2.setGarrison(
			ROMAN_BASIC_MELEE to 4,
			ROMAN_LEVY_MELEE to 6,
			ROMAN_BASIC_RANGED to 3
		)
		Buildings.ROMAN_CITY_CIVIL_3.setGarrison(
			ROMAN_BASIC_MELEE to 4,
			ROMAN_MEDIUM_MELEE to 4,
			ROMAN_LEVY_MELEE to 6,
			ROMAN_BASIC_RANGED to 3
		)
		Buildings.ROMAN_CITY_CIVIL_4.setGarrison(
			ROMAN_BASIC_MELEE to 4,
			ROMAN_MEDIUM_MELEE to 4,
			ROMAN_STRONG_MELEE to 4,
			ROMAN_MEDIUM_RANGED to 4
		)

		Buildings.ROMAN_CITY_GARRISON_2.setGarrison(
			ROMAN_BASIC_MELEE to 4,
			ROMAN_MEDIUM_MELEE to 4,
			ROMAN_MEDIUM_RANGED to 4
		)
		Buildings.ROMAN_CITY_GARRISON_3.setGarrison(
			ROMAN_BASIC_MELEE to 4,
			ROMAN_MEDIUM_MELEE to 4,
			ROMAN_STRONG_MELEE to 4,
			ROMAN_MEDIUM_RANGED to 4
		)
		Buildings.ROMAN_CITY_GARRISON_4.setGarrison(
			ROMAN_MEDIUM_MELEE to 6,
			ROMAN_STRONG_MELEE to 4,
			ROMAN_ELITE_MELEE to 2,
			ROMAN_MEDIUM_RANGED to 4
		)

		for(building in Buildings.ROMAN_TEMPLES) {
			if(building.isLevel2) building.garrison(ROMAN_BASIC_MELEE)
			if(building.isLevel3) building.garrison(ROMAN_MEDIUM_MELEE)
			if(building.isLevel4) building.garrison(ROMAN_STRONG_MELEE)
			if(building.isLevel5) building.garrison(ROMAN_ELITE_MELEE)
		}

		Buildings.ROMAN_TOWN_TRADE_2.setGarrison(
			ROMAN_BASIC_MELEE to 3,
			ROMAN_MEDIUM_MELEE to 3,
			ROMAN_MEDIUM_RANGED to 3
		)
		Buildings.ROMAN_TOWN_TRADE_3.setGarrison(
			ROMAN_BASIC_MELEE to 3,
			ROMAN_MEDIUM_MELEE to 3,
			ROMAN_STRONG_MELEE to 3,
			ROMAN_MEDIUM_RANGED to 3
		)
		Buildings.ROMAN_TOWN_TRADE_4.setGarrison(
			ROMAN_MEDIUM_MELEE to 6,
			ROMAN_STRONG_MELEE to 3,
			ROMAN_ELITE_MELEE to 1,
			ROMAN_MEDIUM_RANGED to 4
		)

		for(building in Buildings.ROMAN_TOWNS) {
			if(building.name.startsWith("rome_town_trade_")) continue

			if(building.isLevel1) building.setGarrison(
				ROMAN_LEVY_MELEE to 4,
				ROMAN_BASIC_RANGED to 2
			)
			if(building.isLevel2) building.setGarrison(
				ROMAN_LEVY_MELEE to 6,
				ROMAN_BASIC_MELEE to 3,
				ROMAN_BASIC_RANGED to 3
			)
			if(building.isLevel3) building.setGarrison(
				ROMAN_LEVY_MELEE to 4,
				ROMAN_BASIC_MELEE to 3,
				ROMAN_MEDIUM_MELEE to 3,
				ROMAN_BASIC_RANGED to 3
			)
			if(building.isLevel4) building.setGarrison(
				ROMAN_LEVY_MELEE to 6,
				ROMAN_BASIC_MELEE to 3,
				ROMAN_MEDIUM_MELEE to 3,
				ROMAN_STRONG_MELEE to 3,
				ROMAN_MEDIUM_RANGED to 3,
			)
		}

		for(building in Buildings.NON_ROMAN_TOWNS) {
			val culture = building.culture!!

			if(building.isLevel2) {
				building.garrison(culture.basicMeleeGarrison)
				building.garrison(culture.basicMeleeGarrison)
			}

			if(building.isLevel3) {
				building.garrison(culture.basicMeleeGarrison)
				building.garrison(culture.basicMeleeGarrison)
				building.garrison(culture.basicMeleeGarrison)
				building.garrison(culture.strongMeleeGarrison)
			}

			if(building.isLevel4) {
				building.garrison(culture.basicMeleeGarrison)
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
				building.garrison(culture.strongMeleeGarrison)
			}

			if(building.isLevel3) {
				building.garrison(culture.basicMeleeGarrison)
				building.garrison(culture.basicMeleeGarrison)
				building.garrison(culture.basicMeleeGarrison)
				building.garrison(culture.strongMeleeGarrison)
				building.garrison(culture.strongMeleeGarrison)
			}

			if(building.isLevel4) {
				building.garrison(culture.basicMeleeGarrison)
				building.garrison(culture.basicMeleeGarrison)
				building.garrison(culture.basicMeleeGarrison)
				building.garrison(culture.basicMeleeGarrison)
				building.garrison(culture.strongMeleeGarrison)
				building.garrison(culture.strongMeleeGarrison)
				building.garrison(culture.strongMeleeGarrison)
				building.garrison(culture.eliteMeleeGarrison)
			}
		}
	}



	private fun modifyTechs() {
		RomanTechs.PROFESSIONAL_SOLDIERY.cost *= 2

		RomanTechs.TRAINING_REFORMS.apply {
			effect(TechEffectType.ROME_UNIT_UPKEEP_MOD, -5)
		}
		RomanTechs.REMUNERATION_REFORMS.apply {
			effect(TechEffectType.ROME_UNIT_UPKEEP_MOD, -5)
		}
		RomanTechs.COHORT_ORGANISATION.apply {
			effect(TechEffectType.ROME_UNIT_UPKEEP_MOD, -5)
		}
		RomanTechs.PROFESSIONAL_SOLDIERY.apply {
			effect(TechEffectType.ROME_UNIT_UPKEEP_MOD, -10)
		}

		RomanTechs.IRON_TOOLS.apply {
			effect(TechEffectType.ROME_AGRI_GDP_MOD, 10)
			effect(TechEffectType.ROME_AGRI_BUILDING_COST_MOD, -5)
		}
		RomanTechs.DOUBLE_CROPPING.apply {
			effect(TechEffectType.ROME_AGRI_GDP_MOD, 10)
			effect(TechEffectType.ROME_AGRI_BUILDING_COST_MOD, -5)
		}
		RomanTechs.IMPROVED_IRRIGATION.apply {
			effect(TechEffectType.ROME_AGRI_GDP_MOD, 10)
			effect(TechEffectType.ROME_AGRI_BUILDING_COST_MOD, -5)
		}
		RomanTechs.LAND_RECLAMATION.apply {
			effect(TechEffectType.ROME_AGRI_GDP_MOD, 15)
			effect(TechEffectType.ROME_AGRI_BUILDING_COST_MOD, -5)
		}
		RomanTechs.SEED_SELECTION.apply {
			effect(TechEffectType.ROME_AGRI_GDP_MOD, 15)
			effect(TechEffectType.ROME_AGRI_BUILDING_COST_MOD, -10)
		}

		RomanTechs.COMMON_WEIGHTS_AND_MEASURES.apply {
			effect(TechEffectType.ROME_TRADE_GDP_MOD, 10)
			effect(TechEffectType.ROME_TARIFF_MOD, 10)
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
			effect(TechEffectType.ROME_TRADE_GDP_MOD, 15)
			effect(TechEffectType.ROME_TARIFF_MOD, 15)
		}

		RomanTechs.PHILOSOPHERS.apply {
			effect(TechEffectType.ROME_CULTURE_GDP_MOD, 10)
		}
		RomanTechs.ASTRONOMY.apply {
			effect(TechEffectType.ROME_CULTURE_GDP_MOD, 10)
		}
		RomanTechs.NATURAL_PHILOSOPHY.apply {
			effect(TechEffectType.ROME_CULTURE_GDP_MOD, 15)
		}
		RomanTechs.CULTISM.apply {
			effect(TechEffectType.ROME_CULTURE_GDP_MOD, 15)
		}

		RomanTechs.LEGAL_DOCUMENTATION.apply {
			effect(TechEffectType.ROME_AGENT_ACTION_COST_MOD, -10)
			effect(TechEffectType.ROME_TAX_MOD, 4)
		}
		RomanTechs.LABOUR_ORGANISATION.apply {
			effect(TechEffectType.ROME_TAX_MOD, 6)
		}
		RomanTechs.LEGAL_INSTITUTIONS.apply {
			effect(TechEffectType.ROME_TAX_MOD, 8)
			effect(TechEffectType.ROME_CORRUPTION_MOD, -10)
		}
		RomanTechs.CONSENSUAL_CONTRACTS.apply {
			effect(TechEffectType.ROME_TAX_MOD, 10)
			effect(TechEffectType.ROME_AGENT_ACTION_COST_MOD, -20)
			effect(TechEffectType.ROME_CORRUPTION_MOD, -15)
		}

		RomanTechs.TAX_LABOUR.apply {
			effect(TechEffectType.ROME_GROWTH_PROVINCE, 3)
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
					0 -> 40
					1 -> 80
					2 -> 120
					3 -> 200
					4 -> 280
					5 -> 360
					6 -> 480
					7 -> 600
					8 -> 720
					else -> error("Invalid rank")
				}

				skillPoints = if(rank == 8) 3 else 2
			} else {
				experience = when(rank) {
					0 -> 50
					1 -> 100
					2 -> 200
					3 -> 300
					4 -> 400
					5 -> 600
					6 -> 800
					7 -> 1000
					8 -> 1400
					else -> error("Invalid rank")
				}

				skillPoints = if(rank == 8) 3 else 2
			}
		}
	}


}*/