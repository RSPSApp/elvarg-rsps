package com.elvarg.game.content.itemteleports;

import com.elvarg.game.content.itemteleports.data.Crumble;
import com.elvarg.game.content.itemteleports.data.Limited;
import com.elvarg.game.content.itemteleports.data.Uncharged;
import com.elvarg.game.content.itemteleports.data.Unlimited;
import com.elvarg.game.content.itemteleports.handlers.Jewellery;
import com.elvarg.game.model.Location;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class JewelleryTeleports {

     private static final Jewellery gamesNecklace = new Jewellery(
            List.of(
                    Pair.of("Barbarian Outpost", new Location(2520, 3571, 0)),
                    Pair.of("Burthorpe Games Room", new Location(2898, 3553, 0)),
                    Pair.of("Tears of Guthix", new Location(2967, 4254, 0)),
                    Pair.of("Corporeal Beast", new Location(3245, 9500, 0)),
                    Pair.of("Wintertodt Camp", new Location(1631, 3940, 0))
            ), new Limited(List.of(3853, 3855, 3857, 3859, 3861, 3863, 3865, 3867), new Crumble())
    );


     private static final Jewellery ringOfDueling = new Jewellery(
            List.of(
                    Pair.of("Al Kharid Duel Arena", new Location(3315, 3235, 0)),
                    Pair.of("Castle Wars Arena", new Location(2440, 3090, 0)),
                    Pair.of("Ferox Enclave", new Location(3151, 3635, 0))
            ), new Limited(List.of(2552, 2554, 2556, 2558, 2560, 2562, 2564, 2566), new Crumble())
    );

     private static final Jewellery combatBracelet = new Jewellery(
            List.of(
                    Pair.of("Warriors' Guild", new Location(2865, 3546, 0)),
                    Pair.of("Champions' Guild", new Location(3192, 3368, 0)),
                    Pair.of("Edgeville Monastery", new Location(3052, 3490, 0)),
                    Pair.of("Ranging Guild", new Location(2653, 3439, 0))
            ),  new Limited(List.of(11972, 11974, 11118, 11120, 11122, 11124), new Uncharged(11126))
    );

     private static final Jewellery skillsNecklace = new Jewellery(
            List.of(
                    Pair.of("Fishing Guild", new Location(2611, 3390, 0)),
                    Pair.of("Mining Guild", new Location(3046, 9735, 0)),
                    Pair.of("Crafting Guild", new Location(2933, 3295, 0)),
                    Pair.of("Cooks' Guild", new Location(3144, 3438, 0)),
                    Pair.of("Woodcutting Guild", new Location(1662, 3505, 0)),
                    Pair.of("Farming Guild", new Location(1248, 3719, 0))
            ),  new Limited(List.of(11105, 11107, 11109, 11111), new Uncharged(11113))
    );

     private static final Jewellery amuletOfGlory = new Jewellery(
            List.of(
                    Pair.of("Edgeville", new Location(3087, 3496, 0)),
                    Pair.of("Karamja", new Location(2918, 3176, 0)),
                    Pair.of("Draynor Village", new Location(3105, 3251, 0)),
                    Pair.of("Al Kharid", new Location(3293, 3163, 0))
            ), new Limited(List.of(11978, 11976, 1712, 1710, 1708, 1706), new Uncharged(1704))
    );

     private static final Jewellery amuletOfEternalGlory = new Jewellery(
            List.of(
                    Pair.of("Edgeville", new Location(3087, 3496, 0)),
                    Pair.of("Karamja", new Location(2918, 3176, 0)),
                    Pair.of("Draynor Village", new Location(3105, 3251, 0)),
                    Pair.of("Al Kharid", new Location(3293, 3163, 0))
            ), new Unlimited(19707)
    );

     private static final Jewellery ringOfWealth = new Jewellery(
            List.of(
                    Pair.of("Miscellania", new Location(2534, 3862, 0)),
                    Pair.of("Grand Exchange", new Location(3163, 3478, 0)),
                    Pair.of("Falador Park", new Location(2995, 3375, 0)),
                    Pair.of("Dondakan", new Location(2824, 10168, 0))
            ), new Limited(List.of(11980, 11982, 1194, 11986, 11988, 2572), new Crumble())
    );

     private static final Jewellery slayerRing = new Jewellery(
            List.of(
                    Pair.of("Stronghold Slayer Cave", new Location(2431, 3422, 0)),
                    Pair.of("Slayer Tower", new Location(3421, 3537, 0)),
                    Pair.of("Fremennik Slayer Dungeon", new Location(2802, 9999, 0)),
                    Pair.of("Tarn's Lai", new Location(3185, 4601, 0)),
                    Pair.of("Dark Beast", new Location(2028, 4636, 0))
            ), new Limited(List.of(11866, 11867, 11868, 11869, 11870, 11871, 11872, 11873), new Crumble())
    );

     private static final Jewellery slayerRingEternal = new Jewellery(
            List.of(
                    Pair.of( "Stronghold Slayer Cave", new Location(2431, 3422, 0)),
                    Pair.of("Slayer Tower", new Location(3421, 3537, 0)),
                    Pair.of( "Fremennik Slayer Dungeon", new Location(2802, 9999, 0)),
                    Pair.of("Tarn's Lai", new Location(3185, 4601, 0)),
                    Pair.of("Dark Beast", new Location(2028, 4636, 0))
            ), new Unlimited(21268)
    );

     private static final Jewellery digsitePendant = new Jewellery(
            List.of(
                    Pair.of("Digsite", new Location(3341, 3445, 0)),
                    Pair.of("House on the Hill", new Location(3763, 3869, 0)),
                    Pair.of("Lithkren Vault", new Location(3549, 10456, 0))
            ), new Limited(List.of(11194, 11193, 11192, 11191, 11190), new Crumble())
    );

     private static final Jewellery burningAmulet = new Jewellery(
            List.of(
                    Pair.of( "Chaos Temple (Level 15 Wilderness)", new Location(3234, 3634, 0)),
                    Pair.of("Bandit Camp (Level 17 Wilderness)", new Location(3038, 3651, 0)),
                    Pair.of( "[Lava (Level 41 Wilderness)", new Location(3028, 3842, 0))
            ), new Limited(List.of(21166, 21169, 21171, 21173, 21175), new Crumble())
    );


    public static List<Jewellery> teleports = List.of(
            gamesNecklace, ringOfDueling,
            combatBracelet, skillsNecklace,
            amuletOfGlory, amuletOfEternalGlory,
            ringOfWealth, slayerRing,
            slayerRingEternal, digsitePendant,
            burningAmulet
    );

    public static boolean isJewellery(int itemId) {
        return teleports.stream().anyMatch(it -> it.itemIds.contains(itemId));
    }

    public static Jewellery getJewellery(int itemId) {
        return teleports.stream().filter(it -> it.itemIds.contains(itemId)).findFirst().get();
    }


}
