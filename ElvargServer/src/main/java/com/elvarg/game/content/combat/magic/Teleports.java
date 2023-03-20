package com.elvarg.game.content.combat.magic;

import com.elvarg.game.GameConstants;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Item;
import com.elvarg.game.model.Location;
import com.elvarg.game.model.Skill;
import com.elvarg.game.model.rights.PlayerRights;
import com.elvarg.game.model.teleportation.TeleportHandler;
import com.elvarg.game.model.teleportation.TeleportType;
import com.elvarg.util.ItemIdentifiers;

import javax.swing.text.html.Option;
import java.util.Arrays;
import java.util.Optional;

import static com.elvarg.util.ItemIdentifiers.*;

/**
 * @author Ynneh | 20/03/2023 - 21:39
 * <https://github.com/drhenny>
 *
 *     * Quick design.. might REDO later with absract classes
 */
public enum Teleports {

    HOME(19210, 0, new Item[] {}, GameConstants.DEFAULT_LOCATION, 0, false),
    VARROCK(1164, 25, new Item[] {new Item(FIRE_RUNE, 1), new Item(AIR_RUNE, 3), new Item(LAW_RUNE, 1)}, new Location(3213, 3424, 0), 4,false),
    LUMBRIDGE(1167, 31, new Item[] {new Item(EARTH_RUNE, 1), new Item(AIR_RUNE, 3), new Item(LAW_RUNE, 1)}, new Location(3223, 3218, 0), 3,false),
    FALADOR(1170, 37, new Item[] {new Item(WATER_RUNE, 1), new Item(AIR_RUNE, 3), new Item(LAW_RUNE, 1)}, new Location(2965, 3379, 0), 4, false),
    HOUSE(19208, 40, new Item[] {new Item(EARTH_RUNE, 1), new Item(AIR_RUNE, 3), new Item(LAW_RUNE, 1)}, null, 2, false),
    CAMELOT(1174, 45, new Item[] {new Item(AIR_RUNE, 5), new Item(LAW_RUNE, 1)}, new Location(2757, 3477, 0), 3, false),
    ARDONGUE(1540, 51, new Item[] {new Item(WATER_RUNE, 2), new Item(LAW_RUNE, 2)}, new Location(2662, 3306, 0), 4, false),
    WATCHTOWER(1541, 58, new Item[] {new Item(EARTH_RUNE, 2), new Item(LAW_RUNE, 2)}, new Location(2549, 3112, 2), 0, false),
    TROLLHEIM(7455, 61, new Item[] {new Item(FIRE_RUNE, 2), new Item(LAW_RUNE, 2)}, new Location(2892, 3680, 0), 2, false),
    APEATOLL(18470, 64, new Item[] {new Item(FIRE_RUNE, 2), new Item(WATER_RUNE, 2), new Item(LAW_RUNE, 2), new Item(BANANA, 1)}, new Location(2798, 2798, 1), 1, false);

    public int widgetId;
    public int levelRequired;
    public Item[] runes;
    public Location destination;
    public boolean dangerous;
    public int randomTileRadius;

    Teleports(int widgetId, int levelRequired, Item[] runes, Location destination, int randomTileRadius, boolean dangerous) {
        this.widgetId = widgetId;
        this.levelRequired = levelRequired;
        this.runes = runes;
        this.destination = destination;
        this.randomTileRadius = randomTileRadius;
        this.dangerous = dangerous;
    }

    /**
     * @param player
     * @param widgetId
     * @return
     */
    public static boolean handleTeleport(Player player, int widgetId) {

        Optional<Teleports> data = getData(widgetId);

        if (!data.isPresent())
            return false;

        Teleports tele = data.get();

        final int levelRequired = tele.levelRequired;

        if (player.getSkillManager().getCurrentLevel(Skill.MAGIC) < levelRequired) {
            player.getPacketSender().sendMessage("You need a Magic Level of "+levelRequired+" to use this spell.");
            return false;
        }

        Optional<Item[]> runes = Optional.of(tele.runes);

        if (runes.isPresent()) {

            Item[] items = PlayerMagicStaff.suppressRunes(player, runes.get());

            if (!player.getInventory().containsAll(items)) {
                player.getPacketSender().sendMessage("You do not have the required items to cast this spell.");
                return false;
            }

            for (Item it : Arrays.asList(items)) {
                if (it != null)
                    player.getInventory().delete(it);
            }
            TeleportHandler.teleport(player, Location.randomTile(tele.destination, tele.randomTileRadius), TeleportType.NORMAL, false);
            return true;
        }
        return false;
    }

    private static Optional<Teleports> getData(int widgetId) {
        return Arrays.stream(values()).filter(w -> w.widgetId == widgetId).findFirst();
    }
}
