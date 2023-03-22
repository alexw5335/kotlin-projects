package rol

fun main() {
	val unitReader = RolReader("/unitrules.xml")
	val buildingReader = RolReader("/buildingrules.xml")

	//unitReader.units.filter { it.name.contains("Rukh") }.forEach(::println)
	buildingReader.buildings.filter { it.name.contains("Glass Citadel") }.forEach(::println)

	unitMods.forEach(unitReader::applyMod)
	buildingMods.forEach(buildingReader::applyMod)
	val modifiedRoot = "module-rol/src/main/resources/modified"
	unitReader.write("$modifiedRoot/unitrules.xml")
	buildingReader.write("$modifiedRoot/buildingrules.xml")
}



val buildingMods = listOf(
	BuildingMod(
		building = "Obelisk",
		cost = "100m/50e",            // 75m/25e
		rampCost = "${50 - 40}m ramp",
		hits = 250 + 50,
		time = "${600 - 100}",
		lineOfSight = 16 + 4,
		groundAttack = "${9 + 1}k",
		airAttack = "${9 + 1}k"
	),

	BuildingMod(
		building = "Sanctuary",
		cost = "250m/50e", // 200m/25e
		rampCost = "${175 - 75}m ramp",
		groundAttack = "${15 + 5}kcn1",
		airAttack = "${15 + 5}kc",
		hits = 750 + 250
	)
)



val unitMods = listOf(
	// POP CHANGES
	UnitMod(
		units = { it.name.contains("Miner") },
		pop = 0 // 1
	),
	UnitMod(
		units = { it.category == "Grunt" },
		pop = 1 // 1 for Alin, 2 for Vinci, 3 for Cuotl
	),



	/*
	ALIN
	 */



	// DESERT WALKERS
	UnitMod(
		unit = "Desert Walkers",
		rampCost = "${3 - 2}m ramp",
		time = (200 - 75).toString(),
		block = { it.moveSpeed += 5; it.hits += 15 }
	),

	// HEARTSEEKER
	UnitMod(
		unit = "Heartseeker",
		time = (350 - 100).toString(),
		pop = 3 - 1,
		hits = 175 + 25,
		rampCost = "1w ramp",
		groundRangedAttack = "${5 + 3}c",
		airRangedAttack = "${10 + 3}c",
		moveSpeed = 42 + 5
	),

	// SCORPION
	UnitMod(
		unit = "Scorpion",
		time = (450 - 150).toString(),
		pop = 4 - 1,
		moveSpeed = 48 + 7,
		rampCost = "2m/1w ramp", // 4m/2w ramp
	),

	// AFREET
	UnitMod(
		unit = "Afreet",
		time = (350 - 50).toString(),
		pop = 3 - 1,
		hits = 170 + 30,
		groundRangedAttack = "${5 + 2}ch2,3,100,100,1",
		airRangedAttack = "${5 + 2}h2"
	),

	// FIRE ELEMENTAL
	UnitMod(
		unit = "Fire Elemental",
		hits = 350 + 50,
		groundRangedAttack = "${11 + 4}ph2n1",
		airRangedAttack = "${11 + 4}ph2"
	),



	/*
	CUOTL
	 */



	// SENTINELS
	UnitMod(
		unit = "Sentinels",
		hits = 140 + 20,
		groundRangedAttack = "${7 + 1}c",
		airRangedAttack = "${7 + 1}c",
		time = "${275 - 75}"
	),
	UnitMod(
		unit = "Guardians",
		hits = 170 + 30,
		groundRangedAttack = "${10 + 2}c",
		airRangedAttack = "${10 + 2}c",
		time = "${275 - 75},x2"
	),
	UnitMod(
		unit = "Defenders",
		hits = 202 + 38,
		groundRangedAttack = "${13 + 3}c",
		airRangedAttack = "${13 + 3}c",
		time = "${275 - 75},x2"
	),

	// SUN JAGUAR
	UnitMod(
		unit = "Sun Jaguar",
		hits = 200 + 75,
		time = (450 - 50).toString()
	),
	UnitMod(
		unit = "Veteran Sun Jaguar",
		hits = 265 + 75,
		time = (450 - 75).toString() + ",x2"
	),
	UnitMod(
		unit = "Elite Sun Jaguar",
		hits = 330 + 75,
		time = (450 - 100).toString() + ",x2"
	),

	// DEATH SNAKE
	UnitMod(
		unit = "Death Snake",
		hits = 450 + 75,
		time = (500 - 50).toString(),
		groundMeleeAttack = "${15 + 5}pcqs2n1"
	),
	UnitMod(
		unit = "Elite Death Snake",
		hits = 575 + 150,
		time = (500 - 50).toString() + ",x2",
		groundMeleeAttack = "${20 + 5}pcqs2n1"
	),

	// SUN IDOL
	UnitMod(
		unit = "Sun Idol",
		time = "${750 - 100}",
		hits = 800 + 100,
		groundRangedAttack = "${14 + 4}h1n1",
		airRangedAttack = "${14 + 4}h1"
	),
	UnitMod(
		unit = "Elite Sun Idol",
		time = "${750 - 100},x1",
		hits = 1000 + 200,
		groundRangedAttack = "${20 + 6}h2n1",
		airRangedAttack = "${20 + 6}h1"
	),
	// DEATH SPHERE
	UnitMod(
		unit = "Death Sphere",
		hits = 1600 + 200,
		groundRangedAttack = "${31 + 9}qp1n1r1,4,100,100,10"
	)
)