package com.elvarg.game.content.itemteleports;


import com.elvarg.game.content.itemteleports.handlers.Tablet;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Location;
import com.elvarg.util.ItemIdentifiers;

import java.util.List;

public class TabletTeleports {


    private static final Tablet varrockTeleport = new Tablet(ItemIdentifiers.VARROCK_TELEPORT, new Location(3213, 3424, 0));
    private static final Tablet faladorTeleport = new Tablet(ItemIdentifiers.FALADOR_TELEPORT, new Location(2964, 3378, 0));
    private static final Tablet lumbridgeTeleport = new Tablet(ItemIdentifiers.LUMBRIDGE_TELEPORT, new Location(3221, 3218, 0));
    private static final Tablet camelotTeleport = new Tablet(ItemIdentifiers.CAMELOT_TELEPORT, new Location(2757, 3478, 0));
    private static final Tablet ardougneTeleport = new Tablet(ItemIdentifiers.ARDOUGNE_TELEPORT, new Location(2662, 3305, 0));
    private static final Tablet watchtowerTeleport = new Tablet(ItemIdentifiers.WATCHTOWER_TELEPORT, new Location(2553, 3113, 0));
    private static final Tablet rimmingtonTeleport = new Tablet(ItemIdentifiers.RIMMINGTON_TELEPORT, new Location(2953, 3222, 0));
    private static final Tablet taverleyTeleport = new Tablet(ItemIdentifiers.TAVERLEY_TELEPORT, new Location(2894, 3465, 0));
    private static final Tablet pollnivneachTeleport = new Tablet(ItemIdentifiers.POLLNIVNEACH_TELEPORT, new Location(3340, 2998, 0));
    private static final Tablet rellekkaTeleport = new Tablet(ItemIdentifiers.RELLEKKA_TELEPORT, new Location(2670, 3631, 0));
    private static final Tablet brimhavenTeleport = new Tablet(ItemIdentifiers.BRIMHAVEN_TELEPORT, new Location(2759, 3178, 0));
    private static final Tablet yanilleTeleport = new Tablet(ItemIdentifiers.YANILLE_TELEPORT, new Location(2544, 3094, 0));
    private static final Tablet trollheimTeleport = new Tablet(ItemIdentifiers.TROLLHEIM_TELEPORT, new Location(2891, 3677, 0));
    private static final Tablet hosidiusTeleport = new Tablet(ItemIdentifiers.KOUREND_TELEPORT, new Location(1744, 3516, 0));

    private static final Tablet moonclanTeleport = new Tablet(ItemIdentifiers.MOONCLAN_TELEPORT, new Location(2114, 3914, 0));
    private static final Tablet ouraniaTeleport = new Tablet(ItemIdentifiers.OURANIA_TELEPORT, new Location(2498, 3247, 0));
    private static final Tablet waterbirthTeleport = new Tablet(ItemIdentifiers.WATERBIRTH_TELEPORT, new Location(2545, 3757, 0));
    private static final Tablet barbarianTeleport = new Tablet(ItemIdentifiers.BARBARIAN_TELEPORT, new Location(2544, 3568, 0));
    private static final Tablet khazardTeleport = new Tablet(ItemIdentifiers.KHAZARD_TELEPORT, new Location(2636, 3166, 0));
    private static final Tablet fishingGuildTeleport = new Tablet(ItemIdentifiers.FISHING_GUILD_TELEPORT, new Location(2610, 3391, 0));
    private static final Tablet catherbyTeleport = new Tablet(ItemIdentifiers.CATHERBY_TELEPORT, new Location(2800, 3450, 0));
    private static final Tablet icePlateauTeleport = new Tablet(ItemIdentifiers.ICE_PLATEAU_TELEPORT, new Location(2974, 3938, 0));

    public static List<Tablet> teleports = List.of(
            varrockTeleport, faladorTeleport,
            lumbridgeTeleport, camelotTeleport,
            ardougneTeleport, watchtowerTeleport,
            rimmingtonTeleport, taverleyTeleport,
            pollnivneachTeleport, rellekkaTeleport,
            brimhavenTeleport, yanilleTeleport,
            trollheimTeleport, hosidiusTeleport,
            moonclanTeleport, ouraniaTeleport,
            waterbirthTeleport, barbarianTeleport,
            khazardTeleport, fishingGuildTeleport,
            catherbyTeleport, icePlateauTeleport
    );


    public static boolean isTab(int itemId) {
        return teleports.stream().anyMatch(it -> it.getItemId() == itemId);
    }

    public static Tablet getTab(int itemId) {
        return teleports.stream().filter(it -> it.getItemId() == itemId).findFirst().get();
    }
    
    public static boolean handleOptions(Player player, int itemID) {
        if (isTab(itemID)) {
            getTab(itemID).handle(player,itemID,player.getInventory());
            return true;
        }
        return false;
    }

}
