package com.elvarg.game.definition;

import static com.elvarg.util.ItemIdentifiers.*;

import com.elvarg.game.content.combat.WeaponInterfaces.WeaponInterface;

public class WeaponInterfaces {

	public static WeaponInterface get(String type, int id) {
		switch (type) {
		case "scythe":
			return WeaponInterface.SCYTHE;
		case "gun":
			return WeaponInterface.UNARMED;
		case "crossbow":
			switch (id) {
			case KARILS_CROSSBOW:
			case KARILS_CROSSBOW_100:
			case KARILS_CROSSBOW_75:
			case KARILS_CROSSBOW_50:
			case KARILS_CROSSBOW_25:
				return WeaponInterface.KARILS_CROSSBOW;
			case LIGHT_BALLISTA:
			case HEAVY_BALLISTA:
				return WeaponInterface.BALLISTA;
			default:
				return WeaponInterface.CROSSBOW;//armadyl cbow is 8 square range rather than 7.
			}
		case "slash_sword":
			switch (id) {
			case BRONZE_LONGSWORD:
			case IRON_LONGSWORD:
			case STEEL_LONGSWORD:
			case BLACK_LONGSWORD:
			case MITHRIL_LONGSWORD:
			case ADAMANT_LONGSWORD:
			case RUNE_LONGSWORD:
			case DRAGON_LONGSWORD:
			case WHITE_LONGSWORD:
			case BLURITE_SWORD:
			case CASTLEWARS_SWORD_RED:
			case CASTLEWARS_SWORD_WHITE:
			case CASTLEWARS_SWORD_GOLD:
			case EXCALIBUR:
			case SILVERLIGHT:
			case IRON_SICKLE:
			case SILVER_SICKLE:
			case SILVER_SICKLE_B_:
			case EMERALD_SICKLE_B_:
			case RUBY_SICKLE_B_:
			case ENCHANTED_RUBY_SICKLE_B_:
			case BLISTERWOOD_SICKLE:
			case SILVERLIGHT_2:
			case DARKLIGHT:
				return WeaponInterface.LONGSWORD;
			default:
				return WeaponInterface.SCIMITAR;
			}
		case "pickaxe":
			return WeaponInterface.PICKAXE;
		case "spiked":
			switch (id) {
			case VERACS_FLAIL:
			case VERACS_FLAIL_100:
			case VERACS_FLAIL_75:
			case VERACS_FLAIL_50:
			case VERACS_FLAIL_25:
				return WeaponInterface.VERACS_FLAIL;
			case BARRELCHEST_ANCHOR:
				return WeaponInterface.WARHAMMER;
			default:
				return WeaponInterface.MACE;
			}
		case "bow"://a lot of these are fucked
			switch (id) {
			//TODO: crystal bow+composite+bowfa bow have speed 5 and distance 10.
			//longbow is distance 10 with speed 6
			//shortbow is speed 4 distance 7.
			//seercull is speed 5 and 8 distance
			//dbow is 9 speed 10 distance
			//craws, 3rd age, webweaver  is 4 and 9
			//ventator is 5 and 6
			//twisted is 6 and 10 range
			
			
			case OGRE_BOW://10 range 8 tick
			case COMP_OGRE_BOW://5 range 5 ticks
				return WeaponInterface.UNARMED;
			case DARK_BOW:
				return WeaponInterface.DARK_BOW;
				
			case SIGNED_OAK_BOW:
			case LONGBOW:
			case OAK_LONGBOW:
			case WILLOW_LONGBOW:
			case MAPLE_LONGBOW:
			case YEW_LONGBOW:
			case MAGIC_LONGBOW:
			case AVERNIC_DEFENDER:
			case COIN_POUCH_14:
			case COIN_POUCH_17:
			case GILDED_SPADE_3:
			case DIVINE_MAGIC_POTION_4_:
			case DIVINE_MAGIC_POTION_4_2:
			case DIVINE_MAGIC_POTION_4_3:
			case ARDOUGNE_KNIGHT_TABARD:
			case BLUE_LIQUID:
			case GREEN_POWDER:
			case BURNT_FISH_12:
			case GRYM_LEAF_2:
			case CRYSTAL_SHARDS_3:
			case PROPELLER_HAT_2:
			case THICK_DYE:
			case S_T_A_S_H_BLUEPRINT:
			case PLANK_SACK_3:
			case STEAK_SANDWICH:
			case IMCANDO_HAMMER_BROKEN_:
			case BARRONITE_HEAD:
			case BARRONITE_HANDLE:
			case BARRONITE_GUARD:
			case BARRONITE_MACE:
				return WeaponInterface.LONGBOW;
			case SHORTBOW:
			case OAK_SHORTBOW:
			case WILLOW_SHORTBOW:
			case MAPLE_SHORTBOW:
			case YEW_SHORTBOW:
			case MAGIC_SHORTBOW:
			case NEW_CRYSTAL_BOW:
			case CRYSTAL_BOW_FULL:
			case CRYSTAL_BOW_9_10:
			case CRYSTAL_BOW_8_10:
			case CRYSTAL_BOW_7_10:
			case CRYSTAL_BOW_6_10:
			case CRYSTAL_BOW_5_10:
			case CRYSTAL_BOW_4_10:
			case CRYSTAL_BOW_3_10:
			case CRYSTAL_BOW_2_10:
			case CRYSTAL_BOW_1_10:
			case SEERCULL:
				return WeaponInterface.SHORTBOW;
				default:
	                return WeaponInterface.SHORTBOW;
			}
		case "salamander":
			return WeaponInterface.UNARMED;//TODO salamanders are missing
		case "powered_staff":
			return WeaponInterface.STAFF;
		case "axe":
			switch (id) {
			case DHAROKS_GREATAXE:
			case DHAROKS_GREATAXE_100:
			case DHAROKS_GREATAXE_75:
			case DHAROKS_GREATAXE_50:
			case DHAROKS_GREATAXE_25:
				return WeaponInterface.GREATAXE;
			default:
				return WeaponInterface.BATTLEAXE;
			}
		case "whip":
			return WeaponInterface.WHIP;
		case "bulwark":
			return WeaponInterface.UNARMED;//TODO bulwark interface is missing
		case "spear":		
			return WeaponInterface.SPEAR;
		case "bladed_staff":
			return WeaponInterface.STAFF;
		case "partisan":
			return WeaponInterface.SWORD;
		case "stab_sword":
			switch (id) {
			case KITCHEN_KNIFE://was thrown knife
			case WOLFBANE://was unarmed
			case DARK_DAGGER:
			case GLOWING_DAGGER:
			case IRON_DAGGER:
			case BRONZE_DAGGER:
			case STEEL_DAGGER:
			case MITHRIL_DAGGER:
			case ADAMANT_DAGGER:
			case RUNE_DAGGER:
			case BLACK_DAGGER:
			case IRON_DAGGER_P_:
			case BRONZE_DAGGER_P_:
			case STEEL_DAGGER_P_:
			case MITHRIL_DAGGER_P_:
			case ADAMANT_DAGGER_P_:
			case RUNE_DAGGER_P_:
			case BLACK_DAGGER_P_:
			case IRON_DAGGER_P_PLUS_:
			case BRONZE_DAGGER_P_PLUS_:
			case STEEL_DAGGER_P_PLUS_:
			case MITHRIL_DAGGER_P_PLUS_:
			case ADAMANT_DAGGER_P_PLUS_:
			case RUNE_DAGGER_P_PLUS_:
			case BLACK_DAGGER_P_PLUS_:
			case IRON_DAGGER_P_PLUS_PLUS_:
			case BRONZE_DAGGER_P_PLUS_PLUS_:
			case STEEL_DAGGER_P_PLUS_PLUS_:
			case MITHRIL_DAGGER_P_PLUS_PLUS_:
			case ADAMANT_DAGGER_P_PLUS_PLUS_:
			case RUNE_DAGGER_P_PLUS_PLUS_:
			case BLACK_DAGGER_P_PLUS_PLUS_:
			case TOKTZ_XIL_AK://?
			case WHITE_DAGGER:
			case WHITE_DAGGER_P_:
			case WHITE_DAGGER_P_PLUS_:
			case WHITE_DAGGER_P_PLUS_PLUS_:
				return WeaponInterface.DAGGER;
			case DRAGON_DAGGER:
			case DRAGON_DAGGER_P_:
			case DRAGON_DAGGER_P_PLUS_:
			case DRAGON_DAGGER_P_PLUS_PLUS_:
				return WeaponInterface.DRAGON_DAGGER;
			case GHRAZI_RAPIER://22483 is noted, 22484 is dup. 
			case GHRAZI_RAPIER_4://23628 LMS version.23629 is noted
			case HOLY_GHRAZI_RAPIER://25735 is noted
				return WeaponInterface.GHRAZI_RAPIER;
			case ABYSSAL_DAGGER_P_PLUS_PLUS_:
				return WeaponInterface.ABYSSAL_DAGGER;
			default:
				return WeaponInterface.SWORD;
			}
		case "2h_sword":
			switch (id) {
			case SARADOMIN_SWORD://TODO charged variants of SS.
				return WeaponInterface.SARADOMIN_SWORD;
			case ARMADYL_GODSWORD:
			case BANDOS_GODSWORD:
			case SARADOMIN_GODSWORD:
			case ZAMORAK_GODSWORD:
			case ANCIENT_GODSWORD:
				return WeaponInterface.GODSWORD;
			default:
				return WeaponInterface.TWO_HANDED_SWORD;
			}
		case "chinchompas":
			return WeaponInterface.CHIN;//TODO interface is missing
		case "polearm":
			return WeaponInterface.HALBERD;
		case "blunt":
			switch (id) {
			case TZHAAR_KET_OM:
				return WeaponInterface.MAUL;
			case ELDER_MAUL_3:
				return WeaponInterface.ELDER_MAUL;
			case GRANITE_MAUL:
			case GRANITE_MAUL_3:
				return WeaponInterface.GRANITE_MAUL;
			case DRAMEN_STAFF:
				return WeaponInterface.STAFF;//TODO changed to WH in 2019.
			default:
				return WeaponInterface.WARHAMMER;
			}
		case "thrown":
			switch (id) {
			case BRONZE_THROWNAXE:
			case IRON_THROWNAXE:
			case STEEL_THROWNAXE:
			case MITHRIL_THROWNAXE:
			case ADAMANT_THROWNAXE:
			case RUNE_THROWNAXE:
				return WeaponInterface.THROWNAXE;
			case IRON_KNIFE:
			case BRONZE_KNIFE:
			case STEEL_KNIFE:
			case MITHRIL_KNIFE:
			case ADAMANT_KNIFE:
			case RUNE_KNIFE:
			case BLACK_KNIFE:
			case BRONZE_KNIFE_P_:
			case IRON_KNIFE_P_:
			case STEEL_KNIFE_P_:
			case MITHRIL_KNIFE_P_:
			case BLACK_KNIFE_P_:
			case ADAMANT_KNIFE_P_:
			case RUNE_KNIFE_P_:
			case BRONZE_KNIFE_P_PLUS_:
			case IRON_KNIFE_P_PLUS_:
			case STEEL_KNIFE_P_PLUS_:
			case MITHRIL_KNIFE_P_PLUS_:
			case BLACK_KNIFE_P_PLUS_:
			case ADAMANT_KNIFE_P_PLUS_:
			case RUNE_KNIFE_P_PLUS_:
			case BRONZE_KNIFE_P_PLUS_PLUS_:
			case IRON_KNIFE_P_PLUS_PLUS_:
			case STEEL_KNIFE_P_PLUS_PLUS_:
			case MITHRIL_KNIFE_P_PLUS_PLUS_:
			case BLACK_KNIFE_P_PLUS_PLUS_:
			case ADAMANT_KNIFE_P_PLUS_PLUS_:
			case RUNE_KNIFE_P_PLUS_PLUS_:
				return WeaponInterface.KNIFE;
			case TOKTZ_XIL_UL:
				return WeaponInterface.OBBY_RINGS;
			case TOXIC_BLOWPIPE:
				return WeaponInterface.BLOWPIPE;
			default:
				return WeaponInterface.DART;
			}
		case "banner":
			return WeaponInterface.UNARMED;
		case "staff":
			switch (id) {
			case AHRIMS_STAFF:
			case AHRIMS_STAFF_100:
			case AHRIMS_STAFF_75:
			case AHRIMS_STAFF_50:
			case AHRIMS_STAFF_25:
				return WeaponInterface.STAFF;//TODO add 6 tick staff or remove attack speed
			case SLAYERS_STAFF://4 speed. Check if this causes issues?
			case BEGINNER_WAND://4 speed. Check if this causes issues.
			case APPRENTICE_WAND:
			case TEACHER_WAND:
			case MASTER_WAND:
			case KODAI_WAND:
			case ANCIENT_STAFF:
				return WeaponInterface.ANCIENT_STAFF;
			default:
				return WeaponInterface.STAFF;
			}
		case "polestaff":
			return WeaponInterface.UNARMED;
		case "idk"://these should be null or unequippable items
			switch (id) {
			case BOW_SWORD:
			case ZAROS_MJOLNIR:
				return WeaponInterface.UNARMED;
			case POISONED_DAGGER_P_:
			case POISON_DAGGER_P_PLUS_:
			case POISON_DAGGER_P_PLUS_PLUS_:
				return WeaponInterface.DAGGER;
			case RUNE_LONGSWORD_3:
				return WeaponInterface.LONGSWORD;
			case ABYSSAL_WHIP_3:
				return WeaponInterface.WHIP;
			}
		case "claw":
			return WeaponInterface.CLAWS;
		case "unarmed":
			return WeaponInterface.UNARMED;
		case "bludgeon":
			return WeaponInterface.ABYSSAL_BLUDGEON;
		}
		return WeaponInterface.UNARMED;
	}

}
