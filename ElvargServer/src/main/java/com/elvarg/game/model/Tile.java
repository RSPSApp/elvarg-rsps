package com.elvarg.game.model;

import com.elvarg.game.collision.Region;
import com.elvarg.game.entity.impl.Mobile;

/**
 * @author Ynneh | 29/04/2023 - 10:56 am
 * <https://github.com/drhenny>
 *
 * Only used for TileStacking NPCs
 */
public class Tile {

    public final Region region;
    public int playerCount, npcCount;
    public Tile(Region region) {
        this.region = region;
    }

    public static void occupy(Mobile entity) {
        if (entity.occupyingTiles) {
            fill(entity, entity.getLocation(), -1);
            entity.occupyingTiles = false;
        }
        if (entity.npc != null && !entity.npc.getDefinition().canTileStack())
            return;
        fill(entity, entity.getLocation(), 1);
        entity.occupyingTiles = true;
    }

    public static boolean isOccupied(Mobile entity, int stepX, int stepY) {
        Location position = entity.getLocation();
        int size = entity.size();
        int absX = position.getX();
        int absY = position.getY();
        int z = position.getZ();
        int eastMostX = absX + (size - 1);
        int northMostY = absY + (size - 1);
        for(int x = stepX; x < (stepX + size); x++) {
            for(int y = stepY; y < (stepY + size); y++) {
                if(x >= absX && x <= eastMostX && y >= absY && y <= northMostY) {
                    /* stepping within itself, allow it */
                    continue;
                }
                Tile tile = Tile.get(x, y, z, true);
                if(tile.playerCount > 0 || tile.npcCount > 0)
                    return true;
            }
        }
        return false;
    }

    private static void fill(Mobile entity, Location pos, int increment) {
        int size = entity.size();
        int absX = pos.getX();
        int absY = pos.getY();
        int z = pos.getZ();
        for(int x = absX; x < (absX + size); x++) {
            for(int y = absY; y < (absY + size); y++) {
                Tile tile = Tile.get(x, y, z, true);
                if(entity.player != null)
                    tile.playerCount += increment;
                else
                    tile.npcCount += increment;
            }
        }
    }

    public static Tile get(int x, int y, int z, boolean create) {
        return Region.get(x, y).getTile(x, y, z, create);
    }


}
