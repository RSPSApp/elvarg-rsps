package com.runescape.draw.flames;

import com.runescape.Client;
import com.runescape.draw.Rasterizer2D;
import com.runescape.graphics.sprite.Sprite;
import net.runelite.rs.api.RSLoginScreenAnimation;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import static com.runescape.draw.Rasterizer2D.drawAlpha;

public class FlameManager implements RSLoginScreenAnimation {

    private int[] field1234;

    private int field1218;

    private int random1;

    private int random2;

    private int[] flameStrengths;

    private int[] field1214;

    private int[] titleFlames;

    private int[] titleFlamesTemp;

    private int field1229;

    private int field1226;

    private int currentCycle;

    private FlameColours flameColours;

    public FlameManager() {
        field1234 = new int[256];
        field1218 = 0;
        random1 = 0;
        random2 = 0;
        field1229 = 0;
        field1226 = 0;
        currentCycle = 0;

        initColors();
    }


    void initColors() {
        flameColours = new FlameColours();
        field1229 = 0;
        titleFlames = new int[32768];
        titleFlamesTemp = new int[32768];
        updateFlameShape(null);
        flameStrengths = new int[32768];
        field1214 = new int[32768];
    }


    public void reset() {
        flameColours.redFlameColours = null;
        flameColours.greenFlameColours = null;
        flameColours.blueFlameColours = null;
        flameColours.currentFlameColours = null;
        titleFlames = null;
        titleFlamesTemp = null;
        flameStrengths = null;
        field1214 = null;
        field1229 = 0;
        field1226 = 0;
    }


    public void draw(int xPosition, int yPosition,int cycle, int alpha) {
        if (Client.instance.shouldRenderLoginScreenFire())
        {
            if (flameStrengths == null) {
                initColors();
            }

            if (currentCycle == 0) {
                currentCycle = cycle;
            }

            int lastCycle = cycle - currentCycle;
            if (lastCycle >= 256) {
                lastCycle = 0;
            }

            currentCycle = cycle;
            if (lastCycle > 0) {
                method2207(lastCycle);
            }

            initializeFlames(xPosition,yPosition,alpha);
        }
    }

    int[] flameSprites = IntStream.rangeClosed(655, 666).toArray();

