package rome2

object Mod4 {


	fun mod() {
		modGarrisons()
		for(t in techs.values)
			if(t.unitUpgrades.isNotEmpty()) {
				println(t.name)
				t.unitUpgrades.forEach { println("    ${it.unit} -> ${it.upgradedUnit}") }
				println()
			}
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



	private fun modUnits() {
		Units.RORARII.mod {
			attack  = 20  // 13 + 7
			defence = 30  // 24 + 6
			morale  = 35  // 30 + 5
			bonusHp = 10  // 5 + 5
			charge  = 10  // 11 - 1
			cost    = 250 // 200 + 50
			upkeep  = 60  // 60
			armour  = armour("mail")        // cloth
			level   = TrainingLevel.TRAINED // poorly_trained
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
			level   = TrainingLevel.TRAINED   // poorly_trained
		}

		Units.HASTATI.mod {
			attack  = 40  // 35 + 5
			defence = 25  // 18 + 7
			morale  = 45  // 45
			bonusHp = 10  // 10
			charge  = 15  // 12 + 3
			cost    = 400 // 350 + 50
			upkeep  = 100 // 90 + 10
		}

		Units.PRINCIPES.mod {
			attack  = 50  // 47 + 3
			defence = 35  // 23 + 12
			morale  = 55  // 55
			bonusHp = 20  // 15 + 5
			charge  = 20  // 14 + 6
			cost    = 700 // 680 + 20
			upkeep  = 140 // 120 + 20
		}

		Units.TRIARII.mod {
			attack  = 50   // 31 + 19
			defence = 50   // 34 + 16
			morale  = 75   // 65 + 10
			bonusHp = 30   // 20 + 10
			charge  = 25   // 24 + 1
			cost    = 1400 // 800 + 600
			upkeep  = 200  // 140 + 60
		}
	}



	private fun modGarrisons() {
		GarrisonGroups.ROMAN_STRONG_MELEE.entries.forEach { println("${it.unit} ${it.priority}") }
		GarrisonGroups.ROMAN_STRONG_MELEE.unit(Units.LEGIONARIES, Units.VETERAN_LEGIONARIES)
		GarrisonGroups.ROMAN_STRONG_MELEE.unit(Units.LEGIONARY_COHORT, Units.VETERAN_LEGIONARIES)
		GarrisonGroups.ROMAN_ELITE_MELEE.unit(Units.TRIARII)
	}

	// Rom_Legionaries
	// Rom_Legionary_Cohort
	// Rom_Principes


}