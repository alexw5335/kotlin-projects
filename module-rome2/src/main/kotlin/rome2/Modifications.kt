package rome2



class UnitMod(
	val unit     : Unit,
	val attack   : Int? = null,
	val defence  : Int? = null,
	val morale   : Int? = null,
	val bonusHp  : Int? = null,
	val charge   : Int? = null,
	val armour   : String? = null,
	val weapon   : String? = null,
	val shield   : String? = null,
	val cost     : Int? = null,
	val upkeep   : Int? = null
)



class ArmourMod(
	val name   : String,
	val armour : Int? = null
)



class ShieldMod(
	val name               : String,
	val defence            : Int? = null,
	val armour             : Int? = null,
	val missileBlockChance : Int? = null
)



class WeaponMod(
	val name          : String,
	val damage        : Int? = null,
	val apDamage      : Int? = null,
	val cavalryBonus  : Int? = null,
	val largeBonus    : Int? = null,
	val infantryBonus : Int? = null
)



class BuildingMod(
	val name: String,
	val cost: Int? = null,
	val turns: Int? = null,
	val effects: List<Pair<String, Int>>? = emptyList()
) {
	constructor(name: String, effects: List<Pair<String, Int>>?) : this(name, null, null, effects)
}



private const val GROWTH = "rom_building_growth_all"
private const val SANITATION = "rom_building_public_order_happiness_sanitation"
private const val CULTURE_CONVERSION = "rom_building_culture_conversion_latin"
private const val FOOD_CONSUMPTION = "rom_building_food_consumption"
private const val GDP_ENTERTAINMENT = "rom_building_gdp_culture_entertainment"



val BUILDING_MODS = listOf(
	BuildingMod("water_1", listOf(
		GROWTH to 4,
		SANITATION to 4
	)),

	BuildingMod("water_baths_2", listOf(
		SANITATION to 4,
		GROWTH to 4,
		GDP_ENTERTAINMENT to 100
	)),

	BuildingMod("water_baths_3", listOf(
		SANITATION to 2,
		GROWTH to 6,
		GDP_ENTERTAINMENT to 120
	))
)





val WEAPON_MODS = listOf(
	WeaponMod("rome_gladius",          damage = 30),
	WeaponMod("rome_gladius_marian",   damage = 35),
	WeaponMod("rome_gladius_imperial", damage = 40),
	WeaponMod("rome_gladius_elite",    damage = 45, apDamage = 10),
	WeaponMod("rome_spear_cav_elite",  damage = 30, apDamage = 10)
)



val SHIELD_MODS = listOf(
	ShieldMod("scutum",          defence = 30, armour = 35, missileBlockChance = 50),
	ShieldMod("scutum_marian",   defence = 35, armour = 40, missileBlockChance = 55),
	ShieldMod("scutum_imperial", defence = 40, armour = 45, missileBlockChance = 60)
)



val ARMOUR_MODS = listOf(
	ArmourMod("cloth",          armour = 15),
	ArmourMod("mail",           armour = 40),
	ArmourMod("mail_improved",  armour = 50),
	ArmourMod("segmentata",     armour = 60),
	ArmourMod("segmentata_arm", armour = 70)
)



val UNIT_MODS = listOf(
	UnitMod(
		RORARII,
		attack  = 15,
		defence = 30,
		morale  = 40,
		bonusHp = 10,
		charge  = 10
	),

	UnitMod(
		VIGILES,
		attack  = 25,
		defence = 40,
		morale  = 50,
		bonusHp = 20,
		charge  = 15,
		armour  = "chest",
		shield  = "scutum_marian",
		cost    = 400,
		upkeep  = 100
	),

	UnitMod(
		HASTATI,
		attack = 35,
		defence = 20,
		morale = 45,
		bonusHp = 10,
		charge = 15,
		cost = 400,
		upkeep = 100
	),

	UnitMod(
		PRINCIPES,
		attack = 50,
		defence = 25,
		morale = 55,
		bonusHp = 15,
		charge = 20,
		cost = 700,
		upkeep = 120
	),

	UnitMod(
		LEGIONARIES,
		attack = 55,
		defence = 30,
		morale = 65,
		bonusHp = 20,
		charge = 20,
		cost = 850,
		upkeep = 150
	),

	UnitMod(
		LEGIONARY_COHORT,
		attack = 60,
		defence = 35,
		morale = 75,
		bonusHp = 25,
		charge = 25,
		cost = 1000,
		upkeep = 200
	),

	UnitMod(
		ARMOURED_LEGIONARIES,
		attack = 60,
		defence = 40,
		morale = 80,
		bonusHp = 30,
		charge = 25,
		cost = 1200,
		upkeep = 220
	),

	UnitMod(
		TRIARII,
		attack = 35,
		defence = 40,
		morale = 65,
		bonusHp = 20,
		charge = 20,
		cost = 800,
		upkeep = 150
	),

	UnitMod(
		VETERAN_LEGIONARIES,
		attack = 55,
		defence = 45,
		morale = 70,
		bonusHp = 25,
		charge = 20,
		cost = 1000,
		upkeep = 200
	),

	UnitMod(
		EVOCATI_COHORT,
		attack = 60,
		defence = 50,
		morale = 75,
		bonusHp = 30,
		charge = 25,
		cost = 1500,
		upkeep = 250
	),

	UnitMod(
		FIRST_COHORT,
		attack = 65,
		defence = 35,
		morale = 70,
		bonusHp = 20,
		charge = 25,
		cost = 1200,
		upkeep = 220
	),

	UnitMod(
		EAGLE_COHORT,
		attack = 75,
		defence = 40,
		morale = 75,
		bonusHp = 25,
		charge = 30,
		cost = 1750,
		upkeep = 275
	),

	UnitMod(
		PRAETORIANS,
		attack = 70,
		defence = 50,
		morale = 80,
		bonusHp = 25,
		charge = 30,
		cost = 2000,
		upkeep = 400
	),

	UnitMod(
		PRAETORIAN_GUARD,
		attack = 85,
		defence = 60,
		morale = 100,
		bonusHp = 30,
		charge = 35,
		shield = "scutum_imperial",
		cost = 4000,
		upkeep = 500
	),

	UnitMod(
		EQUITES,
		attack = 40,
		defence = 15,
		morale = 45,
		bonusHp = 10,
		charge = 30,
		cost = 500,
		upkeep = 100
	),

	UnitMod(
		LEGIONARY_CAVALRY,
		attack = 50,
		defence = 25,
		morale = 55,
		bonusHp = 15,
		charge = 35,
		cost = 1000,
		upkeep = 150
	),

	UnitMod(
		PRAETORIAN_CAVALRY,
		attack = 64,
		defence = 40,
		morale = 70,
		bonusHp = 25,
		charge = 45,
		cost = 3000,
		upkeep = 400
	)
)