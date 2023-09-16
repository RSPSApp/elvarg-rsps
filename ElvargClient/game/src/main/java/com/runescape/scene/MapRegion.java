package com.runescape.scene;

import com.runescape.Client;
import com.runescape.cache.def.FloorDefinition;
import com.runescape.cache.def.ObjectDefinition;
import com.runescape.draw.Rasterizer3D;
import com.runescape.entity.Renderable;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;
import com.runescape.util.ChunkUtil;
import com.runescape.util.ObjectKeyUtil;

public final class MapRegion {

    private final int[] hues;
    private final int[] saturations;
    private final int[] luminances;
    private final int[] chromas;
    private final int[] anIntArray128;
    private final int[][][] tileHeights;
    public short[][][] overlays;
    public static int anInt131;
    private final byte[][][] shading;
    private final int[][][] anIntArrayArrayArray135;
    public byte[][][] overlayTypes;
    private static final int COSINE_VERTICES[] = { 1, 0, -1, 0 };
    private final int[][] tileLighting;
    private static final int anIntArray140[] = { 16, 32, 64, 128 };
    public short[][][] underlays;
    private static final int SINE_VERTICIES[] = { 0, -1, 0, 1 };
    public static int maximumPlane = 99;
    private final int regionSizeX;
    private final int regionSizeY;
    private final byte[][][] overlayOrientations;
    private final byte[][][] tileFlags;
    public static boolean lowMem = false;
    private static final int anIntArray152[] = { 1, 2, 4, 8 };

    private static final int BLOCKED_TILE = 1;
    public static final int BRIDGE_TILE = 2;
    private static final int FORCE_LOWEST_PLANE = 8;

    public MapRegion(byte fileFlags[][][], int tileHeights[][][]) {
        maximumPlane = 99;
        regionSizeX = 104;
        regionSizeY = 104;
        this.tileHeights = tileHeights;
        this.tileFlags = fileFlags;
        underlays = new short[4][regionSizeX][regionSizeY];
        overlays = new short[4][regionSizeX][regionSizeY];
        overlayTypes = new byte[4][regionSizeX][regionSizeY];
        overlayOrientations = new byte[4][regionSizeX][regionSizeY];
        anIntArrayArrayArray135 = new int[4][regionSizeX + 1][regionSizeY + 1];
        shading = new byte[4][regionSizeX + 1][regionSizeY + 1];
        tileLighting = new int[regionSizeX + 1][regionSizeY + 1];
        hues = new int[regionSizeY];
        saturations = new int[regionSizeY];
        luminances = new int[regionSizeY];
        chromas = new int[regionSizeY];
        anIntArray128 = new int[regionSizeY];
    }

    private static int calculateNoise(int x, int y) {
        int k = x + y * 57;
        k = k << 13 ^ k;
        int l = k * (k * k * 15731 + 0xc0ae5) + 0x5208dd0d & 0x7fffffff;
        return l >> 19 & 0xff;
    }

