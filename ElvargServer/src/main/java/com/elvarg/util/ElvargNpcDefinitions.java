package com.elvarg.util;

import static com.elvarg.util.NpcIdentifiers.*;

/**
 * Class representing NPC Definition values for Elvarg NPCs.
 * <p>
 * This is WIP and will be used to dump an updated definition file for npcs.
 * <br>
 * @author Advocatus | https://www.rune-server.ee/members/119929-advocatus/
 */
public class ElvargNpcDefinitions {

	/**
	 * In current data this is zero if non attackable and 7 if they are with some exceptions. 
	 */
	public static int combatFollowDistance(int id, boolean attackable) {
		switch(id) {
		case EVIL_CREATURE: // Evil creature id: 1241
		case EVIL_CREATURE_2: // Evil creature id: 1244
		case EVIL_CREATURE_3: // Evil creature id: 1247
		case EVIL_CREATURE_4: // Evil creature id: 1250
		case EVIL_CREATURE_5: // Evil creature id: 1253
		case EVIL_CREATURE_6: // Evil creature id: 1256
		case THING_UNDER_THE_BED: // Thing under the bed id: 2115
		case 2668: // Combat dummy id: 2668
		case VOID_KNIGHT_5: // Void Knight id: 2950
		case VOID_KNIGHT_8: // Void Knight id: 2953
		case RANGER: // Ranger id: 4961
		case BARRICADE: // Barricade id: 5722
		case BARRICADE_2: // Barricade id: 5723
		case BARRICADE_3: // Barricade id: 5724
		case BARRICADE_4: // Barricade id: 5725
		case RESPIRATORY_SYSTEM: // Respiratory system id: 5914
		case REANIMATED_OGRE: // Reanimated ogre id: 7028
		case AWAKENED_ALTAR: // Awakened Altar id: 7288
		case AWAKENED_ALTAR_2: // Awakened Altar id: 7290
		case AWAKENED_ALTAR_3: // Awakened Altar id: 7292
		case AWAKENED_ALTAR_4: // Awakened Altar id: 7294
		case 7413: // Undead Combat dummy id: 7413
		case 7533: // Abyssal portal id: 7533
		case 7568: // Glowing crystal id: 7568
		case 7569: // Guardian id: 7569
		case 7570: // Guardian id: 7570
			return 0;

		case VETION: // Vet'ion id: 6611
		case VETION_REBORN: // Vet'ion reborn id: 6612
			return 10;

		case AHRIM_THE_BLIGHTED: // Ahrim the Blighted id: 1672
		case DHAROK_THE_WRETCHED: // Dharok the Wretched id: 1673
		case GUTHAN_THE_INFESTED: // Guthan the Infested id: 1674
		case KARIL_THE_TAINTED: // Karil the Tainted id: 1675
		case TORAG_THE_CORRUPTED: // Torag the Corrupted id: 1676
		case VERAC_THE_DEFILED: // Verac the Defiled id: 1677
		case ELDER_CHAOS_DRUID: // Elder Chaos druid id: 6607
			return 14;

		case KING_BLACK_DRAGON: // King Black Dragon id: 239
			return 15;

		case TZTOK_JAD: // TzTok-Jad id: 3127
			return 80;
		}
		return attackable ? 7 : 0;
	}

