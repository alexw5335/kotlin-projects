package rome2

object Armours {

	                                                    //  armour, strongVsMissiles, weakVsMissiles
	val BRONZE            = armour("bronze")            //  60   0  0
	val BRONZE_ELITE      = armour("bronze_elite")      //  70   0  0
	val CATAPHRACT        = armour("cataphract")        //  90   0  0
	val CATAPHRACT_ELITE  = armour("cataphract_elite")  //  110  0  1
	val CHEST             = armour("chest")             //  25   0  0
	val CHEST_IMPROVED    = armour("chest_improved")    //  30   0  0
	val CLOTH             = armour("cloth")             //  10   0  0
	val LEATHER           = armour("leather")           //  30   0  0
	val LIGHT_PADDED      = armour("light_padded")      //  15   0  0
	val MAIL              = armour("mail")              //  40   0  0
	val MAIL_IMPROVED     = armour("mail_improved")     //  45   0  0
	val NONE              = armour("none")              //  0    0  0
	val PADDED            = armour("padded")            //  20   1  0
	val PADDED_IMPROVED   = armour("padded_improved")   //  25   1  0
	val SCALE             = armour("scale")             //  40   0  0
	val SCALE_ELITE       = armour("scale")             //  60   0  0
	val SEGMENTATA        = armour("segmentata")        //  50   0  0
	val SEGMENTATA_ARM    = armour("segmentata_arm")    //  55   0  0
	val SPOLAS            = armour("spolas")            //  35   0  1
	val SPOLAS_IMPROVED   = armour("spolas_improved")   //  40   0  0

}



object Shields {
	                                                            //  armour, defence, missile block chance
	val CAETRA                = shield("caetra")                //  40  5   20
	val CAETRA_CAVALRY        = shield("caetra_cavalry")        //  40  5   10
	val CAETRA_FAST_CAV       = shield("caetra_fast_cav")       //  40  5   25
	val CAVALRY               = shield("cavalry")               //  20  30  20
	val CELTIC                = shield("celtic")                //  30  35  40
	val CELTIC_MISSILE        = shield("celtic_missile")        //  15  30  50
	val CELTIC_ROUND          = shield("celtic_round")          //  25  15  25
	val CELTIC_SHOCK          = shield("celtic_shock")          //  35  10  30
	val GERMANIC_CAV          = shield("germanic_cav")          //  30  20  20
	val GERMANIC_HEX          = shield("germanic_hex")          //  30  40  55
	val GERMANIC_SHOCK        = shield("germanic_shock")        //  35  10  40
	val GERMANIC_SMALL_SKIRM  = shield("germanic_small_skirm")  //  20  20  60
	val GERMANIC_WALL         = shield("germanic_wall")         //  35  40  60
	val GERMANIC_WOOD         = shield("germanic_wood")         //  30  30  55
	val HOPLITE               = shield("hoplite")               //  15  45  50
	val HOPLITE_CAV           = shield("hoplite_cav")           //  15  35  25
	val IBERIAN_SCUTA         = shield("iberian_scuta")         //  30  30  40
	val LARGE_HIDE            = shield("large_hide")            //  35  25  50
	val NONE                  = shield("none")                  //  0   0   0
	val OVAL                  = shield("oval")                  //  25  35  40
	val PELTA                 = shield("pelta")                 //  25  5   50
	val PELTA_CAVALRY         = shield("pelta_cavalry")         //  25  5   25
	val PELTAST_THUREOS       = shield("peltast_thureos")       //  25  30  50
	val PIKEMAN               = shield("pikemen")               //  10  30  25
	val ROUND                 = shield("round")                 //  30  30  50
	val SCUTUM                = shield("scutum")                //  30  35  50
	val SCUTUM_IMPERIAL       = shield("scutum_imperial")       //  25  40  50
	val SCUTUM_MARIAN         = shield("scutum_marian")         //  25  40  50
	val SMALL_BRONZE          = shield("small_bronze")          //  10  10  30
	val SMALL_ROUND           = shield("small_round")           //  10  5   30
	val THRACIAN              = shield("thracian")              //  0   20  0
	val THUREOS               = shield("thureos")               //  25  35  40
	val TOWER                 = shield("tower")                 //  10  35  55

}



