package com.runescape.scene;
import com.runescape.Client;
import com.runescape.collection.Deque;
import com.runescape.draw.Rasterizer2D;
import com.runescape.draw.Rasterizer3D;
import com.runescape.engine.impl.MouseHandler;
import com.runescape.entity.GroundItemTile;
import com.runescape.entity.Renderable;
import com.runescape.entity.GameObject;
import com.runescape.entity.model.Model;
import com.runescape.entity.model.VertexNormal;
import com.runescape.scene.object.GroundDecoration;
import com.runescape.scene.object.WallObject;
import com.runescape.scene.object.WallDecoration;
import com.runescape.scene.object.tile.ShapedTile;
import com.runescape.scene.object.tile.SimpleTile;
import com.runescape.scene.object.tile.Tile;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ItemSpawned;
import net.runelite.api.hooks.DrawCallbacks;
import net.runelite.rs.api.*;

import java.util.HashSet;
import java.util.Set;

import static net.runelite.api.Constants.*;

public final class SceneGraph implements RSScene {

    public static int viewDistance = 9;

    public static boolean pitchRelaxEnabled;
    public static boolean hdMinimapEnabled = false;

    public static int skyboxColor;
    public static int[] tmpX = new int[6];
    public static int[] tmpY = new int[6];
    public static int roofRemovalMode = 0;
    public static final Set<RSTile> tilesToRemove = new HashSet<RSTile>();


    public SceneGraph(int heightMap[][][]) {
        int yLocSize = 104;// was parameter
        int xLocSize = 104;// was parameter
        int zLocSize = 4;// was parameter
        gameObjectsCache = new GameObject[5000];
        anIntArray486 = new int[10000];
        anIntArray487 = new int[10000];
        maxY = zLocSize;
        maxX = xLocSize;
        maxZ = yLocSize;
        tileArray = new Tile[zLocSize][xLocSize][yLocSize];
        anIntArrayArrayArray445 = new int[zLocSize][xLocSize + 1][yLocSize + 1];
        this.heightMap = heightMap;
        initToNull();
    }

    /**
     * The class destructor.
     */
    public static void destructor() {
        interactableObjects = null;
        sceneClusterCounts = null;
        sceneClusters = null;
        tileDeque = null;
        visibilityMap = null;
        renderArea = null;
    }

    public void initToNull() {
        for (int zLoc = 0; zLoc < maxY; zLoc++)
            for (int xLoc = 0; xLoc < maxX; xLoc++)
                for (int yLoc = 0; yLoc < maxZ; yLoc++)
                    tileArray[zLoc][xLoc][yLoc] = null;
        for (int plane = 0; plane < cullingClusterPlaneCount; plane++) {
            for (int j1 = 0; j1 < sceneClusterCounts[plane]; j1++)
                sceneClusters[plane][j1] = null;
            sceneClusterCounts[plane] = 0;
        }

        for (int i = 0; i < interactableObjectCacheCurrPos; i++)
            gameObjectsCache[i] = null;
        interactableObjectCacheCurrPos = 0;
        for (int i = 0; i < interactableObjects.length; i++)
            interactableObjects[i] = null;

    }

    public void method275(int zLoc) {
        minLevel = zLoc;
        for (int xLoc = 0; xLoc < maxX; xLoc++) {
            for (int yLoc = 0; yLoc < maxZ; yLoc++)
                if (tileArray[zLoc][xLoc][yLoc] == null)
                    tileArray[zLoc][xLoc][yLoc] = new Tile(zLoc, xLoc, yLoc);
        }
    }

    public void applyBridgeMode(int yLoc, int xLoc) {
        Tile tileFirstFloor = tileArray[0][xLoc][yLoc];
        for (int zLoc = 0; zLoc < 3; zLoc++) {
            Tile tile = tileArray[zLoc][xLoc][yLoc] = tileArray[zLoc + 1][xLoc][yLoc];
            if (tile != null) {
                tile.z1AnInt1307--;
                for (int j1 = 0; j1 < tile.gameObjectIndex; j1++) {
                    GameObject gameObject = tile.gameObjects[j1];
                    if ((gameObject.uid >> 29 & 3) == 2 && gameObject.xLocLow == xLoc && gameObject.yLocHigh == yLoc)
                        gameObject.zLoc--;
                }
            }
        }
        if (tileArray[0][xLoc][yLoc] == null)
            tileArray[0][xLoc][yLoc] = new Tile(0, xLoc, yLoc);
        tileArray[0][xLoc][yLoc].firstFloorTile = tileFirstFloor;
        tileArray[3][xLoc][yLoc] = null;
    }

    public static void createNewSceneCluster(int z, int lowestX, int lowestZ, int highestX, int highestY, int highestZ, int lowestY, int searchMask) {
        SceneCluster sceneCluster = new SceneCluster();
        sceneCluster.startXLoc = lowestX / 128;
        sceneCluster.endXLoc = highestX / 128;
        sceneCluster.startYLoc = lowestY / 128;
        sceneCluster.endYLoc = highestY / 128;
        sceneCluster.orientation = searchMask;
        sceneCluster.startXPos = lowestX;
        sceneCluster.endXPos = highestX;
        sceneCluster.startYPos = lowestY;
        sceneCluster.endYPos = highestY;
        sceneCluster.startZPos = highestZ;
        sceneCluster.endZPos = lowestZ;
        sceneClusters[z][sceneClusterCounts[z]++] = sceneCluster;
    }

    public void setTileLogicHeight(int zLoc, int xLoc, int yLoc, int logicHeight) {
        Tile tile = tileArray[zLoc][xLoc][yLoc];
        if (tile != null)
            tileArray[zLoc][xLoc][yLoc].logicHeight = logicHeight;
    }

    public void addTile(int zLoc, int xLoc, int yLoc, int shape, int i1, int j1, int k1, int l1, int i2, int j2, int k2, int l2, int i3, int j3, int k3, int l3, int i4, int j4, int k4, int l4) {
        if (shape == 0) {
            SimpleTile simpleTile = new SimpleTile(k2, l2, i3, j3, -1, k4, false);
            for (int lowerZLoc = zLoc; lowerZLoc >= 0; lowerZLoc--)
                if (tileArray[lowerZLoc][xLoc][yLoc] == null)
                    tileArray[lowerZLoc][xLoc][yLoc] = new Tile(lowerZLoc, xLoc, yLoc);

            tileArray[zLoc][xLoc][yLoc].mySimpleTile = simpleTile;
        } else if (shape == 1) {
            SimpleTile simpleTile = new SimpleTile(k3, l3, i4, j4, j1, l4, k1 == l1 && k1 == i2 && k1 == j2);
            for (int lowerZLoc = zLoc; lowerZLoc >= 0; lowerZLoc--)
                if (tileArray[lowerZLoc][xLoc][yLoc] == null)
                    tileArray[lowerZLoc][xLoc][yLoc] = new Tile(lowerZLoc, xLoc, yLoc);

            tileArray[zLoc][xLoc][yLoc].mySimpleTile = simpleTile;
        } else {
            ShapedTile shapedTile = new ShapedTile(yLoc, k3, j3, i2, j1, i4, i1, k2, k4, i3, j2, l1, k1, shape, j4, l3, l2, xLoc, l4);
            for (int k5 = zLoc; k5 >= 0; k5--)
                if (tileArray[k5][xLoc][yLoc] == null)
                    tileArray[k5][xLoc][yLoc] = new Tile(k5, xLoc, yLoc);

            tileArray[zLoc][xLoc][yLoc].myShapedTile = shapedTile;
        }
    }

    public void addGroundDecoration(int zLoc, int zPos, int yLoc, Renderable renderable, byte objectRotationType, long uid, int xLoc) {
        if (renderable == null)
            return;
        GroundDecoration groundDecoration = new GroundDecoration();
        groundDecoration.renderable = renderable;
        groundDecoration.xPos = xLoc * 128 + 64;
        groundDecoration.yPos = yLoc * 128 + 64;
        groundDecoration.tileHeights = zPos;
        groundDecoration.uid = uid;
        groundDecoration.mask = objectRotationType;
        if (tileArray[zLoc][xLoc][yLoc] == null) {
            tileArray[zLoc][xLoc][yLoc] = new Tile(zLoc, xLoc, yLoc);
            groundDecoration.zLoc = zLoc;
        }
        tileArray[zLoc][xLoc][yLoc].groundDecoration = groundDecoration;
        tileArray[zLoc][xLoc][yLoc].groundObjectChanged();
    }

    public void addGroundItemTile(Object obj, int xLoc, long uid, Renderable firstNode, int zPos, Renderable secondNode, Renderable thirdNode, int zLoc, int yLoc) {
        GroundItemTile groundItemTile = new GroundItemTile();
        groundItemTile.topNode = thirdNode;
        groundItemTile.xPos = xLoc * 128 + 64;
        groundItemTile.yPos = yLoc * 128 + 64;
        groundItemTile.tileHeights = zPos;
        groundItemTile.uid = uid;
        groundItemTile.lowerNode = firstNode;
        groundItemTile.middleNode = secondNode;
        int largestItemDropHeight = 0;
        Tile parentTile = tileArray[zLoc][xLoc][yLoc];
        if (parentTile != null) {
            for (int i = 0; i < parentTile.gameObjectIndex; i++)
                if (parentTile.gameObjects[i].renderable instanceof Model) {
                    int objectItemDropHeight = ((Model) parentTile.gameObjects[i].renderable).itemDropHeight;
                    if (objectItemDropHeight > largestItemDropHeight)
                        largestItemDropHeight = objectItemDropHeight;
                }

        }
        groundItemTile.itemDropHeight = largestItemDropHeight;
        if (tileArray[zLoc][xLoc][yLoc] == null) {
            tileArray[zLoc][xLoc][yLoc] = new Tile(zLoc, xLoc, yLoc);
            groundItemTile.zLoc = zLoc;
        }
        tileArray[zLoc][xLoc][yLoc].groundItemTile = groundItemTile;
        ItemSpawned event = new ItemSpawned(tileArray[zLoc][xLoc][yLoc], (TileItem) obj);
        Client.instance.getCallbacks().post(event);
    }

    public void addWallObject(int orientation1, Renderable renderable1, long uid, int yLoc, byte objectFaceType, int xLoc, Renderable renderable2, int zPos, int orientation2, int zLoc) {
        if (renderable1 == null && renderable2 == null)
            return;
        WallObject wallObject = new WallObject();
        wallObject.uid = uid;
        wallObject.mask = objectFaceType;
        wallObject.xPos = xLoc * 128 + 64;
        wallObject.yPos = yLoc * 128 + 64;
        wallObject.tileHeights = zPos;
        wallObject.renderable1 = renderable1;
        wallObject.renderable2 = renderable2;
        wallObject.orientation1 = orientation1;
        wallObject.orientation2 = orientation2;
        for (int z = zLoc; z >= 0; z--) {
            if (tileArray[z][xLoc][yLoc] == null) {
                tileArray[z][xLoc][yLoc] = new Tile(z, xLoc, yLoc);
                wallObject.zLoc = z;
            }
        }

        tileArray[zLoc][xLoc][yLoc].wallObject = wallObject;
        tileArray[zLoc][xLoc][yLoc].wallObjectChanged();
    }

    public void addWallDecoration(long uid, int yLoc, int orientation2, int zLoc, int xOffset, int zPos, Renderable renderable, int xLoc, byte objectRotationType, int yOffset, int orientation) {
        if (renderable == null)
            return;


        WallDecoration wallDecoration = new WallDecoration();
        wallDecoration.uid = uid;
        wallDecoration.mask = objectRotationType;
        wallDecoration.xPos = xLoc * 128 + 64 + xOffset;
        wallDecoration.yPos = yLoc * 128 + 64 + yOffset;
        wallDecoration.tileHeights = zPos;
        wallDecoration.renderable = renderable;
        wallDecoration.orientation = orientation;
        wallDecoration.orientation2 = orientation2;

        for (int z = zLoc; z >= 0; z--) {
            if (tileArray[z][xLoc][yLoc] == null) {
                tileArray[z][xLoc][yLoc] = new Tile(z, xLoc, yLoc);
                wallDecoration.zLoc = z;
            }
        }

        tileArray[zLoc][xLoc][yLoc].wallDecoration = wallDecoration;
        tileArray[zLoc][xLoc][yLoc].decorativeObjectChanged();
    }

    public boolean addTiledObject(long uid, byte objectRotationType, int tileHeight, int sizeY, Renderable renderable, int sizeX, int zLoc, int turnValue, int yLoc, int xLoc) {
        if (renderable == null) {
            return true;
        } else {
            int xPos = xLoc * 128 + 64 * sizeX;
            int yPos = yLoc * 128 + 64 * sizeY;
            return addAnimableC(zLoc, xLoc, yLoc, sizeX, sizeY, xPos, yPos, tileHeight, renderable, turnValue, false, uid, objectRotationType);
        }
    }

    public boolean addAnimableA(int zLoc, int turnValue, int k, long uid, int yPos, int halfSizePos, int xPos, Renderable animable, boolean flag) {
        if (animable == null)
            return true;
        if(!Client.instance.addEntityMarker(xPos, yPos,animable)) {
            return true;
        }
        int startXLoc = xPos - halfSizePos;
        int startYLoc = yPos - halfSizePos;
        int endXLoc = xPos + halfSizePos;
        int endYLoc = yPos + halfSizePos;
        if (flag) {
            if (turnValue > 640 && turnValue < 1408)
                endYLoc += 128;
            if (turnValue > 1152 && turnValue < 1920)
                endXLoc += 128;
            if (turnValue > 1664 || turnValue < 384)
                startYLoc -= 128;
            if (turnValue > 128 && turnValue < 896)
                startXLoc -= 128;
        }
        startXLoc /= 128;
        startYLoc /= 128;
        endXLoc /= 128;
        endYLoc /= 128;
        return addAnimableC(zLoc, startXLoc, startYLoc, (endXLoc - startXLoc) + 1, (endYLoc - startYLoc) + 1, xPos, yPos, k, animable, turnValue, true, uid, (byte) 0);
    }

    public boolean addToScenePlayerAsObject(int zLoc, int playerYPos, Renderable playerAsObject, int playerTurnValue, int objectEndYLoc, int playerXPos, int playerHeight, int objectStartXLoc, int objectEndXLoc, long uid, int objectStartYLoc) {
        return playerAsObject == null || addAnimableC(zLoc, objectStartXLoc, objectStartYLoc, (objectEndXLoc - objectStartXLoc) + 1, (objectEndYLoc - objectStartYLoc) + 1, playerXPos, playerYPos, playerHeight, playerAsObject, playerTurnValue, true, uid, (byte) 0);
    }

    private boolean addAnimableC(int zLoc, int xLoc, int yLoc, int sizeX, int sizeY, int xPos, int yPos, int tileHeight, Renderable renderable, int turnValue, boolean isDynamic, long uid, byte objectRotationType) {
        if(!Client.instance.addEntityMarker(xPos, yPos,renderable)) {
            return true;
        }
        for (int x = xLoc; x < xLoc + sizeX; x++) {
            for (int y = yLoc; y < yLoc + sizeY; y++) {
                if (x < 0 || y < 0 || x >= maxX || y >= maxZ)
                    return false;
                Tile tile = tileArray[zLoc][x][y];
                if (tile != null && tile.gameObjectIndex >= 5)
                    return false;
            }

        }

        GameObject gameObject = new GameObject();
        gameObject.uid = uid;
        gameObject.mask = objectRotationType;
        gameObject.zLoc = zLoc;
        gameObject.xPos = xPos;
        gameObject.yPos = yPos;
        gameObject.tileHeight = tileHeight;
        gameObject.renderable = renderable;
        gameObject.turnValue = turnValue;
        gameObject.xLocLow = xLoc;
        gameObject.yLocHigh = yLoc;
        gameObject.xLocHigh = (xLoc + sizeX) - 1;
        gameObject.yLocLow = (yLoc + sizeY) - 1;
        for (int x = xLoc; x < xLoc + sizeX; x++) {
            for (int y = yLoc; y < yLoc + sizeY; y++) {
                int mask = 0;
                if (x > xLoc)
                    mask++;
                if (x < (xLoc + sizeX) - 1)
                    mask += 4;
                if (y > yLoc)
                    mask += 8;
                if (y < (yLoc + sizeY) - 1)
                    mask += 2;
                for (int z = zLoc; z >= 0; z--)
                    if (tileArray[z][x][y] == null)
                        tileArray[z][x][y] = new Tile(z, x, y);

                Tile tile = tileArray[zLoc][x][y];
                tile.gameObjects[tile.gameObjectIndex] = gameObject;
                tile.gameObjectsChanged(tile.gameObjectIndex);
                tile.tiledObjectMasks[tile.gameObjectIndex] = mask;
                tile.totalTiledObjectMask |= mask;
                tile.gameObjectIndex++;
            }

        }

        if (isDynamic) {
            gameObjectsCache[interactableObjectCacheCurrPos++] = gameObject;
        }

        return true;
    }