	/**
	 * Most NPCs respawn after 25 ticks if attackable and 0 if now. Exceptions are below.
	 */
	public static int respawnTime(int id, boolean attackable) {
		switch(id) {			
		case AHRIM_THE_BLIGHTED: // Ahrim the Blighted id: 1672
		case DHAROK_THE_WRETCHED: // Dharok the Wretched id: 1673
		case GUTHAN_THE_INFESTED: // Guthan the Infested id: 1674
		case KARIL_THE_TAINTED: // Karil the Tainted id: 1675
		case TORAG_THE_CORRUPTED: // Torag the Corrupted id: 1676
		case VERAC_THE_DEFILED: // Verac the Defiled id: 1677
		case SPLATTER: // Splatter id: 1689
		case SPLATTER_2: // Splatter id: 1690
		case SPLATTER_3: // Splatter id: 1691
		case SPLATTER_4: // Splatter id: 1692
		case SPLATTER_5: // Splatter id: 1693
		case SHIFTER: // Shifter id: 1694
		case SHIFTER_3: // Shifter id: 1696
		case SHIFTER_5: // Shifter id: 1698
		case SHIFTER_7: // Shifter id: 1700
		case SHIFTER_9: // Shifter id: 1702
		case PORTAL_9: // Portal id: 1747
		case PORTAL_10: // Portal id: 1748
		case PORTAL_11: // Portal id: 1749
		case PORTAL_12: // Portal id: 1750
		case TZTOK_JAD: // TzTok-Jad id: 3127
		case BARRICADE: // Barricade id: 5722
		case BARRICADE_2: // Barricade id: 5723
		case SKELETON_HELLHOUND_3: // Skeleton Hellhound id: 6613
		case GREATER_SKELETON_HELLHOUND: // Greater Skeleton Hellhound id: 6614
			return -1;

		case ELDER_CHAOS_DRUID: // Elder Chaos druid id: 6607
			return 20;

		case VENENATIS: // Venenatis id: 6504
		case KING_BLACK_DRAGON: // King Black Dragon id: 239
		case CRAZY_ARCHAEOLOGIST: // Crazy archaeologist id: 6618
			return 30;

		case CALLISTO_2: // Callisto id: 6609
		case VETION: // Vet'ion id: 6611
		case VETION_REBORN: // Vet'ion reborn id: 6612
		case CHAOS_FANATIC: // Chaos Fanatic id: 6619
			return 35;

		case CHAOS_ELEMENTAL: // Chaos Elemental id: 2054
			return 45;

		case ROCK_CRAB: // Rock Crab id: 100
		case ROCKS: // Rocks id: 101
		case ROCK_CRAB_2: // Rock Crab id: 102
		case ROCKS_2: // Rocks id: 103
			return 50;
		}
		return attackable ? 25 : 0;
	}

	/**
	 * Whether the npc builds aggression tolerance as player stays in area. This is always true in existing defs.
	 */
	public static boolean aggressionTolerance(int id) {
		return true;
	}


	/**
	 * Whether the NPC retreats back when being farcasted. Usually true with some exceptions.
	 */
	public static boolean retreats(int id, boolean attackable) {
		switch(id) {
		case KING_BLACK_DRAGON: // King Black Dragon id: 239
		case EVIL_CREATURE: // Evil creature id: 1241
		case EVIL_CREATURE_2: // Evil creature id: 1244
		case EVIL_CREATURE_3: // Evil creature id: 1247
		case EVIL_CREATURE_4: // Evil creature id: 1250
		case EVIL_CREATURE_5: // Evil creature id: 1253
		case EVIL_CREATURE_6: // Evil creature id: 1256
		case AHRIM_THE_BLIGHTED: // Ahrim the Blighted id: 1672
		case DHAROK_THE_WRETCHED: // Dharok the Wretched id: 1673
		case GUTHAN_THE_INFESTED: // Guthan the Infested id: 1674
		case KARIL_THE_TAINTED: // Karil the Tainted id: 1675
		case TORAG_THE_CORRUPTED: // Torag the Corrupted id: 1676
		case VERAC_THE_DEFILED: // Verac the Defiled id: 1677
		case SPLATTER: // Splatter id: 1689
		case SPLATTER_2: // Splatter id: 1690
		case SPLATTER_3: // Splatter id: 1691
		case SPLATTER_4: // Splatter id: 1692
		case SPLATTER_5: // Splatter id: 1693
		case SHIFTER: // Shifter id: 1694
		case SHIFTER_3: // Shifter id: 1696
		case SHIFTER_5: // Shifter id: 1698
		case SHIFTER_7: // Shifter id: 1700
		case SHIFTER_9: // Shifter id: 1702
		case RAVAGER: // Ravager id: 1704
		case RAVAGER_2: // Ravager id: 1705
		case RAVAGER_3: // Ravager id: 1706
		case RAVAGER_4: // Ravager id: 1707
		case RAVAGER_5: // Ravager id: 1708
		case SPINNER: // Spinner id: 1709
		case SPINNER_2: // Spinner id: 1710
		case SPINNER_3: // Spinner id: 1711
		case SPINNER_4: // Spinner id: 1712
		case SPINNER_5: // Spinner id: 1713
		case TORCHER: // Torcher id: 1714
		case TORCHER_3: // Torcher id: 1716
		case TORCHER_5: // Torcher id: 1718
		case TORCHER_7: // Torcher id: 1720
		case TORCHER_9: // Torcher id: 1722
		case TORCHER_10: // Torcher id: 1723
		case BRAWLER: // Brawler id: 1734
		case BRAWLER_2: // Brawler id: 1735
		case BRAWLER_3: // Brawler id: 1736
		case BRAWLER_4: // Brawler id: 1737
		case BRAWLER_5: // Brawler id: 1738
		case THING_UNDER_THE_BED: // Thing under the bed id: 2115
		case 2668: // Combat dummy id: 2668
		case VOID_KNIGHT_5: // Void Knight id: 2950
		case VOID_KNIGHT_8: // Void Knight id: 2953
		case TZTOK_JAD: // TzTok-Jad id: 3127
		case RANGER: // Ranger id: 4961
		case BARRICADE: // Barricade id: 5722
		case BARRICADE_2: // Barricade id: 5723
		case BARRICADE_3: // Barricade id: 5724
		case BARRICADE_4: // Barricade id: 5725
		case RESPIRATORY_SYSTEM: // Respiratory system id: 5914
		case REANIMATED_OGRE: // Reanimated ogre id: 7028
		case AWAKENED_ALTAR: // Awakened Altar id: 7288
		case AWAKENED_ALTAR_2: // Awakened Altar id: 7290
		case AWAKENED_ALTAR_3: // Awakened Altar id: 7292
		case AWAKENED_ALTAR_4: // Awakened Altar id: 7294
		case 7413: // Undead Combat dummy id: 7413
		case 7533: // Abyssal portal id: 7533
		case 7568: // Glowing crystal id: 7568
		case 7569: // Guardian id: 7569
		case 7570: // Guardian id: 7570
			return false;
		}
		return attackable;
	}