object Weapons {
                                                                                           // damage, apDamage
	val BALKAN_SWORD                     = weapon("rome_balkan_sword")                     // 25  10
	val CELTIC_AXE                       = weapon("rome_celtic_axe")                       // 16  10
	val CELTIC_HELLENIC_SWORD            = weapon("rome_celtic_hellenic_sword")            // 30  4
	val CELTIC_HELLENIC_SWORD_CAV        = weapon("rome_celtic_hellenic_sword_cav")        // 30  4
	val CELTIC_HELLENIC_SWORD_ELITE      = weapon("rome_celtic_hellenic_sword_elite")      // 36  4
	val CELTIC_LONGSWORD                 = weapon("rome_celtic_longsword")                 // 36  4
	val CELTIC_LONGSWORD_BONUS_INFANTRY  = weapon("rome_celtic_longsword_bonus_infantry")  // 20  5
	val CELTIC_LONGSWORD_ELITE           = weapon("rome_celtic_longsword_elite")           // 40  5
	val CELTIC_SPEAR                     = weapon("rome_celtic_spear")                     // 20  6
	val CELTIC_SPEAR_ANTI_CAV            = weapon("rome_celtic_spear_anti_cav")            // 15  7
	val CELTIC_SPEAR_CAV                 = weapon("rome_celtic_spear_cav")                 // 20  7
	val CELTIC_SPEAR_CAV_ELITE           = weapon("rome_celtic_spear_cav_elite")           // 24  7
	val CELTIC_SPEAR_ELITE               = weapon("rome_celtic_spear_elite")               // 24  6
	val CLUB                             = weapon("rome_club")                             // 6   9
	val EASTERN_AXE                      = weapon("rome_eastern_axe")                      // 16  10
	val EASTERN_AXE_ELITE                = weapon("rome_eastern_axe_elite")                // 20  12
	val EASTERN_SWORD                    = weapon("rome_eastern_sword")                    // 30  4
	val EASTERN_SWORD_ELITE              = weapon("rome_eastern_sword_elite")              // 34  4
	val FALCATA                          = weapon("rome_falcata")                          // 30  5
	val FALCATA_CAV                      = weapon("rome_falcata_cav")                      // 34  5
	val FALCATA_ELITE                    = weapon("rome_falcata_elite")                    // 36  5
	val FALX                             = weapon("rome_falx")                             // 25  14
	val GENERIC_SWORD                    = weapon("rome_generic_sword")                    // 30  4
	val GENERIC_SWORD_CAV                = weapon("rome_generic_sword_cav")                // 34  4
	val GLADIUS                          = weapon("rome_gladius")                          // 30  5
	val GLADIUS_ELITE                    = weapon("rome_gladius_elite")                    // 34  5
	val GLADIUS_ELITE_CAV                = weapon("rome_gladius_elite_Cav")                // 36  5
	val GLADIUS_IMPERIAL                 = weapon("rome_gladius_imperial")                 // 30  5
	val GLADIUS_MARIAN                   = weapon("rome_gladius_marian")                   // 30  5
	val HOPLITE_SPEAR                    = weapon("rome_hoplite_spear")                    // 20  6
	val HOPLITE_SPEAR_CAV                = weapon("rome_hoplite_spear_cav")                // 20  6
	val HOPLITE_SPEAR_ELITE              = weapon("rome_hoplite_spear_elite")              // 22  8
	val HOPLITE_SWORD                    = weapon("rome_hoplite_sword")                    // 30  4
	val HORSE_HEWER                      = weapon("rome_horse_hewer")                      // 15  10
	val IBERIAN_SWORD                    = weapon("rome_iberian_sword")                    // 30  4
	val IOLEAN_GLOVE                     = weapon("rome_iolean_glove")                     // 40  15
	val KOPIS                            = weapon("rome_kopis")                            // 30  6
	val KOPIS_CAV                        = weapon("rome_kopis_cav")                        // 32  6
	val LANCE                            = weapon("rome_lance")                            // 14  11
	val LANCE_ELITE                      = weapon("rome_lance_elite")                      // 15  12
	val PIKE                             = weapon("rome_pike")                             // 20  4
	val PIKE_ELITE                       = weapon("rome_pike_elite")                       // 24  4
	val PIKE_LEVY                        = weapon("rome_pike_levy")                        // 16  4
	val RHOMPHAIA                        = weapon("rome_rhomphaia")                        // 30  15
	val SHORTSWORD                       = weapon("rome_shortsword")                       // 20  4
	val SHOTEL                           = weapon("rome_shotel")                           // 23  12
	val SHOTEL_ELITE                     = weapon("rome_shotel_elite")                     // 30  20
	val SPATHA                           = weapon("rome_spatha")                           // 30  4
	val SPEAR                            = weapon("rome_spear")                            // 20  5
	val SPEAR_ANTI_CAV                   = weapon("rome_spear_anti_cav")                   // 15  5
	val SPEAR_CAV                        = weapon("rome_spear_cav")                        // 20  5
	val SPEAR_CAV_ELITE                  = weapon("rome_spear_cav_elite")                  // 24  5
	val SPEAR_ELITE                      = weapon("rome_spear_elite")                      // 24  5
	val STEPPE_PICKAXE                   = weapon("rome_steppe_pickaxe")                   // 16  10
	val STEPPE_SWORD                     = weapon("rome_steppe_sword")                     // 30  4
	val TUSKS                            = weapon("tusks")                                 // 30  30

}