    public void clearGameObjectCache() {
        for (int i = 0; i < interactableObjectCacheCurrPos; i++) {
            GameObject object5 = gameObjectsCache[i];
            remove(object5);
            gameObjectsCache[i] = null;
        }

        interactableObjectCacheCurrPos = 0;
    }

    private void remove(GameObject gameObject) {
        for (int x = gameObject.xLocLow; x <= gameObject.xLocHigh; x++) {
            for (int y = gameObject.yLocHigh; y <= gameObject.yLocLow; y++) {
                Tile tile = tileArray[gameObject.zLoc][x][y];
                if (tile != null) {
                    for (int i = 0; i < tile.gameObjectIndex; i++) {
                        if (tile.gameObjects[i] != gameObject)
                            continue;
                        tile.gameObjectIndex--;
                        for (int i1 = i; i1 < tile.gameObjectIndex; i1++) {
                            tile.gameObjects[i1] = tile.gameObjects[i1 + 1];
                            tile.tiledObjectMasks[i1] = tile.tiledObjectMasks[i1 + 1];
                        }

                        tile.gameObjects[tile.gameObjectIndex] = null;
                        tile.gameObjectsChanged(tile.gameObjectIndex);
                        break;
                    }

                    tile.totalTiledObjectMask = 0;
                    for (int i = 0; i < tile.gameObjectIndex; i++)
                        tile.totalTiledObjectMask |= tile.tiledObjectMasks[i];
                }
            }
        }
    }

    public void method290(int yLoc, int k, int xLoc, int zLoc) { //TODO scale position?
        Tile tile = tileArray[zLoc][xLoc][yLoc];
        if (tile == null)
            return;
        WallDecoration wallDecoration = tile.wallDecoration;
        if (wallDecoration != null) {
            int xPos = xLoc * 128 + 64;
            int yPos = yLoc * 128 + 64;
            wallDecoration.xPos = xPos + ((wallDecoration.xPos - xPos) * k) / 16;
            wallDecoration.yPos = yPos + ((wallDecoration.yPos - yPos) * k) / 16;
        }
    }

    public void removeWallObject(int xLoc, int zLoc, int yLoc) {
        Tile tile = tileArray[zLoc][xLoc][yLoc];
        if (tile != null) {
            tile.wallObject = null;
            tile.wallObjectChanged();
        }
    }

    public void removeWallDecoration(int yLoc, int zLoc, int xLoc) {
        Tile tile = tileArray[zLoc][xLoc][yLoc];
        if (tile != null) {
            tile.wallDecoration = null;
            tile.decorativeObjectChanged();
        }
    }

    public void removeTiledObject(int zLoc, int xLoc, int yLoc) {
        Tile tile = tileArray[zLoc][xLoc][yLoc];
        if (tile == null)
            return;
        for (int j1 = 0; j1 < tile.gameObjectIndex; j1++) {
            GameObject gameObject = tile.gameObjects[j1];
            if ((gameObject.uid >> 29 & 3) == 2 && gameObject.xLocLow == xLoc && gameObject.yLocHigh == yLoc) {
                remove(gameObject);
                return;
            }
        }

    }

    public void removeGroundDecoration(int zLoc, int yLoc, int xLoc) {
        Tile tile = tileArray[zLoc][xLoc][yLoc];
        if (tile == null)
            return;
        tile.groundDecoration = null;
        tile.groundObjectChanged();
    }

    public void removeGroundItemTile(int zLoc, int xLoc, int yLoc) {
        Tile tile = tileArray[zLoc][xLoc][yLoc];
        if (tile != null)
            tile.groundItemTile = null;
    }

    public WallObject getWallObject(int zLoc, int xLoc, int yLoc) {
        Tile tile = tileArray[zLoc][xLoc][yLoc];
        if (tile == null)
            return null;
        else
            return tile.wallObject;
    }

    public WallDecoration getWallDecoration(int xLoc, int yLoc, int zLoc) {
        Tile tile = tileArray[zLoc][xLoc][yLoc];
        if (tile == null)
            return null;
        else
            return tile.wallDecoration;
    }

    public GameObject getGameObject(int xLoc, int yLoc, int zLoc) {
        Tile tile = tileArray[zLoc][xLoc][yLoc];
        if (tile == null)
            return null;
        for (int i = 0; i < tile.gameObjectIndex; i++) {
            GameObject gameObject = tile.gameObjects[i];
            if ((gameObject.uid >> 29 & 3) == 2 && gameObject.xLocLow == xLoc && gameObject.yLocHigh == yLoc)
                return gameObject;
        }
        return null;
    }

    public GroundDecoration getGroundDecoration(int yLoc, int xLoc, int zLoc) {
        Tile tile = tileArray[zLoc][xLoc][yLoc];
        if (tile == null || tile.groundDecoration == null)
            return null;
        else
            return tile.groundDecoration;
    }

    public long getWallObjectUid(int zLoc, int xLoc, int yLoc) {
        Tile tile = tileArray[zLoc][xLoc][yLoc];
        if (tile == null || tile.wallObject == null)
            return 0;
        else
            return tile.wallObject.uid;
    }

    public long getWallDecorationUid(int zLoc, int xLoc, int yLoc) {
        Tile tile = tileArray[zLoc][xLoc][yLoc];
        if (tile == null || tile.wallDecoration == null)
            return 0;
        else
            return tile.wallDecoration.uid;
    }

    public long getGameObjectUid(int zLoc, int xLoc, int yLoc) {
        Tile tile = tileArray[zLoc][xLoc][yLoc];
        if (tile == null)
            return 0;
        for (int i = 0; i < tile.gameObjectIndex; i++) {
            GameObject gameObject = tile.gameObjects[i];
            if ((gameObject.uid >> 29 & 3) == 2 && gameObject.xLocLow == xLoc && gameObject.yLocHigh == yLoc)
                return gameObject.uid;
        }
        return 0;
    }

    public long getGroundDecorationUid(int zLoc, int xLoc, int yLoc) {
        Tile tile = tileArray[zLoc][xLoc][yLoc];
        if (tile == null || tile.groundDecoration == null)
            return 0;
        else
            return tile.groundDecoration.uid;
    }


    public void shadeModels(int lightY, int lightX, int lightZ) {
        int intensity = 64;// was parameter
        int diffusion = 768;// was parameter
        int lightDistance = (int) Math.sqrt(lightX * lightX + lightY * lightY + lightZ * lightZ);
        int someLightQualityVariable = diffusion * lightDistance >> 8;
        for (int zLoc = 0; zLoc < maxY; zLoc++) {
            for (int xLoc = 0; xLoc < maxX; xLoc++) {
                for (int yLoc = 0; yLoc < maxZ; yLoc++) {
                    Tile tile = tileArray[zLoc][xLoc][yLoc];
                    if (tile != null) {
                        WallObject wallObject = tile.wallObject;
                        if (wallObject != null && wallObject.renderable1 != null
                                && wallObject.renderable1.normals != null) {
                            method307(zLoc, 1, 1, xLoc, yLoc, (Model) wallObject.renderable1);
                            if (wallObject.renderable2 != null && wallObject.renderable2.normals != null) {
                                method307(zLoc, 1, 1, xLoc, yLoc, (Model) wallObject.renderable2);
                                mergeNormals((Model) wallObject.renderable1, (Model) wallObject.renderable2, 0, 0,
                                        0, false);
                                ((Model) wallObject.renderable2).setLighting(intensity, someLightQualityVariable,
                                        lightX, lightY, lightZ);
                            }
                            ((Model) wallObject.renderable1).setLighting(intensity, someLightQualityVariable,
                                    lightX, lightY, lightZ);
                        }
                        for (int k2 = 0; k2 < tile.gameObjectIndex; k2++) {
                            GameObject interactableObject = tile.gameObjects[k2];
                            if (interactableObject != null && interactableObject.renderable != null
                                    && interactableObject.renderable.normals != null) {
                                method307(zLoc, (interactableObject.xLocHigh - interactableObject.xLocLow) + 1,
                                        (interactableObject.yLocLow - interactableObject.yLocHigh) + 1, xLoc, yLoc,
                                        (Model) interactableObject.renderable);
                                ((Model) interactableObject.renderable).setLighting(intensity,
                                        someLightQualityVariable, lightX, lightY, lightZ);
                            }
                        }

                        GroundDecoration groundDecoration = tile.groundDecoration;
                        if (groundDecoration != null && groundDecoration.renderable.normals != null) {
                            method306GroundDecorationOnly(xLoc, zLoc, (Model) groundDecoration.renderable, yLoc);
                            ((Model) groundDecoration.renderable).setLighting(intensity, someLightQualityVariable, lightX, lightY, lightZ);
                        }

                    }
                }
            }
        }
    }

    private void method306GroundDecorationOnly(int modelXLoc, int modelZLoc, Model model, int modelYLoc) { //TODO figure it out
        if (modelXLoc < maxX) {
            Tile tile = tileArray[modelZLoc][modelXLoc + 1][modelYLoc];
            if (tile != null && tile.groundDecoration != null && tile.groundDecoration.renderable.normals != null)
                mergeNormals(model, (Model) tile.groundDecoration.renderable, 128, 0, 0, true);
        }
        if (modelYLoc < maxX) {
            Tile tile = tileArray[modelZLoc][modelXLoc][modelYLoc + 1];
            if (tile != null && tile.groundDecoration != null && tile.groundDecoration.renderable.normals != null)
                mergeNormals(model, (Model) tile.groundDecoration.renderable, 0, 0, 128, true);
        }
        if (modelXLoc < maxX && modelYLoc < maxZ) {
            Tile tile = tileArray[modelZLoc][modelXLoc + 1][modelYLoc + 1];
            if (tile != null && tile.groundDecoration != null && tile.groundDecoration.renderable.normals != null)
                mergeNormals(model, (Model) tile.groundDecoration.renderable, 128, 0, 128, true);
        }
        if (modelXLoc < maxX && modelYLoc > 0) {
            Tile tile = tileArray[modelZLoc][modelXLoc + 1][modelYLoc - 1];
            if (tile != null && tile.groundDecoration != null && tile.groundDecoration.renderable.normals != null)
                mergeNormals(model, (Model) tile.groundDecoration.renderable, 128, 0, -128, true);
        }
    }

    private void method307(int modelZLoc, int modelXSize, int modelYSize, int modelXLoc, int modelYLoc, Model model) {
        boolean flag = true;
        int startX = modelXLoc;
        int stopX = modelXLoc + modelXSize;
        int startY = modelYLoc - 1;
        int stopY = modelYLoc + modelYSize;
        for (int zLoc = modelZLoc; zLoc <= modelZLoc + 1; zLoc++)
            if (zLoc != maxY) {//TODO Always?
                for (int xLoc = startX; xLoc <= stopX; xLoc++)
                    if (xLoc >= 0 && xLoc < maxX) {
                        for (int yLoc = startY; yLoc <= stopY; yLoc++)
                            if (yLoc >= 0 && yLoc < maxZ && (!flag || xLoc >= stopX || yLoc >= stopY || yLoc < modelYLoc && xLoc != modelXLoc)) {
                                Tile tile = tileArray[zLoc][xLoc][yLoc];
                                if (tile != null) {
                                    int relativeHeightToModelTile = (heightMap[zLoc][xLoc][yLoc] + heightMap[zLoc][xLoc + 1][yLoc] + heightMap[zLoc][xLoc][yLoc + 1] + heightMap[zLoc][xLoc + 1][yLoc + 1]) / 4 - (heightMap[modelZLoc][modelXLoc][modelYLoc] + heightMap[modelZLoc][modelXLoc + 1][modelYLoc] + heightMap[modelZLoc][modelXLoc][modelYLoc + 1] + heightMap[modelZLoc][modelXLoc + 1][modelYLoc + 1]) / 4;
                                    WallObject wallObject = tile.wallObject;
                                    if (wallObject != null && wallObject.renderable1 != null && wallObject.renderable1.normals != null)
                                        mergeNormals(model, (Model) wallObject.renderable1, (xLoc - modelXLoc) * 128 + (1 - modelXSize) * 64, relativeHeightToModelTile, (yLoc - modelYLoc) * 128 + (1 - modelYSize) * 64, flag);
                                    if (wallObject != null && wallObject.renderable2 != null && wallObject.renderable2.normals != null)
                                        mergeNormals(model, (Model) wallObject.renderable2, (xLoc - modelXLoc) * 128 + (1 - modelXSize) * 64, relativeHeightToModelTile, (yLoc - modelYLoc) * 128 + (1 - modelYSize) * 64, flag);
                                    for (int i = 0; i < tile.gameObjectIndex; i++) {
                                        GameObject gameObject = tile.gameObjects[i];
                                        if (gameObject != null && gameObject.renderable != null && gameObject.renderable.normals != null) {
                                            int tiledObjectXSize = (gameObject.xLocHigh - gameObject.xLocLow) + 1;
                                            int tiledObjectYSize = (gameObject.yLocLow - gameObject.yLocHigh) + 1;
                                            mergeNormals(model, (Model) gameObject.renderable, (gameObject.xLocLow - modelXLoc) * 128 + (tiledObjectXSize - modelXSize) * 64, relativeHeightToModelTile, (gameObject.yLocHigh - modelYLoc) * 128 + (tiledObjectYSize - modelYSize) * 64, flag);
                                        }
                                    }
                                }
                            }
                    }
                startX--; //TODO why?
                flag = false;
            }

    }

    private void mergeNormals(final Model model, final Model secondModel, final int posX, final int posY, final int posZ, final boolean flag) {
        this.anInt488++;
        int count = 0;
        final int[] vertices = secondModel.verticesX;
        final int vertexCount = secondModel.verticesCount;

        for (int vertex = 0; vertex < model.verticesCount; vertex++) {
            final VertexNormal vertexNormal = model.normals[vertex];
            final VertexNormal offsetVertexNormal = model.vertexNormalsOffsets[vertex];
            if (offsetVertexNormal.magnitude != 0) {
                final int y = model.verticesY[vertex] - posY;
                if (y <= secondModel.maxY) {
                    final int x = model.verticesX[vertex] - posX;
                    if (x >= secondModel.minX && x <= secondModel.maxX) {
                        final int z = model.verticesZ[vertex] - posZ;
                        if (z >= secondModel.minZ && z <= secondModel.maxZ) {
                            for (int v = 0; v < vertexCount; v++) {
                                final VertexNormal vertexNormal2 = secondModel.normals[v];
                                final VertexNormal offsetVertexNormal2 = secondModel.vertexNormalsOffsets[v];
                                if (x == vertices[v] && z == secondModel.verticesZ[v] && y == secondModel.verticesY[v]
                                        && offsetVertexNormal2.magnitude != 0) {
                                    vertexNormal.x += offsetVertexNormal2.x;
                                    vertexNormal.y += offsetVertexNormal2.y;
                                    vertexNormal.z += offsetVertexNormal2.z;
                                    vertexNormal.magnitude += offsetVertexNormal2.magnitude;
                                    vertexNormal2.x += offsetVertexNormal.x;
                                    vertexNormal2.y += offsetVertexNormal.y;
                                    vertexNormal2.z += offsetVertexNormal.z;
                                    vertexNormal2.magnitude += offsetVertexNormal.magnitude;
                                    count++;
                                    this.anIntArray486[vertex] = this.anInt488;
                                    this.anIntArray487[v] = this.anInt488;
                                }
                            }

                        }
                    }
                }
            }
        }

        if (count < 3 || !flag) {
            return;
        }

        for (int triangle = 0; triangle < model.trianglesCount; triangle++) {
            if (this.anIntArray486[model.trianglesX[triangle]] == this.anInt488
                    && this.anIntArray486[model.trianglesY[triangle]] == this.anInt488
                    && this.anIntArray486[model.trianglesZ[triangle]] == this.anInt488) {
                model.drawType[triangle] = -1;
            }
        }

        for (int triangle = 0; triangle < secondModel.trianglesCount; triangle++) {
            if (this.anIntArray487[secondModel.trianglesX[triangle]] == this.anInt488
                    && this.anIntArray487[secondModel.trianglesY[triangle]] == this.anInt488
                    && this.anIntArray487[secondModel.trianglesZ[triangle]] == this.anInt488) {
                secondModel.drawType[triangle] = -1;
            }
        }

    }

