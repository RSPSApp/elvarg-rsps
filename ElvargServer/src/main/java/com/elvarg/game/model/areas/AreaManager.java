package com.elvarg.game.model.areas;

import com.elvarg.game.entity.impl.Mobile;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Boundary;
import com.elvarg.game.model.Location;
import com.elvarg.game.model.areas.impl.*;

import java.util.ArrayList;
import java.util.List;

public class AreaManager {

    public static List<Area> areas = new ArrayList<>();

    private static final Area safeZone = new ResourceArea();

    static {
        areas.add(new BarrowsArea());
        areas.add(new DuelArenaArea());
        areas.add(new WildernessArea());
        areas.add(new KingBlackDragonArea());
        areas.add(new GodwarsDungeonArea());
        areas.add(new CombatRingArea());
    }

    /**
     * Processes areas for the given character.
     *
     * @param c
     */
    public static void process(Mobile c) {
        Location position = c.getLocation();

        Area area = c.getArea();
        Area exitedArea = null;

        if (inside(position,safeZone)){
            c.setArea(null);
            if (area != null)
                area.leave(c, false);
            return;
        }

        if (area != null) {
            if (!inside(position, area)) {
                exitedArea = area;
                area = null;
            }
        }


        if (area == null) {
            area = get(position);
            if (area != null) {
                area.enter(c);
            }
        }

        // Handle processing..
        if (area != null) {
            area.process(c);
        }

        // Handle multiicon update..
        if (c.isPlayer()) {
            Player player = c.getAsPlayer();

            int multiIcon = 0;

            if (area != null) {
                multiIcon = area.isMulti(player) ? 1 : 0;
            }

            if (player.getMultiIcon() != multiIcon) {
                player.getPacketSender().sendMultiIcon(multiIcon);
            }
        }

        // Update area..
            c.setArea(area);

        if (exitedArea != null) {
            // Now that the player's area has been fully updated, call leave on the previous one
            exitedArea.leave(c, false);
        }
    }

    /**
     * Checks if a {@link Mobile} is in multi.
     *
     * @param player
     * @return
     */
    public static boolean inMulti(Mobile c) {
        if (c.getArea() != null) {
            return c.getArea().isMulti(c);
        }
        return false;
    }

    /**
     * Checks if a {@link Mobile} can attack another one.
     *
     * @param attacker
     * @param target
     * @return
     */
    public static boolean canAttack(Mobile attacker, Mobile target) {
        if (attacker.getPrivateArea() != target.getPrivateArea()) {
            return false;
        }
        
        if (attacker.getArea() != null) {
            return attacker.getArea().canAttack(attacker, target);
        }

        // Don't allow PvP by default
        if (attacker.isPlayer() && target.isPlayer()) {
            return false;
        }

        return true;
    }

    /**
     * Gets a {@link PublicArea} based on a given {@link Location}.
     *
     * @param player
     * @return
     */
    public static Area get(Location position) {
        for (Area area : areas) {
            if (inside(position, area)) {
                return area;
            }
        }
        return null;
    }

    /**
     * Checks if a position is inside of an area's boundaries.
     *
     * @param position
     * @return
     */
    public static boolean inside(Location position, Area area) {
        for (Boundary b : area.getBoundaries()) {
            if (b.inside(position)) {
                return true;
            }
        }
        return false;
    }
}
