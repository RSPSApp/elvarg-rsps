package com.runescape.cache.def;

import java.nio.ByteBuffer;

import com.runescape.cache.FileArchive;

public class FloorDefinition {

    public static FloorDefinition[] overlays;
    public static FloorDefinition[] underlays;

    public int rgb = 0;

    public int texture = -1;

    public boolean hideUnderlay = true;

    public int secondaryRgb = -1;

    public int hue;

    public int saturation;

    public int lightness;

    public int secondaryHue;

    public int secondarySaturation;

    public int secondaryLightness;

    public static void init(FileArchive archive) {
        ByteBuffer buffer = ByteBuffer.wrap(archive.readFile("flo.dat"));
        int underlayAmount = buffer.getShort();
        underlays = new FloorDefinition[underlayAmount];

        for (int i = 0; i < underlayAmount; i++) {
            if (underlays[i] == null) {
                underlays[i] = new FloorDefinition();
            }

            underlays[i].readValuesUnderlay(buffer);
            underlays[i].generateHsl(true);
        }

        int overlayAmount = buffer.getShort();
        overlays = new FloorDefinition[overlayAmount];

        for (int i = 0; i < overlayAmount; i++) {
            if (overlays[i] == null) {
                overlays[i] = new FloorDefinition();
            }

            overlays[i].readValuesOverlay(buffer);
            overlays[i].postDecode();
        }
        System.out.println("Floors read -> (" + underlayAmount + " underlays) | (" + overlayAmount + " overlays)");
    }


    private FloorDefinition() {
        texture = -1;
        hideUnderlay = true;
    }


    public int luminance;
    public int anotherHue;
    public int anotherSaturation;
    public int anotherLuminance;
    public int blendHue;
    public int blendHueMultiplier;
    public int hsl16;

    private void generateHsl(boolean isUnderlay) {
        if (secondaryRgb != -1) {
            rgbToHsl(secondaryRgb);
            anotherHue = hue;
            anotherSaturation = saturation;
            anotherLuminance = luminance;
        }
        int color = rgb;
        rgbToHsl(color);
    }


    private void readValuesUnderlay(ByteBuffer buffer) {
        for (;;) {
            int opcode = buffer.get();
            if (opcode == 0) {
                break;
            } else if (opcode == 1) {
                rgb = ((buffer.get() & 0xff) << 16) + ((buffer.get() & 0xff) << 8) + (buffer.get() & 0xff);
            } else {
                System.out.println("Error unrecognised underlay code: " + opcode);
            }
        }
    }

    private void readValuesOverlay(ByteBuffer buffer) {
        for (;;) {
            int opcode = buffer.get();
            if (opcode == 0) {
                break;
            } else if (opcode == 1) {
                rgb = ((buffer.get() & 0xff) << 16) + ((buffer.get() & 0xff) << 8) + (buffer.get() & 0xff);
            } else if (opcode == 2) {
                texture = buffer.get() & 0xff;
            } else if (opcode == 5) {
                hideUnderlay = false;
            } else if (opcode == 7) {
                secondaryRgb = ((buffer.get() & 0xff) << 16) + ((buffer.get() & 0xff) << 8) + (buffer.get() & 0xff);
            } else {
                System.out.println("Error unrecognised overlay code: " + opcode);
            }
        }
    }

    public void postDecode() {
        if (this.secondaryRgb != -1) {
            this.setHsl(this.secondaryRgb);
            this.secondaryHue = this.hue;
            this.secondarySaturation = this.saturation;
            this.secondaryLightness = this.lightness;
        }
        this.setHsl(this.rgb);
    }

    void setHsl(int var1) {
        double var2 = ((double) (var1 >> 16 & 255)) / 256.0;
        double var4 = ((double) (var1 >> 8 & 255)) / 256.0;
        double var6 = ((double) (var1 & 255)) / 256.0;
        double var8 = var2;
        if (var4 < var2) {
            var8 = var4;
        }
        if (var6 < var8) {
            var8 = var6;
        }
        double var10 = var2;
        if (var4 > var2) {
            var10 = var4;
        }
        if (var6 > var10) {
            var10 = var6;
        }
        double var12 = 0.0;
        double var14 = 0.0;
        double var16 = (var10 + var8) / 2.0;
        if (var10 != var8) {
            if (var16 < 0.5) {
                var14 = (var10 - var8) / (var8 + var10);
            }
            if (var16 >= 0.5) {
                var14 = (var10 - var8) / (2.0 - var10 - var8);
            }
            if (var10 == var2) {
                var12 = (var4 - var6) / (var10 - var8);
            } else if (var4 == var10) {
                var12 = 2.0 + (var6 - var2) / (var10 - var8);
            } else if (var6 == var10) {
                var12 = 4.0 + (var2 - var4) / (var10 - var8);
            }
        }
        var12 /= 6.0;
        this.hue = ((int) (256.0 * var12));
        this.saturation = ((int) (256.0 * var14));
        this.lightness = ((int) (256.0 * var16));
        if (this.saturation < 0) {
            this.saturation = 0;
        } else if (this.saturation > 255) {
            this.saturation = 255;
        }
        if (this.lightness < 0) {
            this.lightness = 0;
        } else if (this.lightness > 255) {
            this.lightness = 255;
        }
    }


    private void rgbToHsl(int rgb) {
        double r = (rgb >> 16 & 0xff) / 256.0;
        double g = (rgb >> 8 & 0xff) / 256.0;
        double b = (rgb & 0xff) / 256.0;
        double min = r;
        if (g < min) {
            min = g;
        }
        if (b < min) {
            min = b;
        }
        double max = r;
        if (g > max) {
            max = g;
        }
        if (b > max) {
            max = b;
        }
        double h = 0.0;
        double s = 0.0;
        double l = (min + max) / 2.0;
        if (min != max) {
            if (l < 0.5) {
                s = (max - min) / (max + min);
            }
            if (l >= 0.5) {
                s = (max - min) / (2.0 - max - min);
            }
            if (r == max) {
                h = (g - b) / (max - min);
            } else if (g == max) {
                h = 2.0 + (b - r) / (max - min);
            } else if (b == max) {
                h = 4.0 + (r - g) / (max - min);
            }
        }
        h /= 6.0;
        hue = (int) (h * 256.0);
        saturation = (int) (s * 256.0);
        luminance = (int) (l * 256.0);
        if (saturation < 0) {
            saturation = 0;
        } else if (saturation > 255) {
            saturation = 255;
        }
        if (luminance < 0) {
            luminance = 0;
        } else if (luminance > 255) {
            luminance = 255;
        }
        if (l > 0.5) {
            blendHueMultiplier = (int) ((1.0 - l) * s * 512.0);
        } else {
            blendHueMultiplier = (int) (l * s * 512.0);
        }
        if (blendHueMultiplier < 1) {
            blendHueMultiplier = 1;
        }
        blendHue = (int) (h * blendHueMultiplier);
        hsl16 = hsl24to16(hue, saturation, luminance);
    }

    private final static int hsl24to16(int h, int s, int l) {
        if (l > 179) {
            s /= 2;
        }
        if (l > 192) {
            s /= 2;
        }
        if (l > 217) {
            s /= 2;
        }
        if (l > 243) {
            s /= 2;
        }
        return (h / 4 << 10) + (s / 32 << 7) + l / 2;
    }
}