    public void drawTileMinimap(int[] pixels, int pixelOffset, int z, int x, int y)
    {
        if (!Client.instance.isHdMinimapEnabled())
        {
            drawTileMinimapSD(pixels, pixelOffset, z, x, y);
            return;
        }
        RSTile tile = getTiles()[z][x][y];
        if (tile == null)
        {
            return;
        }
        SceneTilePaint sceneTilePaint = tile.getSceneTilePaint();
        if (sceneTilePaint != null)
        {
            int rgb = sceneTilePaint.getRBG();
            if (sceneTilePaint.getSwColor() != INVALID_HSL_COLOR)
            {
                // hue and saturation
                int hs = sceneTilePaint.getSwColor() & ~0x7F;
                // I know this looks dumb (and it probably is) but I don't feel like hunting down the problem
                int seLightness = sceneTilePaint.getNwColor() & 0x7F;
                int neLightness = sceneTilePaint.getNeColor() & 0x7F;
                int southDeltaLightness = (sceneTilePaint.getSwColor() & 0x7F) - seLightness;
                int northDeltaLightness = (sceneTilePaint.getSeColor() & 0x7F) - neLightness;
                seLightness <<= 2;
                neLightness <<= 2;
                for (int i = 0; i < 4; i++)
                {
                    if (sceneTilePaint.getTexture() == -1)
                    {
                        pixels[pixelOffset] = Rasterizer3D.hslToRgb[hs | seLightness >> 2];
                        pixels[pixelOffset + 1] = Rasterizer3D.hslToRgb[hs | seLightness * 3 + neLightness >> 4];
                        pixels[pixelOffset + 2] = Rasterizer3D.hslToRgb[hs | seLightness + neLightness >> 3];
                        pixels[pixelOffset + 3] = Rasterizer3D.hslToRgb[hs | seLightness + neLightness * 3 >> 4];
                    }
                    else
                    {
                        int lig = 0xFF - ((seLightness >> 1) * (seLightness >> 1) >> 8);
                        pixels[pixelOffset] = ((rgb & 0xFF00FF) * lig & ~0xFF00FF) + ((rgb & 0xFF00) * lig & 0xFF0000) >> 8;
                        lig = 0xFF - ((seLightness * 3 + neLightness >> 3) * (seLightness * 3 + neLightness >> 3) >> 8);
                        pixels[pixelOffset + 1] = ((rgb & 0xFF00FF) * lig & ~0xFF00FF) + ((rgb & 0xFF00) * lig & 0xFF0000) >> 8;
                        lig = 0xFF - ((seLightness + neLightness >> 2) * (seLightness + neLightness >> 2) >> 8);
                        pixels[pixelOffset + 2] = ((rgb & 0xFF00FF) * lig & ~0xFF00FF) + ((rgb & 0xFF00) * lig & 0xFF0000) >> 8;
                        lig = 0xFF - ((seLightness + neLightness * 3 >> 3) * (seLightness + neLightness * 3 >> 3) >> 8);
                        pixels[pixelOffset + 3] = ((rgb & 0xFF00FF) * lig & ~0xFF00FF) + ((rgb & 0xFF00) * lig & 0xFF0000) >> 8;
                    }
                    seLightness += southDeltaLightness;
                    neLightness += northDeltaLightness;

                    pixelOffset += 512;
                }
            }
            else if (rgb != 0)
            {
                for (int i = 0; i < 4; i++)
                {
                    pixels[pixelOffset] = rgb;
                    pixels[pixelOffset + 1] = rgb;
                    pixels[pixelOffset + 2] = rgb;
                    pixels[pixelOffset + 3] = rgb;
                    pixelOffset += 512;
                }
            }
            return;
        }

        SceneTileModel sceneTileModel = tile.getSceneTileModel();
        if (sceneTileModel != null)
        {
            int shape = sceneTileModel.getShape();
            int rotation = sceneTileModel.getRotation();
            int overlayRgb = sceneTileModel.getModelOverlay();
            int underlayRgb = sceneTileModel.getModelUnderlay();
            int[] points = getTileShape2D()[shape];
            int[] indices = getTileRotation2D()[rotation];

            int shapeOffset = 0;

            if (sceneTileModel.getOverlaySwColor() != INVALID_HSL_COLOR)
            {
                // hue and saturation
                int hs = sceneTileModel.getOverlaySwColor() & ~0x7F;
                int seLightness = sceneTileModel.getOverlaySeColor() & 0x7F;
                int neLightness = sceneTileModel.getOverlayNeColor() & 0x7F;
                int southDeltaLightness = (sceneTileModel.getOverlaySwColor() & 0x7F) - seLightness;
                int northDeltaLightness = (sceneTileModel.getOverlayNwColor() & 0x7F) - neLightness;
                seLightness <<= 2;
                neLightness <<= 2;
                for (int i = 0; i < 4; i++)
                {
                    if (sceneTileModel.getTriangleTextureId() == null)
                    {
                        if (points[indices[shapeOffset++]] != 0)
                        {
                            pixels[pixelOffset] = Rasterizer3D.hslToRgb[hs | (seLightness >> 2)];
                        }
                        if (points[indices[shapeOffset++]] != 0)
                        {
                            pixels[pixelOffset + 1] = Rasterizer3D.hslToRgb[hs | (seLightness * 3 + neLightness >> 4)];
                        }
                        if (points[indices[shapeOffset++]] != 0)
                        {
                            pixels[pixelOffset + 2] = Rasterizer3D.hslToRgb[hs | (seLightness + neLightness >> 3)];
                        }
                        if (points[indices[shapeOffset++]] != 0)
                        {
                            pixels[pixelOffset + 3] = Rasterizer3D.hslToRgb[hs | (seLightness + neLightness * 3 >> 4)];
                        }
                    }
                    else
                    {
                        if (points[indices[shapeOffset++]] != 0)
                        {
                            int lig = 0xFF - ((seLightness >> 1) * (seLightness >> 1) >> 8);
                            pixels[pixelOffset] = ((overlayRgb & 0xFF00FF) * lig & ~0xFF00FF) +
                                    ((overlayRgb & 0xFF00) * lig & 0xFF0000) >> 8;
                        }
                        if (points[indices[shapeOffset++]] != 0)
                        {
                            int lig = 0xFF - ((seLightness * 3 + neLightness >> 3) *
                                    (seLightness * 3 + neLightness >> 3) >> 8);
                            pixels[pixelOffset + 1] = ((overlayRgb & 0xFF00FF) * lig & ~0xFF00FF) +
                                    ((overlayRgb & 0xFF00) * lig & 0xFF0000) >> 8;
                        }
                        if (points[indices[shapeOffset++]] != 0)
                        {
                            int lig = 0xFF - ((seLightness + neLightness >> 2) *
                                    (seLightness + neLightness >> 2) >> 8);
                            pixels[pixelOffset + 2] = ((overlayRgb & 0xFF00FF) * lig & ~0xFF00FF) +
                                    ((overlayRgb & 0xFF00) * lig & 0xFF0000) >> 8;
                        }
                        if (points[indices[shapeOffset++]] != 0)
                        {
                            int lig = 0xFF - ((seLightness + neLightness * 3 >> 3) *
                                    (seLightness + neLightness * 3 >> 3) >> 8);
                            pixels[pixelOffset + 3] = ((overlayRgb & 0xFF00FF) * lig & ~0xFF00FF) +
                                    ((overlayRgb & 0xFF00) * lig & 0xFF0000) >> 8;
                        }
                    }
                    seLightness += southDeltaLightness;
                    neLightness += northDeltaLightness;

                    pixelOffset += 512;
                }
                if (underlayRgb != 0 && sceneTileModel.getUnderlaySwColor() != INVALID_HSL_COLOR)
                {
                    pixelOffset -= 512 << 2;
                    shapeOffset -= 16;
                    hs = sceneTileModel.getUnderlaySwColor() & ~0x7F;
                    seLightness = sceneTileModel.getUnderlaySeColor() & 0x7F;
                    neLightness = sceneTileModel.getUnderlayNeColor() & 0x7F;
                    southDeltaLightness = (sceneTileModel.getUnderlaySwColor() & 0x7F) - seLightness;
                    northDeltaLightness = (sceneTileModel.getUnderlayNwColor() & 0x7F) - neLightness;
                    seLightness <<= 2;
                    neLightness <<= 2;
                    for (int i = 0; i < 4; i++)
                    {
                        if (points[indices[shapeOffset++]] == 0)
                        {
                            pixels[pixelOffset] = Rasterizer3D.hslToRgb[hs | (seLightness >> 2)];
                        }
                        if (points[indices[shapeOffset++]] == 0)
                        {
                            pixels[pixelOffset + 1] = Rasterizer3D.hslToRgb[hs | (seLightness * 3 + neLightness >> 4)];
                        }
                        if (points[indices[shapeOffset++]] == 0)
                        {
                            pixels[pixelOffset + 2] = Rasterizer3D.hslToRgb[hs | (seLightness + neLightness >> 3)];
                        }
                        if (points[indices[shapeOffset++]] == 0)
                        {
                            pixels[pixelOffset + 3] = Rasterizer3D.hslToRgb[hs | (seLightness + neLightness * 3 >> 4)];
                        }
                        seLightness += southDeltaLightness;
                        neLightness += northDeltaLightness;

                        pixelOffset += 512;
                    }
                }
            }
            else if (underlayRgb != 0)
            {
                for (int i = 0; i < 4; i++)
                {
                    pixels[pixelOffset] = points[indices[shapeOffset++]] != 0 ? overlayRgb : underlayRgb;
                    pixels[pixelOffset + 1] =
                            points[indices[shapeOffset++]] != 0 ? overlayRgb : underlayRgb;
                    pixels[pixelOffset + 2] =
                            points[indices[shapeOffset++]] != 0 ? overlayRgb : underlayRgb;
                    pixels[pixelOffset + 3] =
                            points[indices[shapeOffset++]] != 0 ? overlayRgb : underlayRgb;
                    pixelOffset += 512;
                }
            }
            else
            {
                for (int i = 0; i < 4; i++)
                {
                    if (points[indices[shapeOffset++]] != 0)
                    {
                        pixels[pixelOffset] = overlayRgb;
                    }
                    if (points[indices[shapeOffset++]] != 0)
                    {
                        pixels[pixelOffset + 1] = overlayRgb;
                    }
                    if (points[indices[shapeOffset++]] != 0)
                    {
                        pixels[pixelOffset + 2] = overlayRgb;
                    }
                    if (points[indices[shapeOffset++]] != 0)
                    {
                        pixels[pixelOffset + 3] = overlayRgb;
                    }
                    pixelOffset += 512;
                }
            }
        }
    }

    public void drawTileMinimapSD(int pixels[], int drawIndex, int zLoc, int xLoc, int yLoc) {
        int leftOverWidth = 512;// was parameter
        Tile tile = tileArray[zLoc][xLoc][yLoc];
        if (tile == null)
            return;
        SimpleTile simpleTile = tile.mySimpleTile;
        if (simpleTile != null) {
            int tileRGB = simpleTile.getColourRGB();
            if (tileRGB == 0)
                return;
            for (int i = 0; i < 4; i++) {
                pixels[drawIndex] = tileRGB;
                pixels[drawIndex + 1] = tileRGB;
                pixels[drawIndex + 2] = tileRGB;
                pixels[drawIndex + 3] = tileRGB;
                drawIndex += leftOverWidth;
            }
            return;
        }
        ShapedTile shapedTile = tile.myShapedTile;

        if (shapedTile == null) {
            return;
        }

        int shape = shapedTile.shape;
        int rotation = shapedTile.rotation;
        int underlayRGB = shapedTile.colourRGB;
        int overlayRGB = shapedTile.colourRGBA;
        int shapePoints[] = tileVertices[shape];
        int shapePointIndices[] = tileVertexIndices[rotation];
        int shapePtr = 0;
        if (underlayRGB != 0) {
            for (int i = 0; i < 4; i++) {
                pixels[drawIndex] = shapePoints[shapePointIndices[shapePtr++]] != 0 ? overlayRGB : underlayRGB;
                pixels[drawIndex + 1] = shapePoints[shapePointIndices[shapePtr++]] != 0 ? overlayRGB : underlayRGB;
                pixels[drawIndex + 2] = shapePoints[shapePointIndices[shapePtr++]] != 0 ? overlayRGB : underlayRGB;
                pixels[drawIndex + 3] = shapePoints[shapePointIndices[shapePtr++]] != 0 ? overlayRGB : underlayRGB;
                drawIndex += leftOverWidth;
            }
            return;
        }
        for (int i = 0; i < 4; i++) {
            if (shapePoints[shapePointIndices[shapePtr++]] != 0)
                pixels[drawIndex] = overlayRGB;
            if (shapePoints[shapePointIndices[shapePtr++]] != 0)
                pixels[drawIndex + 1] = overlayRGB;
            if (shapePoints[shapePointIndices[shapePtr++]] != 0)
                pixels[drawIndex + 2] = overlayRGB;
            if (shapePoints[shapePointIndices[shapePtr++]] != 0)
                pixels[drawIndex + 3] = overlayRGB;
            drawIndex += leftOverWidth;
        }
    }

    public static void buildVisibilityMap(int i, int j, int viewportWidth, int viewportHeight, int ai[]) {
        xMin = 0;
        yMin = 0;
        SceneGraph.xMax = viewportWidth;
        SceneGraph.yMax = viewportHeight;
        viewportHalfWidth = viewportWidth / 2;
        viewportHalfHeight = viewportHeight / 2;
        boolean aflag[][][][] = new boolean[9][32][53][53];
        for (int zAngle = 128; zAngle <= 384; zAngle += 32) {
            for (int xyAngle = 0; xyAngle < 2048; xyAngle += 64) {
                camUpDownY = Model.SINE[zAngle];
                camUpDownX = Model.COSINE[zAngle];
                camLeftRightY = Model.SINE[xyAngle];
                camLeftRightX = Model.COSINE[xyAngle];
                int angularZSegment = (zAngle - 128) / 32;
                int angularXYSegment = xyAngle / 64;
                for (int xRelativeToCamera = -26; xRelativeToCamera <= 26; xRelativeToCamera++) {
                    for (int yRelativeToCamera = -26; yRelativeToCamera <= 26; yRelativeToCamera++) {
                        int xRelativeToCameraPos = xRelativeToCamera * 128;
                        int yRelativeToCameraPos = yRelativeToCamera * 128;
                        boolean flag2 = false;
                        for (int k4 = -i; k4 <= j; k4 += 128) {
                            if (!method311(ai[angularZSegment] + k4, yRelativeToCameraPos, xRelativeToCameraPos))
                                continue;
                            flag2 = true;
                            break;
                        }
                        aflag[angularZSegment][angularXYSegment][xRelativeToCamera + 26][yRelativeToCamera + 26] = flag2;
                    }
                }
            }
        }

        for (int angularZSegment = 0; angularZSegment < 8; angularZSegment++) {
            for (int angularXYSegment = 0; angularXYSegment < 32; angularXYSegment++) {
                for (int xRelativeToCamera = -25; xRelativeToCamera < 25; xRelativeToCamera++) {
                    for (int yRelativeToCamera = -25; yRelativeToCamera < 25; yRelativeToCamera++) {
                        boolean flag1 = false;
                        label0:
                        for (int l3 = -1; l3 <= 1; l3++) {
                            for (int j4 = -1; j4 <= 1; j4++) {
                                if (aflag[angularZSegment][angularXYSegment][xRelativeToCamera + l3 + 26][yRelativeToCamera + j4 + 26])
                                    flag1 = true;
                                else if (aflag[angularZSegment][(angularXYSegment + 1) % 31][xRelativeToCamera + l3 + 26][yRelativeToCamera + j4 + 26])
                                    flag1 = true;
                                else if (aflag[angularZSegment + 1][angularXYSegment][xRelativeToCamera + l3 + 26][yRelativeToCamera + j4 + 26]) {
                                    flag1 = true;
                                } else {
                                    if (!aflag[angularZSegment + 1][(angularXYSegment + 1) % 31][xRelativeToCamera + l3 + 26][yRelativeToCamera + j4 + 26])
                                        continue;
                                    flag1 = true;
                                }
                                break label0;
                            }
                        }
                        visibilityMap[angularZSegment][angularXYSegment][xRelativeToCamera + 25][yRelativeToCamera + 25] = flag1;
                    }
                }
            }
        }
    }

