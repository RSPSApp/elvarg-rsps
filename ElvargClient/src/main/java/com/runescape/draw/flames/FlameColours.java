package com.runescape.draw.flames;

class FlameColours {
    public int[] currentFlameColours;
    public int[] redFlameColours;
    public int[] greenFlameColours;
    public int[] blueFlameColours;

    private int random1;
    private int random2;

    public FlameColours() {
        this.redFlameColours = new int[256];
        for (int c = 0; c < 64; c++) {
            this.redFlameColours[c] = c * 0x040000;
        }

        for (int c = 0; c < 64; c++) {
            this.redFlameColours[c + 64] = 0xFF0000 + 0x000400 * c;
        }

        for (int c = 0; c < 64; c++) {
            this.redFlameColours[c + 128] = 0xFFFF00 + 0x000004 * c;
        }

        for (int c = 0; c < 64; c++) {
            this.redFlameColours[c + 192] = 0xFFFFFF;
        }

        this.greenFlameColours = new int[256];
        for (int c = 0; c < 64; c++) {
            this.greenFlameColours[c] = c * 0x000400;
        }

        for (int c = 0; c < 64; c++) {
            this.greenFlameColours[c + 64] = 0x00FF00 + 0x000004 * c;
        }

        for (int c = 0; c < 64; c++) {
            this.greenFlameColours[c + 128] = 0x00FFFF + 0x040000 * c;
        }

        for (int c = 0; c < 64; c++) {
            this.greenFlameColours[c + 192] = 0xFFFFFF;
        }

        this.blueFlameColours = new int[256];
        for (int c = 0; c < 64; c++) {
            this.blueFlameColours[c] = c * 0x000004;
        }

        for (int c = 0; c < 64; c++) {
            this.blueFlameColours[c + 64] = 0x0000FF + 0x040000 * c;
        }

        for (int c = 0; c < 64; c++) {
            this.blueFlameColours[c + 128] = 0xFF00FF + 0x000400 * c;
        }

        for (int c = 0; c < 64; c++) {
            this.blueFlameColours[c + 192] = 0xFFFFFF;
        }

        this.currentFlameColours = new int[256];
    }

    public int getCurrentColour(final int strength) {
        return this.currentFlameColours[strength];
    }

}