	/**
	 * Whether the NPC fights back.
	 */
	public static boolean fightsBack(int id, boolean attackable) {
		switch (id) {			
		case SPINNER: // Spinner id: 1709
		case SPINNER_2: // Spinner id: 1710
		case SPINNER_3: // Spinner id: 1711
		case SPINNER_4: // Spinner id: 1712
		case SPINNER_5: // Spinner id: 1713
		case PORTAL_9: // Portal id: 1747
		case PORTAL_10: // Portal id: 1748
		case PORTAL_11: // Portal id: 1749
		case PORTAL_12: // Portal id: 1750
		case VOID_KNIGHT_5: // Void Knight id: 2950
		case VOID_KNIGHT_8: // Void Knight id: 2953
		case BARRICADE: // Barricade id: 5722
		case BARRICADE_2: // Barricade id: 5723
			return false;
		}
		return attackable;
	}

	/**
	 * Unused value but this is how the original elvarg defs had the data so keeping this here if its needed.
	 */
	@Deprecated
	public static int walkRadius(int id) {
		switch(id) {
		case ELDER_CHAOS_DRUID: // Elder Chaos druid id: 6607
			return 2;

		case CHAOS_FANATIC: // Chaos Fanatic id: 6619
			return 5;

		case SPLATTER: // Splatter id: 1689
		case SPLATTER_2: // Splatter id: 1690
		case SPLATTER_3: // Splatter id: 1691
		case SPLATTER_4: // Splatter id: 1692
		case SPLATTER_5: // Splatter id: 1693
		case SHIFTER: // Shifter id: 1694
		case SHIFTER_3: // Shifter id: 1696
		case SHIFTER_5: // Shifter id: 1698
		case SHIFTER_7: // Shifter id: 1700
		case SHIFTER_9: // Shifter id: 1702
		case CHAOS_ELEMENTAL: // Chaos Elemental id: 2054
			return 7;

		case KING_BLACK_DRAGON: // King Black Dragon id: 239
		case SPINNER: // Spinner id: 1709
		case SPINNER_2: // Spinner id: 1710
		case SPINNER_3: // Spinner id: 1711
		case SPINNER_4: // Spinner id: 1712
		case SPINNER_5: // Spinner id: 1713
		case TORCHER: // Torcher id: 1714
		case TORCHER_3: // Torcher id: 1716
		case TORCHER_5: // Torcher id: 1718
		case TORCHER_7: // Torcher id: 1720
		case TORCHER_9: // Torcher id: 1722
		case TORCHER_10: // Torcher id: 1723
		case BRAWLER: // Brawler id: 1734
		case BRAWLER_2: // Brawler id: 1735
		case BRAWLER_3: // Brawler id: 1736
		case BRAWLER_4: // Brawler id: 1737
		case BRAWLER_5: // Brawler id: 1738
			return 10;

		case RAVAGER: // Ravager id: 1704
		case RAVAGER_2: // Ravager id: 1705
		case RAVAGER_3: // Ravager id: 1706
		case RAVAGER_4: // Ravager id: 1707
		case RAVAGER_5: // Ravager id: 1708
			return 15;

		case DEFILER: // Defiler id: 1724
		case DEFILER_3: // Defiler id: 1726
		case DEFILER_5: // Defiler id: 1728
		case DEFILER_6: // Defiler id: 1729
		case DEFILER_7: // Defiler id: 1730
		case DEFILER_9: // Defiler id: 1732
			return 20;
		}
		return 0;
	}
}