    private static boolean method311(int i, int j, int k) {
        int l = j * camLeftRightY + k * camLeftRightX >> 16;
        int i1 = j * camLeftRightX - k * camLeftRightY >> 16;
        int j1 = i * camUpDownY + i1 * camUpDownX >> 16;
        int k1 = i * camUpDownX - i1 * camUpDownY >> 16;
        if (j1 < 50 || j1 >= 3500) {
            return false;
        }

        int l1 = viewportHalfWidth + l * Rasterizer3D.fieldOfView / j1;
        int i2 = viewportHalfHeight + k1 * Rasterizer3D.fieldOfView / j1;
        return l1 >= xMin && l1 <= xMax && i2 >= yMin && i2 <= yMax;
    }

    /**
     * Clicks on the screen and requests recomputation of the clicked tile.
     * @param clickY The click's Y-coordinate on the applet.
     * @param clickX The click's X-coordinate on the applet.
     */
    public void clickTile(int clickY, int clickX) {
        clicked = true;
        clickScreenX = clickX;
        clickScreenY = clickY;
        clickedTileX = -1;
        clickedTileY = -1;
    }


    private static final int INVALID_HSL_COLOR = 12345678;
    private static final int DEFAULT_DISTANCE = 25;
    private static final int PITCH_LOWER_LIMIT = 128;
    private static final int PITCH_UPPER_LIMIT = 383;

    /**
     * Renders the terrain.
     * The coordinates use the WorldCoordinate Axes but the modelWorld coordinates.
     * @param cameraXPos The cameraViewpoint's X-coordinate.
     * @param cameraYPos The cameraViewpoint's Y-coordinate.
     * @param camAngleXY The cameraAngle in the XY-plain.
     * @param cameraZPos The cameraViewpoint's X-coordinate.
     * @param planeZ The plain the camera's looking at.
     * @param camAngleZ  The cameraAngle on the Z-axis.
     */
    public void render(int cameraXPos, int cameraYPos, int camAngleXY, int cameraZPos, int planeZ, int camAngleZ) {

        final DrawCallbacks drawCallbacks = Client.instance.getDrawCallbacks();
        if (drawCallbacks != null)
        {
            Client.instance.getDrawCallbacks().drawScene(cameraXPos, cameraZPos, cameraYPos, camAngleZ, camAngleXY, planeZ);
        }

        final boolean isGpu = Client.instance.isGpu();
        final boolean checkClick = Client.instance.isCheckClick();
        final boolean menuOpen = Client.instance.isMenuOpen();

        if (!menuOpen && !checkClick)
        {
            Client.instance.getScene().menuOpen(Client.instance.getPlane(), Client.instance.getMouseX() - Client.instance.getViewportXOffset(), Client.instance.getMouseY() - Client.instance.getViewportYOffset(), false);
        }

        if (!isGpu && skyboxColor != 0)
        {
            Client.instance.rasterizerFillRectangle(
                    Client.instance.getViewportXOffset(),
                    Client.instance.getViewportYOffset(),
                    Client.instance.getViewportWidth(),
                    Client.instance.getViewportHeight(),
                    skyboxColor
            );
        }

        final int maxX = getMaxX();
        final int maxY = getMaxY();
        final int maxZ = getMaxZ();

        final int minLevel = getMinLevel();

        final RSTile[][][] tiles = getTiles();
        final int distance = isGpu ? drawDistance : DEFAULT_DISTANCE;

        if (cameraXPos < 0)
        {
            cameraXPos = 0;
        }
        else if (cameraXPos >= maxX * Perspective.LOCAL_TILE_SIZE)
        {
            cameraXPos = maxX * Perspective.LOCAL_TILE_SIZE - 1;
        }

        if (cameraYPos < 0)
        {
            cameraYPos = 0;
        }
        else if (cameraYPos >= maxZ * Perspective.LOCAL_TILE_SIZE)
        {
            cameraYPos = maxZ * Perspective.LOCAL_TILE_SIZE - 1;
        }


        // we store the uncapped pitch for setting camera angle for the pitch relaxer
        // we still have to cap the pitch in order to access the visibility map, though
        int realPitch = camAngleZ;
        if (camAngleZ < PITCH_LOWER_LIMIT)
        {
            camAngleZ = PITCH_LOWER_LIMIT;
        }
        else if (camAngleZ > PITCH_UPPER_LIMIT)
        {
            camAngleZ = PITCH_UPPER_LIMIT;
        }
        if (!pitchRelaxEnabled)
        {
            realPitch = camAngleZ;
        }


        Client.instance.setCycle(Client.instance.getCycle() + 1);
        Client.instance.setPitchSin(Perspective.SINE[realPitch]);
        Client.instance.setPitchCos(Perspective.COSINE[realPitch]);
        Client.instance.setYawSin(Perspective.SINE[camAngleXY]);
        Client.instance.setYawCos(Perspective.COSINE[camAngleXY]);


        final int[][][] tileHeights = Client.instance.getTileHeights();
        boolean[][] renderArea = Client.instance.getVisibilityMaps()[(camAngleZ - 128) / 32][camAngleXY / 64];
        Client.instance.setRenderArea(renderArea);

        Client.instance.setCameraX2(cameraXPos);
        Client.instance.setCameraY2(cameraZPos);
        Client.instance.setCameraZ2(cameraYPos);

        int screenCenterX = cameraXPos / Perspective.LOCAL_TILE_SIZE;
        int screenCenterZ = cameraYPos / Perspective.LOCAL_TILE_SIZE;

        Client.instance.setScreenCenterX(screenCenterX);
        Client.instance.setScreenCenterZ(screenCenterZ);
        Client.instance.setScenePlane(planeZ);

        int minTileX = screenCenterX - distance;
        if (minTileX < 0)
        {
            minTileX = 0;
        }

        int minTileZ = screenCenterZ - distance;
        if (minTileZ < 0)
        {
            minTileZ = 0;
        }

        int maxTileX = screenCenterX + distance;
        if (maxTileX > maxX)
        {
            maxTileX = maxX;
        }

        int maxTileZ = screenCenterZ + distance;
        if (maxTileZ > maxZ)
        {
            maxTileZ = maxZ;
        }

        Client.instance.setMinTileX(minTileX);
        Client.instance.setMinTileZ(minTileZ);
        Client.instance.setMaxTileX(maxTileX);
        Client.instance.setMaxTileZ(maxTileZ);

        updateOccluders();

        Client.instance.setTileUpdateCount(0);

        if (roofRemovalMode != 0)
        {
            tilesToRemove.clear();
            RSPlayer localPlayer = Client.instance.getLocalPlayer();
            if (localPlayer != null && (roofRemovalMode & ROOF_FLAG_POSITION) != 0)
            {
                LocalPoint localLocation = localPlayer.getLocalLocation();
                if (localLocation.isInScene())
                {
                    tilesToRemove.add(tileArray[Client.instance.getPlane()][localLocation.getSceneX()][localLocation.getSceneY()]);
                }
            }

            if (hoverX >= 0 && hoverX < 104 && hoverY >= 0 && hoverY < 104 && (roofRemovalMode & ROOF_FLAG_HOVERED) != 0)
            {
                tilesToRemove.add(tileArray[Client.instance.getPlane()][hoverX][hoverY]);
            }

            LocalPoint localDestinationLocation = Client.instance.getLocalDestinationLocation();
            if (localDestinationLocation != null && localDestinationLocation.isInScene() && (roofRemovalMode & ROOF_FLAG_DESTINATION) != 0)
            {
                tilesToRemove.add(tileArray[Client.instance.getPlane()][localDestinationLocation.getSceneX()][localDestinationLocation.getSceneY()]);
            }

            if (Client.instance.getCameraPitch() < 310 && (roofRemovalMode & ROOF_FLAG_BETWEEN) != 0 && localPlayer != null)
            {
                int playerX = localPlayer.getX() >> 7;
                int playerY = localPlayer.getY() >> 7;
                int var29 = Client.instance.getCameraX() >> 7;
                int var30 = Client.instance.getCameraY() >> 7;
                if (playerX >= 0 && playerY >= 0 && var29 >= 0 && var30 >= 0 && playerX < 104 && playerY < 104 && var29 < 104 && var30 < 104)
                {
                    int var31 = Math.abs(playerX - var29);
                    int var32 = Integer.compare(playerX, var29);
                    int var33 = -Math.abs(playerY - var30);
                    int var34 = Integer.compare(playerY, var30);
                    int var35 = var31 + var33;

                    while (var29 != playerX || var30 != playerY)
                    {
                        if (blocking(Client.instance.getPlane(), var29, var30))
                        {
                            tilesToRemove.add(tileArray[Client.instance.getPlane()][var29][var30]);
                        }

                        int var36 = 2 * var35;
                        if (var36 >= var33)
                        {
                            var35 += var33;
                            var29 += var32;
                        }
                        else
                        {
                            var35 += var31;
                            var30 += var34;
                        }
                    }
                }
            }
        }

        if (!menuOpen)
        {
            hoverY = -1;
            hoverX = -1;
        }

        for (int z = minLevel; z < maxY; ++z)
        {
            RSTile[][] planeTiles = tileArray[z];



            for (int x = minTileX; x < maxTileX; ++x)
            {
                for (int y = minTileZ; y < maxTileZ; ++y)
                {
                    RSTile tile = planeTiles[x][y];
                    if (tile != null)
                    {

                        RSTile var30 = tileArray[Client.instance.getPlane()][x][y];
                        if (tile.getPhysicalLevel() > planeZ && roofRemovalMode == 0
                                || !isGpu && !renderArea[x - screenCenterX + DEFAULT_DISTANCE][y - screenCenterZ + DEFAULT_DISTANCE]
                                && tileHeights[z][x][y] - cameraYPos < 2000
                                || roofRemovalMode != 0 && Client.instance.getPlane() < tile.getPhysicalLevel()
                                && tilesToRemove.contains(var30))
                        {
                            tile.setDraw(false);
                            tile.setVisible(false);
                            tile.setWallCullDirection(0);
                        }
                        else
                        {
                            tile.setDraw(true);
                            tile.setVisible(true);
                            tile.setDrawEntities(true);
                            Client.instance.setTileUpdateCount(Client.instance.getTileUpdateCount() + 1);
                        }
                    }
                }
            }
        }

        for (int z = minLevel; z < maxY; ++z)
        {
            RSTile[][] planeTiles = tileArray[z];

            for (int x = -distance; x <= 0; ++x)
            {
                int var10 = x + screenCenterX;
                int var16 = screenCenterX - x;
                if (var10 >= minTileX || var16 < maxTileX)
                {
                    for (int y = -distance; y <= 0; ++y)
                    {
                        int var13 = y + screenCenterZ;
                        int var14 = screenCenterZ - y;
                        if (var10 >= minTileX)
                        {
                            if (var13 >= minTileZ)
                            {
                                RSTile tile = planeTiles[var10][var13];
                                if (tile != null && tile.isDraw())
                                {
                                    draw(tile, true);
                                }
                            }

                            if (var14 < maxTileZ)
                            {
                                RSTile tile = planeTiles[var10][var14];
                                if (tile != null && tile.isDraw())
                                {
                                    draw(tile, true);
                                }
                            }
                        }

                        if (var16 < maxTileX)
                        {
                            if (var13 >= minTileZ)
                            {
                                RSTile tile = planeTiles[var16][var13];
                                if (tile != null && tile.isDraw())
                                {
                                    draw(tile, true);
                                }
                            }

                            if (var14 < maxTileZ)
                            {
                                RSTile tile = planeTiles[var16][var14];
                                if (tile != null && tile.isDraw())
                                {
                                    draw(tile, true);
                                }
                            }
                        }

                        if (Client.instance.getTileUpdateCount() == 0)
                        {
                            if (!isGpu && (Client.instance.getOculusOrbState() != 0 && !Client.instance.getComplianceValue("orbInteraction")))
                            {
                                Client.instance.setEntitiesAtMouseCount(0);
                            }
                            Client.instance.setCheckClick(false);
                            Client.instance.getCallbacks().drawScene();

                            if (Client.instance.getDrawCallbacks() != null)
                            {
                                Client.instance.getDrawCallbacks().postDrawScene();
                            }

                            return;
                        }
                    }
                }
            }
        }

        outer:
        for (int z = minLevel; z < maxY; ++z)
        {
            RSTile[][] planeTiles = tiles[z];

            for (int x = -distance; x <= 0; ++x)
            {
                int var10 = x + screenCenterX;
                int var16 = screenCenterX - x;
                if (var10 >= minTileX || var16 < maxTileX)
                {
                    for (int y = -distance; y <= 0; ++y)
                    {
                        int var13 = y + screenCenterZ;
                        int var14 = screenCenterZ - y;
                        if (var10 >= minTileX)
                        {
                            if (var13 >= minTileZ)
                            {
                                RSTile tile = planeTiles[var10][var13];
                                if (tile != null && tile.isDraw())
                                {
                                    draw(tile, false);
                                }
                            }

                            if (var14 < maxTileZ)
                            {
                                RSTile tile = planeTiles[var10][var14];
                                if (tile != null && tile.isDraw())
                                {
                                    draw(tile, false);
                                }
                            }
                        }

                        if (var16 < maxTileX)
                        {
                            if (var13 >= minTileZ)
                            {
                                RSTile tile = planeTiles[var16][var13];
                                if (tile != null && tile.isDraw())
                                {
                                    draw(tile, false);
                                }
                            }

                            if (var14 < maxTileZ)
                            {
                                RSTile tile = planeTiles[var16][var14];
                                if (tile != null && tile.isDraw())
                                {
                                    draw(tile, false);
                                }
                            }
                        }

                        if (Client.instance.getTileUpdateCount() == 0)
                        {
                            // exit the loop early and go straight to "if (!isGpu && (Client.instance..."
                            break outer;
                        }
                    }
                }
            }
        }

        if (!isGpu && (Client.instance.getOculusOrbState() != 0 && !Client.instance.getComplianceValue("orbInteraction")))
        {
            Client.instance.setEntitiesAtMouseCount(0);
        }
        Client.instance.setCheckClick(false);
        Client.instance.getCallbacks().drawScene();
        if (Client.instance.getDrawCallbacks() != null)
        {
            Client.instance.getDrawCallbacks().postDrawScene();
        }
    }

    public static boolean blocking(int plane, int x, int y)
    {
        return (Client.instance.getTileSettings()[plane][x][y] & 4) != 0;
    }