    public static int getRandom(int[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    final void method2207(int var1) {
        field1229 += var1 * 128;
        int var2;
        if (field1229 > titleFlames.length) {
            field1229 -= titleFlames.length;
            updateFlameShape(Client.spriteCache.lookup(getRandom(flameSprites)));
        }

        var2 = 0;
        int var3 = var1 * 128;
        int var4 = (256 - var1) * 128;

        int var6;
        for (int strengthIndex = 0; strengthIndex < var4; ++strengthIndex) {
            var6 = flameStrengths[var3 + var2] - titleFlames[var2 + field1229 & titleFlames.length - 1] * var1 / 6;
            if (var6 < 0) {
                var6 = 0;
            }

            flameStrengths[var2++] = var6;
        }

        byte var15 = 10;
        var6 = 128 - var15;

        int rand;
        int var10;
        for (rand = 256 - var1; rand < 256; ++rand) {
            int var8 = rand * 128;

            for (int var9 = 0; var9 < 128; ++var9) {
                var10 = (int)(Math.random() * 100.0D);
                if (var10 < 50 && var9 > var15 && var9 < var6) {
                    flameStrengths[var9 + var8] = 255;
                } else {
                    flameStrengths[var8 + var9] = 0;
                }
            }
        }

        if (random1 > 0) {
            random1 -= var1 * 4;
        }

        if (random2 > 0) {
            random2 -= var1 * 4;
        }

        if (random1 == 0 && random2 == 0) {
            rand = (int)(Math.random() * (double)(2000 / var1));
            if (rand == 0) {
                random1 = 1024;
            }

            if (rand == 1) {
                random2 = 1024;
            }
        }

        for (rand = 0; rand < 256 - var1; ++rand) {
            field1234[rand] = field1234[rand + var1];
        }

        for (rand = 256 - var1; rand < 256; ++rand) {
            field1234[rand] = (int)(Math.sin((double)field1218 / 14.0D) * 16.0D + Math.sin((double)field1218 / 15.0D) * 14.0D + Math.sin((double)field1218 / 16.0D) * 12.0D);
            ++field1218;
        }

        field1226 += var1;
        rand = ((Client.tick & 1) + var1) / 2;
        if (rand > 0) {
            short var16 = 128;
            byte var17 = 2;
            var10 = 128 - var17 - var17;

            int var11;
            int var12;
            int var13;
            for (var11 = 0; var11 < field1226 * 100; ++var11) {
                var12 = (int)(Math.random() * (double)var10) + var17;
                var13 = (int)(Math.random() * (double)var16) + var16;
                flameStrengths[var12 + (var13 << 7)] = 192;
            }

            field1226 = 0;

            int var14;
            for (var11 = 0; var11 < 256; ++var11) {
                var12 = 0;
                var13 = var11 * 128;

                for (var14 = -rand; var14 < 128; ++var14) {
                    if (var14 + rand < 128) {
                        var12 += flameStrengths[rand + var13 + var14];
                    }

                    if (var14 - (rand + 1) >= 0) {
                        var12 -= flameStrengths[var13 + var14 - (rand + 1)];
                    }

                    if (var14 >= 0) {
                        field1214[var13 + var14] = var12 / (rand * 2 + 1);
                    }
                }
            }

            for (var11 = 0; var11 < 128; ++var11) {
                var12 = 0;

                for (var13 = -rand; var13 < 256; ++var13) {
                    var14 = var13 * 128;
                    if (rand + var13 < 256) {
                        var12 += field1214[var11 + var14 + rand * 128];
                    }

                    if (var13 - (rand + 1) >= 0) {
                        var12 -= field1214[var14 + var11 - (rand + 1) * 128];
                    }

                    if (var13 >= 0) {
                        flameStrengths[var14 + var11] = var12 / (rand * 2 + 1);
                    }
                }
            }
        }

    }

    final int rotateFlameColour(int r, int g, int b) {
        int alpha = 256 - b;
        return ((r & 0xFF00FF) * alpha + (g & 0xFF00FF) * b & 0xFF00FF00)
                + ((r & 0x00FF00) * alpha + (g & 0x00FF00) * b & 0xFF0000) >> 8;
    }


    final void initializeFlames(int xPosition, int yPosition, int alpha) {
        int length = flameColours.currentFlameColours.length;
        if (random1 > 0) {
            changeColours(random1, flameColours.greenFlameColours);
        } else if (random2 > 0) {
            changeColours(random2, flameColours.blueFlameColours);
        } else {
            System.arraycopy(flameColours.redFlameColours, 0, flameColours.currentFlameColours, 0, length);
        }

        drawFlames(xPosition,yPosition,alpha);
    }

    final void changeColours(int random1, int[] colors) {
        int currentColorSize = flameColours.currentFlameColours.length;

        for (int strength = 0; strength < currentColorSize; ++strength) {
            if (random1 > 768) {
                flameColours.currentFlameColours[strength] = rotateFlameColour(flameColours.redFlameColours[strength], colors[strength], 1024 - random1);
            } else if (random1 > 256) {
                flameColours.currentFlameColours[strength] = colors[strength];
            } else {
                flameColours.currentFlameColours[strength] = rotateFlameColour(colors[strength], flameColours.redFlameColours[strength], 256 - random1);
            }
        }

    }


    final void drawFlames(int xPosition, int yPosition, int alpha) {
        int pixels = 0;

        for (int var3 = 1; var3 < 255; ++var3) {
            int var4 = (256 - var3) * this.field1234[var3] / 256;
            int var5 = var4 + xPosition;
            int width = 0;
            int var7 = 128;
            if (var5 < 0) {
                width = -var5;
                var5 = 0;
            }

            if (var5 + 128 >= Client.rasterProvider.width) {
                var7 = Client.rasterProvider.width - var5;
            }

            int pos = var5 + (var3 + 8) * Client.rasterProvider.width;
            pixels += width;

            for (int xPixel = width; xPixel < var7; ++xPixel) {
                int strength = this.flameStrengths[pixels++];
                int currentWidth = pos % Rasterizer2D.width;
                if (strength != 0 && currentWidth >= Rasterizer2D.leftX && currentWidth < Rasterizer2D.bottomX) {
                    int off = 256 - strength;
                    int colour = flameColours.getCurrentColour(strength);
                    int bg = Client.rasterProvider.pixels[pos];
                    drawAlpha(Client.rasterProvider.pixels, pos++, -16777216 | (strength * (colour & 65280) + off * (bg & 65280) & 16711680) + ((colour & 16711935) * strength + (bg & 16711935) * off & -16711936) >> 8, alpha);
                } else {
                    ++pos;
                }
            }

            pixels += 128 - var7;
        }

    }


    final void updateFlameShape(Sprite runeImage) {
        final int alpha = 256;

        Arrays.fill(titleFlames, 0);

        for (int i = 0; i < 5000; i++) {
            final int pos = (int) (Math.random() * 128D * alpha);
            titleFlames[pos] = (int) (Math.random() * 256D);
        }

        for (int i = 0; i < 20; i++) {
            for (int x = 1; x < alpha - 1; x++) {
                for (int y = 1; y < 127; y++) {
                    final int pos = y + (x << 7);
                    titleFlamesTemp[pos] = (
                            titleFlames[pos - 1]
                                    + titleFlames[pos + 1]
                                    + titleFlames[pos - 128]
                                    + titleFlames[pos + 128]
                    ) / 4;
                }
            }

            final int[] temp = titleFlames;
            titleFlames = titleFlamesTemp;
            titleFlamesTemp = temp;
        }

        if (runeImage != null) { // L: 235
            int imagePos = 0; // L: 236
            for (int y = 0; y < runeImage.getHeight(); ++y) {
                for (int x = 0; x < runeImage.getWidth(); ++x) {
                    if (runeImage.getPixels()[imagePos++] != 0) {
                        int var5 = x + runeImage.getOffsetX() + 16;
                        int var6 = y + runeImage.getOffsetY() + 16;
                        int var7 = var5 + (var6 << 7);
                        this.titleFlames[var7] = 0;
                    }
                }
            }
        }

    }

}