    public final void createRegionScene(CollisionMap maps[], SceneGraph scene) {

        try {

            for (int z = 0; z < 4; z++) {
                for (int x = 0; x < 104; x++) {
                    for (int y = 0; y < 104; y++)
                        if ((tileFlags[z][x][y] & BLOCKED_TILE) == 1) {
                            int plane = z;
                            if ((tileFlags[1][x][y] & BRIDGE_TILE) == 2)
                                plane--;
                            if (plane >= 0)
                                maps[plane].block(x, y);
                        }

                }

            }
            int mapLight = Client.instance.isHdMinimapEnabled() ? 52 : 96;
            for (int z = 0; z < 4; z++) {
                byte shading[][] = this.shading[z];

                char diffusion = '\u0300';
                byte lightX = -50;
                byte lightY = -10;
                byte lightZ = -50;
                int light = (int) Math.sqrt(lightX * lightX + lightY * lightY + lightZ * lightZ);
                int l3 = diffusion * light >> 8;
                for (int j4 = 1; j4 < regionSizeY - 1; j4++) {
                    for (int j5 = 1; j5 < regionSizeX - 1; j5++) {
                        int k6 = tileHeights[z][j5 + 1][j4] - tileHeights[z][j5 - 1][j4];
                        int l7 = tileHeights[z][j5][j4 + 1] - tileHeights[z][j5][j4 - 1];
                        int j9 = (int) Math.sqrt(k6 * k6 + 0x10000 + l7 * l7);
                        int k12 = (k6 << 8) / j9;
                        int l13 = 0x10000 / j9;
                        int j15 = (l7 << 8) / j9;
                        int j16 = 96 + (lightX * k12 + lightY * l13 + lightZ * j15) / l3;
                        int j17 = (shading[j5 - 1][j4] >> 2) + (shading[j5 + 1][j4] >> 3) + (shading[j5][j4 - 1] >> 2) + (shading[j5][j4 + 1] >> 3) + (shading[j5][j4] >> 1);
                        tileLighting[j5][j4] = j16 - j17;
                    }

                }

                for (int k5 = 0; k5 < regionSizeY; k5++) {
                    hues[k5] = 0;
                    saturations[k5] = 0;
                    luminances[k5] = 0;
                    chromas[k5] = 0;
                    anIntArray128[k5] = 0;
                }

                for (int l6 = -5; l6 < regionSizeX + 5; l6++) {
                    for (int i8 = 0; i8 < regionSizeY; i8++) {
                        int k9 = l6 + 5;
                        if (k9 >= 0 && k9 < regionSizeX) {
                            int l12 = underlays[z][k9][i8] & 0x7FFF;
                            if (l12 > 0) {
                                if (l12 > FloorDefinition.underlays.length) {
                                    l12 = FloorDefinition.underlays.length;
                                }
                                FloorDefinition flo = FloorDefinition.underlays[l12 - 1];
                                hues[i8] += flo.blendHue;
                                saturations[i8] += flo.saturation;
                                luminances[i8] += flo.luminance;
                                chromas[i8] += flo.blendHueMultiplier;
                                anIntArray128[i8]++;
                            }
                        }
                        int i13 = l6 - 5;
                        if (i13 >= 0 && i13 < regionSizeX) {
                            int i14 = underlays[z][i13][i8] & 0x7FFF;
                            if (i14 > 0) {
                                if (i14 > FloorDefinition.underlays.length) {
                                    i14 = FloorDefinition.underlays.length;
                                }
                                FloorDefinition flo_1 = FloorDefinition.underlays[i14 - 1];
                                hues[i8] -= flo_1.blendHue;
                                saturations[i8] -= flo_1.saturation;
                                luminances[i8] -= flo_1.luminance;
                                chromas[i8] -= flo_1.blendHueMultiplier;
                                anIntArray128[i8]--;
                            }
                        }
                    }

                    if (l6 >= 1 && l6 < regionSizeX - 1) {
                        int l9 = 0;
                        int j13 = 0;
                        int j14 = 0;
                        int k15 = 0;
                        int k16 = 0;
                        for (int k17 = -5; k17 < regionSizeY + 5; k17++) {
                            int j18 = k17 + 5;
                            if (j18 >= 0 && j18 < regionSizeY) {
                                l9 += hues[j18];
                                j13 += saturations[j18];
                                j14 += luminances[j18];
                                k15 += chromas[j18];
                                k16 += anIntArray128[j18];
                            }
                            int k18 = k17 - 5;
                            if (k18 >= 0 && k18 < regionSizeY) {
                                l9 -= hues[k18];
                                j13 -= saturations[k18];
                                j14 -= luminances[k18];
                                k15 -= chromas[k18];
                                k16 -= anIntArray128[k18];
                            }
                            if (k17 >= 1 && k17 < regionSizeY - 1 && (!lowMem || (tileFlags[0][l6][k17] & 2) != 0 || (tileFlags[z][l6][k17] & 0x10) == 0 && getCollisionPlane(k17, z, l6) == anInt131)) {
                                if (z < maximumPlane)
                                    maximumPlane = z;
                                int l18 = underlays[z][l6][k17] & 0x7FFF;
                                int i19 = overlays[z][l6][k17] & 0x7FFF;
                                if (l18 > 0 || i19 > 0) {
                                    int j19 = tileHeights[z][l6][k17];
                                    int k19 = tileHeights[z][l6 + 1][k17];
                                    int l19 = tileHeights[z][l6 + 1][k17 + 1];
                                    int i20 = tileHeights[z][l6][k17 + 1];
                                    int j20 = tileLighting[l6][k17];
                                    int k20 = tileLighting[l6 + 1][k17];
                                    int l20 = tileLighting[l6 + 1][k17 + 1];
                                    int i21 = tileLighting[l6][k17 + 1];
                                    int j21 = -1;
                                    int k21 = -1;
                                    if (l18 > 0) {
                                        int l21 = (l9 * 256) / k15;
                                        int j22 = j13 / k16;
                                        int l22 = j14 / k16;
                                        j21 = encode(l21, j22, l22);

                                        if (l22 < 0)
                                            l22 = 0;
                                        else if (l22 > 255)
                                            l22 = 255;

                                        k21 = encode(l21, j22, l22);
                                    }
                                    if (z > 0) {
                                        boolean flag = true;
                                        if (l18 == 0 && overlayTypes[z][l6][k17] != 0)
                                            flag = false;
                                        if (i19 > 0 && !FloorDefinition.overlays[i19 - 1].hideUnderlay)
                                            flag = false;
                                        if (flag && j19 == k19 && j19 == l19 && j19 == i20)
                                            anIntArrayArrayArray135[z][l6][k17] |= 0x924;
                                    }
                                    int i22 = 0;

                                    if (j21 != -1)
                                        i22 = Rasterizer3D.hslToRgb[method187(k21, mapLight)];
                                    if (i19 == 0) {
                                        scene.addTile(z, l6, k17, 0, 0, -1, j19, k19, l19, i20, method187(j21, j20), method187(j21, k20), method187(j21, l20), method187(j21, i21), 0, 0, 0, 0, i22, 0);
                                    } else {

                                        int k22 = overlayTypes[z][l6][k17] + 1;
                                        byte byte4 = overlayOrientations[z][l6][k17];
                                        if (i19 - 1 > FloorDefinition.overlays.length) {
                                            i19 = FloorDefinition.overlays.length;
                                        }
                                        FloorDefinition overlay_flo = FloorDefinition.overlays[i19 - 1];
                                        int texture = overlay_flo.texture;
                                        int encodedTile;
                                        int minimapColor;
                                        int encodedMinimap;
                                        int lightness;
                                        if (texture >= 0) {
                                            minimapColor = Rasterizer3D.textureLoader.getAverageTextureRGB(texture);
                                            encodedTile = -1;
                                        } else if (overlay_flo.rgb == 16711935) {
                                            encodedTile = -2;
                                            texture = -1;
                                            minimapColor = -2;
                                        } else {
                                            encodedTile = encode(overlay_flo.hue, overlay_flo.saturation, overlay_flo.lightness);
                                            encodedMinimap = overlay_flo.hue;
                                            lightness = overlay_flo.lightness;
                                            if (lightness < 0) {
                                                lightness = 0;
                                            } else if (lightness > 255) {
                                                lightness = 255;
                                            }
                                            minimapColor = encode(encodedMinimap, overlay_flo.saturation, lightness);
                                        }
                                        encodedMinimap = 0;
                                        if (minimapColor != -2) {
                                            encodedMinimap = Rasterizer3D.hslToRgb[checkedLight(minimapColor, 96)];
                                        }
                                        if (overlay_flo.secondaryRgb != -1) {
                                            lightness = overlay_flo.secondaryHue;
                                            int secondaryLightness = overlay_flo.secondaryLightness;
                                            if (secondaryLightness < 0) {
                                                secondaryLightness = 0;
                                            } else if (secondaryLightness > 255) {
                                                secondaryLightness = 255;
                                            }
                                            minimapColor = encode(lightness, overlay_flo.secondarySaturation, secondaryLightness);
                                            encodedMinimap = Rasterizer3D.hslToRgb[checkedLight(minimapColor, 96)];
                                        }

                                        scene.addTile(z, l6, k17, k22, byte4, texture, j19, k19, l19, i20, method187(j21, j20), method187(j21, k20), method187(j21, l20), method187(j21, i21), checkedLight(encodedTile, j20), checkedLight(encodedTile, k20), checkedLight(encodedTile, l20), checkedLight(encodedTile, i21), i22, encodedMinimap);
                                    }
                                }
                            }
                        }

                    }
                }

                for (int j8 = 1; j8 < regionSizeY - 1; j8++) {
                    for (int i10 = 1; i10 < regionSizeX - 1; i10++)
                        scene.setTileLogicHeight(z, i10, j8, getCollisionPlane(j8, z, i10));

                }
            }

            scene.shadeModels(-10, -50, -50);
            for (int j1 = 0; j1 < regionSizeX; j1++) {
                for (int l1 = 0; l1 < regionSizeY; l1++)
                    if ((tileFlags[1][j1][l1] & 2) == 2)
                        scene.applyBridgeMode(l1, j1);

            }

            int i2 = 1;
            int j2 = 2;
            int k2 = 4;
            for (int l2 = 0; l2 < 4; l2++) {
                if (l2 > 0) {
                    i2 <<= 3;
                    j2 <<= 3;
                    k2 <<= 3;
                }
                for (int i3 = 0; i3 <= l2; i3++) {
                    for (int k3 = 0; k3 <= regionSizeY; k3++) {
                        for (int i4 = 0; i4 <= regionSizeX; i4++) {
                            if ((anIntArrayArrayArray135[i3][i4][k3] & i2) != 0) {
                                int k4 = k3;
                                int l5 = k3;
                                int i7 = i3;
                                int k8 = i3;
                                for (; k4 > 0 && (anIntArrayArrayArray135[i3][i4][k4 - 1] & i2) != 0; k4--)
                                    ;
                                for (; l5 < regionSizeY && (anIntArrayArrayArray135[i3][i4][l5 + 1] & i2) != 0; l5++)
                                    ;
                                label0: for (; i7 > 0; i7--) {
                                    for (int j10 = k4; j10 <= l5; j10++)
                                        if ((anIntArrayArrayArray135[i7 - 1][i4][j10] & i2) == 0)
                                            break label0;

                                }

                                label1: for (; k8 < l2; k8++) {
                                    for (int k10 = k4; k10 <= l5; k10++)
                                        if ((anIntArrayArrayArray135[k8 + 1][i4][k10] & i2) == 0)
                                            break label1;

                                }

                                int l10 = ((k8 + 1) - i7) * ((l5 - k4) + 1);
                                if (l10 >= 8) {
                                    char c1 = '\360';
                                    int k14 = tileHeights[k8][i4][k4] - c1;
                                    int l15 = tileHeights[i7][i4][k4];
                                    SceneGraph.createNewSceneCluster(l2, i4 * 128, l15, i4 * 128, l5 * 128 + 128, k14, k4 * 128, 1);
                                    for (int l16 = i7; l16 <= k8; l16++) {
                                        for (int l17 = k4; l17 <= l5; l17++)
                                            anIntArrayArrayArray135[l16][i4][l17] &= ~i2;

                                    }

                                }
                            }
                            if ((anIntArrayArrayArray135[i3][i4][k3] & j2) != 0) {
                                int l4 = i4;
                                int i6 = i4;
                                int j7 = i3;
                                int l8 = i3;
                                for (; l4 > 0 && (anIntArrayArrayArray135[i3][l4 - 1][k3] & j2) != 0; l4--)
                                    ;
                                for (; i6 < regionSizeX && (anIntArrayArrayArray135[i3][i6 + 1][k3] & j2) != 0; i6++)
                                    ;
                                label2: for (; j7 > 0; j7--) {
                                    for (int i11 = l4; i11 <= i6; i11++)
                                        if ((anIntArrayArrayArray135[j7 - 1][i11][k3] & j2) == 0)
                                            break label2;

                                }

                                label3: for (; l8 < l2; l8++) {
                                    for (int j11 = l4; j11 <= i6; j11++)
                                        if ((anIntArrayArrayArray135[l8 + 1][j11][k3] & j2) == 0)
                                            break label3;

                                }

                                int k11 = ((l8 + 1) - j7) * ((i6 - l4) + 1);
                                if (k11 >= 8) {
                                    char c2 = '\360';
                                    int l14 = tileHeights[l8][l4][k3] - c2;
                                    int i16 = tileHeights[j7][l4][k3];
                                    SceneGraph.createNewSceneCluster(l2, l4 * 128, i16, i6 * 128 + 128, k3 * 128, l14, k3 * 128, 2);
                                    for (int i17 = j7; i17 <= l8; i17++) {
                                        for (int i18 = l4; i18 <= i6; i18++)
                                            anIntArrayArrayArray135[i17][i18][k3] &= ~j2;

                                    }

                                }
                            }
                            if ((anIntArrayArrayArray135[i3][i4][k3] & k2) != 0) {
                                int i5 = i4;
                                int j6 = i4;
                                int k7 = k3;
                                int i9 = k3;
                                for (; k7 > 0 && (anIntArrayArrayArray135[i3][i4][k7 - 1] & k2) != 0; k7--)
                                    ;
                                for (; i9 < regionSizeY && (anIntArrayArrayArray135[i3][i4][i9 + 1] & k2) != 0; i9++)
                                    ;
                                label4: for (; i5 > 0; i5--) {
                                    for (int l11 = k7; l11 <= i9; l11++)
                                        if ((anIntArrayArrayArray135[i3][i5 - 1][l11] & k2) == 0)
                                            break label4;

                                }

                                label5: for (; j6 < regionSizeX; j6++) {
                                    for (int i12 = k7; i12 <= i9; i12++)
                                        if ((anIntArrayArrayArray135[i3][j6 + 1][i12] & k2) == 0)
                                            break label5;

                                }

                                if (((j6 - i5) + 1) * ((i9 - k7) + 1) >= 4) {
                                    int j12 = tileHeights[i3][i5][k7];
                                    SceneGraph.createNewSceneCluster(l2, i5 * 128, j12, j6 * 128 + 128, i9 * 128 + 128, j12, k7 * 128, 4);
                                    for (int k13 = i5; k13 <= j6; k13++) {
                                        for (int i15 = k7; i15 <= i9; i15++)
                                            anIntArrayArrayArray135[i3][k13][i15] &= ~k2;

                                    }

                                }
                            }
                        }

                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void initiateVertexHeights(int yOffset, int yLength, int xLength, int xOffset) {
        for (int y = yOffset; y <= yOffset + yLength; y++) {
            for (int x = xOffset; x <= xOffset + xLength; x++) {
                if (x >= 0 && x < regionSizeX && y >= 0 && y < regionSizeY) {
                    shading[0][x][y] = 127;
                    if (x == xOffset && x > 0) {
                        tileHeights[0][x][y] = tileHeights[0][x - 1][y];
                    }
                    if (x == xOffset + xLength && x < regionSizeX - 1) {
                        tileHeights[0][x][y] = tileHeights[0][x + 1][y];
                    }
                    if (y == yOffset && y > 0) {
                        tileHeights[0][x][y] = tileHeights[0][x][y - 1];
                    }
                    if (y == yOffset + yLength && y < regionSizeY - 1) {
                        tileHeights[0][x][y] = tileHeights[0][x][y + 1];
                    }
                }
            }
        }
    }

    private void renderObject(int y, SceneGraph scene, CollisionMap class11, int type, int z, int x, int id, int rotation) {
        if (lowMem && (tileFlags[0][x][y] & BRIDGE_TILE) == 0) {
            if ((tileFlags[z][x][y] & 0x10) != 0) {
                return;
            }

            if (getCollisionPlane(y, z, x) != anInt131) {
                return;
            }
        }
        if (z < maximumPlane) {
            maximumPlane = z;
        }
        int mX = Client.instance.currentRegionX - 6;
        int mY = Client.instance.currentRegionY - 6;
        int actualX = mX * 8 + x;
        int actualY = mY * 8 + y;
        int actualH = z;

        ObjectDefinition definition = ObjectDefinition.lookup(id);

        boolean flag = rotation == 1 || rotation == 3;
        int sizeY = flag ? definition.sizeY : definition.sizeX;
        int sizeX = flag ? definition.sizeX : definition.sizeY;

        if(actualH == 0) {
            //EDGEVILLE HOUSE IN FRONT OF BANK WALLS REMOVAL
            if(actualX >= 3092 && actualX <= 3094 && (actualY == 3514 || actualY == 3513 || actualY == 3506 || actualY == 3505 || actualY == 3507)) {
                return;
            }
        }

        flag = 104 >= sizeY + x;
        int modX = flag ? x + (sizeY >> 1) : x;
        int modX1 = flag ? x + (sizeY + 1 >> 1) : x + 1;

        flag = 104 >= sizeX + y;
        int modY = flag ? (sizeX >> 1) + y : y;
        int modY1 = flag ? y + (sizeX + 1 >> 1) : y + 1;

        int center = tileHeights[z][modX][modY];
        int east = tileHeights[z][modX1][modY];
        int northEast = tileHeights[z][modX1][modY1];
        int north = tileHeights[z][modX][modY1];

        int mean = center + east + northEast + north >> 2;

        long key = (long) ((long) rotation << 20 | (long) type << 14 | ((long) y << 7 | x) + 0x40000000);
        if (!definition.isInteractive)
            key |= ~0x7fffffffffffffffL;
        if(definition.supportItems == 1) {
            key |= 0x400000L;
        }
        key |= (long) id << 32;
        byte config = (byte) ((rotation << 6) + type);
        if (type == 22) {
            if (lowMem && !definition.isInteractive && !definition.obstructsGround) {
                return;
            }
            Object obj;
            if (definition.animation == -1 && definition.configs == null) {
                obj = definition.modelAt(22, rotation, center, east, northEast, north, -1);
            } else {
                obj = new SceneObject(id, rotation, 22, east, northEast, center, north, definition.animation,
                        true);
            }
            scene.addGroundDecoration(z, mean, y, ((Renderable) (obj)), config, key, x);
            if (definition.interactType && definition.isInteractive && class11 != null) {
                class11.block(x, y);
            }
            return;
        }
        if (type == 10 || type == 11) {
            Object obj1;
            if (definition.animation == -1 && definition.configs == null) {
                obj1 = definition.modelAt(10, rotation, center, east, northEast, north, -1);
            } else {
                obj1 = new SceneObject(id, rotation, 10, east, northEast, center, north, definition.animation,
                        true);
            }
            if (obj1 != null) {
                int i5 = 0;
                if (type == 11) {
                    i5 += 256;
                }
                int j4;
                int l4;
                if (rotation == 1 || rotation == 3) {
                    j4 = definition.sizeY;
                    l4 = definition.sizeX;
                } else {
                    j4 = definition.sizeX;
                    l4 = definition.sizeY;
                }
                if (scene.addTiledObject(key, config, mean, l4, ((Renderable) (obj1)), j4, z, i5, y, x)
                        && definition.castsShadow) {
                    Model model;
                    if (obj1 instanceof Model) {
                        model = (Model) obj1;
                    } else {
                        model = definition.modelAt(10, rotation, center, east, northEast, north, -1);
                    }
                    if (model != null) {
                        for (int j5 = 0; j5 <= j4; j5++) {
                            for (int k5 = 0; k5 <= l4; k5++) {
                                int l5 = model.diagonal2DAboveOrigin / 4;
                                if (l5 > 30) {
                                    l5 = 30;
                                }
                                if (l5 > shading[z][x + j5][y + k5]) {
                                    shading[z][x + j5][y + k5] = (byte) l5;
                                }
                            }

                        }

                    }
                }
            }
            if (definition.interactType && class11 != null) {
                class11.method212(definition.blocksProjectile, definition.sizeX, definition.sizeY, x, y, rotation);
            }
            return;
        }
        if (type >= 12) {
            Object obj2;
            if (definition.animation == -1 && definition.configs == null) {
                obj2 = definition.modelAt(type, rotation, center, east, northEast, north, -1);
            } else {
                obj2 = new SceneObject(id, rotation, type, east, northEast, center, north, definition.animation,
                        true);
            }
            scene.addTiledObject(key, config, mean, 1, ((Renderable) (obj2)), 1, z, 0, y, x);
            if (type <= 17 && type != 13 && z > 0) {
                anIntArrayArrayArray135[z][x][y] |= 0x924;
            }
            if (definition.interactType && class11 != null) {
                class11.method212(definition.blocksProjectile, definition.sizeX, definition.sizeY,
                        x, y, rotation);
            }
            return;
        }
        if (type == 0) {
            Object obj3;
            if (definition.animation == -1 && definition.configs == null) {
                obj3 = definition.modelAt(0, rotation, center, east, northEast, north, -1);
            } else {
                obj3 = new SceneObject(id, rotation, 0, east, northEast, center, north, definition.animation,
                        true);
            }
            scene.addWallObject(anIntArray152[rotation], ((Renderable) (obj3)), key, y, config, x, null, mean,
                    0, z);
            if (rotation == 0) {
                if (definition.castsShadow) {
                    shading[z][x][y] = 50;
                    shading[z][x][y + 1] = 50;
                }
                if (definition.occludes) {
                    anIntArrayArrayArray135[z][x][y] |= 0x249;
                }
            } else if (rotation == 1) {
                if (definition.castsShadow) {
                    shading[z][x][y + 1] = 50;
                    shading[z][x + 1][y + 1] = 50;
                }
                if (definition.occludes) {
                    anIntArrayArrayArray135[z][x][y + 1] |= 0x492;
                }
            } else if (rotation == 2) {
                if (definition.castsShadow) {
                    shading[z][x + 1][y] = 50;
                    shading[z][x + 1][y + 1] = 50;
                }
                if (definition.occludes) {
                    anIntArrayArrayArray135[z][x + 1][y] |= 0x249;
                }
            } else {
                if (definition.castsShadow) {
                    shading[z][x][y] = 50;
                    shading[z][x + 1][y] = 50;
                }
                if (definition.occludes) {
                    anIntArrayArrayArray135[z][x][y] |= 0x492;
                }
            }
            if (definition.interactType && class11 != null) {
                class11.method211(y, rotation, x, type, definition.blocksProjectile);
            }
            if (definition.decorDisplacement != 16) {
                scene.method290(y, definition.decorDisplacement, x, z);
            }
            return;
        }
        if (type == 1) {
            Object obj4;
            if (definition.animation == -1 && definition.configs == null) {
                obj4 = definition.modelAt(1, rotation, center, east, northEast, north, -1);
            } else {
                obj4 = new SceneObject(id, rotation, 1, east, northEast, center, north, definition.animation,
                        true);
            }
            scene.addWallObject(anIntArray140[rotation], ((Renderable) (obj4)), key, y, config, x, null, mean,
                    0, z);
            if (definition.castsShadow) {
                if (rotation == 0) {
                    shading[z][x][y + 1] = 50;
                } else if (rotation == 1) {
                    shading[z][x + 1][y + 1] = 50;
                } else if (rotation == 2) {
                    shading[z][x + 1][y] = 50;
                } else {
                    shading[z][x][y] = 50;
                }
            }
            if (definition.interactType && class11 != null) {
                class11.method211(y, rotation, x, type, definition.blocksProjectile);
            }
            return;
        }
        if (type == 2) {
            int i3 = rotation + 1 & 3;
            Object obj11;
            Object obj12;
            if (definition.animation == -1 && definition.configs == null) {
                obj11 = definition.modelAt(2, 4 + rotation, center, east, northEast, north, -1);
                obj12 = definition.modelAt(2, i3, center, east, northEast, north, -1);
            } else {
                obj11 = new SceneObject(id, 4 + rotation, 2, east, northEast, center, north, definition.animation,
                        true);
                obj12 = new SceneObject(id, i3, 2, east, northEast, center, north, definition.animation,
                        true);
            }
            scene.addWallObject(anIntArray152[rotation], ((Renderable) (obj11)), key, y, config, x,
                    ((Renderable) (obj12)), mean, anIntArray152[i3], z);
            if (definition.occludes) {
                if (rotation == 0) {
                    anIntArrayArrayArray135[z][x][y] |= 0x249;
                    anIntArrayArrayArray135[z][x][y + 1] |= 0x492;
                } else if (rotation == 1) {
                    anIntArrayArrayArray135[z][x][y + 1] |= 0x492;
                    anIntArrayArrayArray135[z][x + 1][y] |= 0x249;
                } else if (rotation == 2) {
                    anIntArrayArrayArray135[z][x + 1][y] |= 0x249;
                    anIntArrayArrayArray135[z][x][y] |= 0x492;
                } else {
                    anIntArrayArrayArray135[z][x][y] |= 0x492;
                    anIntArrayArrayArray135[z][x][y] |= 0x249;
                }
            }
            if (definition.interactType && class11 != null) {
                class11.method211(y, rotation, x, type, definition.blocksProjectile);
            }
            if (definition.decorDisplacement != 16) {
                scene.method290(y, definition.decorDisplacement, x, z);
            }
            return;
        }
        if (type == 3) {
            Object obj5;
            if (definition.animation == -1 && definition.configs == null) {
                obj5 = definition.modelAt(3, rotation, center, east, northEast, north, -1);
            } else {
                obj5 = new SceneObject(id, rotation, 3, east, northEast, center, north, definition.animation,
                        true);
            }
            scene.addWallObject(anIntArray140[rotation], ((Renderable) (obj5)), key, y, config, x, null, mean,
                    0, z);
            if (definition.castsShadow) {
                if (rotation == 0) {
                    shading[z][x][y + 1] = 50;
                } else if (rotation == 1) {
                    shading[z][x + 1][y + 1] = 50;
                } else if (rotation == 2) {
                    shading[z][x + 1][y] = 50;
                } else {
                    shading[z][x][y] = 50;
                }
            }
            if (definition.interactType && class11 != null) {
                class11.method211(y, rotation, x, type, definition.blocksProjectile);
            }
            return;
        }
        if (type == 9) {
            Object obj6;
            if (definition.animation == -1 && definition.configs == null) {
                obj6 = definition.modelAt(type, rotation, center, east, northEast, north, -1);
            } else {
                obj6 = new SceneObject(id, rotation, type, east, northEast, center, north, definition.animation,
                        true);
            }
            scene.addTiledObject(key, config, mean, 1, ((Renderable) (obj6)), 1, z, 0, y, x);
            if (definition.interactType && class11 != null) {
                class11.method212(definition.blocksProjectile, definition.sizeX, definition.sizeY,
                        x, y, rotation);
            }
            return;
        }
        if (definition.contouredGround) {
            if (rotation == 1) {
                int j3 = north;
                north = northEast;
                northEast = east;
                east = center;
                center = j3;
            } else if (rotation == 2) {
                int k3 = north;
                north = east;
                east = k3;
                k3 = northEast;
                northEast = center;
                center = k3;
            } else if (rotation == 3) {
                int l3 = north;
                north = center;
                center = east;
                east = northEast;
                northEast = l3;
            }
        }
        if (type == 4) {
            Object obj7;
            if (definition.animation == -1 && definition.configs == null) {
                obj7 = definition.modelAt(4, 0, center, east, northEast, north, -1);
            } else {
                obj7 = new SceneObject(id, 0, 4, east, northEast, center, north, definition.animation,
                        true);
            }
            scene.addWallDecoration(key, y, rotation * 512, z, 0, mean, ((Renderable) (obj7)), x, config, 0,
                    anIntArray152[rotation]);
            return;
        }
        if (type == 5) {
            int i4 = 16;
            long k4 = scene.getWallObjectUid(z, x, y);
            if (k4 > 0) {
                i4 = ObjectDefinition.lookup(ObjectKeyUtil.getObjectId(k4)).decorDisplacement;
            }
            Object obj13;
            if (definition.animation == -1 && definition.configs == null) {
                obj13 = definition.modelAt(4, 0, center, east, northEast, north, -1);
            } else {
                obj13 = new SceneObject(id, 0, 4, east, northEast, center, north, definition.animation,
                        true);
            }
            scene.addWallDecoration(key, y, rotation * 512, z, COSINE_VERTICES[rotation] * i4, mean,
                    ((Renderable) (obj13)), x, config, SINE_VERTICIES[rotation] * i4, anIntArray152[rotation]);
            return;
        }
        if (type == 6) {
            Object obj8;
            if (definition.animation == -1 && definition.configs == null) {
                obj8 = definition.modelAt(4, 0, center, east, northEast, north, -1);
            } else {
                obj8 = new SceneObject(id, 0, 4, east, northEast, center, north, definition.animation,
                        true);
            }
            scene.addWallDecoration(key, y, rotation, z, 0, mean, ((Renderable) (obj8)), x, config, 0, 256);
            return;
        }
        if (type == 7) {
            Object obj9;
            if (definition.animation == -1 && definition.configs == null) {
                obj9 = definition.modelAt(4, 0, center, east, northEast, north, -1);
            } else {
                obj9 = new SceneObject(id, 0, 4, east, northEast, center, north, definition.animation,
                        true);
            }
            scene.addWallDecoration(key, y, rotation, z, 0, mean, ((Renderable) (obj9)), x, config, 0, 512);
            return;
        }
        if (type == 8) {
            Object obj10;
            if (definition.animation == -1 && definition.configs == null) {
                obj10 = definition.modelAt(4, 0, center, east, northEast, north, -1);
            } else {
                obj10 = new SceneObject(id, 0, 4, east, northEast, center, north, definition.animation,
                        true);
            }
            scene.addWallDecoration(key, y, rotation, z, 0, mean, ((Renderable) (obj10)), x, config, 0, 768);
        }
    }

    private static int interpolatedNoise(int x, int y, int frequencyReciprocal) {
        int l = x / frequencyReciprocal;
        int i1 = x & frequencyReciprocal - 1;
        int j1 = y / frequencyReciprocal;
        int k1 = y & frequencyReciprocal - 1;
        int l1 = smoothNoise(l, j1);
        int i2 = smoothNoise(l + 1, j1);
        int j2 = smoothNoise(l, j1 + 1);
        int k2 = smoothNoise(l + 1, j1 + 1);
        int l2 = interpolate(l1, i2, i1, frequencyReciprocal);
        int i3 = interpolate(j2, k2, i1, frequencyReciprocal);
        return interpolate(l2, i3, k1, frequencyReciprocal);
    }

    /**
     * Encodes the hue, saturation, and luminance into a colour value.
     *
     * @param hue The hue.
     * @param saturation The saturation.
     * @param luminance The luminance.
     * @return The colour.
     */
    private int encode(int hue, int saturation, int luminance) {
        if (luminance > 179)
            saturation /= 2;
        if (luminance > 192)
            saturation /= 2;
        if (luminance > 217)
            saturation /= 2;
        if (luminance > 243)
            saturation /= 2;
        return (hue / 4 << 10) + (saturation / 32 << 7) + luminance / 2;
    }

    public static boolean modelReady(int i, int j) {
        ObjectDefinition class46 = ObjectDefinition.lookup(i);
        if (j == 11)
            j = 10;
        if (j >= 5 && j <= 8)
            j = 4;
        return class46.method577(j);
    }

    public final void loadMapChunk(final int subBlockZ, final int rotation, final CollisionMap[] collisionMap, final int mapRegionX,
                                   final int subBlockX, final byte[] terrainData, final int subBlockY, final int blockPlane, final int mapRegionY) {
        for (int regionX = 0; regionX < 8; regionX++) {
            for (int regionY = 0; regionY < 8; regionY++) {
                if (mapRegionX + regionX > 0 && mapRegionX + regionX < 103 && mapRegionY + regionY > 0
                        && mapRegionY + regionY < 103) {
                    collisionMap[blockPlane].clipData[mapRegionX + regionX][mapRegionY + regionY] &= 0xfeffffff;
                }
            }

        }
        final Buffer terrainDataStream = new Buffer(terrainData);
        for (int plane = 0; plane < 4; plane++) {
            for (int regionX = 0; regionX < 64; regionX++) {
                for (int regionY = 0; regionY < 64; regionY++) {
                    if (plane == subBlockZ && regionX >= subBlockX && regionX < subBlockX + 8 && regionY >= subBlockY
                            && regionY < subBlockY + 8) {
                        this.loadTile(mapRegionY + ChunkUtil.getRotatedMapChunkY(regionY & 7, rotation, regionX & 7),
                                0, terrainDataStream,
                                mapRegionX + ChunkUtil.getRotatedMapChunkX(rotation, regionY & 7, regionX & 7),
                                blockPlane, rotation, 0);
                    } else {
                        this.loadTile(-1, 0, terrainDataStream, -1, 0, 0, 0);
                    }
                }

            }
        }
    }

    public final void loadTerrainBlock(byte[] blockData, int blockY, int blockX, int k, int l, CollisionMap[] collisionMap) {
        for (int plane = 0; plane < 4; plane++) {
            for (int tileX = 0; tileX < 64; tileX++) {
                for (int tileY = 0; tileY < 64; tileY++) {
                    if (blockX + tileX > 0 && blockX + tileX < 103 && blockY + tileY > 0 && blockY + tileY < 103) {
                        collisionMap[plane].clipData[blockX + tileX][blockY + tileY] &= 0xfeffffff;
                    }
                }

            }

        }

        final Buffer stream = new Buffer(blockData);
        for (int plane = 0; plane < 4; plane++) {
            for (int tileX = 0; tileX < 64; tileX++) {
                for (int tileY = 0; tileY < 64; tileY++) {
                    this.loadTile(tileY + blockY, l, stream, tileX + blockX, plane, 0, k);
                }

            }

        }
    }

    private void loadTile(int tileY, int xOffset, Buffer stream, int tileX, int tileZ, int overlayRotation, int seed) {
        if (tileX >= 0 && tileX < 104 && tileY >= 0 && tileY < 104) {
            tileFlags[tileZ][tileX][tileY] = 0;
            do {
                int value = stream.readUShort();
                if (value == 0)
                    if (tileZ == 0) {
                        tileHeights[0][tileX][tileY] = -calculateVertexHeight(0xe3b7b + tileX + seed, 0x87cce + tileY + xOffset) * 8;
                        return;
                    } else {
                        tileHeights[tileZ][tileX][tileY] = tileHeights[tileZ - 1][tileX][tileY] - 240;
                        return;
                    }
                if (value == 1) {
                    int height = stream.readUnsignedByte();
                    if (height == 1)
                        height = 0;
                    if (tileZ == 0) {
                        tileHeights[0][tileX][tileY] = -height * 8;
                        return;
                    } else {
                        tileHeights[tileZ][tileX][tileY] = tileHeights[tileZ - 1][tileX][tileY] - height * 8;
                        return;
                    }
                }
                if (value <= 49) {
                    overlays[tileZ][tileX][tileY] = (short) stream.readShort();
                    overlayTypes[tileZ][tileX][tileY] = (byte) ((value - 2) / 4);
                    overlayOrientations[tileZ][tileX][tileY] = (byte) ((value - 2) + overlayRotation & 3);
                } else if (value <= 81)
                    tileFlags[tileZ][tileX][tileY] = (byte) (value - 49);
                else
                    underlays[tileZ][tileX][tileY] = (byte) (value - 81);
            } while (true);
        }
        do {
            int value = stream.readUShort();
            if (value == 0)
                break;
            if (value == 1) {
                stream.readUnsignedByte();
                return;
            }
            if (value <= 49)
                stream.readShort();
        } while (true);
    }

    private static int calculateVertexHeight(int x, int y) {
        int vertexHeight = (interpolatedNoise(x + 45365, y + 0x16713, 4) - 128) + (interpolatedNoise(x + 10294, y + 37821, 2) - 128 >> 1) + (interpolatedNoise(x, y, 1) - 128 >> 2);
        vertexHeight = (int) ((double) vertexHeight * 0.29999999999999999D) + 35;
        if (vertexHeight < 10) {
            vertexHeight = 10;
        }
        else if (vertexHeight > 60) {
            vertexHeight = 60;
        }
        return vertexHeight;
    }

    /**
     * Returns the plane that actually contains the collision flag, to adjust for objects such as bridges. TODO better
     * name
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param z The z coordinate.
     * @return The correct z coordinate.
     */
    private int getCollisionPlane(int y, int z, int x) {
        if ((tileFlags[z][x][y] & FORCE_LOWEST_PLANE) != 0) {
            return 0;
        }
        if (z > 0 && (tileFlags[1][x][y] & BRIDGE_TILE) != 0) {
            return z - 1;
        } else {
            return z;
        }
    }

    public final void readObjectMap(CollisionMap aclass11[], SceneGraph worldController, int i, int j, int k, int l, byte abyte0[], int i1, int j1, int k1) {
        label0: {
            Buffer stream = new Buffer(abyte0);
            int l1 = -1;
            do {
                int i2 = stream.readUSmart2();
                if (i2 == 0)
                    break label0;
                l1 += i2;
                int j2 = 0;
                do {
                    int k2 = stream.readUSmart();
                    if (k2 == 0)
                        break;
                    j2 += k2 - 1;
                    int l2 = j2 & 0x3f;
                    int i3 = j2 >> 6 & 0x3f;
                    int j3 = j2 >> 12;
                    int k3 = stream.readUnsignedByte();
                    int l3 = k3 >> 2;
                    int i4 = k3 & 3;
                    if (j3 == i && i3 >= i1 && i3 < i1 + 8 && l2 >= k && l2 < k + 8) {
                        ObjectDefinition class46 = ObjectDefinition.lookup(l1);
                        int j4 = j + ChunkUtil.method157(j1, class46.sizeY, i3 & 7, l2 & 7, class46.sizeX);
                        int k4 = k1 + ChunkUtil.method158(l2 & 7, class46.sizeY, j1, class46.sizeX, i3 & 7);
                        if (j4 > 0 && k4 > 0 && j4 < 103 && k4 < 103) {
                            int l4 = j3;
                            if ((tileFlags[1][j4][k4] & 2) == 2)
                                l4--;
                            CollisionMap class11 = null;
                            if (l4 >= 0)
                                class11 = aclass11[l4];
                            renderObject(k4, worldController, class11, l3, l, j4, l1, i4 + j1 & 3);
                        }
                    }
                } while (true);
            } while (true);
        }
    }

    private static int interpolate(int a, int b, int angle, int frequencyReciprocal) {
        int cosine = 0x10000 - Rasterizer3D.COSINE[(angle * 1024) / frequencyReciprocal] >> 1;
        return (a * (0x10000 - cosine) >> 16) + (b * cosine >> 16);
    }



    private int checkedLight(int color, int light) {
        if (color == -2) {
            return 12345678;
        } else if (color == -1) {
            if (SceneGraph.lowMem) {
                if (light < 0)
                    light = 0;
                else if (light > 127)
                    light = 127;
                light = 127 - light;
                return light;
            } else {
                if (light < 2) {
                    light = 2;
                } else if (light > 126) {
                    light = 126;
                }

                return light;
            }
        } else {
            light = (color & 0x7F) * light / 128;
            if (light < 2) {
                light = 2;
            } else if (light > 126) {
                light = 126;
            }

            return (color & 0xFF80) + light;
        }
    }

    private static int method187(int hslBitmap, int lightIntensity) {
        if (hslBitmap == -1) {
            return 12345678;
        } else {
            lightIntensity = (hslBitmap & 0x7F) * lightIntensity / 128;
            if (lightIntensity < 2) {
                lightIntensity = 2;
            } else if (lightIntensity > 126) {
                lightIntensity = 126;
            }

            return (hslBitmap & 0xFF80) + lightIntensity;
        }
    }

    private static int smoothNoise(int x, int y) {
        int corners = calculateNoise(x - 1, y - 1) + calculateNoise(x + 1, y - 1) + calculateNoise(x - 1, y + 1) + calculateNoise(x + 1, y + 1);
        int sides = calculateNoise(x - 1, y) + calculateNoise(x + 1, y) + calculateNoise(x, y - 1) + calculateNoise(x, y + 1);
        int center = calculateNoise(x, y);
        return corners / 16 + sides / 8 + center / 4;
    }

    public static void placeObject(SceneGraph worldController, int rotation, int regionY, int type, int plane,
                                   CollisionMap class11, int[][][] ai, int regionX, int j1, int k1) {
        int l1 = ai[plane][regionX][regionY];
        int i2 = ai[plane][regionX + 1][regionY];
        int j2 = ai[plane][regionX + 1][regionY + 1];
        int k2 = ai[plane][regionX][regionY + 1];
        int l2 = l1 + i2 + j2 + k2 >> 2;
        ObjectDefinition definition = ObjectDefinition.lookup(j1);

        long key = (long) ((long) rotation << 20 | (long) type << 14 | ((long) regionY << 7 | regionX) + 0x40000000);
        if (!definition.isInteractive)
            key |= ~0x7fffffffffffffffL;

        if(definition.supportItems == 1) {
            key |= 0x400000L;
        }
        key |= (long) j1 << 32;
        byte byte1 = (byte) ((rotation << 6) + type);
        if (type == 22) {
            Object obj;
            if (definition.animation == -1 && definition.configs == null) {
                obj = definition.modelAt(22, rotation, l1, i2, j2, k2, -1);
            } else {
                obj = new SceneObject(j1, rotation, 22, i2, j2, l1, k2, definition.animation, true);
            }
            worldController.addGroundDecoration(k1, l2, regionY, ((Renderable) (obj)), byte1, key, regionX);
            if (definition.interactType && definition.isInteractive) {
                class11.block(regionX, regionY);
            }
            return;
        }
        if (type == 10 || type == 11) {
            Object obj1;
            if (definition.animation == -1 && definition.configs == null) {
                obj1 = definition.modelAt(10, rotation, l1, i2, j2, k2, -1);
            } else {
                obj1 = new SceneObject(j1, rotation, 10, i2, j2, l1, k2, definition.animation, true);
            }
            if (obj1 != null) {
                int j5 = 0;
                if (type == 11) {
                    j5 += 256;
                }
                int k4;
                int i5;
                if (rotation == 1 || rotation == 3) {
                    k4 = definition.sizeY;
                    i5 = definition.sizeX;
                } else {
                    k4 = definition.sizeX;
                    i5 = definition.sizeY;
                }
                worldController.addTiledObject(key, byte1, l2, i5, ((Renderable) (obj1)), k4, k1, j5, regionY, regionX);
            }
            if (definition.interactType) {
                class11.method212(definition.blocksProjectile, definition.sizeX, definition.sizeY, regionX, regionY, rotation);
            }
            return;
        }
        if (type >= 12) {
            Object obj2;
            if (definition.animation == -1 && definition.configs == null) {
                obj2 = definition.modelAt(type, rotation, l1, i2, j2, k2, -1);
            } else {
                obj2 = new SceneObject(j1, rotation, type, i2, j2, l1, k2, definition.animation, true);
            }
            worldController.addTiledObject(key, byte1, l2, 1, ((Renderable) (obj2)), 1, k1, 0, regionY, regionX);
            if (definition.interactType) {
                class11.method212(definition.blocksProjectile, definition.sizeX, definition.sizeY, regionX, regionY, rotation);
            }
            return;
        }
        if (type == 0) {
            Object obj3;
            if (definition.animation == -1 && definition.configs == null) {
                obj3 = definition.modelAt(0, rotation, l1, i2, j2, k2, -1);
            } else {
                obj3 = new SceneObject(j1, rotation, 0, i2, j2, l1, k2, definition.animation, true);
            }
            worldController.addWallObject(anIntArray152[rotation], ((Renderable) (obj3)), key, regionY, byte1, regionX, null,
                    l2, 0, k1);
            if (definition.interactType) {
                class11.method211(regionY, rotation, regionX, type, definition.blocksProjectile);
            }
            return;
        }
        if (type == 1) {
            Object obj4;
            if (definition.animation == -1 && definition.configs == null) {
                obj4 = definition.modelAt(1, rotation, l1, i2, j2, k2, -1);
            } else {
                obj4 = new SceneObject(j1, rotation, 1, i2, j2, l1, k2, definition.animation, true);
            }
            worldController.addWallObject(anIntArray140[rotation], ((Renderable) (obj4)), key, regionY, byte1, regionX, null,
                    l2, 0, k1);
            if (definition.interactType) {
                class11.method211(regionY, rotation, regionX, type, definition.blocksProjectile);
            }
            return;
        }
        if (type == 2) {
            int j3 = rotation + 1 & 3;
            Object obj11;
            Object obj12;
            if (definition.animation == -1 && definition.configs == null) {
                obj11 = definition.modelAt(2, 4 + rotation, l1, i2, j2, k2, -1);
                obj12 = definition.modelAt(2, j3, l1, i2, j2, k2, -1);
            } else {
                obj11 = new SceneObject(j1, 4 + rotation, 2, i2, j2, l1, k2, definition.animation, true);
                obj12 = new SceneObject(j1, j3, 2, i2, j2, l1, k2, definition.animation, true);
            }
            worldController.addWallObject(anIntArray152[rotation], ((Renderable) (obj11)), key, regionY, byte1, regionX,
                    ((Renderable) (obj12)), l2, anIntArray152[j3], k1);
            if (definition.interactType) {
                class11.method211(regionY, rotation, regionX, type, definition.blocksProjectile);
            }
            return;
        }
        if (type == 3) {
            Object obj5;
            if (definition.animation == -1 && definition.configs == null) {
                obj5 = definition.modelAt(3, rotation, l1, i2, j2, k2, -1);
            } else {
                obj5 = new SceneObject(j1, rotation, 3, i2, j2, l1, k2, definition.animation, true);
            }
            worldController.addWallObject(anIntArray140[rotation], ((Renderable) (obj5)), key, regionY, byte1, regionX, null,
                    l2, 0, k1);
            if (definition.interactType) {
                class11.method211(regionY, rotation, regionX, type, definition.blocksProjectile);
            }
            return;
        }
        if (type == 9) {
            Object obj6;
            if (definition.animation == -1 && definition.configs == null) {
                obj6 = definition.modelAt(type, rotation, l1, i2, j2, k2, -1);
            } else {
                obj6 = new SceneObject(j1, rotation, type, i2, j2, l1, k2, definition.animation, true);
            }
            worldController.addTiledObject(key, byte1, l2, 1, ((Renderable) (obj6)), 1, k1, 0, regionY, regionX);
            if (definition.interactType) {
                class11.method212(definition.blocksProjectile, definition.sizeX, definition.sizeY, regionX, regionY, rotation);
            }
            return;
        }
        if (definition.contouredGround) {
            if (rotation == 1) {
                int k3 = k2;
                k2 = j2;
                j2 = i2;
                i2 = l1;
                l1 = k3;
            } else if (rotation == 2) {
                int l3 = k2;
                k2 = i2;
                i2 = l3;
                l3 = j2;
                j2 = l1;
                l1 = l3;
            } else if (rotation == 3) {
                int i4 = k2;
                k2 = l1;
                l1 = i2;
                i2 = j2;
                j2 = i4;
            }
        }
        if (type == 4) {
            Object obj7;
            if (definition.animation == -1 && definition.configs == null) {
                obj7 = definition.modelAt(4, 0, l1, i2, j2, k2, -1);
            } else {
                obj7 = new SceneObject(j1, 0, 4, i2, j2, l1, k2, definition.animation, true);
            }
            worldController.addWallDecoration(key, regionY, rotation * 512, k1, 0, l2, ((Renderable) (obj7)), regionX, byte1,
                    0, anIntArray152[rotation]);
            return;
        }
        if (type == 5) {
            int j4 = 16;
            long l4 = worldController.getWallObjectUid(k1, regionX, regionY);
            if (l4 > 0) {
                j4 = ObjectDefinition.lookup(ObjectKeyUtil.getObjectId(l4)).decorDisplacement;
            }
            Object obj13;
            if (definition.animation == -1 && definition.configs == null) {
                obj13 = definition.modelAt(4, 0, l1, i2, j2, k2, -1);
            } else {
                obj13 = new SceneObject(j1, 0, 4, i2, j2, l1, k2, definition.animation, true);
            }
            worldController.addWallDecoration(key, regionY, rotation * 512, k1, COSINE_VERTICES[rotation] * j4, l2,
                    ((Renderable) (obj13)), regionX, byte1, SINE_VERTICIES[rotation] * j4, anIntArray152[rotation]);
            return;
        }
        if (type == 6) {
            Object obj8;
            if (definition.animation == -1 && definition.configs == null) {
                obj8 = definition.modelAt(4, 0, l1, i2, j2, k2, -1);
            } else {
                obj8 = new SceneObject(j1, 0, 4, i2, j2, l1, k2, definition.animation, true);
            }
            worldController.addWallDecoration(key, regionY, rotation, k1, 0, l2, ((Renderable) (obj8)), regionX, byte1, 0,
                    256);
            return;
        }
        if (type == 7) {
            Object obj9;
            if (definition.animation == -1 && definition.configs == null) {
                obj9 = definition.modelAt(4, 0, l1, i2, j2, k2, -1);
            } else {
                obj9 = new SceneObject(j1, 0, 4, i2, j2, l1, k2, definition.animation, true);
            }
            worldController.addWallDecoration(key, regionY, rotation, k1, 0, l2, ((Renderable) (obj9)), regionX, byte1, 0,
                    512);
            return;
        }
        if (type == 8) {
            Object obj10;
            if (definition.animation == -1 && definition.configs == null) {
                obj10 = definition.modelAt(4, 0, l1, i2, j2, k2, -1);
            } else {
                obj10 = new SceneObject(j1, 0, 4, i2, j2, l1, k2, definition.animation, true);
            }
            worldController.addWallDecoration(key, regionY, rotation, k1, 0, l2, ((Renderable) (obj10)), regionX, byte1, 0,
                    768);
        }
    }

    public static void requestModelPreload(byte[] is) {
        label0: {
            Buffer stream = new Buffer(is);
            int l = -1;
            do {
                int i1 = stream.readUSmart();
                if (i1 == 0)
                    break label0;
                l += i1;
                int j1 = 0;
                do {
                    int k1 = stream.readUSmart();
                    if (k1 == 0)
                        break;
                    j1 += k1 - 1;
                    int j2 = j1 >> 12;
                    int k2 = stream.readUnsignedByte();
                    ObjectDefinition class46 = ObjectDefinition.lookup(l);
                    class46.method579();


                } while (true);
            } while (true);
        }
    }

    public static boolean method189(int i, byte[] is, int i_250_) // xxx bad
    // method,
    // decompiled
    // with JODE
    {
        boolean bool = true;
        Buffer stream = new Buffer(is);
        int i_252_ = -1;
        for (;;) {
            int i_253_ = stream.readUSmart();
            if (i_253_ == 0)
                break;
            i_252_ += i_253_;
            int i_254_ = 0;
            boolean bool_255_ = false;
            for (;;) {
                if (bool_255_) {
                    int i_256_ = stream.readUSmart();
                    if (i_256_ == 0)
                        break;
                    stream.readUnsignedByte();
                } else {
                    int i_257_ = stream.readUSmart();
                    if (i_257_ == 0)
                        break;
                    i_254_ += i_257_ - 1;
                    int i_258_ = i_254_ & 0x3f;
                    int i_259_ = i_254_ >> 6 & 0x3f;
                    int i_260_ = stream.readUnsignedByte() >> 2;
                    int i_261_ = i_259_ + i;
                    int i_262_ = i_258_ + i_250_;
                    if (i_261_ > 0 && i_262_ > 0 && i_261_ < 103 && i_262_ < 103) {
                        ObjectDefinition class46 = ObjectDefinition.lookup(i_252_);
                        if (i_260_ != 22 || !lowMem || class46.isInteractive || class46.obstructsGround) {
                            bool &= class46.method579();
                            bool_255_ = true;
                        }
                    }
                }
            }
        }
        return bool;
    }

    public final void method190(int i, CollisionMap aclass11[], int j, SceneGraph worldController, byte abyte0[]) {
        label0: {
            Buffer stream = new Buffer(abyte0);
            int l = -1;
            do {
                int i1 = stream.readUSmart();
                if (i1 == 0)
                    break label0;
                l += i1;
                int j1 = 0;
                do {
                    int k1 = stream.readUSmart();
                    if (k1 == 0)
                        break;
                    j1 += k1 - 1;
                    int l1 = j1 & 0x3f;
                    int i2 = j1 >> 6 & 0x3f;
                    int j2 = j1 >> 12;
                    int k2 = stream.readUnsignedByte();
                    int l2 = k2 >> 2;
                    int i3 = k2 & 3;
                    int j3 = i2 + i;
                    int k3 = l1 + j;
                    if (j3 > 0 && k3 > 0 && j3 < 103 && k3 < 103 && j2 >= 0 && j2 < 4) {
                        int l3 = j2;
                        if ((tileFlags[1][j3][k3] & 2) == 2)
                            l3--;
                        CollisionMap class11 = null;
                        if (l3 >= 0)
                            class11 = aclass11[l3];
                        renderObject(k3, worldController, class11, l2, j2, j3, l, i3);
                    }
                } while (true);
            } while (true);
        }
    }
}