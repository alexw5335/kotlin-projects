package rome2.previous

/*import rome2.TrainingLevel.*

object Mod5 {

	
	fun mod() {
		modOthers()
		modUnits()
		modGarrisons()
		modTechs()
		modBuildings()
		modSkills()
	}



	private fun modOthers() {
		campaignVariables["trade_route_value_combined_gdp_proportion"]!!.value = 2.5F

		for(incident in incidents.values)
			incident.mod { enabled = false; prioritised = false }

		for(dilemma in dilemmas.values)
			if(!dilemma.name.startsWith("female_leader"))
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
		"trespass_foreign_land".disable()
		"faction_destroyed".disable()
		"faction_destroyed_minor".disable()
		"campmap_your_general_returns_to_capital".disable()
		"campmap_you_secession_or_civil_war_protection_expired".disable()
		"campmap_you_secession_or_civil_war_protection_will_expire".disable()

		for(b in budgetAllocations.values) b.mod {
			b.agentFundingCap = 0
			b.agentFundingAllocationPercentage = 0
			b.agentTurnOfInactivityUntilCap = 0
			b.agentPercentageOfPoolToSaveOnFail = 0
			b.armyFundingCap = 400000
			b.constructionFundingCap = 30000
			b.diplomacyFundingCap = 10000
		}

		effectBundle("govt_type_republic").effect("rom_faction_political_party_loyalty", 15)
		effectBundle("govt_type_empire").effect("rom_faction_political_party_loyalty", 30)

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
				d.bestFriendsValue -= 50F
				d.bitterEnemiesValue -= 100F
				d.friendlyValue -= 75F
				d.neutralValue -= 100F
				d.unfriendlyValue -= 100F
				d.veryFriendlyValue -= -50F
				d.veryUnfriendlyValue -= 100F
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
					d.peacePriority += 60F
					d.tensionPriority += 40F
					d.warPriority += 30F
					d.totalWarPriority += 30F
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
			peacePriority     = 15
			tensionPriority   = 15
			warPriority       = 15
			totalWarPriority  = 15
		}

		for(d in difficulties.values)
			d.addEffect("rom_payload_food", EffectScope.ALL_PROVINCES, 50F)
	}



	private fun modGarrisons() {
		val levyMelee     = newGarrisonGroup("roman_levy", 100, Units.RORARII, Units.VIGILES)
		val basicMelee    = newGarrisonGroup("ROMAN_MODDED_basic_melee", 300, Units.HASTATI, Units.LEGIONARIES, Units.LEGIONARY_COHORT)
		val mediumMelee   = newGarrisonGroup("ROMAN_MODDED_strong_melee", 200, Units.PRINCIPES, Units.FIRST_COHORT, Units.EAGLE_COHORT)
		val eliteMelee    = newGarrisonGroup("ROMAN_MODDED_elite_melee", 100, Units.TRIARII, Units.PRAETORIANS, Units.PRAETORIAN_GUARD)

		val basicRanged   = newGarrisonGroup("ROMAN_MODDED_basic_ranged", 300, Units.LEVES)
		val mediumRanged  = newGarrisonGroup("ROMAN_MODDED_medium_ranged", 300, Units.VELITES)
		val strongRanged  = newGarrisonGroup("ROMAN_MODDED_strong_ranged", 100, Units.AUX_PELTASTS)
		val eliteRanged   = newGarrisonGroup("ROMAN_MODDED_elite_ranged", 100, Units.AUX_CRETAN_ARCHERS)

		val mediumCavalry = newGarrisonGroup("ROMAN_MODDED_medium_cavalry", 200, Units.EQUITES, Units.LEGIONARY_CAVALRY)
		val eliteCavalry  = newGarrisonGroup("ROMAN_MODDED_elite_cavalry", 100, Units.PRAETORIAN_CAVALRY)

		val mediumSiege   = newGarrisonGroup("ROMAN_MODDED_medium_siege", 200, Units.POLYBOLOS)
		val eliteSiege    = newGarrisonGroup("ROMAN_MODDED_elite_siege", 100, Units.SCORPION)

		val civil1 = arrayOf(
			levyMelee to 6,
			basicRanged to 2
		)

		val civil2 = arrayOf(
			levyMelee to 8,
			basicMelee to 2,
			basicRanged to 2,
		)

		val civil3 = arrayOf(
			levyMelee to 8,
			basicMelee to 4,
			mediumRanged to 3,
			mediumCavalry to 1
		)

		val civil4 = arrayOf(
			levyMelee to 4,
			basicMelee to 4,
			mediumMelee to 2,
			mediumRanged to 4,
			mediumCavalry to 2,
			mediumSiege to 2
		)

		val garrison2 = arrayOf(
			basicMelee to 6,
			mediumMelee to 2,
			mediumRanged to 2,
			mediumCavalry to 2
		)

		val garrison3 = arrayOf(
			basicMelee to 4,
			mediumMelee to 4,
			mediumRanged to 4,
			mediumCavalry to 2,
			mediumSiege to 2
		)

		val garrison4 = arrayOf(
			basicMelee to 4,
			mediumMelee to 4,
			eliteMelee to 2,
			strongRanged to 2,
			eliteRanged to 2,
			eliteCavalry to 2,
			eliteSiege to 2
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
			basicMelee to 2,
			mediumMelee to 2
		)
		Buildings.ROMAN_BARRACKS_MAIN_3.setGarrison(
			mediumMelee to 4,
			eliteMelee to 1
		)
		Buildings.ROMAN_BARRACKS_MAIN_4.setGarrison(
			mediumMelee to 3,
			eliteMelee to 3
		)

		Buildings.ROMAN_BARRACKS_AUX_2.setGarrison(
			basicMelee to 2,
			mediumRanged to 1,
			mediumCavalry to 1
		)
		Buildings.ROMAN_BARRACKS_AUX_3.setGarrison(
			mediumMelee to 2,
			strongRanged to 1,
			mediumCavalry to 1,
			mediumSiege to 1
		)
		Buildings.ROMAN_BARRACKS_AUX_4.setGarrison(
			mediumMelee to 1,
			eliteMelee to 1,
			eliteRanged to 1,
			eliteCavalry to 1,
			eliteSiege to 1
		)

		for(temple in Buildings.ROMAN_TEMPLES) {
			when(temple.adjustedLevel) {
				2 -> temple.setGarrison(basicMelee to 1)
				3 -> temple.setGarrison(mediumMelee to 1)
				4 -> temple.setGarrison(eliteMelee to 1)
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
		Weapons.GLADIUS.mod           { damage = 30 }                // 30  5
		Weapons.GLADIUS_MARIAN.mod    { damage = 30 }                // 30  5
		Weapons.GLADIUS_IMPERIAL.mod  { damage = 35 }                // 30  5
		Weapons.GLADIUS_ELITE.mod     { damage = 40; apDamage = 10 } // 34  5
		Weapons.SPEAR_CAV_ELITE.mod   { damage = 30; apDamage = 10 } // 24  5

		Shields.SCUTUM.mod           { defence = 30; armour = 35; blockChance = 55 } // 30  35  50
		Shields.SCUTUM_MARIAN.mod    { defence = 35; armour = 40; blockChance = 65 } // 25  40  50
		Shields.SCUTUM_IMPERIAL.mod  { defence = 40; armour = 45; blockChance = 75 } // 25  40  50

		Armours.MAIL.mod            { armour = 40 } // 40
		Armours.MAIL_IMPROVED.mod   { armour = 50 } // 45
		Armours.SEGMENTATA.mod      { armour = 60 } // 50
		Armours.SEGMENTATA_ARM.mod  { armour = 70 } // 55

		Units.POLYBOLOS.mod {
			accuracy = 20   // 5
			reload   = 50   // 4
			ammo     = 100  // 60
			cost     = 1500 // 620
			upkeep   = 150  // 120
			level    = WELL_TRAINED
			spacing  = "missile_cav" // artillery
			numGuns  = 8 // 4

			newProjectile {
				damage      = 40 // 35
				apDamage    = 50 // 50
				range       = 300 // 260
				reloadTime  = 5 // 5
				penetration = "low" // low
			}
		}

		Units.SCORPION.mod {
			accuracy = 40   // 5
			ammo     = 100  // 40
			reload   = 50   // 4
			cost     = 2500
			upkeep   = 250
			level    = ELITE
			spacing  = "missile_cav" // artillery
			numGuns  = 8 // 4

			newProjectile {
				damage       = 50    // 50
				apDamage     = 60    // 70
				range        = 400   // 350
				reloadTime   = 5     // 10
				penetration  = "low" // low
			}
		}

		Units.LEVES.mod {
			reload   = 10  // 8
			accuracy = 10  // 5
			ammo     = 15  // 7
		}

		Units.VELITES.mod {
			reload   = 20  // 13
			accuracy = 20  // 5
			ammo     = 20  // 7
			cost     = 400 // 340
			level    = TRAINED
		}

		Units.AUX_PELTASTS.mod {
			reload   = 30   // 28
			accuracy = 30   // 5
			ammo     = 25   // 7
			cost     = 1500 // 420 + 580
			upkeep   = 150  // 90 + 10
			level    = ELITE // poorly_trained

			newProjectile {
				damage   = 20 // 20
				apDamage = 15 // 12
				range    = 100 // 80 + 20
			}
		}

		Units.AUX_CRETAN_ARCHERS.mod {
			reload   = 30    // 28
			ammo     = 30    // 15
			accuracy = 30    // 5
			cost     = 1500  // 600
			upkeep   = 150   // 130
			level    = ELITE // poorly_trained

			newProjectile {
				damage   = 35 // 36
				apDamage = 5 // 4
				range    = 200 // 150
			}
		}

		Units.RORARII.mod {
			attack  = 15  // 13
			defence = 30  // 24
			morale  = 35  // 30
			bonusHp = 10  // 5
			charge  = 10  // 11
			cost    = 200 // 200
			upkeep  = 40  // 60
			armour  = armour("mail") // cloth
			level   = POORLY_TRAINED // poorly_trained
		}

		Units.VIGILES.mod {
			attack  = 25  // 13
			defence = 40  // 24
			morale  = 45  // 30
			bonusHp = 15  // 5
			charge  = 15  // 11
			cost    = 200 // 200
			upkeep  = 40  // 60
			armour  = armour("mail_improved") // cloth
			shield  = shield("scutum_marian") // scutum
			level   = TRAINED // poorly_trained
		}

		Units.HASTATI.mod {
			attack  = 35  // 35
			defence = 20  // 18
			morale  = 45  // 45
			bonusHp = 10  // 10
			charge  = 15  // 12
			cost    = 350 // 350
			upkeep  = 70  // 90
			level   = TRAINED // trained
		}

		Units.PRINCIPES.mod {
			attack  = 50  // 47
			defence = 25  // 23
			morale  = 55  // 55
			bonusHp = 15  // 15
			charge  = 15  // 14
			cost    = 700 // 680
			upkeep  = 100 // 120
			level   = WELL_TRAINED // trained
		}

		Units.TRIARII.mod {
			attack  = 40   // 31
			defence = 40   // 34
			morale  = 65   // 65
			bonusHp = 25   // 20
			charge  = 25   // 24
			cost    = 1500 // 800
			upkeep  = 130  // 140
			level   = WELL_TRAINED // elite
		}

		Units.LEGIONARIES.mod {
			attack  = 45   // 47
			defence = 25   // 23
			morale  = 55   // 55
			bonusHp = 15   // 15
			charge  = 15   // 15
			cost    = 1000 // 660
			upkeep  = 150  // 140
			level   = TRAINED // trained
		}

		Units.FIRST_COHORT.mod {
			attack  = 55   // 47
			defence = 35   // 33
			morale  = 65   // 65
			bonusHp = 20   // 20
			charge  = 20   // 12
			cost    = 1500 // 910
			upkeep  = 200  // 180
			level   = WELL_TRAINED // trained
		}

		Units.PRAETORIANS.mod {
			attack  = 65   // 65
			defence = 40   // 30
			morale  = 75   // 70
			bonusHp = 25   // 20
			charge  = 25   // 19
			cost    = 4000 // 1280
			upkeep  = 400  // 200
			level   = ELITE
		}

		Units.LEGIONARY_COHORT.mod {
			attack  = 50   // 47
			defence = 25   // 23
			morale  = 65   // 55
			bonusHp = 20   // 15
			charge  = 20   // 14
			cost    = 1500 // 700
			upkeep  = 150  // 140
			level   = TRAINED
		}

		Units.EAGLE_COHORT.mod {
			attack  = 60   // 47
			defence = 35   // 35
			morale  = 75   // 65
			bonusHp = 25   // 20
			charge  = 25   // 12
			cost    = 2000 // 930
			upkeep  = 200  // 180
			level   = WELL_TRAINED
		}

		Units.PRAETORIAN_GUARD.mod {
			attack  = 75   // 65
			defence = 45   // 30
			morale  = 85   // 70
			bonusHp = 30   // 20
			charge  = 30   // 19
			shield  = shield("scutum_imperial")
			cost    = 5000 // 1280
			upkeep  = 500  // 200
			level   = ELITE
		}

		Units.EQUITES.mod {
			attack  = 35  // 33
			defence = 15  // 12
			morale  = 40  // 40
			bonusHp = 10  // 10
			charge  = 30  // 29
			cost    = 500 // 500
			upkeep  = 100 // 100
			level   = TRAINED
		}

		Units.LEGIONARY_CAVALRY.mod {
			attack  = 40   // 33
			defence = 20   // 10
			morale  = 50   // 45
			bonusHp = 25   // 15
			charge  = 35   // 29
			cost    = 700  // 620
			upkeep  = 150  // 120
			level   = WELL_TRAINED
		}

		Units.PRAETORIAN_CAVALRY.mod {
			attack  = 50   // 49
			defence = 30   // 22
			morale  = 70   // 70
			bonusHp = 30   // 20
			charge  = 45   // 42
			cost    = 2500 // 1250
			upkeep  = 250  // 200
			level   = ELITE
		}

		UnitExclusive(Units.LEVES.name, "rom_lusitani", true).addMod()
		UnitExclusive(Units.VELITES.name, "rom_rome", false).addMod()
		UnitExclusive(Units.HASTATI.name, "rom_knossos", false).addMod()

		for(u in landUnits.values)
			if(u.category == "artillery" && !u.name.startsWith("Rom_") && !u.name.startsWith("3c_"))
				u.mod { cap = 1 }

		for(c in commanderUnits) {
			if(c.unit == "Rom_Vet_Legionaries") {
				c.addMod()
				c.unit = Units.FIRST_COHORT.name
			} else if(c.unit == "Rom_Evocati_Cohort") {
				c.addMod()
				c.unit = Units.EAGLE_COHORT.name
			}
		}

		for(unit in landUnits.values) {
			if(unit.category != "artillery" || unit.name.startsWith("Rom_")) continue

			for(b in Buildings.ALL) {
				b.units.firstOrNull { it.unit == unit.name }?.mod { this.unit = "" }
			}
		}

		for(unit in mainUnitsData.values) {
			val naval = if(unit.isNaval) navalUnit(unit.navalUnit) else continue
			if(naval.type != "shp_art") continue

			for(b in Buildings.ALL) {
				b.units.firstOrNull { it.unit == unit.name }?.mod { this.unit = "" }
			}
		}

		Techs.COHORT_ORGANISATION.unitUpgrade(Units.TRIARII, Units.PRAETORIANS)
		Techs.PROFESSIONAL_SOLDIERY.unitUpgrade(Units.TRIARII, Units.PRAETORIAN_GUARD)
		Techs.COHORT_ORGANISATION.unitUpgrade(Units.PRINCIPES, Units.FIRST_COHORT)
		Techs.PROFESSIONAL_SOLDIERY.unitUpgrade(Units.PRINCIPES, Units.EAGLE_COHORT)

		Techs.COHORT_ORGANISATION.modifyUnitUpgradeCosts()
		Techs.PROFESSIONAL_SOLDIERY.modifyUnitUpgradeCosts()

		Buildings.ROMAN_SIEGE_2.removeUnit(Units.SCORPION)
		Buildings.ROMAN_SIEGE_3.unit(Units.SCORPION)
		Buildings.ROMAN_BARRACKS_MAIN_4.removeUnit(Units.ARMOURED_LEGIONARIES)
	}



	private fun modBuildings() {
		for(b in Buildings.ALL) b.mod {
			b.turns = 1
			if(!b.name.startsWith("rome_"))
				b.cost = 0
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
			effect(BuildingEffectType.GDP_AGRICULTURE_FARMING, 100) // 100
			effect(BuildingEffectType.GDP_MOD_AGRICULTURE, 10) // 3 + 7
			effect(BuildingEffectType.GROWTH, 2) // 2
		}
		Buildings.ROMAN_TOWN_FARM_3.apply {
			effect(BuildingEffectType.GDP_AGRICULTURE_FARMING, 150) // 150
			effect(BuildingEffectType.GDP_MOD_AGRICULTURE, 15) // 6 + 14
			effect(BuildingEffectType.GROWTH, 4) // 3 + 1
		}
		Buildings.ROMAN_TOWN_FARM_4.apply {
			effect(BuildingEffectType.GDP_AGRICULTURE_FARMING, 200) // 200
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
			effect(BuildingEffectType.GDP_MOD_ALL, 12) // 10
			effect(BuildingEffectType.GROWTH, 8) // 7
		}
		Buildings.ROMAN_CITY_CIVIL_4.apply {
			effect(BuildingEffectType.GDP_SUBSISTENCE, 500) // 500
			effect(BuildingEffectType.GDP_MOD_ALL, 16) // 12
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
			effect(BuildingEffectType.RESEARCH_RATE, 4)
			effect(BuildingEffectType.GDP_MOD_CULTURE, 8)
			effect(BuildingEffectType.LATIN_INFLUENCE, 2)
		}
		Buildings.ROMAN_TEMPLE_MINERVA_3.apply {
			effect(BuildingEffectType.RESEARCH_RATE, 8)
			effect(BuildingEffectType.GDP_MOD_CULTURE, 16)
			effect(BuildingEffectType.LATIN_INFLUENCE, 3)
		}
		Buildings.ROMAN_TEMPLE_MINERVA_4.apply {
			effect(BuildingEffectType.RESEARCH_RATE, 24)
			effect(BuildingEffectType.GDP_MOD_CULTURE, 32)
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
			effect(BuildingEffectType.RESEARCH_RATE, 8) // 6
			effect(BuildingEffectType.GDP_CULTURE_LEARNING, 100) // 70
		}
		Buildings.ROMAN_LIBRARY_3.apply {
			effect(BuildingEffectType.RESEARCH_RATE, 16) // 12
			effect(BuildingEffectType.GDP_CULTURE_LEARNING, 150) // 100
		}
		Buildings.ROMAN_LIBRARY_4.apply {
			effect(BuildingEffectType.RESEARCH_RATE, 32) // 24
			effect(BuildingEffectType.GDP_CULTURE_LEARNING, 200) // 150
		}


		Buildings.ROMAN_FARM_2.apply {
			effect(BuildingEffectType.FOOD_PRODUCTION, 8) // 7 + 1
			effect(BuildingEffectType.GDP_AGRICULTURE_FARMING, 60) // 60
			effect(BuildingEffectType.SQUALOR, 1) // 1
		}
		Buildings.ROMAN_FARM_3.apply {
			effect(BuildingEffectType.FOOD_PRODUCTION, 12) // 11 + 1
			effect(BuildingEffectType.GDP_AGRICULTURE_FARMING, 80) // 80
			effect(BuildingEffectType.SQUALOR, 4) // 3
		}
		Buildings.ROMAN_FARM_4.apply {
			effect(BuildingEffectType.FOOD_PRODUCTION, 16) // 15 + 1
			effect(BuildingEffectType.GDP_AGRICULTURE_FARMING, 100) // 100
			effect(BuildingEffectType.SQUALOR, 8) // 6
		}
		Buildings.ROMAN_GRANARY_2.apply {
			effect(BuildingEffectType.FOOD_PRODUCTION, 4) // 4
			effect(BuildingEffectType.GDP_AGRICULTURE_FARMING, 100) // 100
			effect(BuildingEffectType.UNIT_REPLENISHMENT, 10) // 3 + 7
			addEffect(BuildingEffectType.SANITATION, EffectScope.PROVINCE, 2)
		}
		Buildings.ROMAN_GRANARY_3.apply {
			effect(BuildingEffectType.FOOD_PRODUCTION, 4) // 4
			effect(BuildingEffectType.GDP_AGRICULTURE_FARMING, 150) // 150
			effect(BuildingEffectType.UNIT_REPLENISHMENT, 20) // 6 + 14
			addEffect(BuildingEffectType.SANITATION, EffectScope.PROVINCE, 3)
		}
		Buildings.ROMAN_GRANARY_4.apply {
			effect(BuildingEffectType.FOOD_PRODUCTION, 5) // 4
			effect(BuildingEffectType.GDP_AGRICULTURE_FARMING, 200) // 200
			effect(BuildingEffectType.UNIT_REPLENISHMENT, 30) // 9 + 21
			addEffect(BuildingEffectType.SANITATION, EffectScope.PROVINCE, 4)
		}
		Buildings.ROMAN_HERD_2.apply {
			effect(BuildingEffectType.FOOD_PRODUCTION, 4) // 5
			effect(BuildingEffectType.GDP_AGRICULTURE_HERDING, 100) // 100
			effect(BuildingEffectType.SQUALOR, 1) // 1
		}
		Buildings.ROMAN_HERD_3.apply {
			effect(BuildingEffectType.FOOD_PRODUCTION, 6) // 7
			effect(BuildingEffectType.GDP_AGRICULTURE_HERDING, 175) // 175
			effect(BuildingEffectType.SQUALOR, 4) // 3
		}
		Buildings.ROMAN_HERD_4.apply {
			effect(BuildingEffectType.FOOD_PRODUCTION, 8) // 9
			effect(BuildingEffectType.GDP_AGRICULTURE_HERDING, 250) // 250
			effect(BuildingEffectType.SQUALOR, 8) // 6
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
			effect(BuildingEffectType.GDP_CULTURE_ENTERTAINMENT, 80) // 80
		}
		Buildings.ROMAN_THEATRE_3.apply {
			effect(BuildingEffectType.HAPPINESS, 8) // 4 + 4
			effect(BuildingEffectType.GDP_CULTURE_ENTERTAINMENT, 160) // 160
		}
		Buildings.ROMAN_THEATRE_4.apply {
			effect(BuildingEffectType.HAPPINESS, 12) // 6 + 6
			effect(BuildingEffectType.GDP_CULTURE_ENTERTAINMENT, 240) // 240
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

		Techs.LEGAL_DOCUMENTATION.effect(TechEffectType.ROME_TAX_MOD, 5) // 2
		Techs.LABOUR_ORGANISATION.effect(TechEffectType.ROME_TAX_MOD, 5) // 3
		Techs.LEGAL_INSTITUTIONS.effect(TechEffectType.ROME_TAX_MOD, 5) // 4
		Techs.CONSENSUAL_CONTRACTS.effect(TechEffectType.ROME_TAX_MOD, 5) // 6

		Techs.PHILOSOPHERS.effect(TechEffectType.ROME_CULTURE_GDP_MOD, 10)
		Techs.ASTRONOMY.effect(TechEffectType.ROME_CULTURE_GDP_MOD, 10)
		Techs.NATURAL_PHILOSOPHY.effect(TechEffectType.ROME_CULTURE_GDP_MOD, 10)
		Techs.CULTISM.effect(TechEffectType.ROME_CULTURE_GDP_MOD, 20)

		Techs.COMMON_WEIGHTS_AND_MEASURES.effect(TechEffectType.ROME_TRADE_GDP_MOD, 5)
		Techs.COMMON_WEIGHTS_AND_MEASURES.effect(TechEffectType.ROME_TARIFF_MOD, 5)
		Techs.COMMON_CURRENCY.effect(TechEffectType.ROME_TRADE_GDP_MOD, 5)
		Techs.COMMON_CURRENCY.effect(TechEffectType.ROME_TARIFF_MOD, 5)
		Techs.DENOMINATIONAL_SYSTEM.effect(TechEffectType.ROME_TRADE_GDP_MOD, 10)
		Techs.DENOMINATIONAL_SYSTEM.effect(TechEffectType.ROME_TARIFF_MOD, 10)
		Techs.PRODUCTION_LINES.effect(TechEffectType.ROME_TRADE_GDP_MOD, 15)
		Techs.PRODUCTION_LINES.effect(TechEffectType.ROME_TARIFF_MOD, 15)

		Techs.TAX_LABOUR.effect(TechEffectType.ROME_GROWTH_PROVINCE, 4) // 1
		Techs.WATER_SLUICING.effect(TechEffectType.ROME_GROWTH_PROVINCE, 4) // 3
		Techs.WATER_SLUICING.effect(TechEffectType.ROME_INDUSTRY_GDP_MOD, 10) // 3
		Techs.FIRED_BRICK.effect(TechEffectType.ROME_INDUSTRY_GDP_MOD, 15)
		Techs.MOULDED_ARCHITECTURE.effect(TechEffectType.ROME_INDUSTRY_GDP_MOD, 25)
	}



	private fun modSkills() {
		fun agentXp(i: Int) = i * 10 + 4 * i * (i + 1) / 2
		fun armyXp(i: Int) = i * 15 + 8 * i * (i + 1) / 2

		for(tier in experienceTiers) tier.mod {
			skillPoints = 2
			experience = if(tier.agent.isNotEmpty()) agentXp(rank + 1) else armyXp(rank + 1)
		}

		for(i in 9.. 17) {
			val agentXp = agentXp(i + 1)
			val armyXp = armyXp(i + 1)
			ExperienceTier("champion", agentXp, 2, i, false, false).addMod()
			ExperienceTier("dignitary", agentXp, 2, i, false, false).addMod()
			ExperienceTier("general", agentXp, 2, i, false, false).addMod()
			ExperienceTier("spy", agentXp, 2, i, false, false).addMod()
			ExperienceTier("", armyXp, 2, i, true, false).addMod()
			ExperienceTier("", armyXp, 2, i, false, true).addMod()
		}
	}


}*/