object Projectiles {
	                                                                                        // damage, apDamage, range
	val ANIMAL_CARCASS                    = projectile("animal_carcass")                    // 10   0    350
	val ANIMAL_CARCASS_BASTION            = projectile("animal_carcass_bastion")            // 10   0    400
	val ANIMAL_CARCASS_DEBRIS             = projectile("animal_carcass_debris")             // 0    5    50
	val ANIMAL_CARCASS_ONAGER_LARGE       = projectile("animal_carcass_onager_large")       // 20   0    480
	val ARROW_COMPOSITE                   = projectile("arrow_composite")                   // 36   4    150
	val ARROW_COMPOSITE_HEAVY             = projectile("arrow_composite_heavy")             // 32   8    110
	val ARROW_COMPOSITE_HORSE             = projectile("arrow_composite_horse")             // 36   4    125
	val ARROW_COMPOSITE_HORSE_HEAVY       = projectile("arrow_composite_horse_heavy")       // 32   8    100
	val ARROW_FIGHTING_TOWER              = projectile("arrow_fighting_tower")              // 30   2    125
	val ARROW_FLAMING                     = projectile("arrow_flaming")                     // 15   1    125
	val ARROW_FLAMING_HORSE               = projectile("arrow_flaming_horse")               // 15   1    125
	val ARROW_FLAMING_LONG                = projectile("arrow_flaming_long")                // 15   1    125
	val ARROW_LONG                        = projectile("arrow_long")                        // 31   4    150
	val ARROW_LONG_HEAVY                  = projectile("arrow_long_heavy")                  // 32   8    110
	val ARROW_LONGBOW                     = projectile("arrow_longbow")                     // 36   4    125
	val ARROW_NORMAL                      = projectile("arrow_normal")                      // 31   4    125
	val ARROW_NORMAL_HORSE                = projectile("arrow_normal_horse")                // 31   4    125
	val ARROW_NORMAL_HORSE_HEAVY          = projectile("arrow_normal_horse_heavy")          // 27   8    100
	val ARROW_NORMAL_LONG                 = projectile("arrow_normal_long")                 // 31   4    150
	val ARROW_POISON                      = projectile("arrow_poison")                      // 20   3    150
	val ARROW_POISON_HORSE                = projectile("arrow_poison_horse")                // 20   3    125
	val ARROW_POLYBOLOS                   = projectile("arrow_polybolos")                   // 35   50   260
	val ARROW_POLYBOLOS_BASTION           = projectile("arrow_polybolos_bastion")           // 35   50   320
	val ARROW_RECURVE                     = projectile("arrow_recurve")                     // 31   4    150
	val ARROW_RECURVE_HEAVY               = projectile("arrow_recurve_heavy")               // 32   8    110
	val ARROW_RECURVE_HORSE               = projectile("arrow_recurve_horse")               // 31   4    125
	val ARROW_RECURVE_HORSE_HEAVY         = projectile("arrow_recurve_horse_heavy")         // 32   8    100
	val ARROW_TOWER                       = projectile("arrow_tower")                       // 40   7    125
	val ARROW_TOWER_UPGRADED              = projectile("arrow_tower_upgraded")              // 40   15   125
	val ARROW_WHISTLING                   = projectile("arrow_whistling")                   // 5    0    125
	val ARROW_WHISTLING_HORSE             = projectile("arrow_whistling_horse")             // 5    0    125
	val BEEHIVE                           = projectile("beehive")                           // 10   10   350
	val BOILING_OIL                       = projectile("boiling_oil")                       // 0    40   80
	val BOLT                              = projectile("bolt")                              // 50   70   350
	val BOLT_BASTION                      = projectile("bolt_bastion")                      // 50   70   400
	val BOLT_FLAMMABLE                    = projectile("bolt_flammable")                    // 50   70   350
	val BOLT_FLAMMABLE_BASTION            = projectile("bolt_flammable_bastion")            // 50   70   400
	val BOLT_FLAMMABLE_LARGE              = projectile("bolt_flammable_large")              // 0    180  150
	val BOLT_LARGE                        = projectile("bolt_large")                        // 0    180  150
	val BOLT_TOWER                        = projectile("bolt_tower")                        // 10   70   175
	val EXPLOSION_DEBRIS                  = projectile("explosion_debris")                  // 10   10   50
	val EXPLOSION_DEBRIS_FIREPOT          = projectile("explosion_debris_firepot")          // 1    1    50
	val EXPLOSIVE_POT_ARTILLERY           = projectile("explosive_pot_artillery")           // 10   10   400
	val EXPLOSIVE_POT_ARTILLERY_LARGE     = projectile("explosive_pot_artillery_large")     // 15   15   550
	val EXPLOSIVE_POT_BALLISTA_BASTION    = projectile("explosive_pot_ballista_bastion")    // 0    20   500
	val EXPLOSIVE_POT_NAVAL               = projectile("explosive_pot_naval")               // 200  130  350
	val FIREBALL                          = projectile("fireball")                          // 0    180  22
	val FIREBALL_TEUTOBURG                = projectile("fireball_teutoburg")                // 0    60   22
	val FLAME                             = projectile("flame")                             // 0    180  80
	val HARPAX                            = projectile("harpax")                            // 1    0    175
	val JAVELIN_ANKYLE                    = projectile("javelin_ankyle")                    // 20   12   100
	val JAVELIN_FIRE                      = projectile("javelin_fire")                      // 7    1    80
	val JAVELIN_IMPROVED                  = projectile("javelin_improved")                  // 29   12   80
	val JAVELIN_IRON                      = projectile("javelin_iron")                      // 20   12   40
	val JAVELIN_LIGHT                     = projectile("javelin_light")                     // 20   9    80
	val JAVELIN_NORMAL                    = projectile("javelin_normal")                    // 20   12   80
	val JAVELIN_PREC                      = projectile("javelin_prec")                      // 20   12   40
	val JAVELIN_WOODEN                    = projectile("javelin_wooden")                    // 20   9    80
	val PILUM_HEAVY                       = projectile("pilum_heavy")                       // 20   10   40
	val PILUM_IRON                        = projectile("pilum_iron")                        // 20   12   40
	val PILUM_LIGHT                       = projectile("pilum_light")                       // 20   10   40
	val PILUM_NORMAL                      = projectile("pilum_normal")                      // 20   10   40
	val ROCK                              = projectile("rock")                              // 100  100  350
	val ROCK_BASTION                      = projectile("rock_bastion")                      // 100  100  400
	val ROCK_FLAMMABLE                    = projectile("rock_flammable")                    // 130  70   350
	val ROCK_FLAMMABLE_BASTION            = projectile("rock_flammable_bastion")            // 130  70   400
	val ROCK_FLAMMABLE_PROLOGUE           = projectile("rock_flammable_prologue")           // 150  0    850
	val ROCK_LARGE                        = projectile("rock_large")                        // 115  115  480
	val ROCK_LARGE_FLAMMABLE              = projectile("rock_large_flammable")              // 145  85   480
	val SCORPION_POT                      = projectile("scorpion_pot")                      // 10   20   420
	val SHP_BOLT_FLAMMABLE                = projectile("shp_bolt_flammable")                // 50   50   250
	val SHP_ROCK                          = projectile("shp_rock")                          // 100  100  350
	val SHP_ROCK_FLAMMABLE                = projectile("shp_rock_flammable")                // 130  70   350
	val SHP_ROCK_LARGE                    = projectile("shp_rock_large")                    // 115  115  480
	val SHP_ROCK_LARGE_FLAMMABLE          = projectile("shp_rock_large_flammable")          // 145  85   480
	val SHP_STONE                         = projectile("shp_stone")                         // 90   90   420
	val SHP_STONE_FLAMMABLE               = projectile("shp_stone_flammable")               // 120  60   420
	val SHP_STONE_LARGE                   = projectile("shp_stone_large")                   // 105  105  350
	val SHP_STONE_LARGE_FLAMMABLE         = projectile("shp_stone_large_flammable")         // 135  70   350
	val SLING_STONE                       = projectile("sling_stone")                       // 16   4    150
	val SLING_STONE_IMPROVED              = projectile("sling_stone_improved")              // 17   6    150
	val SLING_STONE_LEAD                  = projectile("sling_stone_lead")                  // 21   8    150
	val SNAKE_POT                         = projectile("snake_pot")                         // 10   20   420
	val STONE                             = projectile("stone")                             // 90   90   420
	val STONE_BALLISTA_BASTION            = projectile("stone_ballista_bastion")            // 90   90   460
	val STONE_FLAMMABLE                   = projectile("stone_flammable")                   // 120  60   420
	val STONE_FLAMMABLE_BALLISTA_BASTION  = projectile("stone_flammable_ballista_bastion")  // 120  60   460
	val STONE_HAND                        = projectile("stone_hand")                        // 5    1    75
	val STONE_LARGE                       = projectile("stone_large")                       // 105  105  550
	val STONE_LARGE_FLAMMABLE             = projectile("stone_large_flammable")             // 135  70   550
	val TORCH                             = projectile("torch")                             // 1    0    25

}



