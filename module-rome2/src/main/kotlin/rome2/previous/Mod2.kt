package rome2.previous

/*import rome2.TrainingLevel.*

object Mod2 {

	
	fun mod() {
		modOthers()
		modUnits()
		modGarrisons()
		modTechs()
		modBuildings()
		modSkills()
	}



	private fun modOthers() {
		campaignVariables["trade_route_value_combined_gdp_proportion"]!!.value = 2F

		for(incident in incidents.values)
			incident.mod { enabled = false; prioritised = false }

		for(dilemma in dilemmas.values)
			if(!dilemma.name.startsWith("female_leader") && dilemma.name != "rom_young_challenger")
				dilemma.mod { enabled = false; prioritised = false }

		for(mission in missions.values)
			if(mission.name.startsWith("senate") || mission.name.startsWith("diplomatic"))
				mission.mod { enabled = false; prioritised = false }

		fun String.disable() = messageEvent(this).mod { instantOpen = false }

		"agent_discovered_yours".disable()
		"character_gains_level".disable()
		"character_gains_level_female".disable()
		"politics_character_diplomatic_mission_ended".disable()
		"politics_character_mission_ended".disable()
		"politics_character_released_from_prison".disable()
		"your_army_gains_level".disable()
		"your_region_raided".disable()
		"your_trade_route_raided".disable()
		"your_fleet_gains_level".disable()
		"military_tech_advance".disable()

		for(b in budgetAllocations.values) b.mod {
			b.agentFundingCap = 0
			b.agentFundingAllocationPercentage = 0
			b.agentTurnOfInactivityUntilCap = 0
			b.agentPercentageOfPoolToSaveOnFail = 0
			b.armyFundingCap = 400000
			b.constructionFundingCap = 30000
			b.diplomacyFundingCap = 10000
		}

		effectBundle("govt_type_republic").effect("rom_faction_political_party_loyalty", 30)
		effectBundle("govt_type_elected_monarchy").effect("rom_faction_political_party_loyalty", 15)
		effectBundle("govt_type_empire").effect("rom_faction_political_party_loyalty", 45)

		for(d in dealEvalComponents) {
			if(d.deal == "WAR") {
				d.addMod()
				d.bestFriendsValue += 40F
				d.bitterEnemiesValue += 60F
				d.friendlyValue += 40F
				d.neutralValue += 40F
				d.unfriendlyValue += 60F
				d.veryFriendlyValue += 40F
				d.veryUnfriendlyValue += 60F
			} else if(d.deal == "PEACE") {
				d.addMod()
				d.bestFriendsValue -= 20F
				d.bitterEnemiesValue -= 20F
				d.friendlyValue -= 20F
				d.neutralValue -= 20F
				d.unfriendlyValue -= 30F
				d.veryFriendlyValue -= 20F
				d.veryUnfriendlyValue -= 30F
			}
		}

		for(d in dealGenPriorities.values) {
			d.addMod()

			when(d.name) {
				"PEACE" -> {
					d.lastStandPriority -= 100
					d.warPriority -= 100
				}

				"DECLARE_WAR_ON_FACTIONS_I_DISLIKE" -> {
					d.peacePriority += 60
					d.tensionPriority += 60
					d.warPriority += 60
					d.totalWarPriority += 60
				}

				else -> {
					d.peacePriority -= 300
					d.failureTimeout = 100
					d.tensionPriority -= 300
					d.warPriority -= 300
					d.totalWarPriority -= 300
				}
			}
		}

		effectBundle("rom_stance_army_forced_march").effect("rom_force_campaign_mod_movement_range", 0)
		effectBundle("rom_stance_navy_double_time").effect("rom_force_campaign_mod_movement_range", 0)

		occupationPriorities["occupation_decision_occupy"]!!.mod {
			lastStandPriority = 15
			peacePriority = 15
			tensionPriority = 15
			warPriority = 15
			totalWarPriority = 15
		}

		for(d in difficulties.values)
			d.addEffect("rom_payload_food", EffectScope.ALL_PROVINCES, 25F)
	}



	private fun modGarrisons() {
		val strongRanged  = newGarrisonGroup("rom_roman_strong_ranged", 100, Units.AUX_PELTASTS)
		val eliteRanged   = newGarrisonGroup("rom_roman_elite_ranged", 100, Units.AUX_CRETAN_ARCHERS)
		val mediumSiege   = newGarrisonGroup("rom_roman_medium_artillery", 100, Units.POLYBOLOS)
		val eliteSiege    = newGarrisonGroup("rom_roman_elite_artillery", 100, Units.SCORPION)
		val mediumCavalry = newGarrisonGroup("rom_roman_medium_cavalry", 200, Units.EQUITES, Units.LEGIONARY_CAVALRY)
		val eliteCavalry  = newGarrisonGroup("rom_roman_elite_cavalry", 100, Units.PRAETORIAN_CAVALRY)
		val levyMelee     = GarrisonGroups.ROMAN_LEVY_MELEE
		val basicMelee    = GarrisonGroups.ROMAN_BASIC_MELEE
		val mediumMelee   = GarrisonGroups.ROMAN_MEDIUM_MELEE
		val strongMelee   = GarrisonGroups.ROMAN_STRONG_MELEE
		val eliteMelee    = GarrisonGroups.ROMAN_ELITE_MELEE
		val basicRanged   = GarrisonGroups.ROMAN_BASIC_RANGED
		val mediumRanged  = GarrisonGroups.ROMAN_MEDIUM_RANGED

		levyMelee.priority(Units.RORARII, 400)
		levyMelee.priority(Units.VIGILES, 400)

		basicMelee.unit(Units.RORARII, Units.HASTATI, 300)
		basicMelee.unit(Units.VIGILES, Units.LEGIONARIES, 300)
		basicMelee.unit(Units.LEGIONARY_COHORT, 300)

		mediumMelee.unit(Units.HASTATI, Units.PRINCIPES, 200)
		mediumMelee.unit(Units.LEGIONARIES, Units.VETERAN_LEGIONARIES, 200)
		mediumMelee.unit(Units.LEGIONARY_COHORT, Units.EVOCATI_COHORT, 200)

		strongMelee.unit(Units.PRINCIPES, Units.TRIARII, 100)
		strongMelee.unit(Units.LEGIONARIES, Units.FIRST_COHORT, 100)
		strongMelee.unit(Units.LEGIONARY_COHORT, Units.EAGLE_COHORT, 100)

		val civil1 = arrayOf(
			levyMelee to 6,
			basicRanged to 2
		)

		val civil2 = arrayOf(
			levyMelee to 4,
			basicMelee to 4,
			basicRanged to 4,
		)

		val civil3 = arrayOf(
			levyMelee to 4,
			basicMelee to 4,
			mediumMelee to 4,
			mediumRanged to 4,
			mediumSiege to 1,
			mediumCavalry to 1
		)

		val civil4 = arrayOf(
			basicMelee to 4,
			mediumMelee to 4,
			strongMelee to 4,
			strongRanged to 4,
			mediumSiege to 2,
			mediumCavalry to 2,
		)

		val garrison2 = arrayOf(
			basicMelee to 4,
			mediumMelee to 4,
			mediumRanged to 4,
			mediumCavalry to 2
		)

		val garrison3 = arrayOf(
			basicMelee to 4,
			mediumMelee to 4,
			strongMelee to 4,
			strongRanged to 4,
			mediumSiege to 2,
			mediumCavalry to 2
		)

		val garrison4 = arrayOf(
			basicMelee to 2,
			mediumMelee to 4,
			strongMelee to 4,
			eliteMelee to 2,
			eliteRanged to 4,
			eliteCavalry to 2,
			eliteSiege to 2,
		)

		Buildings.ROMAN_CITY_1.setGarrison(*civil1)
		Buildings.ROMAN_CITY_CIVIL_2.setGarrison(*civil2)
		Buildings.ROMAN_CITY_CIVIL_3.setGarrison(*civil3)
		Buildings.ROMAN_CITY_CIVIL_4.setGarrison(*civil4)
		Buildings.ROMAN_CITY_GARRISON_2.setGarrison(*garrison2)
		Buildings.ROMAN_CITY_GARRISON_3.setGarrison(*garrison3)
		Buildings.ROMAN_CITY_GARRISON_4.setGarrison(*garrison4)

		Buildings.ROMAN_TOWN_1.setGarrison(*civil1)
		Buildings.ROMAN_TOWN_FARM_2.setGarrison(*civil2)
		Buildings.ROMAN_TOWN_FARM_3.setGarrison(*civil3)
		Buildings.ROMAN_TOWN_FARM_4.setGarrison(*civil4)
		Buildings.ROMAN_TOWN_CIVIL_2.setGarrison(*civil2)
		Buildings.ROMAN_TOWN_CIVIL_3.setGarrison(*civil3)
		Buildings.ROMAN_TOWN_CIVIL_4.setGarrison(*civil4)
		Buildings.ROMAN_TOWN_TRADE_2.setGarrison(*garrison2)
		Buildings.ROMAN_TOWN_TRADE_3.setGarrison(*garrison3)
		Buildings.ROMAN_TOWN_TRADE_4.setGarrison(*garrison4)

		for(building in Buildings.ROMAN_RESOURCE_TOWNS) {
			when(building.adjustedLevel) {
				2 -> building.setGarrison(*garrison2)
				3 -> building.setGarrison(*garrison3)
				4 -> building.setGarrison(*garrison4)
			}
		}

		Buildings.ROMAN_BARRACKS_MAIN_2.setGarrison(
			mediumMelee to 4
		)
		Buildings.ROMAN_BARRACKS_MAIN_3.setGarrison(
			strongMelee to 4,
			eliteMelee to 1,
		)
		Buildings.ROMAN_BARRACKS_MAIN_4.setGarrison(
			strongMelee to 3,
			eliteMelee to 3
		)

		Buildings.ROMAN_BARRACKS_AUX_2.setGarrison(
			basicMelee to 2,
			mediumRanged to 1,
			mediumCavalry to 1
		)
		Buildings.ROMAN_BARRACKS_AUX_3.setGarrison(
			strongMelee to 2,
			strongRanged to 1,
			mediumCavalry to 1,
			mediumSiege to 1
		)
		Buildings.ROMAN_BARRACKS_AUX_4.setGarrison(
			strongMelee to 1,
			eliteMelee to 1,
			eliteRanged to 1,
			eliteCavalry to 1,
			eliteSiege to 1
		)

		for(temple in Buildings.ROMAN_TEMPLES) {
			when(temple.adjustedLevel) {
				2 -> temple.setGarrison(basicMelee to 1)
				3 -> temple.setGarrison(mediumMelee to 1)
				4 -> temple.setGarrison(strongMelee to 1)
				5 -> temple.setGarrison(eliteMelee to 2)
			}
		}

		for(town in Buildings.NON_ROMAN_TOWNS) {
			val culture = town.culture!!

			when(town.adjustedLevel) {
				0 -> town.addGarrison(culture.basicMeleeGarrison to 2)
				1 -> town.addGarrison(culture.basicMeleeGarrison to 4, culture.strongMeleeGarrison to 1)
				2 -> town.addGarrison(culture.basicMeleeGarrison to 5, culture.strongMeleeGarrison to 2)
				3 -> town.addGarrison(culture.basicMeleeGarrison to 6, culture.strongMeleeGarrison to 3)
				4 -> town.addGarrison(culture.basicMeleeGarrison to 6, culture.strongMeleeGarrison to 4)
			}
		}

		for(city in Buildings.NON_ROMAN_CITIES) {
			val culture = city.culture!!

			when(city.adjustedLevel) {
				0 -> city.addGarrison(culture.basicMeleeGarrison to 2)
				1 -> city.addGarrison(culture.basicMeleeGarrison to 4, culture.strongMeleeGarrison to 1)
				2 -> city.addGarrison(culture.basicMeleeGarrison to 5, culture.strongMeleeGarrison to 2)
				3 -> city.addGarrison(culture.basicMeleeGarrison to 6, culture.strongMeleeGarrison to 3)
				4 -> city.addGarrison(culture.basicMeleeGarrison to 6, culture.strongMeleeGarrison to 4)
			}
		}
	}



	private fun modUnits() {
		Weapons.GLADIUS.mod           { damage = 30 }                // 30       5
		Weapons.GLADIUS_MARIAN.mod    { damage = 35 }                // 30 + 5   5
		Weapons.GLADIUS_IMPERIAL.mod  { damage = 40 }                // 30 + 10  5
		Weapons.GLADIUS_ELITE.mod     { damage = 45; apDamage = 10 } // 34 + 11  5 + 5
		Weapons.SPEAR_CAV_ELITE.mod   { damage = 35; apDamage = 10 } // 24 + 12  5 + 5

		Shields.SCUTUM.mod           { defence = 30; armour = 35; blockChance = 50 } // 30       35       50
		Shields.SCUTUM_MARIAN.mod    { defence = 40; armour = 45; blockChance = 60 } // 25 + 15  40 + 5   50 + 5
		Shields.SCUTUM_IMPERIAL.mod  { defence = 50; armour = 55; blockChance = 70 } // 25 + 25  40 + 15  50 + 10

		Armours.MAIL.mod            { armour = 40 } // 40
		Armours.MAIL_IMPROVED.mod   { armour = 50 } // 45 + 5
		Armours.SEGMENTATA.mod      { armour = 65 } // 50 + 15
		Armours.SEGMENTATA_ARM.mod  { armour = 80 } // 55 + 25

		Units.POLYBOLOS.mod {
			accuracy = 15   // 5
			reload   = 15   // 4
			ammo     = 100  // 60
			cost     = 1500 // 620
			upkeep   = 150  // 120
			level    = WELL_TRAINED

			newProjectile {
				damage     = 40 // 35
				apDamage   = 60 // 50
				range      = 300 // 260
				reloadTime = 5 // 5
				penetration = "medium" // low
			}
		}

		Units.SCORPION.mod {
			accuracy = 25   // 5
			ammo     = 120  // 40
			reload   = 30   // 4
			cost     = 2500
			upkeep   = 250
			level    = ELITE

			newProjectile {
				damage       = 60  // 50
				apDamage     = 80  // 70
				range        = 400 // 350
				reloadTime   = 5   // 10
				penetration  = "medium" // low
				collision    = 0.15F // 0.1
				marksmanship = 40F // 30
				velocity     = 120F // 90
			}
		}

		Units.LEVES.mod {
			reload   = 10  // 8 + 2
			accuracy = 5   // 5
			ammo     = 10  // 7 + 3
		}

		Units.VELITES.mod {
			reload   = 20  // 13 + 7
			accuracy = 8   // 5 + 3
			ammo     = 12  // 7 + 5
			cost     = 400 // 340
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

			newProjectile {
				damage   = 25 // 20 + 5
				apDamage = 15 // 12 + 3
				range    = 100 // 80 + 20
			}
		}

		Units.AUX_CRETAN_ARCHERS.mod {
			reload   = 30    // 28 + 2
			ammo     = 25    // 15 + 10
			accuracy = 15    // 5 + 10
			cost     = 2000  // 600 + 1400
			upkeep   = 200   // 130 + 70
			level    = ELITE // poorly_trained

			newProjectile {
				damage   = 35 // 36 - 1
				apDamage = 4 // 4 + 1
				range    = 175 // 150 + 25
			}
		}

		Units.RORARII.mod {
			attack  = 20  // 13 + 7
			defence = 30  // 24 + 6
			morale  = 35  // 30 + 5
			bonusHp = 10  // 5 + 5
			charge  = 10  // 11 - 1
			cost    = 200 // 200
			upkeep  = 50  // 60
			armour  = armour("mail") // cloth
			level   = POORLY_TRAINED // poorly_trained
		}

		Units.VIGILES.mod {
			attack  = 25  // 13 + 12
			defence = 35  // 24 + 11
			morale  = 45  // 30 + 15
			bonusHp = 15  // 5 + 10
			charge  = 10  // 11 - 1
			cost    = 200 // 200
			upkeep  = 40  // 60
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
			cost    = 350 // 350 + 50
			upkeep  = 70 // 90
			level   = TRAINED // trained
		}

		Units.PRINCIPES.mod {
			attack  = 50  // 47 + 3
			defence = 30  // 23 + 7
			morale  = 55  // 55
			bonusHp = 15  // 15
			charge  = 20  // 14 + 6
			cost    = 700 // 680 + 20
			upkeep  = 100 // 120
			level   = WELL_TRAINED // trained
		}

		Units.TRIARII.mod {
			attack  = 50   // 31 + 19
			defence = 50   // 34 + 16
			morale  = 65   // 65
			bonusHp = 25   // 20 + 5
			charge  = 25   // 24 + 1
			cost    = 1200 // 800
			upkeep  = 150  // 140
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
			attack  = 65   // 47 + 18     + 15
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
			bonusHp = 35   // 20 + 15
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
			cost    = 2000 // 890
			upkeep  = 200  // 160
			level   = WELL_TRAINED
		}

		Units.EAGLE_COHORT.mod {
			attack  = 70   // 47 + 23     + 5
			defence = 55   // 35 + 20     + 5
			morale  = 85   // 65 + 20     + 10
			bonusHp = 35   // 20 + 15     + 5
			charge  = 30   // 12 + 18     + 5
			cost    = 2500 // 930
			upkeep  = 250  // 180
			level   = ELITE
		}

		Units.PRAETORIAN_GUARD.mod {
			attack  = 75   // 65 + 10     + 10
			defence = 60   // 30 + 30     + 10
			morale  = 95   // 70 + 25     + 10
			bonusHp = 45   // 20 + 25     + 10
			charge  = 30   // 19 + 11     + 5
			shield  = shield("scutum_imperial")
			cost    = 5000 // 1280 + 3720
			upkeep  = 500  // 200 + 300
			level   = ELITE
		}

		Units.EQUITES.mod {
			attack  = 40  // 33 + 7
			defence = 15  // 12 + 3
			morale  = 45  // 40 + 5
			bonusHp = 10  // 10
			charge  = 30  // 29 + 1
			cost    = 500 // 500 + 100
			upkeep  = 100 // 100 + 20
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
			cost    = 5000 // 1250 + 2750
			upkeep  = 500  // 200 + 300
			level   = ELITE
		}

		for(u in landUnits.values)
			if(u.category == "artillery" && !u.name.startsWith("Rom_") && !u.name.startsWith("3c_"))
				u.mod { cap = 1 }

		Techs.COHORT_ORGANISATION.unitUpgrade(Units.TRIARII, Units.FIRST_COHORT)
		Techs.PROFESSIONAL_SOLDIERY.unitUpgrade(Units.TRIARII, Units.EAGLE_COHORT)
		Techs.COHORT_ORGANISATION.unitUpgrade(Units.PRINCIPES, Units.VETERAN_LEGIONARIES)
		Techs.PROFESSIONAL_SOLDIERY.unitUpgrade(Units.PRINCIPES, Units.EVOCATI_COHORT)
		Techs.COHORT_ORGANISATION.modifyUnitUpgradeCosts()
		Techs.PROFESSIONAL_SOLDIERY.modifyUnitUpgradeCosts()

		Buildings.ROMAN_SIEGE_2.removeUnitUpgrade(Units.SCORPION)
		Buildings.ROMAN_SIEGE_3.unitUpgrade(Units.SCORPION)
	}



	private fun modBuildings() {
		for(b in Buildings.ROMAN) b.mod {
			b.turns = when(b.adjustedLevel) {
				1 -> 1
				2 -> 1
				3 -> 2
				4 -> 3
				5 -> 5
				else -> error("")
			}
		}

		Buildings.ROMAN_CIRCUS_MAXIMUS.mod {
			cost = 30000 // 13900
		}
		Buildings.ROMAN_COLOSSEUM.mod {
			cost = 30000 // 13600
		}
		Buildings.ROMAN_TEMPLE_JUPITER_5.mod {
			cost = 20000 // 12600
		}

		Buildings.ROMAN_CITY_GARRISON_2.mod {
			cost = 3000 // 2000
		}
		Buildings.ROMAN_CITY_GARRISON_3.mod {
			cost = 5000 // 3000
		}
		Buildings.ROMAN_CITY_GARRISON_4.mod {
			cost = 10000 // 6000
		}

		Buildings.ROMAN_CITY_CIVIL_2.mod {
			cost = 3000 // 2700
		}
		Buildings.ROMAN_CITY_CIVIL_3.mod {
			cost = 5000 // 4400
		}
		Buildings.ROMAN_CITY_CIVIL_4.mod {
			cost = 10000 // 7100
		}

		for(b in Buildings.ROMAN_TOWNS) {
			b.mod {
				when(b.adjustedLevel) {
					1 -> cost = 1500 // 1200
					2 -> cost = 2500 // ~1900
					3 -> cost = 4000 // ~2700
					4 -> cost = 7000 // ~3800
				}
			}
		}

		Buildings.ROMAN_TOWN_TRADE_2.apply {
			effect(BuildingEffectType.GDP_LOCAL_TRADE, 150)
			effect(BuildingEffectType.GDP_MOD_TRADE, 5) // 3
			addEffect(BuildingEffectType.HAPPINESS, EffectScope.PROVINCE, 4)
		}
		Buildings.ROMAN_TOWN_TRADE_3.apply {
			effect(BuildingEffectType.GDP_LOCAL_TRADE, 200)
			effect(BuildingEffectType.GDP_MOD_TRADE, 5) // 6
			addEffect(BuildingEffectType.HAPPINESS, EffectScope.PROVINCE, 6)
		}
		Buildings.ROMAN_TOWN_TRADE_4.apply {
			effect(BuildingEffectType.GDP_LOCAL_TRADE, 250)
			effect(BuildingEffectType.GDP_MOD_TRADE, 5) // 9
			addEffect(BuildingEffectType.HAPPINESS, EffectScope.PROVINCE, 8)
		}
		Buildings.ROMAN_TOWN_FARM_2.apply {
			effect(BuildingEffectType.GDP_AGRICULTURE_FARMING, 150) // 100
			effect(BuildingEffectType.GDP_MOD_AGRICULTURE, 10) // 3 + 7
			effect(BuildingEffectType.GROWTH, 2) // 2
		}
		Buildings.ROMAN_TOWN_FARM_3.apply {
			effect(BuildingEffectType.GDP_AGRICULTURE_FARMING, 200) // 150
			effect(BuildingEffectType.GDP_MOD_AGRICULTURE, 15) // 6 + 14
			effect(BuildingEffectType.GROWTH, 4) // 3 + 1
		}
		Buildings.ROMAN_TOWN_FARM_4.apply {
			effect(BuildingEffectType.GDP_AGRICULTURE_FARMING, 250) // 200
			effect(BuildingEffectType.GDP_MOD_AGRICULTURE, 20) // 9
			effect(BuildingEffectType.GROWTH, 8) // 4 + 4
		}
		Buildings.ROMAN_TOWN_CIVIL_2.apply {
			effect(BuildingEffectType.GDP_SUBSISTENCE, 100) // 100
			effect(BuildingEffectType.GDP_MOD_ALL, 4) // 2
		}
		Buildings.ROMAN_TOWN_CIVIL_3.apply {
			effect(BuildingEffectType.GDP_SUBSISTENCE, 150) // 150
			effect(BuildingEffectType.GDP_MOD_ALL, 6) // 4
		}
		Buildings.ROMAN_TOWN_CIVIL_4.apply {
			effect(BuildingEffectType.GDP_SUBSISTENCE, 200) // 200
			effect(BuildingEffectType.GDP_MOD_ALL, 8) // 6
		}


		Buildings.ROMAN_CITY_CIVIL_2.apply {
			effect(BuildingEffectType.GDP_SUBSISTENCE, 300) // 300
			effect(BuildingEffectType.GDP_MOD_ALL, 8) // 8
			effect(BuildingEffectType.GROWTH, 6) // 5
		}
		Buildings.ROMAN_CITY_CIVIL_3.apply {
			effect(BuildingEffectType.GDP_SUBSISTENCE, 400) // 400
			effect(BuildingEffectType.GDP_MOD_ALL, 10) // 10
			effect(BuildingEffectType.GROWTH, 8) // 7
		}
		Buildings.ROMAN_CITY_CIVIL_4.apply {
			effect(BuildingEffectType.GDP_SUBSISTENCE, 500) // 500
			effect(BuildingEffectType.GDP_MOD_ALL, 12) // 12
			effect(BuildingEffectType.GROWTH, 10) // 9
		}
		Buildings.ROMAN_CITY_GARRISON_2.apply {
			effect(BuildingEffectType.GDP_SUBSISTENCE, 250) // 250
			effect(BuildingEffectType.GDP_MOD_ALL, 5) // 6
			effect(BuildingEffectType.HAPPINESS, 4) // 2
			effect(BuildingEffectType.GROWTH, 3) // 3
		}
		Buildings.ROMAN_CITY_GARRISON_3.apply {
			effect(BuildingEffectType.GDP_SUBSISTENCE, 300) // 300
			effect(BuildingEffectType.GDP_MOD_ALL, 5) // 6
			effect(BuildingEffectType.HAPPINESS, 8) // 4
			effect(BuildingEffectType.GROWTH, 4) // 3
		}
		Buildings.ROMAN_CITY_GARRISON_4.apply {
			effect(BuildingEffectType.GDP_SUBSISTENCE, 350) // 350
			effect(BuildingEffectType.GDP_MOD_ALL, 5) // 6
			effect(BuildingEffectType.HAPPINESS, 12) // 6
			effect(BuildingEffectType.GROWTH, 5) // 3
		}


		Buildings.ROMAN_TEMPLE_MARS_2.apply {
			effect(BuildingEffectType.LATIN_INFLUENCE, 2)
		}
		Buildings.ROMAN_TEMPLE_MARS_3.apply {
			effect(BuildingEffectType.LATIN_INFLUENCE, 3)
		}
		Buildings.ROMAN_TEMPLE_MARS_4.apply {
			effect(BuildingEffectType.LATIN_INFLUENCE, 4)
		}


		Buildings.ROMAN_TEMPLE_VULCAN_2.apply {
			effect(BuildingEffectType.LATIN_INFLUENCE, 2)
		}
		Buildings.ROMAN_TEMPLE_VULCAN_3.apply {
			effect(BuildingEffectType.LATIN_INFLUENCE, 3)
		}
		Buildings.ROMAN_TEMPLE_VULCAN_4.apply {
			effect(BuildingEffectType.LATIN_INFLUENCE, 4)
		}


		Buildings.ROMAN_TEMPLE_MINERVA_2.apply {
			effect(BuildingEffectType.RESEARCH_RATE, 10)
			effect(BuildingEffectType.GDP_MOD_CULTURE, 10)
			effect(BuildingEffectType.LATIN_INFLUENCE, 2)
		}
		Buildings.ROMAN_TEMPLE_MINERVA_3.apply {
			effect(BuildingEffectType.RESEARCH_RATE, 20)
			effect(BuildingEffectType.GDP_MOD_CULTURE, 15)
			effect(BuildingEffectType.LATIN_INFLUENCE, 3)
		}
		Buildings.ROMAN_TEMPLE_MINERVA_4.apply {
			effect(BuildingEffectType.RESEARCH_RATE, 40)
			effect(BuildingEffectType.GDP_MOD_CULTURE, 25)
			effect(BuildingEffectType.LATIN_INFLUENCE, 4)
		}


		Buildings.ROMAN_TEMPLE_JUPITER_2.apply {
			effect(BuildingEffectType.GDP_MOD_ALL, 4) // 4
		}
		Buildings.ROMAN_TEMPLE_JUPITER_3.apply {
			effect(BuildingEffectType.GDP_MOD_ALL, 8) // 8
		}
		Buildings.ROMAN_TEMPLE_JUPITER_4.apply {
			effect(BuildingEffectType.GDP_MOD_ALL, 16) // 16
		}
		Buildings.ROMAN_TEMPLE_JUPITER_5.apply {
			effect(BuildingEffectType.GDP_MOD_ALL, 32) // 20
		}


		Buildings.ROMAN_LIBRARY_2.apply {
			effect(BuildingEffectType.RESEARCH_RATE, 20) // 6
			effect(BuildingEffectType.GDP_CULTURE_LEARNING, 100) // 70
		}
		Buildings.ROMAN_LIBRARY_3.apply {
			effect(BuildingEffectType.RESEARCH_RATE, 40) // 12
			effect(BuildingEffectType.GDP_CULTURE_LEARNING, 150) // 100
		}
		Buildings.ROMAN_LIBRARY_4.apply {
			effect(BuildingEffectType.RESEARCH_RATE, 80) // 24
			effect(BuildingEffectType.GDP_CULTURE_LEARNING, 200) // 150
		}


		Buildings.ROMAN_FARM_2.apply {
			effect(BuildingEffectType.FOOD_PRODUCTION, 8) // 7 + 1
			effect(BuildingEffectType.GDP_AGRICULTURE_FARMING, 60) // 60
		}
		Buildings.ROMAN_FARM_3.apply {
			effect(BuildingEffectType.FOOD_PRODUCTION, 12) // 11 + 1
			effect(BuildingEffectType.GDP_AGRICULTURE_FARMING, 80) // 80
		}
		Buildings.ROMAN_FARM_4.apply {
			effect(BuildingEffectType.FOOD_PRODUCTION, 16) // 15 + 1
			effect(BuildingEffectType.GDP_AGRICULTURE_FARMING, 100) // 100
		}
		Buildings.ROMAN_GRANARY_2.apply {
			effect(BuildingEffectType.FOOD_PRODUCTION, 4) // 4
			effect(BuildingEffectType.GDP_AGRICULTURE_FARMING, 100) // 100
			effect(BuildingEffectType.UNIT_REPLENISHMENT, 10) // 3 + 7
			addEffect(BuildingEffectType.SANITATION, EffectScope.PROVINCE, 2)
		}
		Buildings.ROMAN_GRANARY_3.apply {
			effect(BuildingEffectType.FOOD_PRODUCTION, 5) // 4
			effect(BuildingEffectType.GDP_AGRICULTURE_FARMING, 150) // 150
			effect(BuildingEffectType.UNIT_REPLENISHMENT, 20) // 6 + 14
			addEffect(BuildingEffectType.SANITATION, EffectScope.PROVINCE, 3)
		}
		Buildings.ROMAN_GRANARY_4.apply {
			effect(BuildingEffectType.FOOD_PRODUCTION, 6) // 4
			effect(BuildingEffectType.GDP_AGRICULTURE_FARMING, 200) // 200
			effect(BuildingEffectType.UNIT_REPLENISHMENT, 30) // 9 + 21
			addEffect(BuildingEffectType.SANITATION, EffectScope.PROVINCE, 4)
		}
		Buildings.ROMAN_HERD_2.apply {
			effect(BuildingEffectType.FOOD_PRODUCTION, 5) // 5
			effect(BuildingEffectType.GDP_AGRICULTURE_HERDING, 100) // 100
		}
		Buildings.ROMAN_HERD_3.apply {
			effect(BuildingEffectType.FOOD_PRODUCTION, 7) // 7
			effect(BuildingEffectType.GDP_AGRICULTURE_HERDING, 175) // 175
		}
		Buildings.ROMAN_HERD_4.apply {
			effect(BuildingEffectType.FOOD_PRODUCTION, 9) // 9
			effect(BuildingEffectType.GDP_AGRICULTURE_HERDING, 250) // 250
		}


		Buildings.ROMAN_TRADER_2.apply {
			effect(BuildingEffectType.FOOD_CONSUMPTION, 1)
			effect(BuildingEffectType.GDP_LOCAL_TRADE, 150)
		}
		Buildings.ROMAN_TRADER_3.apply {
			effect(BuildingEffectType.FOOD_CONSUMPTION, 2)
			effect(BuildingEffectType.GDP_LOCAL_TRADE, 250)
		}
		Buildings.ROMAN_TRADER_4.apply {
			effect(BuildingEffectType.FOOD_CONSUMPTION, 4)
			effect(BuildingEffectType.GDP_LOCAL_TRADE, 400)
		}
		Buildings.ROMAN_THEATRE_2.apply {
			effect(BuildingEffectType.HAPPINESS, 4) // 2 + 2
			effect(BuildingEffectType.GDP_CULTURE_ENTERTAINMENT, 100) // 80
		}
		Buildings.ROMAN_THEATRE_3.apply {
			effect(BuildingEffectType.HAPPINESS, 8) // 4 + 4
			effect(BuildingEffectType.GDP_CULTURE_ENTERTAINMENT, 150) // 160
		}
		Buildings.ROMAN_THEATRE_4.apply {
			effect(BuildingEffectType.HAPPINESS, 12) // 6 + 6
			effect(BuildingEffectType.GDP_CULTURE_ENTERTAINMENT, 200) // 240
		}

		Buildings.ROMAN_CIRCUS_2.apply {
			effect(BuildingEffectType.HAPPINESS, 6) // 5
			effect(BuildingEffectType.GDP_CULTURE_ENTERTAINMENT, 100) // 60
		}
		Buildings.ROMAN_CIRCUS_3.apply {
			effect(BuildingEffectType.HAPPINESS, 12) // 12
			effect(BuildingEffectType.GDP_CULTURE_ENTERTAINMENT, 200) // 130
		}
		Buildings.ROMAN_CIRCUS_4.apply {
			effect(BuildingEffectType.HAPPINESS, 24) // 24
			effect(BuildingEffectType.GDP_CULTURE_ENTERTAINMENT, 300) // 200
		}
		Buildings.ROMAN_CIRCUS_MAXIMUS.apply {
			effect(BuildingEffectType.GDP_CULTURE_ENTERTAINMENT, 500) // 160
			effect(BuildingEffectType.HAPPINESS, 50) // 30
		}
		Buildings.ROMAN_COLOSSEUM.apply {
			effect(BuildingEffectType.GDP_CULTURE_ENTERTAINMENT, 1000) // 500
			effect(BuildingEffectType.HAPPINESS, 24) // 12
		}

		Buildings.ROMAN_BUFF_ATTACK_2.apply {
			effect(BuildingEffectType.FOOD_CONSUMPTION, 4)
			effect(BuildingEffectType.ATTACK_BUFF, 10)
		}
		Buildings.ROMAN_BUFF_ATTACK_3.apply {
			effect(BuildingEffectType.FOOD_CONSUMPTION, 8)
			effect(BuildingEffectType.ATTACK_BUFF, 20)
		}
		Buildings.ROMAN_BUFF_DEFENCE_2.apply {
			effect(BuildingEffectType.FOOD_CONSUMPTION, 4)
			effect(BuildingEffectType.DEFENCE_BUFF, 10)
		}
		Buildings.ROMAN_BUFF_DEFENCE_3.apply {
			effect(BuildingEffectType.FOOD_CONSUMPTION, 8)
			effect(BuildingEffectType.DEFENCE_BUFF, 20)
		}
		Buildings.ROMAN_BUFF_HORSE_2.apply {
			effect(BuildingEffectType.FOOD_CONSUMPTION, 4)
			effect(BuildingEffectType.HORSE_BUFF, 10)
		}
		Buildings.ROMAN_BUFF_HORSE_3.apply {
			effect(BuildingEffectType.FOOD_CONSUMPTION, 8)
			effect(BuildingEffectType.HORSE_BUFF, 20)
		}
		Buildings.ROMAN_BUFF_RANGE_2.apply {
			effect(BuildingEffectType.FOOD_CONSUMPTION, 4)
			effect(BuildingEffectType.RANGE_BUFF, 10)
		}
		Buildings.ROMAN_BUFF_RANGE_3.apply {
			effect(BuildingEffectType.FOOD_CONSUMPTION, 8)
			effect(BuildingEffectType.RANGE_BUFF, 20)
		}
	}



	private fun modTechs() {
		for(t in techs.values)
			if(t.name.startsWith("rom_") && !t.name.startsWith("rom_roman_"))
				t.mod { cost = 0 }

		Techs.TRAINING_REFORMS.effect(TechEffectType.ROME_UNIT_UPKEEP_MOD, -5)
		Techs.REMUNERATION_REFORMS.effect(TechEffectType.ROME_UNIT_UPKEEP_MOD, -5)
		Techs.COHORT_ORGANISATION.effect(TechEffectType.ROME_UNIT_UPKEEP_MOD, -5)
		Techs.PROFESSIONAL_SOLDIERY.effect(TechEffectType.ROME_UNIT_UPKEEP_MOD, -10)

		Techs.IRON_TOOLS.effect(TechEffectType.ROME_AGRI_GDP_MOD, 5)
		Techs.IRON_TOOLS.effect(TechEffectType.ROME_AGRI_BUILDING_COST_MOD, -5)
		Techs.DOUBLE_CROPPING.effect(TechEffectType.ROME_AGRI_GDP_MOD, 5)
		Techs.DOUBLE_CROPPING.effect(TechEffectType.ROME_AGRI_BUILDING_COST_MOD, -5)
		Techs.IMPROVED_IRRIGATION.effect(TechEffectType.ROME_AGRI_GDP_MOD, 10)
		Techs.IMPROVED_IRRIGATION.effect(TechEffectType.ROME_AGRI_BUILDING_COST_MOD, -5)
		Techs.LAND_RECLAMATION.effect(TechEffectType.ROME_AGRI_GDP_MOD, 10)
		Techs.LAND_RECLAMATION.effect(TechEffectType.ROME_AGRI_BUILDING_COST_MOD, -5)
		Techs.SEED_SELECTION.effect(TechEffectType.ROME_AGRI_GDP_MOD, 20)
		Techs.SEED_SELECTION.effect(TechEffectType.ROME_AGRI_BUILDING_COST_MOD, -5)

		Techs.PHILOSOPHERS.effect(TechEffectType.ROME_CULTURE_GDP_MOD, 5)
		Techs.ASTRONOMY.effect(TechEffectType.ROME_CULTURE_GDP_MOD, 10)
		Techs.NATURAL_PHILOSOPHY.effect(TechEffectType.ROME_CULTURE_GDP_MOD, 15)
		Techs.CULTISM.effect(TechEffectType.ROME_CULTURE_GDP_MOD, 20)

		Techs.COMMON_WEIGHTS_AND_MEASURES.effect(TechEffectType.ROME_TRADE_GDP_MOD, 5)
		Techs.COMMON_WEIGHTS_AND_MEASURES.effect(TechEffectType.ROME_TARIFF_MOD, 5)
		Techs.COMMON_CURRENCY.effect(TechEffectType.ROME_TRADE_GDP_MOD, 5)
		Techs.COMMON_CURRENCY.effect(TechEffectType.ROME_TARIFF_MOD, 5)
		Techs.DENOMINATIONAL_SYSTEM.effect(TechEffectType.ROME_TRADE_GDP_MOD, 10)
		Techs.DENOMINATIONAL_SYSTEM.effect(TechEffectType.ROME_TARIFF_MOD, 10)
		Techs.PRODUCTION_LINES.effect(TechEffectType.ROME_TRADE_GDP_MOD, 20)
		Techs.PRODUCTION_LINES.effect(TechEffectType.ROME_TARIFF_MOD, 20)

		Techs.TAX_LABOUR.effect(TechEffectType.ROME_GROWTH_PROVINCE, 4)
		Techs.WATER_SLUICING.effect(TechEffectType.ROME_GROWTH_PROVINCE, 4)
		Techs.FIRED_BRICK.effect(TechEffectType.ROME_INDUSTRY_GDP_MOD, 15)
		Techs.MOULDED_ARCHITECTURE.effect(TechEffectType.ROME_INDUSTRY_GDP_MOD, 25)
	}



	private fun modSkills() {
		for(skill in Skills.ALL) {
			skill.unlockRank = 1
			skill.level1.mod { unlockRank = 1 }
			skill.level2.mod { unlockRank = 2 }
			skill.level3.mod { unlockRank = 3 }
		}

		for(tier in experienceTiers) tier.mod {
			if(agent.isNotEmpty()) {
				experience = when(rank) {
					0 -> 50
					1 -> 100
					2 -> 150
					3 -> 200
					4 -> 250
					5 -> 300
					6 -> 400
					7 -> 500
					8 -> 600
					else -> error("Invalid rank")
				}

				skillPoints = when(rank) {
					0 -> 3
					1 -> 3
					2 -> 3
					3 -> 3
					4 -> 3
					5 -> 3
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
					3 -> 3
					4 -> 3
					5 -> 3
					6 -> 4
					7 -> 4
					8 -> 4
					else -> error("Invalid rank")
				}
			}
		}
	}


}*/