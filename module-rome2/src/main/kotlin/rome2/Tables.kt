package rome2



class Table(val name: String, val keys: String) {
	//fun secondLine(prefix: String) = "#${name}_tables;$keyCount;db/${name}_tables/${prefix}_$name"
	fun outputPath(prefix: String) = "db/${name}_tables/${prefix}_$name.tsv"
}



object Tables {
	val SHIELDS = Table("unit_shield_types", "key\tshield_defence_value\tshield_armour_value\taudio_material\tmissile_block_chance")
	val ARMOURS = Table("unit_armour_types", "key\tarmour_value\tbonus_vs_missiles\tweak_vs_missiles\taudio_material")
	val WEAPONS = Table("melee_weapons", "key\tarmour_penetrating\tarmour_piercing\tbonus_v_cavalry\tbonus_v_elephants\tbonus_v_infantry\tdamage\tap_damage\tfirst_strike\tshield_piercing\tweapon_length\tmelee_weapon_type\taudio_material")
	val LAND_UNITS = Table("land_units", "key\taccuracy\tammo\tarmour\tcampaign_action_points\tcategory\tcharge_bonus\tclass\tdismounted_charge_bonus\tdismounted_melee_attack\tdismounted_melee_defense\thistorical_description_text\tman_animation\tman_entity\tmelee_attack\tmelee_defence\tmorale\tbonus_hit_points\tmount\tnum_animals\tanimal\tnum_mounts\tprimary_melee_weapon\tprimary_missile_weapon\trank_depth\tshield\tshort_description_text\tspacing\tstrengths_&_weaknesses_text\tsupports_first_person\ttraining_level\tnum_guns\tofficers\tarticulated_record\tengine\tis_male\tvisibility_spotting_range_min\tvisibility_spotting_range_max\tability_global_recharge\tattribute_group\tspot_dist_tree\tspot_dist_scrub\tchariot\tnum_chariots\treload\tloose_spacing\tspotting_and_hiding\tselection_vo\tselected_vo_secondary\tselected_vo_tertiary\thiding_scalar")
	val MAIN_UNITS = Table("main_units", "unit\tadditional_building_requirement\tcampaign_cap\tcaste\tcreate_time\tis_naval\tland_unit\tnum_men\tmultiplayer_cap\tmultiplayer_cost\tnaval_unit\tnum_ships\tmin_men_per_ship\tmax_men_per_ship\tprestige\trecruitment_cost\trecruitment_movie\treligion_requirement\tupkeep_cost\tweight\tcampaign_total_cap\tresource_requirement\tworld_leader_only\tcan_trade\tspecial_edition_mask\tunique_index\tin_encyclopedia\tregion_unit_resource_requirement\taudio_language\taudio_vo_actor_group")
	val BUILDING_EFFECTS = Table("building_effects_junction", "building\teffect\teffect_scope\tvalue\tvalue_damaged\tvalue_ruined")
	val BUILDING_LEVELS = Table("building_levels", "level_name\tchain\tlevel\tcondition\tcreate_time\tcreate_cost\tupkeep_cost\tdemolition_cost\tzoc\tlower_happiness\tupper_happiness\trepression\tgdp\ttown_wealth_growth\tpop_change\tmaxpop_change\tcommodity\tgov_type_key\tcommodity_vol\tonly_in_capital\tmilitary_prestige\tnaval_prestige\teconomic_prestige\tenlightenment_prestige\tdestruction_terminator\tfaction_unique\treligion_requirement\tfirst_in_world_bundle\tresource_requirement\tworking_model\tunique_index\tcan_convert\tbuilding_instance_key\taudio_building_type\tshould_show_building_level_in_ui_for_technology\tis_new")
}