    private void drawTile(Tile tile, boolean flag) {
        tileDeque.insertHead(tile);
        do {
            Tile currentTile;
            do {
                currentTile = (Tile) tileDeque.popHead();
                if (currentTile == null)
                    return;
            } while (!currentTile.aBoolean1323);
            int i = currentTile.anInt1308;
            int j = currentTile.anInt1309;
            int k = currentTile.z1AnInt1307;
            int l = currentTile.anInt1310;
            Tile aclass30_sub3[][] = tileArray[k];
            if (currentTile.aBoolean1322) {
                if (flag) {
                    if (k > 0) {
                        Tile class30_sub3_2 = tileArray[k - 1][i][j];
                        if (class30_sub3_2 != null && class30_sub3_2.aBoolean1323)
                            continue;
                    }
                    if (i <= screenCenterX && i > minTileX) {
                        Tile class30_sub3_3 = aclass30_sub3[i - 1][j];
                        if (class30_sub3_3 != null && class30_sub3_3.aBoolean1323
                                && (class30_sub3_3.aBoolean1322 || (currentTile.totalTiledObjectMask & 1) == 0))
                            continue;
                    }
                    if (i >= screenCenterX && i < maxTileX - 1) {
                        Tile class30_sub3_4 = aclass30_sub3[i + 1][j];
                        if (class30_sub3_4 != null && class30_sub3_4.aBoolean1323
                                && (class30_sub3_4.aBoolean1322 || (currentTile.totalTiledObjectMask & 4) == 0))
                            continue;
                    }
                    if (j <= screenCenterZ && j > minTileZ) {
                        Tile class30_sub3_5 = aclass30_sub3[i][j - 1];
                        if (class30_sub3_5 != null && class30_sub3_5.aBoolean1323
                                && (class30_sub3_5.aBoolean1322 || (currentTile.totalTiledObjectMask & 8) == 0))
                            continue;
                    }
                    if (j >= screenCenterZ && j < maxTileZ - 1) {
                        Tile class30_sub3_6 = aclass30_sub3[i][j + 1];
                        if (class30_sub3_6 != null && class30_sub3_6.aBoolean1323
                                && (class30_sub3_6.aBoolean1322 || (currentTile.totalTiledObjectMask & 2) == 0))
                            continue;
                    }
                } else {
                    flag = true;
                }
                currentTile.aBoolean1322 = false;
                if (currentTile.firstFloorTile != null) {
                    Tile class30_sub3_7 = currentTile.firstFloorTile;
                    if (class30_sub3_7.mySimpleTile != null) {
                        if (!method320(0, i, j))
                            drawTileUnderlay(class30_sub3_7.mySimpleTile, 0, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX, i, j);
                    } else if (class30_sub3_7.myShapedTile != null && !method320(0, i, j))
                        drawTileOverlay(i, camUpDownY, camLeftRightY,
                                class30_sub3_7.myShapedTile, camUpDownX, j,
                                camLeftRightX);
                    WallObject class10 = class30_sub3_7.wallObject;
                    if (class10 != null)
                        class10.renderable1.renderAtPoint(0, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX,
                                class10.xPos - xCameraPos, class10.tileHeights - zCameraPos, class10.yPos - yCameraPos,
                                class10.uid);
                    for (int i2 = 0; i2 < class30_sub3_7.gameObjectIndex; i2++) {
                        GameObject class28 = class30_sub3_7.gameObjects[i2];
                        if (class28 != null)
                            class28.renderable.renderAtPoint(class28.turnValue, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX, class28.xPos - xCameraPos, class28.tileHeight - zCameraPos, class28.yPos - yCameraPos, class28.uid);
                    }

                }
                boolean flag1 = false;
                if (currentTile.mySimpleTile != null) {
                    if (!method320(l, i, j)) {
                        flag1 = true;
                        drawTileUnderlay(currentTile.mySimpleTile, l, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX, i, j);
                    }
                } else if (currentTile.myShapedTile != null && !method320(l, i, j)) {
                    flag1 = true;
                    drawTileOverlay(i, camUpDownY, camLeftRightY,
                            currentTile.myShapedTile, camUpDownX, j, camLeftRightX);
                }
                int j1 = 0;
                int j2 = 0;
                WallObject class10_3 = currentTile.wallObject;
                WallDecoration class26_1 = currentTile.wallDecoration;
                if (class10_3 != null || class26_1 != null) {
                    if (screenCenterX == i)
                        j1++;
                    else if (screenCenterX < i)
                        j1 += 2;
                    if (screenCenterZ == j)
                        j1 += 3;
                    else if (screenCenterZ > j)
                        j1 += 6;
                    j2 = anIntArray478[j1];
                    currentTile.anInt1328 = anIntArray480[j1];
                }
                if (class10_3 != null) {
                    if ((class10_3.orientation1 & anIntArray479[j1]) != 0) {
                        if (class10_3.orientation1 == 16) {
                            currentTile.wallCullDirection = 3;
                            currentTile.anInt1326 = anIntArray481[j1];
                            currentTile.anInt1327 = 3 - currentTile.anInt1326;
                        } else if (class10_3.orientation1 == 32) {
                            currentTile.wallCullDirection = 6;
                            currentTile.anInt1326 = anIntArray482[j1];
                            currentTile.anInt1327 = 6 - currentTile.anInt1326;
                        } else if (class10_3.orientation1 == 64) {
                            currentTile.wallCullDirection = 12;
                            currentTile.anInt1326 = anIntArray483[j1];
                            currentTile.anInt1327 = 12 - currentTile.anInt1326;
                        } else {
                            currentTile.wallCullDirection = 9;
                            currentTile.anInt1326 = anIntArray484[j1];
                            currentTile.anInt1327 = 9 - currentTile.anInt1326;
                        }
                    } else {
                        currentTile.wallCullDirection = 0;
                    }
                    if ((class10_3.orientation1 & j2) != 0 && !method321(l, i, j, class10_3.orientation1))
                        class10_3.renderable1.renderAtPoint(0, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX,
                                class10_3.xPos - xCameraPos, class10_3.tileHeights - zCameraPos,
                                class10_3.yPos - yCameraPos, class10_3.uid);
                    if ((class10_3.orientation2 & j2) != 0 && !method321(l, i, j, class10_3.orientation2))
                        class10_3.renderable2.renderAtPoint(0, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX,
                                class10_3.xPos - xCameraPos, class10_3.tileHeights - zCameraPos,
                                class10_3.yPos - yCameraPos, class10_3.uid);
                }
                if (class26_1 != null && !method322(l, i, j, class26_1.renderable.modelBaseY))
                    if ((class26_1.orientation & j2) != 0)
                        class26_1.renderable.renderAtPoint(class26_1.orientation2, camUpDownY, camUpDownX, camLeftRightY,
                                camLeftRightX, class26_1.xPos - xCameraPos, class26_1.tileHeights - zCameraPos,
                                class26_1.yPos - yCameraPos, class26_1.uid);
                    else if ((class26_1.orientation & 0x300) != 0) {
                        int j4 = class26_1.xPos - xCameraPos;
                        int l5 = class26_1.tileHeights - zCameraPos;
                        int k6 = class26_1.yPos - yCameraPos;
                        int i8 = class26_1.orientation2;
                        int k9;
                        if (i8 == 1 || i8 == 2)
                            k9 = -j4;
                        else
                            k9 = j4;
                        int k10;
                        if (i8 == 2 || i8 == 3)
                            k10 = -k6;
                        else
                            k10 = k6;
                        if ((class26_1.orientation & 0x100) != 0 && k10 < k9) {
                            int i11 = j4 + anIntArray463[i8];
                            int k11 = k6 + anIntArray464[i8];
                            class26_1.renderable.renderAtPoint(i8 * 512 + 256, camUpDownY, camUpDownX, camLeftRightY,
                                    camLeftRightX, i11, l5, k11, class26_1.uid);
                        }
                        if ((class26_1.orientation & 0x200) != 0 && k10 > k9) {
                            int j11 = j4 + anIntArray465[i8];
                            int l11 = k6 + anIntArray466[i8];
                            class26_1.renderable.renderAtPoint(i8 * 512 + 1280 & 0x7ff, camUpDownY, camUpDownX,
                                    camLeftRightY, camLeftRightX, j11, l5, l11, class26_1.uid);
                        }
                    }
                if (flag1) {
                    GroundDecoration class49 = currentTile.groundDecoration;
                    if (class49 != null)
                        class49.renderable.renderAtPoint(0, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX,
                                class49.xPos - xCameraPos, class49.tileHeights - zCameraPos, class49.yPos - yCameraPos,
                                class49.uid);
                    GroundItemTile object4_1 = currentTile.groundItemTile;
                    if (object4_1 != null && object4_1.itemDropHeight == 0) {
                        if (object4_1.lowerNode != null)
                            object4_1.lowerNode.renderAtPoint(0, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX,
                                    object4_1.xPos - xCameraPos, object4_1.tileHeights - zCameraPos,
                                    object4_1.yPos - yCameraPos, object4_1.uid);
                        if (object4_1.middleNode != null)
                            object4_1.middleNode.renderAtPoint(0, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX,
                                    object4_1.xPos - xCameraPos, object4_1.tileHeights - zCameraPos,
                                    object4_1.yPos - yCameraPos, object4_1.uid);
                        if (object4_1.topNode != null)
                            object4_1.topNode.renderAtPoint(0, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX,
                                    object4_1.xPos - xCameraPos, object4_1.tileHeights - zCameraPos,
                                    object4_1.yPos - yCameraPos, object4_1.uid);
                    }
                }
                int k4 = currentTile.totalTiledObjectMask;
                if (k4 != 0) {
                    if (i < screenCenterX && (k4 & 4) != 0) {
                        Tile class30_sub3_17 = aclass30_sub3[i + 1][j];
                        if (class30_sub3_17 != null && class30_sub3_17.aBoolean1323)
                            tileDeque.insertHead(class30_sub3_17);
                    }
                    if (j < screenCenterZ && (k4 & 2) != 0) {
                        Tile class30_sub3_18 = aclass30_sub3[i][j + 1];
                        if (class30_sub3_18 != null && class30_sub3_18.aBoolean1323)
                            tileDeque.insertHead(class30_sub3_18);
                    }
                    if (i > screenCenterX && (k4 & 1) != 0) {
                        Tile class30_sub3_19 = aclass30_sub3[i - 1][j];
                        if (class30_sub3_19 != null && class30_sub3_19.aBoolean1323)
                            tileDeque.insertHead(class30_sub3_19);
                    }
                    if (j > screenCenterZ && (k4 & 8) != 0) {
                        Tile class30_sub3_20 = aclass30_sub3[i][j - 1];
                        if (class30_sub3_20 != null && class30_sub3_20.aBoolean1323)
                            tileDeque.insertHead(class30_sub3_20);
                    }
                }
            }
            if (currentTile.wallCullDirection != 0) {
                boolean flag2 = true;
                for (int k1 = 0; k1 < currentTile.gameObjectIndex; k1++) {
                    if (currentTile.gameObjects[k1].anInt528 == cycle || (currentTile.tiledObjectMasks[k1]
                            & currentTile.wallCullDirection) != currentTile.anInt1326)
                        continue;
                    flag2 = false;
                    break;
                }

                if (flag2) {
                    WallObject class10_1 = currentTile.wallObject;
                    if (!method321(l, i, j, class10_1.orientation1))
                        class10_1.renderable1.renderAtPoint(0, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX,
                                class10_1.xPos - xCameraPos, class10_1.tileHeights - zCameraPos,
                                class10_1.yPos - yCameraPos, class10_1.uid);
                    currentTile.wallCullDirection = 0;
                }
            }
            if (currentTile.aBoolean1324)
                try {
                    int i1 = currentTile.gameObjectIndex;
                    currentTile.aBoolean1324 = false;
                    int l1 = 0;
                    label0: for (int k2 = 0; k2 < i1; k2++) {
                        GameObject class28_1 = currentTile.gameObjects[k2];
                        if (class28_1.anInt528 == cycle)
                            continue;
                        for (int k3 = class28_1.xLocLow; k3 <= class28_1.xLocHigh; k3++) {
                            for (int l4 = class28_1.yLocHigh; l4 <= class28_1.yLocLow; l4++) {
                                Tile class30_sub3_21 = aclass30_sub3[k3][l4];
                                if (class30_sub3_21.aBoolean1322) {
                                    currentTile.aBoolean1324 = true;
                                } else {
                                    if (class30_sub3_21.wallCullDirection == 0)
                                        continue;
                                    int l6 = 0;
                                    if (k3 > class28_1.xLocLow)
                                        l6++;
                                    if (k3 < class28_1.xLocHigh)
                                        l6 += 4;
                                    if (l4 > class28_1.yLocHigh)
                                        l6 += 8;
                                    if (l4 < class28_1.yLocLow)
                                        l6 += 2;
                                    if ((l6 & class30_sub3_21.wallCullDirection) != currentTile.anInt1327)
                                        continue;
                                    currentTile.aBoolean1324 = true;
                                }
                                continue label0;
                            }

                        }

                        interactableObjects[l1++] = class28_1;
                        int i5 = screenCenterX - class28_1.xLocLow;
                        int i6 = class28_1.xLocHigh - screenCenterX;
                        if (i6 > i5)
                            i5 = i6;
                        int i7 = screenCenterZ - class28_1.yLocHigh;
                        int j8 = class28_1.yLocLow - screenCenterZ;
                        if (j8 > i7)
                            class28_1.anInt527 = i5 + j8;
                        else
                            class28_1.anInt527 = i5 + i7;
                    }

                    while (l1 > 0) {
                        int i3 = -50;
                        int l3 = -1;
                        for (int j5 = 0; j5 < l1; j5++) {
                            GameObject class28_2 = interactableObjects[j5];
                            if (class28_2.anInt528 != cycle)
                                if (class28_2.anInt527 > i3) {
                                    i3 = class28_2.anInt527;
                                    l3 = j5;
                                } else if (class28_2.anInt527 == i3) {
                                    int j7 = class28_2.xPos - xCameraPos;
                                    int k8 = class28_2.yPos - yCameraPos;
                                    int l9 = interactableObjects[l3].xPos - xCameraPos;
                                    int l10 = interactableObjects[l3].yPos - yCameraPos;
                                    if (j7 * j7 + k8 * k8 > l9 * l9 + l10 * l10)
                                        l3 = j5;
                                }
                        }

                        if (l3 == -1)
                            break;
                        GameObject class28_3 = interactableObjects[l3];
                        class28_3.anInt528 = cycle;
                        if (!method323(l, class28_3.xLocLow, class28_3.xLocHigh, class28_3.yLocHigh,
                                class28_3.yLocLow, class28_3.renderable.modelBaseY))
                            class28_3.renderable.renderAtPoint(class28_3.turnValue, camUpDownY, camUpDownX, camLeftRightY,
                                    camLeftRightX, class28_3.xPos - xCameraPos, class28_3.tileHeight - zCameraPos,
                                    class28_3.yPos - yCameraPos, class28_3.uid);
                        for (int k7 = class28_3.xLocLow; k7 <= class28_3.xLocHigh; k7++) {
                            for (int l8 = class28_3.yLocHigh; l8 <= class28_3.yLocLow; l8++) {
                                Tile class30_sub3_22 = aclass30_sub3[k7][l8];
                                if (class30_sub3_22.wallCullDirection != 0)
                                    tileDeque.insertHead(class30_sub3_22);
                                else if ((k7 != i || l8 != j) && class30_sub3_22.aBoolean1323)
                                    tileDeque.insertHead(class30_sub3_22);
                            }

                        }

                    }
                    if (currentTile.aBoolean1324)
                        continue;
                } catch (Exception _ex) {
                    currentTile.aBoolean1324 = false;
                }
            if (!currentTile.aBoolean1323 || currentTile.wallCullDirection != 0)
                continue;
            if (i <= screenCenterX && i > minTileX) {
                Tile class30_sub3_8 = aclass30_sub3[i - 1][j];
                if (class30_sub3_8 != null && class30_sub3_8.aBoolean1323)
                    continue;
            }
            if (i >= screenCenterX && i < maxTileX - 1) {
                Tile class30_sub3_9 = aclass30_sub3[i + 1][j];
                if (class30_sub3_9 != null && class30_sub3_9.aBoolean1323)
                    continue;
            }
            if (j <= screenCenterZ && j > minTileZ) {
                Tile class30_sub3_10 = aclass30_sub3[i][j - 1];
                if (class30_sub3_10 != null && class30_sub3_10.aBoolean1323)
                    continue;
            }
            if (j >= screenCenterZ && j < maxTileZ - 1) {
                Tile class30_sub3_11 = aclass30_sub3[i][j + 1];
                if (class30_sub3_11 != null && class30_sub3_11.aBoolean1323)
                    continue;
            }
            currentTile.aBoolean1323 = false;
            tileUpdateCount--;
            GroundItemTile object4 = currentTile.groundItemTile;
            if (object4 != null && object4.itemDropHeight != 0) {
                if (object4.lowerNode != null)
                    object4.lowerNode.renderAtPoint(0, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX,
                            object4.xPos - xCameraPos, object4.tileHeights - zCameraPos - object4.itemDropHeight,
                            object4.yPos - yCameraPos, object4.uid);
                if (object4.middleNode != null)
                    object4.middleNode.renderAtPoint(0, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX,
                            object4.xPos - xCameraPos, object4.tileHeights - zCameraPos - object4.itemDropHeight,
                            object4.yPos - yCameraPos, object4.uid);
                if (object4.topNode != null)
                    object4.topNode.renderAtPoint(0, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX,
                            object4.xPos - xCameraPos, object4.tileHeights - zCameraPos - object4.itemDropHeight,
                            object4.yPos - yCameraPos, object4.uid);
            }
            if (currentTile.anInt1328 != 0) {
                WallDecoration class26 = currentTile.wallDecoration;
                if (class26 != null && !method322(l, i, j, class26.renderable.modelBaseY))
                    if ((class26.orientation & currentTile.anInt1328) != 0)
                        class26.renderable.renderAtPoint(class26.orientation2, camUpDownY, camUpDownX, camLeftRightY,
                                camLeftRightX, class26.xPos - xCameraPos, class26.tileHeights - zCameraPos,
                                class26.yPos - yCameraPos, class26.uid);
                    else if ((class26.orientation & 0x300) != 0) {
                        int l2 = class26.xPos - xCameraPos;
                        int j3 = class26.tileHeights - zCameraPos;
                        int i4 = class26.yPos - yCameraPos;
                        int k5 = class26.orientation2;
                        int j6;
                        if (k5 == 1 || k5 == 2)
                            j6 = -l2;
                        else
                            j6 = l2;
                        int l7;
                        if (k5 == 2 || k5 == 3)
                            l7 = -i4;
                        else
                            l7 = i4;
                        if ((class26.orientation & 0x100) != 0 && l7 >= j6) {
                            int i9 = l2 + anIntArray463[k5];
                            int i10 = i4 + anIntArray464[k5];
                            class26.renderable.renderAtPoint(k5 * 512 + 256, camUpDownY, camUpDownX, camLeftRightY,
                                    camLeftRightX, i9, j3, i10, class26.uid);
                        }
                        if ((class26.orientation & 0x200) != 0 && l7 <= j6) {
                            int j9 = l2 + anIntArray465[k5];
                            int j10 = i4 + anIntArray466[k5];
                            class26.renderable.renderAtPoint(k5 * 512 + 1280 & 0x7ff, camUpDownY, camUpDownX,
                                    camLeftRightY, camLeftRightX, j9, j3, j10, class26.uid);
                        }
                    }
                WallObject class10_2 = currentTile.wallObject;
                if (class10_2 != null) {
                    if ((class10_2.orientation2 & currentTile.anInt1328) != 0
                            && !method321(l, i, j, class10_2.orientation2))
                        class10_2.renderable2.renderAtPoint(0, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX,
                                class10_2.xPos - xCameraPos, class10_2.tileHeights - zCameraPos,
                                class10_2.yPos - yCameraPos, class10_2.uid);
                    if ((class10_2.orientation1 & currentTile.anInt1328) != 0
                            && !method321(l, i, j, class10_2.orientation1))
                        class10_2.renderable1.renderAtPoint(0, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX,
                                class10_2.xPos - xCameraPos, class10_2.tileHeights - zCameraPos,
                                class10_2.yPos - yCameraPos, class10_2.uid);
                }
            }
            if (k < maxY - 1) {
                Tile class30_sub3_12 = tileArray[k + 1][i][j];
                if (class30_sub3_12 != null && class30_sub3_12.aBoolean1323)
                    tileDeque.insertHead(class30_sub3_12);
            }
            if (i < screenCenterX) {
                Tile class30_sub3_13 = aclass30_sub3[i + 1][j];
                if (class30_sub3_13 != null && class30_sub3_13.aBoolean1323)
                    tileDeque.insertHead(class30_sub3_13);
            }
            if (j < screenCenterZ) {
                Tile class30_sub3_14 = aclass30_sub3[i][j + 1];
                if (class30_sub3_14 != null && class30_sub3_14.aBoolean1323)
                    tileDeque.insertHead(class30_sub3_14);
            }
            if (i > screenCenterX) {
                Tile class30_sub3_15 = aclass30_sub3[i - 1][j];
                if (class30_sub3_15 != null && class30_sub3_15.aBoolean1323)
                    tileDeque.insertHead(class30_sub3_15);
            }
            if (j > screenCenterZ) {
                Tile class30_sub3_16 = aclass30_sub3[i][j - 1];
                if (class30_sub3_16 != null && class30_sub3_16.aBoolean1323)
                    tileDeque.insertHead(class30_sub3_16);
            }
        } while (true);
    }

