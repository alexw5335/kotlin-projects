package rome2

object Units {

	val ROMAN_ALL = ArrayList<LandUnit>()
	val AUX_ALL = ArrayList<LandUnit>()
	
	private fun romanUnit(name: String) = landUnit(name).also(ROMAN_ALL::add)
	private fun auxUnit(name: String) = landUnit(name).also(AUX_ALL::add)

	val ARMOURED_LEGIONARIES = romanUnit("Rom_Armour_Legionaries")
	val EAGLE_COHORT = romanUnit("Rom_Eagle_Cohort")
	val EQUITES = romanUnit("Rom_Equites")
	val EVOCATI_COHORT = romanUnit("Rom_Evocati_Cohort")
	val FIRST_COHORT = romanUnit("Rom_First_Cohort")
	val HASTATI = romanUnit("Rom_Hastati")
	val LEGIONARIES = romanUnit("Rom_Legionaries")
	val LEGIONARY_CAVALRY = romanUnit("Rom_Legionary_Cav")
	val LEGIONARY_COHORT = romanUnit("Rom_Legionary_Cohort")
	val PRAETORIAN_CAVALRY = romanUnit("Rom_Praetorian_Cav")
	val PRAETORIAN_GUARD = romanUnit("Rom_Praetorian_Guard")
	val PRAETORIANS = romanUnit("Rom_Praetorians")
	val PRINCIPES = romanUnit("Rom_Principes")
	val RORARII = romanUnit("Rom_Rorarii")
	val TRIARII = romanUnit("Rom_Triarii")
	val VETERAN_LEGIONARIES = romanUnit("Rom_Vet_Legionaries")
	val VIGILES = romanUnit("Rom_Vigiles")
	val VELITES = romanUnit("Rom_Velites")
	val LEVES = romanUnit("Rom_Leves")
	val LEGATUS = romanUnit("Rom_Legatus")

	val BALLISTA = romanUnit("Rom_Ballista")
	val BALLISTA_BASTION = romanUnit("Rom_Ballista_Bastion")
	val BEEHIVE_ONAGER = romanUnit("Rom_Beehive_Onager")
	val GIANT_BALLISTA = romanUnit("Rom_Giant_Ballista")
	val LARGE_ONAGER = romanUnit("Rom_Large_Onager")
	val ONAGER = romanUnit("Rom_Onager")
	val ONAGER_BASTION = romanUnit("Rom_Onager_Bastion")
	val POLYBOLOS = romanUnit("Rom_Polybolos")
	val POLYBOLOS_BASTION = romanUnit("Rom_Polybolos_Bastion")
	val SCORPION_FIXED = romanUnit("Rom_Scorpion")
	val SCORPION_BASTION = romanUnit("Rom_Scorpion_Bastion")
	val SCORPION = romanUnit("Rom_Cheiroballistra")

	val AUX_AFRICAN_ARCHERS = auxUnit("Aux_Afr_Archers")
	val AUX_AFRICAN_CAVALRY = auxUnit("Aux_Afr_Cav")
	val AUX_AFRICAN_ELEPHANT = auxUnit("Aux_Afr_Elephant")
	val AUX_GALLIC_HUNTERS = auxUnit("Aux_Gal_Gallic_Hunters")
	val AUX_LONGBOW_HUNTERS = auxUnit("Aux_Ger_Longbow_Hunters")
	val AUX_PELTASTS = auxUnit("Aux_Gre_Peltasts")
	val AUX_CRETAN_ARCHERS = auxUnit("Aux_Gre_Cretan_Archers")
	val AUX_SYRIAN_ARCHERS = auxUnit("Aux_Syrian_Archers")
	val AUX_CAVALRY = auxUnit("Aux_Cav")
	val AUX_SHOCK_CAVALRY = auxUnit("Aux_Ita_Socii_Equites_Extraordinarii")

}