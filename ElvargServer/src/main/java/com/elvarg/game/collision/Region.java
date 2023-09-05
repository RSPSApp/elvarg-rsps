package com.elvarg.game.collision;

import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Location;
import com.elvarg.game.model.Tile;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Represents a region.
 *
 * @author Professor Oak
 */
public class Region {

    /**
     * This region's id.
     */
    private final int regionId;

    /**
     * This region's terrain file id.
     */
    private final int terrainFile;

    /**
     * This region's object file id.
     */
    private final int objectFile;

    /**
     * The clipping in this region.
     */
    public int[][][] clips = new int[4][][];

    public Tile[][][] tiles;

    public List<Tile> activeTiles;

    /**
     * Has this region been loaded?
     */
    private boolean loaded;

    /**
     * The players currently in this region
     */
    public HashMap<Integer, Player> players = new HashMap<>();

    /**
     * Creates a new region.
     *
     * @param regionId
     * @param terrainFile
     * @param objectFile
     */
    public Region(int regionId, int terrainFile, int objectFile) {
        this.regionId = regionId;
        this.terrainFile = terrainFile;
        this.objectFile = objectFile;
        this.activeTiles = Lists.newCopyOnWriteArrayList();
    }

    public int getRegionId() {
        return regionId;
    }

    public int getTerrainFile() {
        return terrainFile;
    }

    public int getObjectFile() {
        return objectFile;
    }

    /**
     * Gets clipping
     *
     * @param x
     * @param y
     * @param height
     * @return
     */
    public int getClip(int x, int y, int height) {
        int regionAbsX = (regionId >> 8) * 64;
        int regionAbsY = (regionId & 0xff) * 64;
        if (height < 0 || height >= 4)
            height = 0;
        if (clips[height] == null) {
            clips[height] = new int[64][64];
        }
        return clips[height][x - regionAbsX][y - regionAbsY];
    }

    public Tile getTile(int x, int y, int z, boolean create) {
        int baseX = (regionId >> 8) * 64;
        int baseY = (regionId & 0xff) * 64;
        int localX = x - baseX;
        int localY = y - baseY;
        if(tiles == null) {
            if(!create)
                return null;
            tiles = new Tile[4][64][64];
        }
        Tile tile = tiles[z][localX][localY];
        if(tile == null && create)
            tile = tiles[z][localX][localY] = new Tile(this);
        return tile;
    }

    /**
     * Adds clipping
     *
     * @param x
     * @param y
     * @param height
     * @param shift
     */
    public void addClip(int x, int y, int height, int shift) {
        int regionAbsX = (regionId >> 8) * 64;
        int regionAbsY = (regionId & 0xff) * 64;
        if (height < 0 || height >= 4)
            height = 0;
        if (clips[height] == null) {
            clips[height] = new int[64][64];
        }
        clips[height][x - regionAbsX][y - regionAbsY] |= shift;
    }

    /**
     * Removes clipping.
     *
     * @param x
     * @param y
     * @param height
     * @param shift
     */
    public void removeClip(int x, int y, int height, int shift) {
        int regionAbsX = (regionId >> 8) * 64;
        int regionAbsY = (regionId & 0xff) * 64;
        if (height < 0 || height >= 4)
            height = 0;
        if (clips[height] == null) {
            clips[height] = new int[64][64];
        }
        clips[height][x - regionAbsX][y - regionAbsY] &= ~shift;
    }

    /**
     * Gets the local region position.
     *
     * @param position
     * @return
     */
    public int[] getLocalPosition(Location position) {
        int absX = position.getX();
        int absY = position.getY();
        int regionAbsX = (regionId >> 8) * 64;
        int regionAbsY = (regionId & 0xff) * 64;
        int localX = absX - regionAbsX;
        int localY = absY - regionAbsY;
        return new int[]{localX, localY};
    }

    public static Region get(int regionId) {
        return RegionManager.regions.get(regionId);
    }

    public static Region get(int absX, int absY) {
        return get(getId(absX, absY));
    }

    public static int getId(int absX, int absY) {
        return ((absX >> 6) << 8) | absY >> 6;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    /**
     * Return a list of regions adjacent to this Region.
     *
     * @return
     */
    public List<Region> getAdjacentRegions(){
        int x = (regionId >> 8) << 6;
        int y = (regionId & 0xFF) << 6;

        List<Region> adjacentRegions = new ArrayList<>();

        int[][] directions = {
                {0, -1},    // West
                {-1, -1},   // Northwest
                {0, -1},    // North
                {1, -1},    // Northeast
                {1, 0},     // East
                {1, 1},     // Southeast
                {0, 1},     // South
                {-1, 1}     // Southwest
        };

        for (int[] dir : directions) {
            int adjX = (x >> 6) + dir[0];
            int adjY = (y >> 6) + dir[1];
            Optional<Region> adjacentRegion = RegionManager.getRegion((adjX << 8) | adjY);
            if (adjacentRegion.isPresent()) {
                adjacentRegions.add(adjacentRegion.get());
            }
        }

        return adjacentRegions;
    }

    /**
     * Function to determine whether this region is eligible for Processing.
     * (e.g. if there are any players within this region or adjacent regions)
     *
     * @return
     */
    public boolean isActive() {
        if (this.players.size() > 0) {
            return true;
        }

        List<Region> adjacentRegions = getAdjacentRegions();
        for (Region adjacentRegion : adjacentRegions) {
            if (adjacentRegion.players.size() > 0) {
                return true;
            }
        }

        return false;
    }
}