object MissileWeapons {

	val DK_COMPOSITE_BOW_POISON_ELITE  = missileWeapon("dk_composite_bow_poison_elite")    // arrow_composite
	val DK_LONG_BOW_POISON             = missileWeapon("dk_long_bow_poison")               // arrow_normal_long
	val FIGHTING_TOWER_ARROW           = missileWeapon("fighting_tower_arrow")             // arrow_tower
	val BALLISTA_GIANT                 = missileWeapon("rome_ballista_giant")              // stone_large
	val BALLISTA_MEDIUM                = missileWeapon("rome_ballista_medium")             // stone
	val BALLISTA_MEDIUM_BASTION        = missileWeapon("rome_ballista_medium_bastion")     // stone_ballista_bastion
	val BALLISTA_SHP                   = missileWeapon("rome_ballista_shp")                // shp_stone_flammable
	val BEEHIVE                        = missileWeapon("rome_beehive")                     // beehive
	val BOW                            = missileWeapon("rome_bow")                         // arrow_normal
	val BOW_HORSE                      = missileWeapon("rome_bow_horse")                   // arrow_normal_horse
	val BOW_LONG                       = missileWeapon("rome_bow_long")                    // arrow_normal_long
	val CHEIROBALLISTRA                = missileWeapon("rome_cheiroballistra")             // bolt
	val COMPOSITE_BOW                  = missileWeapon("rome_composite_bow")               // arrow_composite
	val COMPOSITE_BOW_ELEPHANT         = missileWeapon("rome_composite_bow_elephant")      // arrow_composite
	val COMPOSITE_BOW_ELITE            = missileWeapon("rome_composite_bow_elite")         // arrow_composite
	val COMPOSITE_BOW_HORSE            = missileWeapon("rome_composite_bow_horse")         // arrow_composite_horse
	val FIRERAISER                     = missileWeapon("rome_fireraiser")                  // flame
	val IRON_JAVELIN                   = missileWeapon("rome_iron_javelin")                // javelin_iron
	val JAVELIN                        = missileWeapon("rome_javelin")                     // javelin_normal
	val JAVELIN_ELEPHANT               = missileWeapon("rome_javelin_elephant")            // javelin_normal
	val JAVELIN_IMPROVED               = missileWeapon("rome_javelin_improved")            // javelin_improved
	val JAVELIN_LIGHT                  = missileWeapon("rome_javelin_light")               // javelin_light
	val JAVELIN_LIGHT_NONPRECURSOR     = missileWeapon("rome_javelin_light_nonprecursor")  // javelin_light
	val JAVELIN_PRECURSOR              = missileWeapon("rome_javelin_precursor")           // javelin_prec
	val LONGBOW                        = missileWeapon("rome_longbow")                     // arrow_longbow
	val ONAGER                         = missileWeapon("rome_onager")                      // rock
	val ONAGER_BASTION                 = missileWeapon("rome_onager_bastion")              // rock_bastion
	val ONAGER_LARGE                   = missileWeapon("rome_onager_large")                // rock_large
	val ONAGER_PROLOGUE                = missileWeapon("rome_onager_prologue")             // rock_flammable_prologue
	val ONAGER_SHP                     = missileWeapon("rome_onager_shp")                  // shp_rock_flammable
	val PILUM                          = missileWeapon("rome_pilum")                       // pilum_normal
	val PILUM_HEAVY                    = missileWeapon("rome_pilum_heavy")                 // pilum_heavy
	val PILUM_IRON                     = missileWeapon("rome_pilum_iron")                  // pilum_iron
	val PILUM_LIGHT                    = missileWeapon("rome_pilum_light")                 // pilum_light
	val POLYBOLOS                      = missileWeapon("rome_polybolos")                   // arrow_polybolos
	val POLYBOLOS_BASTION              = missileWeapon("rome_polybolos_bastion")           // arrow_polybolos_bastion
	val RECURVE_BOW                    = missileWeapon("rome_recurve_bow")                 // arrow_recurve
	val RECURVE_BOW_ELITE              = missileWeapon("rome_recurve_bow_elite")           // arrow_recurve
	val RECURVE_BOW_HORSE              = missileWeapon("rome_recurve_bow_horse")           // arrow_recurve_horse
	val SCORPIO                        = missileWeapon("rome_scorpio")                     // bolt
	val SCORPIO_BASTION                = missileWeapon("rome_scorpio_bastion")             // bolt_bastion
	val SCORPION_POT                   = missileWeapon("rome_scorpion_pot")                // scorpion_pot
	val SLING                          = missileWeapon("rome_sling")                       // sling_stone
	val SLING_IMPROVED                 = missileWeapon("rome_sling_improved")              // sling_stone_improved
	val SLING_LEAD                     = missileWeapon("rome_sling_lead")                  // sling_stone_lead
	val SNAKE_POT                      = missileWeapon("rome_snake_pot")                   // snake_pot
	val STONE                          = missileWeapon("rome_stone")                       // stone_hand
	val WOODEN_JAVELIN                 = missileWeapon("rome_wooden_javelin")              // javelin_wooden
	val SHP_SCORPION                   = missileWeapon("shp_scorpion")                     // shp_bolt_flammable
	val TOWER_ARROW                    = missileWeapon("tower_arrow")                      // arrow_tower_upgraded
	val TOWER_SCORPIO                  = missileWeapon("tower_scorpio")                    // bolt_tower
	val TOWER_WATCHTOWER               = missileWeapon("tower_watchtower")                 // arrow_tower

}