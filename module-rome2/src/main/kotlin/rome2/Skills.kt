package rome2

@Suppress("Unused", "MemberVisibilityCanBePrivate")
object Skills {


	val ALL = ArrayList<Skill>()
	val GENERAL = ArrayList<Skill>()
	val SPY = ArrayList<Skill>()
	val DIGNITARY = ArrayList<Skill>()
	val CHAMPION = ArrayList<Skill>()
	val ARMY = skills.values.filter { it.name.startsWith("army_") }
	val NAVY = skills.values.filter { it.name.startsWith("navy_") }

	private fun generalSkill(name: String) = skill(name).also { ALL.add(it); GENERAL.add(it) }
	private fun spySkill(name: String) = skill(name).also { ALL.add(it); SPY.add(it) }
	private fun dignitarySkill(name: String) = skill(name).also { ALL.add(it); DIGNITARY.add(it) }
	private fun championSkill(name: String) = skill(name).also { ALL.add(it); CHAMPION.add(it) }

	init { General; Spy; Dignitary; Champion; ALL += ARMY; ALL += NAVY }



	object General {
		val COMMANDER = generalSkill("general_commander_1")
		val GENERAL = generalSkill("general_commander_2_general")
		val ADMIRAL = generalSkill("general_commander_2_admiral")
		val INFANTRY_COMMAND = generalSkill("general_commander_3_infantry")
		val NAVAL_SUPPORT = generalSkill("general_commander_3_naval_support")
		val ARTILLERY_COMMAND = generalSkill("general_commander_3_artillery")
		val NAVAL_ASSAULT = generalSkill("general_commander_3_naval_assault")
		val CAVALRY_COMMAND = generalSkill("general_commander_3_cavalry")
		val GREAT_LEADER = generalSkill("general_commander_4_great_leader")
		val STRATEGIST = generalSkill("general_strategist_1")
		val TACTICIAN = generalSkill("general_strategist_2_tactician")
		val NAVIGATOR = generalSkill("general_strategist_2_navigator")
		val ADMINISTRATION = generalSkill("general_strategist_3_administration")
		val COVERT_ACTION = generalSkill("general_strategist_3_covert_action")
		val LOGISTICS = generalSkill("general_strategist_3_logistics")
		val SIEGECRAFT = generalSkill("general_strategist_3_siegecraft")
		val MERCENARY_CONTACTS = generalSkill("general_strategist_3_mercenary")
		val ASSIMILATOR = generalSkill("general_strategist_3_assimilator")
		val MASTER_TACTICIAN = generalSkill("general_strategist_4_master_tactician")
		val WARRIOR = generalSkill("general_warmonger_1")
		val MARINE = generalSkill("general_warmonger_2_marine")
		val SOLDIER = generalSkill("general_warmonger_2_soldier")
		val OFFENSE = generalSkill("general_warmonger_3_offense")
		val DEFENCE = generalSkill("general_warmonger_3_defence")
		val VIRTUE = generalSkill("general_warmonger_3_virtue")
		val DREAD = generalSkill("general_warmonger_3_dread")
		val DESPOIL = generalSkill("general_warmonger_3_despoil")
		val FEARLESS_WARRIOR = generalSkill("general_warmonger_4_fearless_warrior")
	}
	
	
	