    private void drawTileUnderlay(SimpleTile tile, int z, int pitchSin, int pitchCos, int yawSin, int yawCos, int x, int y) {
        byte[][][] tileSettings = Client.instance.getTileSettings();
        final boolean checkClick = Client.instance.isCheckClick();

        int tilePlane = z;
        if ((tileSettings[1][x][x] & 2) != 0)
        {
            tilePlane = z - 1;
        }

        if (!Client.instance.isGpu())
        {
            try {
                drawTileUnderlaySD(tile, z, pitchSin, pitchCos, yawSin, yawCos, x, y);
            }
            catch (Exception ex)
            {
                Client.instance.getLogger().warn("error during tile underlay rendering", ex);
            }

            if (roofRemovalMode == 0 || !checkClick || Client.instance.getPlane() != tilePlane)
            {
                return;
            }
        }

        final DrawCallbacks drawCallbacks = Client.instance.getDrawCallbacks();

        if (drawCallbacks == null)
        {
            return;
        }

        try
        {
            final int[][][] tileHeights = getTileHeights();

            final int cameraX2 = Client.instance.getCameraX2();
            final int cameraY2 = Client.instance.getCameraY2();
            final int cameraZ2 = Client.instance.getCameraZ2();

            final int zoom = Client.instance.get3dZoom();
            final int centerX = Client.instance.getCenterX();
            final int centerY = Client.instance.getCenterY();

            final int mouseX2 = Client.instance.getMouseX2();
            final int mouseY2 = Client.instance.getMouseY2();

            int var9;
            int var10 = var9 = (x << 7) - cameraX2;
            int var11;
            int var12 = var11 = (y << 7) - cameraZ2;
            int var13;
            int var14 = var13 = var10 + 128;
            int var15;
            int var16 = var15 = var12 + 128;
            int var17 = tileHeights[z][x][y] - cameraY2;
            int var18 = tileHeights[z][x + 1][y] - cameraY2;
            int var19 = tileHeights[z][x + 1][y + 1] - cameraY2;
            int var20 = tileHeights[z][x][y + 1] - cameraY2;
            int var21 = var10 * yawCos + yawSin * var12 >> 16;
            var12 = var12 * yawCos - yawSin * var10 >> 16;
            var10 = var21;
            var21 = var17 * pitchCos - pitchSin * var12 >> 16;
            var12 = pitchSin * var17 + var12 * pitchCos >> 16;
            var17 = var21;
            if (var12 >= 50)
            {
                var21 = var14 * yawCos + yawSin * var11 >> 16;
                var11 = var11 * yawCos - yawSin * var14 >> 16;
                var14 = var21;
                var21 = var18 * pitchCos - pitchSin * var11 >> 16;
                var11 = pitchSin * var18 + var11 * pitchCos >> 16;
                var18 = var21;
                if (var11 >= 50)
                {
                    var21 = var13 * yawCos + yawSin * var16 >> 16;
                    var16 = var16 * yawCos - yawSin * var13 >> 16;
                    var13 = var21;
                    var21 = var19 * pitchCos - pitchSin * var16 >> 16;
                    var16 = pitchSin * var19 + var16 * pitchCos >> 16;
                    var19 = var21;
                    if (var16 >= 50)
                    {
                        var21 = var9 * yawCos + yawSin * var15 >> 16;
                        var15 = var15 * yawCos - yawSin * var9 >> 16;
                        var9 = var21;
                        var21 = var20 * pitchCos - pitchSin * var15 >> 16;
                        var15 = pitchSin * var20 + var15 * pitchCos >> 16;
                        if (var15 >= 50)
                        {
                            int dy = var10 * zoom / var12 + centerX;
                            int dx = var17 * zoom / var12 + centerY;
                            int cy = var14 * zoom / var11 + centerX;
                            int cx = var18 * zoom / var11 + centerY;
                            int ay = var13 * zoom / var16 + centerX;
                            int ax = var19 * zoom / var16 + centerY;
                            int by = var9 * zoom / var15 + centerX;
                            int bx = var21 * zoom / var15 + centerY;

                            drawCallbacks.drawScenePaint(0, pitchSin, pitchCos, yawSin, yawCos,
                                    -cameraX2, -cameraY2, -cameraZ2,
                                    tile, z, x, y,
                                    zoom, centerX, centerY);

                            if ((ay - by) * (cx - bx) - (ax - bx) * (cy - by) > 0)
                            {

                                if (checkClick && Client.instance.containsBounds(mouseX2, mouseY2, ax, bx, cx, ay, by, cy)) {
                                    setTargetTile(x, y);
                                }
                                if (Client.instance.containsBounds(MouseHandler.mouseX, MouseHandler.mouseY, ax, bx, cx, ay, by, cy)) {
                                    hoverTile(x, y, tilePlane);
                                }

                            }

                            if ((dy - cy) * (bx - cx) - (dx - cx) * (by - cy) > 0)
                            {

                                if (checkClick && inBounds(clickScreenX, clickScreenY, dx, cx, bx, dy, cy, by)) {
                                    setTargetTile(x, y);
                                }
                                if (inBounds(MouseHandler.mouseX, MouseHandler.mouseY, dx, cx, bx, dy, cy, by)) {
                                    hoverTile(x, y, tilePlane);
                                }

                            }

                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            Client.instance.getLogger().warn("error during underlay rendering", ex);
        }
    }


    public static void hoverTile(int x, int y, int plane)
    {
        if (plane == Client.instance.getPlane() && !Client.instance.isMenuOpen())
        {
            hoverX = x;
            hoverY = y;
        }
    }

    private static void setTargetTile(int targetX, int targetY)
    {
        Client.instance.setSelectedSceneTileX(targetX);
        Client.instance.setSelectedSceneTileY(targetY);
    }


    private void drawTileUnderlaySD(SimpleTile simpleTile, int z, int pitchSin, int pitchCos, int yawSin, int yawCos, int x, int y) {

        int l1;
        int i2 = l1 = (x << 7) - xCameraPos;
        int j2;
        int k2 = j2 = (y << 7) - yCameraPos;
        int l2;
        int i3 = l2 = i2 + 128;
        int j3;
        int k3 = j3 = k2 + 128;
        int l3 = heightMap[z][x][y] - zCameraPos;
        int i4 = heightMap[z][x + 1][y] - zCameraPos;
        int j4 = heightMap[z][x + 1][y + 1] - zCameraPos;
        int k4 = heightMap[z][x][y + 1] - zCameraPos;
        int l4 = k2 * yawSin + i2 * yawCos >> 16;
        k2 = k2 * yawCos - i2 * yawSin >> 16;
        i2 = l4;
        l4 = l3 * pitchCos - k2 * pitchSin >> 16;
        k2 = l3 * pitchSin + k2 * pitchCos >> 16;
        l3 = l4;
        if (k2 < 50)
            return;
        l4 = j2 * yawSin + i3 * yawCos >> 16;
        j2 = j2 * yawCos - i3 * yawSin >> 16;
        i3 = l4;
        l4 = i4 * pitchCos - j2 * pitchSin >> 16;
        j2 = i4 * pitchSin + j2 * pitchCos >> 16;
        i4 = l4;
        if (j2 < 50)
            return;
        l4 = k3 * yawSin + l2 * yawCos >> 16;
        k3 = k3 * yawCos - l2 * yawSin >> 16;
        l2 = l4;
        l4 = j4 * pitchCos - k3 * pitchSin >> 16;
        k3 = j4 * pitchSin + k3 * pitchCos >> 16;
        j4 = l4;
        if (k3 < 50)
            return;
        l4 = j3 * yawSin + l1 * yawCos >> 16;
        j3 = j3 * yawCos - l1 * yawSin >> 16;
        l1 = l4;
        l4 = k4 * pitchCos - j3 * pitchSin >> 16;
        j3 = k4 * pitchSin + j3 * pitchCos >> 16;
        k4 = l4;
        if (j3 < 50)
            return;
        int i5 = Rasterizer3D.originViewX + i2 * Rasterizer3D.fieldOfView / k2;
        int j5 = Rasterizer3D.originViewY + l3 * Rasterizer3D.fieldOfView / k2;
        int k5 = Rasterizer3D.originViewX + i3 * Rasterizer3D.fieldOfView / j2;
        int l5 = Rasterizer3D.originViewY + i4 * Rasterizer3D.fieldOfView / j2;
        int i6 = Rasterizer3D.originViewX + l2 * Rasterizer3D.fieldOfView / k3;
        int j6 = Rasterizer3D.originViewY + j4 * Rasterizer3D.fieldOfView / k3;
        int k6 = Rasterizer3D.originViewX + l1 * Rasterizer3D.fieldOfView / j3;
        int l6 = Rasterizer3D.originViewY + k4 * Rasterizer3D.fieldOfView / j3;
        Rasterizer3D.alpha = 0;
        if ((i6 - k6) * (l5 - l6) - (j6 - l6) * (k5 - k6) > 0) {
            Rasterizer3D.textureOutOfDrawingBounds = i6 < 0 || k6 < 0 || k5 < 0 || i6 > Rasterizer2D.lastX || k6 > Rasterizer2D.lastX || k5 > Rasterizer2D.lastX;
            if (clicked && inBounds(clickScreenX, clickScreenY, j6, l6, l5, i6, k6, k5)) {
                clickedTileX = x;
                clickedTileY = y;
            }
            if (inBounds(MouseHandler.mouseX, MouseHandler.mouseY, j6, l6, l5, i6, k6, k5)) {
                hoverX = x;
                hoverY = y;
            }
            if (simpleTile.getTexture() == -1) {
                if (simpleTile.getCenterColor() != 0xbc614e)
                    Rasterizer3D.drawShadedTriangle(j6, l6, l5, i6, k6, k5, simpleTile.getCenterColor(), simpleTile.getEastColor(), simpleTile.getNorthColor());
            } else if (!lowMem) {
                if (simpleTile.isFlat())
                    Rasterizer3D.drawTexturedTriangle(j6, l6, l5, i6, k6, k5, simpleTile.getCenterColor(), simpleTile.getEastColor(), simpleTile.getNorthColor(), i2, i3, l1, l3, i4, k4, k2, j2, j3, simpleTile.getTexture());
                else
                    Rasterizer3D.drawTexturedTriangle(j6, l6, l5, i6, k6, k5, simpleTile.getCenterColor(), simpleTile.getEastColor(), simpleTile.getNorthColor(), l2, l1, i3, j4, k4, i4, k3, j3, j2, simpleTile.getTexture());
            } else {
                int textureColor = TEXTURE_COLORS[simpleTile.getTexture()];
                Rasterizer3D.drawShadedTriangle(j6, l6, l5, i6, k6, k5, light(textureColor, simpleTile.getCenterColor()), light(textureColor, simpleTile.getEastColor()), light(textureColor, simpleTile.getNorthColor()));
            }
        }
        if ((i5 - k5) * (l6 - l5) - (j5 - l5) * (k6 - k5) > 0) {
            Rasterizer3D.textureOutOfDrawingBounds = i5 < 0 || k5 < 0 || k6 < 0 || i5 > Rasterizer2D.lastX || k5 > Rasterizer2D.lastX || k6 > Rasterizer2D.lastX;
            if (clicked && inBounds(clickScreenX, clickScreenY, j5, l5, l6, i5, k5, k6)) {
                clickedTileX = x;
                clickedTileY = y;
            }
            if (inBounds(MouseHandler.mouseX, MouseHandler.mouseY, j5, l5, l6, i5, k5, k6)) {
                hoverX = x;
                hoverY = y;
            }
            if (simpleTile.getTexture() == -1) {
                if (simpleTile.getNorthEastColor() != 0xbc614e) {
                    Rasterizer3D.drawShadedTriangle(j5, l5, l6, i5, k5, k6, simpleTile.getNorthEastColor(), simpleTile.getNorthColor(),
                            simpleTile.getEastColor());
                }
            } else {
                if (!lowMem) {
                    Rasterizer3D.drawTexturedTriangle(j5, l5, l6, i5, k5, k6, simpleTile.getNorthEastColor(), simpleTile.getNorthColor(),
                            simpleTile.getEastColor(), i2, i3, l1, l3, i4, k4, k2, j2, j3, simpleTile.getTexture());
                    return;
                }
                int j7 = TEXTURE_COLORS[simpleTile.getTexture()];
                Rasterizer3D.drawShadedTriangle(j5, l5, l6, i5, k5, k6, light(j7, simpleTile.getNorthEastColor()), light(j7, simpleTile.getNorthColor()), light(j7, simpleTile.getEastColor()));
            }
        }
    }

    private void drawTileOverlay(int tileX, int pitchSin, int yawSin, ShapedTile tile, int pitchCos, int tileY, int yawCos) {
        RSTile rsTile = getTiles()[Client.instance.getPlane()][tileX][tileY];
        final boolean checkClick = Client.instance.isCheckClick();

        if (!Client.instance.isGpu())
        {
            drawTileOverlaySD(tileX,pitchSin,yawSin,tile,pitchCos,tileY, yawCos);

            if (roofRemovalMode == 0 || !checkClick || rsTile == null || rsTile.getSceneTileModel() != tile || rsTile.getPhysicalLevel() != Client.instance.getPlane())
            {
                return;
            }
        }

        final DrawCallbacks drawCallbacks = Client.instance.getDrawCallbacks();

        if (drawCallbacks == null)
        {
            return;
        }

        try
        {
            final int cameraX2 = Client.instance.getCameraX2();
            final int cameraY2 = Client.instance.getCameraY2();
            final int cameraZ2 = Client.instance.getCameraZ2();
            final int zoom = Client.instance.get3dZoom();
            final int centerX = Client.instance.getCenterX();
            final int centerY = Client.instance.getCenterY();

            drawCallbacks.drawSceneModel(0, pitchSin, pitchCos, yawSin, yawCos, -cameraX2, -cameraY2, -cameraZ2,
                    tile, Client.instance.getPlane(), tileX, tileY,
                    zoom, centerX, centerY);



            RSSceneTileModel tileModel = tile;

            final int[] faceX = tileModel.getFaceX();
            final int[] faceY = tileModel.getFaceY();
            final int[] faceZ = tileModel.getFaceZ();

            final int[] vertexX = tileModel.getVertexX();
            final int[] vertexY = tileModel.getVertexY();
            final int[] vertexZ = tileModel.getVertexZ();

            final int vertexCount = vertexX.length;
            final int faceCount = faceX.length;

            final int mouseX2 = Client.instance.getMouseX2();
            final int mouseY2 = Client.instance.getMouseY2();

            for (int i = 0; i < vertexCount; ++i)
            {
                int vx = vertexX[i] - cameraX2;
                int vy = vertexY[i] - cameraY2;
                int vz = vertexZ[i] - cameraZ2;

                int rotA = vz * yawSin + vx * yawCos >> 16;
                int rotB = vz * yawCos - vx * yawSin >> 16;

                int var13 = vy * pitchCos - rotB * pitchSin >> 16;
                int var12 = vy * pitchSin + rotB * pitchCos >> 16;
                if (var12 < 50)
                {
                    return;
                }

                int ax = rotA * zoom / var12 + centerX;
                int ay = var13 * zoom / var12 + centerY;

                tmpX[i] = ax;
                tmpY[i] = ay;
            }

            for (int i = 0; i < faceCount; ++i)
            {
                int va = faceX[i];
                int vb = faceY[i];
                int vc = faceZ[i];

                int x1 = tmpX[va];
                int x2 = tmpX[vb];
                int x3 = tmpX[vc];

                int y1 = tmpY[va];
                int y2 = tmpY[vb];
                int y3 = tmpY[vc];

                if ((x1 - x2) * (y3 - y2) - (y1 - y2) * (x3 - x2) > 0)
                {

                    if (checkClick && Client.instance.containsBounds(mouseX2, mouseY2, y1, y2, y3, x1, x2, x3)) {
                        setTargetTile(tileX, tileY);
                    }
                    if (Client.instance.containsBounds(MouseHandler.mouseX, MouseHandler.mouseY, y1, y2, y3, x1, x2, x3)) {
                        hoverTile(tileX, tileY, rsTile.getPhysicalLevel());
                    }

                }
            }
        }
        catch (Exception ex)
        {
            Client.instance.getLogger().warn("error during overlay rendering", ex);
        }
    }

    private void drawTileOverlaySD(int tileX, int pitchSin, int yawSin, ShapedTile tile, int pitchCos, int tileY, int yawCos) {

        int k1 = tile.origVertexX.length;
        for (int l1 = 0; l1 < k1; l1++) {
            int i2 = tile.origVertexX[l1] - xCameraPos;
            int k2 = tile.origVertexY[l1] - zCameraPos;
            int i3 = tile.origVertexZ[l1] - yCameraPos;
            int k3 = i3 * yawSin + i2 * yawCos >> 16;
            i3 = i3 * yawCos - i2 * yawSin >> 16;
            i2 = k3;
            k3 = k2 * pitchCos - i3 * pitchSin >> 16;
            i3 = k2 * pitchSin + i3 * pitchCos >> 16;
            k2 = k3;
            if (i3 < 50)
                return;
            if (tile.triangleTexture != null) {
                ShapedTile.anIntArray690[l1] = i2;
                ShapedTile.anIntArray691[l1] = k2;
                ShapedTile.anIntArray692[l1] = i3;
            }
            ShapedTile.anIntArray688[l1] = Rasterizer3D.originViewX + i2 * Rasterizer3D.fieldOfView / i3;
            ShapedTile.anIntArray689[l1] = Rasterizer3D.originViewY + k2 * Rasterizer3D.fieldOfView / i3;
        }

        Rasterizer3D.alpha = 0;
        k1 = tile.triangleA.length;
        for (int j2 = 0; j2 < k1; j2++) {
            int l2 = tile.triangleA[j2];
            int j3 = tile.triangleB[j2];
            int l3 = tile.triangleC[j2];
            int i4 = ShapedTile.anIntArray688[l2];
            int j4 = ShapedTile.anIntArray688[j3];
            int k4 = ShapedTile.anIntArray688[l3];
            int l4 = ShapedTile.anIntArray689[l2];
            int i5 = ShapedTile.anIntArray689[j3];
            int j5 = ShapedTile.anIntArray689[l3];
            if ((i4 - j4) * (j5 - i5) - (l4 - i5) * (k4 - j4) > 0) {
                Rasterizer3D.textureOutOfDrawingBounds = i4 < 0 || j4 < 0 || k4 < 0 || i4 > Rasterizer2D.lastX
                        || j4 > Rasterizer2D.lastX || k4 > Rasterizer2D.lastX;
                if (clicked && inBounds(clickScreenX, clickScreenY, l4, i5, j5, i4, j4, k4)) {
                    clickedTileX = tileX;
                    clickedTileY = tileY;
                }
                if (inBounds(MouseHandler.mouseX, MouseHandler.mouseY, l4, i5, j5, i4, j4, k4)) {
                    hoverX = tileX;
                    hoverY = tileY;
                }
                if (tile.triangleTexture == null || tile.triangleTexture[j2] == -1) {
                    if (tile.triangleHslA[j2] != 0xbc614e)
                        Rasterizer3D.drawShadedTriangle(l4, i5, j5, i4, j4, k4, tile.triangleHslA[j2],
                                tile.triangleHslB[j2], tile.triangleHslC[j2]);
                } else if (!lowMem) {
                    if (tile.flat)
                        Rasterizer3D.drawTexturedTriangle(l4, i5, j5, i4, j4, k4, tile.triangleHslA[j2],
                                tile.triangleHslB[j2], tile.triangleHslC[j2], ShapedTile.anIntArray690[0],
                                ShapedTile.anIntArray690[1], ShapedTile.anIntArray690[3], ShapedTile.anIntArray691[0],
                                ShapedTile.anIntArray691[1], ShapedTile.anIntArray691[3], ShapedTile.anIntArray692[0],
                                ShapedTile.anIntArray692[1], ShapedTile.anIntArray692[3], tile.triangleTexture[j2]);
                    else
                        Rasterizer3D.drawTexturedTriangle(l4, i5, j5, i4, j4, k4, tile.triangleHslA[j2],
                                tile.triangleHslB[j2], tile.triangleHslC[j2], ShapedTile.anIntArray690[l2],
                                ShapedTile.anIntArray690[j3], ShapedTile.anIntArray690[l3], ShapedTile.anIntArray691[l2],
                                ShapedTile.anIntArray691[j3], ShapedTile.anIntArray691[l3], ShapedTile.anIntArray692[l2],
                                ShapedTile.anIntArray692[j3], ShapedTile.anIntArray692[l3], tile.triangleTexture[j2]);
                } else {
                    int k5 = TEXTURE_COLORS[tile.triangleTexture[j2]];
                    Rasterizer3D.drawShadedTriangle(l4, i5, j5, i4, j4, k4, light(k5, tile.triangleHslA[j2]),
                            light(k5, tile.triangleHslB[j2]), light(k5, tile.triangleHslC[j2]));
                }
            }
        }
    }

    private int light(int j, int k) {
        k = 127 - k;
        k = (k * (j & 0x7f)) / 160;
        if (k < 2)
            k = 2;
        else if (k > 126)
            k = 126;
        return (j & 0xff80) + k;
    }

    public boolean inBounds(int i, int j, int k, int l, int i1, int j1, int k1, int l1) {
        if (j < k && j < l && j < i1)
            return false;
        if (j > k && j > l && j > i1)
            return false;
        if (i < j1 && i < k1 && i < l1)
            return false;
        if (i > j1 && i > k1 && i > l1)
            return false;
        int i2 = (j - k) * (k1 - j1) - (i - j1) * (l - k);
        int j2 = (j - i1) * (j1 - l1) - (i - l1) * (k - i1);
        int k2 = (j - l) * (l1 - k1) - (i - k1) * (i1 - l);
        return i2 * k2 > 0 && k2 * j2 > 0;
    }

    private void occlude() {
        int sceneClusterCount = sceneClusterCounts[currentRenderPlane];
        SceneCluster sceneClusters[] = SceneGraph.sceneClusters[currentRenderPlane];
        anInt475 = 0;
        for (int sceneIndex = 0; sceneIndex < sceneClusterCount; sceneIndex++) {
            SceneCluster sceneCluster = sceneClusters[sceneIndex];
            if (sceneCluster.orientation == 1) { //YZ-plane
                int relativeX = (sceneCluster.startXLoc - screenCenterX) + 25;
                if (relativeX < 0 || relativeX > 50)
                    continue;
                int minRelativeY = (sceneCluster.startYLoc - screenCenterZ) + 25;
                if (minRelativeY < 0)
                    minRelativeY = 0;
                int maxRelativeY = (sceneCluster.endYLoc - screenCenterZ) + 25;
                if (maxRelativeY > 50)
                    maxRelativeY = 50;
                boolean flag = false;
                while (minRelativeY <= maxRelativeY)
                    if (renderArea[relativeX][minRelativeY++]) {
                        flag = true;
                        break;
                    }
                if (!flag)
                    continue;
                int dXPos = xCameraPos - sceneCluster.startXPos;
                if (dXPos > 32) {
                    sceneCluster.cullDirection = 1;
                } else {
                    if (dXPos >= -32)
                        continue;
                    sceneCluster.cullDirection = 2;
                    dXPos = -dXPos;
                }
                sceneCluster.anInt801 = (sceneCluster.startYPos - yCameraPos << 8) / dXPos;
                sceneCluster.anInt802 = (sceneCluster.endYPos - yCameraPos << 8) / dXPos;
                sceneCluster.anInt803 = (sceneCluster.startZPos - zCameraPos << 8) / dXPos;
                sceneCluster.anInt804 = (sceneCluster.endZPos - zCameraPos << 8) / dXPos;
                aClass47Array476[anInt475++] = sceneCluster;
                continue;
            }
            if (sceneCluster.orientation == 2) { //XZ-plane
                int relativeY = (sceneCluster.startYLoc - screenCenterZ) + 25;
                if (relativeY < 0 || relativeY > 50)
                    continue;
                int minRelativeX = (sceneCluster.startXLoc - screenCenterX) + 25;
                if (minRelativeX < 0)
                    minRelativeX = 0;
                int maxRelativeX = (sceneCluster.endXLoc - screenCenterX) + 25;
                if (maxRelativeX > 50)
                    maxRelativeX = 50;
                boolean flag1 = false;
                while (minRelativeX <= maxRelativeX)
                    if (renderArea[minRelativeX++][relativeY]) {
                        flag1 = true;
                        break;
                    }
                if (!flag1)
                    continue;
                int dYPos = yCameraPos - sceneCluster.startYPos;
                if (dYPos > 32) {
                    sceneCluster.cullDirection = 3;
                } else if (dYPos < -32){
                    sceneCluster.cullDirection = 4;
                    dYPos = -dYPos;
                } else {
                    continue;
                }
                sceneCluster.anInt799 = (sceneCluster.startXPos - xCameraPos << 8) / dYPos;
                sceneCluster.anInt800 = (sceneCluster.endXPos - xCameraPos << 8) / dYPos;
                sceneCluster.anInt803 = (sceneCluster.startZPos - zCameraPos << 8) / dYPos;
                sceneCluster.anInt804 = (sceneCluster.endZPos - zCameraPos << 8) / dYPos;
                aClass47Array476[anInt475++] = sceneCluster;
            } else if (sceneCluster.orientation == 4) { //XY-plane
                int relativeZ = sceneCluster.startZPos - zCameraPos;
                if (relativeZ > 128) {
                    int minRelativeY = (sceneCluster.startYLoc - screenCenterZ) + 25;
                    if (minRelativeY < 0)
                        minRelativeY = 0;
                    int maxRelativeY = (sceneCluster.endYLoc - screenCenterZ) + 25;
                    if (maxRelativeY > 50)
                        maxRelativeY = 50;
                    if (minRelativeY <= maxRelativeY) {
                        int minRelativeX = (sceneCluster.startXLoc - screenCenterX) + 25;
                        if (minRelativeX < 0)
                            minRelativeX = 0;
                        int maxRelativeX = (sceneCluster.endXLoc - screenCenterX) + 25;
                        if (maxRelativeX > 50)
                            maxRelativeX = 50;
                        boolean flag2 = false;
                        label0: for (int i4 = minRelativeX; i4 <= maxRelativeX; i4++) {
                            for (int j4 = minRelativeY; j4 <= maxRelativeY; j4++) {
                                if (!renderArea[i4][j4])
                                    continue;
                                flag2 = true;
                                break label0;
                            }

                        }

                        if (flag2) {
                            sceneCluster.cullDirection = 5;
                            sceneCluster.anInt799 = (sceneCluster.startXPos - xCameraPos << 8) / relativeZ;
                            sceneCluster.anInt800 = (sceneCluster.endXPos - xCameraPos << 8) / relativeZ;
                            sceneCluster.anInt801 = (sceneCluster.startYPos - yCameraPos << 8) / relativeZ;
                            sceneCluster.anInt802 = (sceneCluster.endYPos - yCameraPos << 8) / relativeZ;
                            aClass47Array476[anInt475++] = sceneCluster;
                        }
                    }
                }
            }
        }

    }

    private boolean method320(int zLoc, int xLoc, int yLoc) {
        int l = anIntArrayArrayArray445[zLoc][xLoc][yLoc];
        if (l == -cycle)
            return false;
        if (l == cycle)
            return true;
        int xPos = xLoc << 7;
        int yPos = yLoc << 7;
        if (method324(xPos + 1, heightMap[zLoc][xLoc][yLoc], yPos + 1) && method324((xPos + 128) - 1, heightMap[zLoc][xLoc + 1][yLoc], yPos + 1) && method324((xPos + 128) - 1, heightMap[zLoc][xLoc + 1][yLoc + 1], (yPos + 128) - 1) && method324(xPos + 1, heightMap[zLoc][xLoc][yLoc + 1], (yPos + 128) - 1)) {
            anIntArrayArrayArray445[zLoc][xLoc][yLoc] = cycle;
            return true;
        } else {
            anIntArrayArrayArray445[zLoc][xLoc][yLoc] = -cycle;
            return false;
        }
    }

    private boolean method321(int i, int j, int k, int l) {
        if (!method320(i, j, k))
            return false;
        int i1 = j << 7;
        int j1 = k << 7;
        int k1 = heightMap[i][j][k] - 1;
        int l1 = k1 - 120;
        int i2 = k1 - 230;
        int j2 = k1 - 238;
        if (l < 16) {
            if (l == 1) {
                if (i1 > xCameraPos) {
                    if (!method324(i1, k1, j1))
                        return false;
                    if (!method324(i1, k1, j1 + 128))
                        return false;
                }
                if (i > 0) {
                    if (!method324(i1, l1, j1))
                        return false;
                    if (!method324(i1, l1, j1 + 128))
                        return false;
                }
                return method324(i1, i2, j1) && method324(i1, i2, j1 + 128);
            }
            if (l == 2) {
                if (j1 < yCameraPos) {
                    if (!method324(i1, k1, j1 + 128))
                        return false;
                    if (!method324(i1 + 128, k1, j1 + 128))
                        return false;
                }
                if (i > 0) {
                    if (!method324(i1, l1, j1 + 128))
                        return false;
                    if (!method324(i1 + 128, l1, j1 + 128))
                        return false;
                }
                return method324(i1, i2, j1 + 128) && method324(i1 + 128, i2, j1 + 128);
            }
            if (l == 4) {
                if (i1 < xCameraPos) {
                    if (!method324(i1 + 128, k1, j1))
                        return false;
                    if (!method324(i1 + 128, k1, j1 + 128))
                        return false;
                }
                if (i > 0) {
                    if (!method324(i1 + 128, l1, j1))
                        return false;
                    if (!method324(i1 + 128, l1, j1 + 128))
                        return false;
                }
                return method324(i1 + 128, i2, j1) && method324(i1 + 128, i2, j1 + 128);
            }
            if (l == 8) {
                if (j1 > yCameraPos) {
                    if (!method324(i1, k1, j1))
                        return false;
                    if (!method324(i1 + 128, k1, j1))
                        return false;
                }
                if (i > 0) {
                    if (!method324(i1, l1, j1))
                        return false;
                    if (!method324(i1 + 128, l1, j1))
                        return false;
                }
                return method324(i1, i2, j1) && method324(i1 + 128, i2, j1);
            }
        }
        if (!method324(i1 + 64, j2, j1 + 64))
            return false;
        if (l == 16)
            return method324(i1, i2, j1 + 128);
        if (l == 32)
            return method324(i1 + 128, i2, j1 + 128);
        if (l == 64)
            return method324(i1 + 128, i2, j1);
        if (l == 128) {
            return method324(i1, i2, j1);
        } else {
            System.out.println("Warning unsupported wall type"); //TODO
            return true;
        }
    }

    private boolean method322(int i, int j, int k, int l) {
        if (!method320(i, j, k))
            return false;
        int i1 = j << 7;
        int j1 = k << 7;
        return method324(i1 + 1, heightMap[i][j][k] - l, j1 + 1)
                && method324((i1 + 128) - 1, heightMap[i][j + 1][k] - l, j1 + 1)
                && method324((i1 + 128) - 1, heightMap[i][j + 1][k + 1] - l, (j1 + 128) - 1)
                && method324(i1 + 1, heightMap[i][j][k + 1] - l, (j1 + 128) - 1);
    }

    private boolean method323(int i, int j, int k, int l, int i1, int j1) {
        if (j == k && l == i1) {
            if (!method320(i, j, l))
                return false;
            int k1 = j << 7;
            int i2 = l << 7;
            return method324(k1 + 1, heightMap[i][j][l] - j1, i2 + 1)
                    && method324((k1 + 128) - 1, heightMap[i][j + 1][l] - j1, i2 + 1)
                    && method324((k1 + 128) - 1, heightMap[i][j + 1][l + 1] - j1, (i2 + 128) - 1)
                    && method324(k1 + 1, heightMap[i][j][l + 1] - j1, (i2 + 128) - 1);
        }
        for (int l1 = j; l1 <= k; l1++) {
            for (int j2 = l; j2 <= i1; j2++)
                if (anIntArrayArrayArray445[i][l1][j2] == -cycle)
                    return false;

        }

        int k2 = (j << 7) + 1;
        int l2 = (l << 7) + 2;
        int i3 = heightMap[i][j][l] - j1;
        if (!method324(k2, i3, l2))
            return false;
        int j3 = (k << 7) - 1;
        if (!method324(j3, i3, l2))
            return false;
        int k3 = (i1 << 7) - 1;
        return method324(k2, i3, k3) && method324(j3, i3, k3);
    }

    private boolean method324(int i, int j, int k) {
        for (int l = 0; l < anInt475; l++) {
            SceneCluster class47 = aClass47Array476[l];
            if (class47.cullDirection == 1) {
                int i1 = class47.startXPos - i;
                if (i1 > 0) {
                    int j2 = class47.startYPos + (class47.anInt801 * i1 >> 8);
                    int k3 = class47.endYPos + (class47.anInt802 * i1 >> 8);
                    int l4 = class47.startZPos + (class47.anInt803 * i1 >> 8);
                    int i6 = class47.endZPos + (class47.anInt804 * i1 >> 8);
                    if (k >= j2 && k <= k3 && j >= l4 && j <= i6)
                        return true;
                }
            } else if (class47.cullDirection == 2) {
                int j1 = i - class47.startXPos;
                if (j1 > 0) {
                    int k2 = class47.startYPos + (class47.anInt801 * j1 >> 8);
                    int l3 = class47.endYPos + (class47.anInt802 * j1 >> 8);
                    int i5 = class47.startZPos + (class47.anInt803 * j1 >> 8);
                    int j6 = class47.endZPos + (class47.anInt804 * j1 >> 8);
                    if (k >= k2 && k <= l3 && j >= i5 && j <= j6)
                        return true;
                }
            } else if (class47.cullDirection == 3) {
                int k1 = class47.startYPos - k;
                if (k1 > 0) {
                    int l2 = class47.startXPos + (class47.anInt799 * k1 >> 8);
                    int i4 = class47.endXPos + (class47.anInt800 * k1 >> 8);
                    int j5 = class47.startZPos + (class47.anInt803 * k1 >> 8);
                    int k6 = class47.endZPos + (class47.anInt804 * k1 >> 8);
                    if (i >= l2 && i <= i4 && j >= j5 && j <= k6)
                        return true;
                }
            } else if (class47.cullDirection == 4) {
                int l1 = k - class47.startYPos;
                if (l1 > 0) {
                    int i3 = class47.startXPos + (class47.anInt799 * l1 >> 8);
                    int j4 = class47.endXPos + (class47.anInt800 * l1 >> 8);
                    int k5 = class47.startZPos + (class47.anInt803 * l1 >> 8);
                    int l6 = class47.endZPos + (class47.anInt804 * l1 >> 8);
                    if (i >= i3 && i <= j4 && j >= k5 && j <= l6)
                        return true;
                }
            } else if (class47.cullDirection == 5) {
                int i2 = j - class47.startZPos;
                if (i2 > 0) {
                    int j3 = class47.startXPos + (class47.anInt799 * i2 >> 8);
                    int k4 = class47.endXPos + (class47.anInt800 * i2 >> 8);
                    int l5 = class47.startYPos + (class47.anInt801 * i2 >> 8);
                    int i7 = class47.endYPos + (class47.anInt802 * i2 >> 8);
                    if (i >= j3 && i <= k4 && k >= l5 && k <= i7)
                        return true;
                }
            }
        }

        return false;
    }

    public static boolean lowMem = false;
    private final int maxY;
    private final int maxX;
    private final int maxZ;
    private final int[][][] heightMap;
    private final Tile[][][] tileArray;
    public int minLevel;
    private int interactableObjectCacheCurrPos;
    private final GameObject[] gameObjectsCache;
    private final int[][][] anIntArrayArrayArray445;
    public int tileUpdateCount;
    public int currentRenderPlane;
    public int cycle;
    public int minTileX;
    public int maxTileX;
    public int minTileZ;
    public int maxTileZ;
    public int screenCenterX;
    public int screenCenterZ;
    public static int xCameraPos;
    public static int zCameraPos;
    public static int yCameraPos;
    public static int camUpDownY;
    public static int camUpDownX;
    public static int camLeftRightY;
    public static int camLeftRightX;
    private static GameObject[] interactableObjects = new GameObject[100];
    private static final int[] anIntArray463 = { 53, -53, -53, 53 };
    private static final int[] anIntArray464 = { -53, -53, 53, 53 };
    private static final int[] anIntArray465 = { -45, 45, 45, -45 };
    private static final int[] anIntArray466 = { 45, 45, -45, -45 };
    public static boolean clicked;
    public static int clickScreenX;
    public static int clickScreenY;
    public static int clickedTileX = -1;
    public static int clickedTileY = -1;
    public static int hoverX = -1;
    public static int hoverY = -1;
    private static final int cullingClusterPlaneCount;
    private static int[] sceneClusterCounts;
    private static SceneCluster[][] sceneClusters;
    private static int anInt475;
    private static final SceneCluster[] aClass47Array476 = new SceneCluster[500];
    private static Deque tileDeque = new Deque();
    private static final int[] anIntArray478 = { 19, 55, 38, 155, 255, 110, 137, 205, 76 };
    private static final int[] anIntArray479 = { 160, 192, 80, 96, 0, 144, 80, 48, 160 };
    private static final int[] anIntArray480 = { 76, 8, 137, 4, 0, 1, 38, 2, 19 };
    private static final int[] anIntArray481 = { 0, 0, 2, 0, 0, 2, 1, 1, 0 };
    private static final int[] anIntArray482 = { 2, 0, 0, 2, 0, 0, 0, 4, 4 };
    private static final int[] anIntArray483 = { 0, 4, 4, 8, 0, 0, 8, 0, 0 };
    private static final int[] anIntArray484 = { 1, 1, 0, 0, 0, 8, 0, 0, 8 };
    private static final int[] TEXTURE_COLORS = { 41, 39248, 41, 4643, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 43086,
            41, 41, 41, 41, 41, 41, 41, 8602, 41, 28992, 41, 41, 41, 41, 41, 5056, 41, 41, 41, 7079, 41, 41, 41, 41, 41,
            41, 41, 41, 41, 41, 3131, 41, 41, 41 };
    private final int[] anIntArray486;
    private final int[] anIntArray487;
    private int anInt488;
    private final int[][] tileVertices = { new int[16], { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1 }, { 1, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0 },
            { 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1 }, { 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0 }, { 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1 },
            { 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1 } };
    private final int[][] tileVertexIndices = { { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 },
            { 12, 8, 4, 0, 13, 9, 5, 1, 14, 10, 6, 2, 15, 11, 7, 3 },
            { 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 },
            { 3, 7, 11, 15, 2, 6, 10, 14, 1, 5, 9, 13, 0, 4, 8, 12 } };
    public static boolean[][][][] visibilityMap;
    public static boolean[][] renderArea;
    private static int viewportHalfWidth;
    private static int viewportHalfHeight;
    private static int xMin;
    private static int yMin;
    private static int xMax;
    private static int yMax;

    static {
        cullingClusterPlaneCount = 4;
        sceneClusterCounts = new int[cullingClusterPlaneCount];
        sceneClusters = new SceneCluster[cullingClusterPlaneCount][500];
        visibilityMap = new boolean[8][32][51][51];
    }

    /**
     * Runelite
     */
    private int drawDistance = 25;

    @Override
    public void addItem(int id, int quantity, WorldPoint point) {
        final int sceneX = point.getX() - Client.instance.getBaseX();
        final int sceneY = point.getY() - Client.instance.getBaseY();
        final int plane = point.getPlane();

        if (sceneX < 0 || sceneY < 0 || sceneX >= 104 || sceneY >= 104)
        {
            return;
        }

        RSTileItem item = Client.instance.newTileItem();
        item.setId(id);
        item.setQuantity(quantity);
        RSNodeDeque[][][] groundItems = Client.instance.getGroundItemDeque();

        if (groundItems[plane][sceneX][sceneY] == null)
        {
            groundItems[plane][sceneX][sceneY] = Client.instance.newNodeDeque();
        }

        groundItems[plane][sceneX][sceneY].addFirst(item);

        if (plane == Client.instance.getPlane())
        {
            Client.instance.updateItemPile(sceneX, sceneY);
        }
    }

    @Override
    public void removeItem(int id, int quantity, WorldPoint point) {
        final int sceneX = point.getX() - Client.instance.getBaseX();
        final int sceneY = point.getY() - Client.instance.getBaseY();
        final int plane = point.getPlane();

        if (sceneX < 0 || sceneY < 0 || sceneX >= 104 || sceneY >= 104)
        {
            return;
        }

        RSNodeDeque items = Client.instance.getGroundItemDeque()[plane][sceneX][sceneY];

        if (items == null)
        {
            return;
        }

        for (RSTileItem item = (RSTileItem) items.last(); item != null; item = (RSTileItem) items.previous())
        {
            if (item.getId() == id && quantity == 1)
            {
                item.unlink();
                break;
            }
        }

        if (items.last() == null)
        {
            Client.instance.getGroundItemDeque()[plane][sceneX][sceneY] = null;
        }

        Client.instance.updateItemPile(sceneX, sceneY);
    }

    @Override
    public int getDrawDistance() {
        return drawDistance;
    }

    @Override
    public void setDrawDistance(int drawDistance) {
        this.drawDistance = drawDistance;
    }

    @Override
    public void generateHouses() {

    }

    @Override
    public void setRoofRemovalMode(int flags) {
        roofRemovalMode = flags;
    }

    @Override
    public RSGameObject[] getObjects() {
        return gameObjectsCache;
    }

    @Override
    public RSTile[][][] getTiles() {
        return tileArray;
    }

    @Override
    public int[][] getTileShape2D() {
        return tileVertices;
    }

    @Override
    public int[][] getTileRotation2D() {
        return tileVertexIndices;
    }

    @Override
    public void draw(net.runelite.api.Tile tile, boolean var2) {
        drawTile((Tile) tile, false);
    }

    @Override
    public int[][][] getTileHeights() {
        return heightMap;
    }

    @Override
    public void drawTile(int[] pixels, int pixelOffset, int width, int z, int x, int y) {

    }

    @Override
    public void updateOccluders() {
        occlude();
    }

    @Override
    public int getMaxX() {
        return maxX;
    }

    @Override
    public int getMaxY() {
        return maxY;
    }

    @Override
    public int getMaxZ() {
        return maxZ;
    }

    @Override
    public int getMinLevel() {
        return minLevel;
    }

    @Override
    public void setMinLevel(int lvl) {
        this.minLevel = lvl;
    }

    @Override
    public void newGroundItemPile(int plane, int x, int y, int hash, RSRenderable var5, long var6, RSRenderable var7, RSRenderable var8) {

    }

    @Override
    public boolean newGameObject(int plane, int startX, int startY, int var4, int var5, int centerX, int centerY,
                                 int height, RSRenderable entity, int orientation, boolean tmp, long tag, int flags) {
        return false;
    }

    @Override
    public void removeGameObject(net.runelite.api.GameObject gameObject) {
        removeGameObject(gameObject.getPlane(),gameObject.getX(),gameObject.getY());
    }

    @Override
    public void removeGameObject(int plane, int x, int y) {

    }

    @Override
    public void removeWallObject(net.runelite.api.WallObject wallObject) {
        removeWallObject(wallObject.getPlane(),wallObject.getX(),wallObject.getY());
    }

    public void removeWallObject(WallObject wallObject)
    {
        final RSTile[][][] tiles = getTiles();

        for (int y = 0; y < 104; ++y)
        {
            for (int x = 0; x < 104; ++x)
            {
                RSTile tile = tiles[Client.instance.getPlane()][x][y];
                if (tile != null && tile.getWallObject() == wallObject)
                {
                    tile.setWallObject(null);
                }
            }
        }
    }

    @Override
    public void removeDecorativeObject(DecorativeObject decorativeObject)
    {
        final RSTile[][][] tiles = getTiles();

        for (int y = 0; y < 104; ++y)
        {
            for (int x = 0; x < 104; ++x)
            {
                RSTile tile = tiles[Client.instance.getPlane()][x][y];
                if (tile != null && tile.getDecorativeObject() == decorativeObject)
                {
                    tile.setDecorativeObject(null);
                }
            }
        }
    }

    @Override
    public void removeDecorativeObject(int plane, int x, int y) {

    }


    @Override
    public void removeGroundObject(GroundObject groundObject)
    {
        final RSTile[][][] tiles = getTiles();

        for (int y = 0; y < 104; ++y)
        {
            for (int x = 0; x < 104; ++x)
            {
                RSTile tile = tiles[Client.instance.getPlane()][x][y];
                if (tile != null && tile.getGroundObject() == groundObject)
                {
                    tile.setGroundObject(null);
                }
            }
        }
    }

    @Override
    public void removeGroundObject(int plane, int x, int y) {
    }

    @Override
    public short[][][] getUnderlayIds() {
        return Client.instance.currentMapRegion.underlays;
    }

    @Override
    public void setUnderlayIds(short[][][] underlayIds) {
        Client.instance.currentMapRegion.underlays = underlayIds;
    }

    @Override
    public short[][][] getOverlayIds() {
        return Client.instance.currentMapRegion.overlays;
    }

    @Override
    public void setOverlayIds(short[][][] overlayIds) {
        Client.instance.currentMapRegion.overlays = overlayIds;
    }

    @Override
    public byte[][][] getTileShapes() {
        return Client.instance.currentMapRegion.overlayTypes;
    }

    @Override
    public void setTileShapes(byte[][][] tileShapes) {
        Client.instance.currentMapRegion.overlayTypes = tileShapes;
    }

    @Override
    public void menuOpen(int selectedPlane, int screenX, int screenY, boolean viewportWalking) {

    }

}