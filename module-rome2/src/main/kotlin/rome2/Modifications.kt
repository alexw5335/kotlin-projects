package rome2



/*
private const val GROWTH = "rom_building_growth_all"
private const val SANITATION = "rom_building_public_order_happiness_sanitation"
private const val CULTURE_CONVERSION = "rom_building_culture_conversion_latin"
private const val FOOD_CONSUMPTION = "rom_building_food_consumption"
private const val GDP_ENTERTAINMENT = "rom_building_gdp_culture_entertainment"
*/




fun modifyWeapons() {
	weapons["rome_gladius"]!!.                  damage = 30
	weapons["rome_gladius_marian"]!!.           damage = 35
	weapons["rome_gladius_imperial"]!!.         damage = 40
	weapons["rome_gladius_elite"]!!.let { it.   damage = 40; it.apDamage = 10 }
	weapons["rome_spear_cav_elite"]!!.let { it. damage = 30; it.apDamage = 10 }
}



fun modifyShields() {
	shields["scutum"]!!.let          { it.defence = 30; it.armour = 35; it.block = 50 }
	shields["scutum_marian"]!!.let   { it.defence = 35; it.armour = 40; it.block = 55 }
	shields["scutum_imperial"]!!.let { it.defence = 40; it.armour = 45; it.block = 60 }
}



fun modifyArmours() {
	armours["cloth"]!!.armour          = 15
	armours["mail"]!!.armour           = 40
	armours["mail_improved"]!!.armour  = 50
	armours["segmentata"]!!.armour     = 60
	armours["segmentata_arm"]!!.armour = 70
}



fun modifyUnits() {

	RORARII.apply {
		attack = 15
		defence = 30
		morale = 40
		bonusHp = 10
		charge = 10
	}

	VIGILES.apply {
		attack = 25
		defence = 40
		morale = 50
		bonusHp = 20
		charge = 15
		armour = armours["chest"]!!
		shield = shields["scutum_marian"]!!
		cost = 400
		upkeep = 100
	}


	HASTATI.apply {
		attack = 35
		defence = 20
		morale = 45
		bonusHp = 10
		charge = 15
		cost = 400
		upkeep = 100
	}

	PRINCIPES.apply {
		attack = 50
		defence = 25
		morale = 55
		bonusHp = 15
		charge = 20
		cost = 700
		upkeep = 120
	}

	LEGIONARIES.apply {
		attack = 55
		defence = 30
		morale = 65
		bonusHp = 20
		charge = 20
		cost = 850
		upkeep = 150
	}

	LEGIONARY_COHORT.apply {
		attack = 60
		defence = 35
		morale = 75
		bonusHp = 25
		charge = 25
		cost = 1000
		upkeep = 200
	}

	ARMOURED_LEGIONARIES.apply {
		attack = 60
		defence = 40
		morale = 80
		bonusHp = 30
		charge = 25
		cost = 1200
		upkeep = 220
	}

	TRIARII.apply {
		attack = 35
		defence = 40
		morale = 65
		bonusHp = 20
		charge = 20
		cost = 800
		upkeep = 150
	}

	VETERAN_LEGIONARIES.apply {
		attack = 55
		defence = 45
		morale = 70
		bonusHp = 25
		charge = 20
		cost = 1000
		upkeep = 200
	}

	EVOCATI_COHORT.apply {
		attack = 60
		defence = 50
		morale = 75
		bonusHp = 30
		charge = 25
		cost = 1500
		upkeep = 250
	}

	FIRST_COHORT.apply {
		attack = 65
		defence = 35
		morale = 70
		bonusHp = 20
		charge = 25
		cost = 1200
		upkeep = 220
	}

	EAGLE_COHORT.apply {
		attack = 75
		defence = 40
		morale = 75
		bonusHp = 25
		charge = 30
		cost = 1750
		upkeep = 275
	}

	PRAETORIANS.apply {
		attack = 70
		defence = 50
		morale = 80
		bonusHp = 25
		charge = 30
		cost = 2000
		upkeep = 400
	}

	PRAETORIAN_GUARD.apply {
		attack = 85
		defence = 60
		morale = 100
		bonusHp = 30
		charge = 35
		shield = shields["scutum_imperial"]!!
		cost = 4000
		upkeep = 500
	}

	EQUITES.apply {
		attack = 40
		defence = 15
		morale = 45
		bonusHp = 10
		charge = 30
		cost = 500
		upkeep = 100
	}

	LEGIONARY_CAVALRY.apply {
		attack = 50
		defence = 25
		morale = 55
		bonusHp = 15
		charge = 35
		cost = 1000
		upkeep = 150
	}

	PRAETORIAN_CAVALRY.apply {
		attack = 64
		defence = 40
		morale = 70
		bonusHp = 25
		charge = 45
		cost = 3000
		upkeep = 400
	}
}