	object Spy {
		val CONSPIRATOR = spySkill("age_agent_spy_authority_conspirator")
		val DESPOILER = spySkill("age_agent_spy_authority_despoiler")
		val HUNTER = spySkill("age_agent_spy_authority_hunter")
		val NATURALIST = spySkill("age_agent_spy_authority_naturalist")
		val NETWORK = spySkill("age_agent_spy_authority_network")
		val PATHFINDER = spySkill("age_agent_spy_authority_pathfinder")
		val SPASAKA = spySkill("age_agent_spy_authority_spasaka")
		val SPECULATORS = spySkill("age_agent_spy_authority_speculatore")
		val STUDIOUS = spySkill("age_agent_spy_authority_studious")
		val WATCHMAN = spySkill("age_agent_spy_authority_watchman")
		val ASSISTANT = spySkill("age_agent_spy_subterfuge_assistant")
		val CONTACTS = spySkill("age_agent_spy_subterfuge_contacts")
		val COORDINATOR = spySkill("age_agent_spy_subterfuge_coordinator")
		val DECEIVER = spySkill("age_agent_spy_subterfuge_deceiver")
		val DISCORD = spySkill("age_agent_spy_subterfuge_discord")
		val HALLUCINOGENS = spySkill("age_agent_spy_subterfuge_hallucinogens")
		val HIDEOUT = spySkill("age_agent_spy_subterfuge_hideout")
		val LIAR = spySkill("age_agent_spy_subterfuge_liar")
		val LOOKOUT = spySkill("age_agent_spy_subterfuge_lookout")
		val MANDRAKE = spySkill("age_agent_spy_subterfuge_mandrake")
		val THIEF = spySkill("age_agent_spy_subterfuge_thief")
		val TOXIC = spySkill("age_agent_spy_subterfuge_toxic")
		val TRACKER = spySkill("age_agent_spy_subterfuge_tracker")
		val ANIMAL_HATER = spySkill("age_agent_spy_zeal_animal_hater")
		val BLIGHT = spySkill("age_agent_spy_zeal_blight")
		val BOOKWORM = spySkill("age_agent_spy_zeal_bookworm")
		val CAREFUL = spySkill("age_agent_spy_zeal_careful")
		val COUNTERSPY = spySkill("age_agent_spy_zeal_counterspy")
		val DEADLY = spySkill("age_agent_spy_zeal_deadly")
		val LOOTER = spySkill("age_agent_spy_zeal_looter")
		val MENACE = spySkill("age_agent_spy_zeal_menace")
		val MILITARY_ESPIONAGE = spySkill("age_agent_spy_zeal_military_espionage")
		val SABOTEUR = spySkill("age_agent_spy_zeal_saboteur")
		val SPECIALIST = spySkill("age_agent_spy_zeal_specialist")
	}



	object Dignitary {
		val ADVOCATUS = dignitarySkill("age_agent_dignitary_authority_advocatus")
		val DIPLOMATIC = dignitarySkill("age_agent_dignitary_authority_diplomatic")
		val ENTERTAINER = dignitarySkill("age_agent_dignitary_authority_entertainer")
		val FINANCIST = dignitarySkill("age_agent_dignitary_authority_financist")
		val HERDER = dignitarySkill("age_agent_dignitary_authority_herder")
		val LAWFUL = dignitarySkill("age_agent_dignitary_authority_lawful")
		val OFFICIUM = dignitarySkill("age_agent_dignitary_authority_officium")
		val ORATOR = dignitarySkill("age_agent_dignitary_authority_orator")
		val PERSUASIVE = dignitarySkill("age_agent_dignitary_authority_persuasive")
		val PHILOSOPHER = dignitarySkill("age_agent_dignitary_authority_philosopher")
		val POLITICAL = dignitarySkill("age_agent_dignitary_authority_political")
		val SLAVER = dignitarySkill("age_agent_dignitary_authority_slaver")
		val SUPPLIER = dignitarySkill("age_agent_dignitary_authority_supplier")
		val TUTOR = dignitarySkill("age_agent_dignitary_authority_tutor")
		val CONTRACTOR = dignitarySkill("age_agent_dignitary_subterfuge_contractor")
		val DEALER = dignitarySkill("age_agent_dignitary_subterfuge_dealer")
		val EMBEZZLER = dignitarySkill("age_agent_dignitary_subterfuge_embezzler")
		val EMPLOYER = dignitarySkill("age_agent_dignitary_subterfuge_employer")
		val EMPOROS = dignitarySkill("age_agent_dignitary_subterfuge_emporos")
		val FANATIC = dignitarySkill("age_agent_dignitary_subterfuge_fanatic")
		val GENEROUS = dignitarySkill("age_agent_dignitary_subterfuge_generous")
		val GREEDY = dignitarySkill("age_agent_dignitary_subterfuge_greedy")
		val IMPERIOUS = dignitarySkill("age_agent_dignitary_subterfuge_imperious")
		val MIMAR = dignitarySkill("age_agent_dignitary_subterfuge_mi'mar")
		val SILVER_TONGUE = dignitarySkill("age_agent_dignitary_subterfuge_silver_tongue")
		val SMUGGLER = dignitarySkill("age_agent_dignitary_subterfuge_smuggler")
		val STRICT = dignitarySkill("age_agent_dignitary_subterfuge_strict")
		val VOWER = dignitarySkill("age_agent_dignitary_subterfuge_vower")
		val ADVISOR = dignitarySkill("age_agent_dignitary_zeal_advisor")
		val AZAD = dignitarySkill("age_agent_dignitary_zeal_azad")
		val CHANT = dignitarySkill("age_agent_dignitary_zeal_chant")
		val CORRUPT = dignitarySkill("age_agent_dignitary_zeal_corrupt")
		val LEADER = dignitarySkill("age_agent_dignitary_zeal_leader")
		val MAGI = dignitarySkill("age_agent_dignitary_zeal_magi")
		val MOTIVATOR = dignitarySkill("age_agent_dignitary_zeal_motivator")
		val NOBLE = dignitarySkill("age_agent_dignitary_zeal_noble")
		val RHETORIC = dignitarySkill("age_agent_dignitary_zeal_rhetoric")
		val ROMANIZER = dignitarySkill("age_agent_dignitary_zeal_romanizer")
		val SPHAGIA = dignitarySkill("age_agent_dignitary_zeal_sphagia")
		val UNDERHANDED = dignitarySkill("age_agent_dignitary_zeal_underhanded")
		val WARMONGER = dignitarySkill("age_agent_dignitary_zeal_warmonger")
	}



	object Champion {
		val AGITATOR = championSkill("age_agent_champion_authority_agitator")
		val DRILL_SERGEANT = championSkill("age_agent_champion_authority_drill_sergeant")
		val DRUNKARD = championSkill("age_agent_champion_authority_drunkard")
		val EFFICIENT = championSkill("age_agent_champion_authority_efficient")
		val EVOCATUS = championSkill("age_agent_champion_authority_evocatus")
		val EXPLORER = championSkill("age_agent_champion_authority_explorer")
		val FESTIVE = championSkill("age_agent_champion_authority_festive")
		val GUARDIAN = championSkill("age_agent_champion_authority_guardian")
		val HISTORIAN = championSkill("age_agent_champion_authority_historian")
		val HORSEBREEDER = championSkill("age_agent_champion_authority_horsebreeder")
		val INSPIRATIONAL = championSkill("age_agent_champion_authority_inspirational")
		val MENTOR = championSkill("age_agent_champion_authority_mentor")
		val OFFICER = championSkill("age_agent_champion_authority_officer")
		val OFFICER_NATIVE_UNITS = championSkill("age_agent_champion_authority_officer_native_units")
		val PAEDOTRIBE = championSkill("age_agent_champion_authority_paedotribe")
		val PROTECTOR = championSkill("age_agent_champion_authority_protector")
		val RENOWNED = championSkill("age_agent_champion_authority_renowned")
		val AGEESTIGATOR = championSkill("age_agent_champion_subterfuge_ageestigator")
		val HARASSER = championSkill("age_agent_champion_subterfuge_harasser")
		val HUNTER = championSkill("age_agent_champion_subterfuge_hunter")
		val INTERROGATOR = championSkill("age_agent_champion_subterfuge_interrogator")
		val INTIMIDATING = championSkill("age_agent_champion_subterfuge_intimidating")
		val MURDEROUS = championSkill("age_agent_champion_subterfuge_murderous")
		val NOCTURNAL = championSkill("age_agent_champion_subterfuge_nocturnal")
		val RESOURCEFUL = championSkill("age_agent_champion_subterfuge_resourceful")
		val SIEGE_EXPERT = championSkill("age_agent_champion_subterfuge_siege_expert")
		val TRAPPER = championSkill("age_agent_champion_subterfuge_trapper")
		val WICKED = championSkill("age_agent_champion_subterfuge_wicked")
		val ARSONIST = championSkill("age_agent_champion_zeal_arsonist")
		val BLOODLUST = championSkill("age_agent_champion_zeal_bloodlust")
		val CENTURION = championSkill("age_agent_champion_zeal_centurion")
		val DUELIST = championSkill("age_agent_champion_zeal_duelist")
		val ELOQUENT = championSkill("age_agent_champion_zeal_eloquent")
		val FRENZIED = championSkill("age_agent_champion_zeal_frenzied")
		val HEROIC = championSkill("age_agent_champion_zeal_heroic")
		val HOPLITE = championSkill("age_agent_champion_zeal_hoplite")
		val MARKSMAN = championSkill("age_agent_champion_zeal_marksman")
		val MILITANT = championSkill("age_agent_champion_zeal_militant")
		val PRAEFECTUS = championSkill("age_agent_champion_zeal_praefectus")
		val RAIDER = championSkill("age_agent_champion_zeal_raider")
		val RESILIENT = championSkill("age_agent_champion_zeal_resilient")
		val SADISTIC = championSkill("age_agent_champion_zeal_sadistic")
		val TERRIFYING = championSkill("age_agent_champion_zeal_terrifying")
	}


}