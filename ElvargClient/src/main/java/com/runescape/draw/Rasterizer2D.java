package com.runescape.draw;

import com.runescape.Client;
import com.runescape.collection.Cacheable;
import net.runelite.api.BufferProvider;
import net.runelite.rs.api.RSRasterizer2D;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.*;
import java.util.Hashtable;

public class Rasterizer2D extends Cacheable implements RSRasterizer2D {


    public static void drawAlpha(int[] pixels, int index, int value, int alpha) {
        if (! Client.instance.isGpu() || pixels != Client.instance.getBufferProvider().getPixels())
        {
            pixels[index] = value;
            return;
        }

        // (int) x * 0x8081 >>> 23 is equivalent to (short) x / 255
        int outAlpha = alpha + ((pixels[index] >>> 24) * (255 - alpha) * 0x8081 >>> 23);
        pixels[index] = value & 0x00FFFFFF | outAlpha << 24;
    }

    /**
     * Sets the Rasterizer2D in the upper left corner with height, width and pixels set.
     * @param height The height of the drawingArea.
     * @param width The width of the drawingArea.
     * @param pixels The array of pixels (RGBColours) in the drawingArea.
     */
    public static void initDrawingArea(int height, int width, int pixels[]) {
        Rasterizer2D.pixels = pixels;
        Rasterizer2D.width = width;
        Rasterizer2D.height = height;
        setDrawingArea(height, 0, width, 0);
    }

    /**
     * Draws a transparent box with a gradient that changes from top to bottom.
     * @param leftX The left edge X-Coordinate of the box.
     * @param topY The top edge Y-Coordinate of the box.
     * @param width The width of the box.
     * @param height The height of the box.
     * @param topColour The top rgbColour of the gradient.
     * @param bottomColour The bottom rgbColour of the gradient.
     * @param opacity The opacity value ranging from 0 to 256.
     */
    public static void drawTransparentGradientBox(int leftX, int topY, int width, int height, int topColour, int bottomColour, int opacity) {
        int gradientProgress = 0;
        int progressPerPixel = 0x10000 / height;
        if(leftX < Rasterizer2D.leftX) {
            width -= Rasterizer2D.leftX - leftX;
            leftX = Rasterizer2D.leftX;
        }
        if(topY < Rasterizer2D.topY) {
            gradientProgress += (Rasterizer2D.topY - topY) * progressPerPixel;
            height -= Rasterizer2D.topY - topY;
            topY = Rasterizer2D.topY;
        }
        if(leftX + width > bottomX)
            width = bottomX - leftX;
        if(topY + height > bottomY)
            height = bottomY - topY;
        int leftOver = Rasterizer2D.width - width;
        int transparency = 256 - opacity;
        int pixelIndex = leftX + topY * Rasterizer2D.width;
        for(int rowIndex = 0; rowIndex < height; rowIndex++) {
            int gradient = 0x10000 - gradientProgress >> 8;
            int inverseGradient = gradientProgress >> 8;
            int gradientColour = ((topColour & 0xff00ff) * gradient + (bottomColour & 0xff00ff) * inverseGradient & 0xff00ff00) + ((topColour & 0xff00) * gradient + (bottomColour & 0xff00) * inverseGradient & 0xff0000) >>> 8;
            int transparentPixel = ((gradientColour & 0xff00ff) * opacity >> 8 & 0xff00ff) + ((gradientColour & 0xff00) * opacity >> 8 & 0xff00);
            for(int columnIndex = 0; columnIndex < width; columnIndex++) {
                int backgroundPixel = pixels[pixelIndex];
                backgroundPixel = ((backgroundPixel & 0xff00ff) * transparency >> 8 & 0xff00ff) + ((backgroundPixel & 0xff00) * transparency >> 8 & 0xff00);
                drawAlpha(pixels, pixelIndex++, transparentPixel + backgroundPixel, opacity);
            }
            pixelIndex += leftOver;
            gradientProgress += progressPerPixel;
        }
    }

    /**
     * Sets the drawingArea based on the coordinates of the edges.
     * @param bottomY The bottom edge Y-Coordinate.
     * @param leftX The left edge X-Coordinate.
     * @param rightX The right edge X-Coordinate.
     * @param topY The top edge Y-Coordinate.
     */
    public static void setDrawingArea(int bottomY, int leftX, int rightX, int topY) {
        if(leftX < 0) {
            leftX = 0;
        }
        if(topY < 0) {
            topY = 0;
        }
        if(rightX > width) {
            rightX = width;
        }
        if(bottomY > height) {
            bottomY = height;
        }
        Rasterizer2D.leftX = leftX;
        Rasterizer2D.topY = topY;
        bottomX = rightX;
        Rasterizer2D.bottomY = bottomY;
        lastX = bottomX;
        viewportCenterX = bottomX / 2;
        viewportCenterY = Rasterizer2D.bottomY / 2;
    }

    /**
     * Clears the drawingArea by setting every pixel to 0 (black).
     */
    public static void clear()	{
        int i = width * height;
        for(int j = 0; j < i; j++) {
            pixels[j] = 0;
        }
    }

    /**
     * Draws a box filled with a certain colour.
     * @param leftX The left edge X-Coordinate of the box.
     * @param topY The top edge Y-Coordinate of the box.
     * @param width The width of the box.
     * @param height The height of the box.
     * @param rgbColour The RGBColour of the box.
     */
    public static void drawBox(int leftX, int topY, int width, int height, int rgbColour) {
        if (leftX < Rasterizer2D.leftX) {
            width -= Rasterizer2D.leftX - leftX;
            leftX = Rasterizer2D.leftX;
        }
        if (topY < Rasterizer2D.topY) {
            height -= Rasterizer2D.topY - topY;
            topY = Rasterizer2D.topY;
        }
        if (leftX + width > bottomX)
            width = bottomX - leftX;
        if (topY + height > bottomY)
            height = bottomY - topY;
        int leftOver = Rasterizer2D.width - width;
        int pixelIndex = leftX + topY * Rasterizer2D.width;
        for (int rowIndex = 0; rowIndex < height; rowIndex++) {
            for (int columnIndex = 0; columnIndex < width; columnIndex++)
                drawAlpha(pixels, pixelIndex++, rgbColour, 255);
            pixelIndex += leftOver;
        }
    }

    public static void fillPixelsReverseOrder(int height, int topY, int topX, int pixel, int width) {
        if (topX < Rasterizer2D.leftX) {
            width -= Rasterizer2D.leftX - topX;
            topX = Rasterizer2D.leftX;
        }
        if (topY < Rasterizer2D.topY) {
            height -= Rasterizer2D.topY - topY;
            topY = Rasterizer2D.topY;
        }
        if (topX + width > bottomX) {
            width = bottomX - topX;
        }
        if (topY + height > bottomY) {
            height = bottomY - topY;
        }
        int increment = Rasterizer2D.width - width;
        int i = topX + topY * Rasterizer2D.width;

        for (int y = -height; y < 0; y++) {
            for (int x = -width; x < 0; x++) {
                drawAlpha(pixels, i++, pixel, 255);
            }
            i += increment;
        }
    }

    public static void method341(int i, int j, int k, int l) {
        if (l < leftX || l >= bottomX)
            return;
        if (i < topY) {
            k -= topY - i;
            i = topY;
        }
        if (i + k > bottomY)
            k = bottomY - i;
        int j1 = l + i * width;
        for (int k1 = 0; k1 < k; k1++)
            drawAlpha(pixels, j1 + k1 * width, j, 255);

    }

    public static void method339(int i, int j, int k, int l) {
        if (i < topY || i >= bottomY)
            return;
        if (l < leftX) {
            k -= leftX - l;
            l = leftX;
        }
        if (l + k > bottomX)
            k = bottomX - l;
        int i1 = l + i * width;
        for (int j1 = 0; j1 < k; j1++)
            drawAlpha(pixels, i1 + j1, j, 255);

    }

    /**
     * Draws an item box filled with a certain colour.
     *
     * @param leftX     The left edge X-Coordinate of the box.
     * @param topY      The top edge Y-Coordinate of the box.
     * @param width     The width of the box.
     * @param height    The height of the box.
     * @param rgbColour The RGBColour of the box.
     */
    public static void drawItemBox(int leftX, int topY, int width, int height, int rgbColour) {
        if (leftX < Rasterizer2D.leftX) {
            width -= Rasterizer2D.leftX - leftX;
            leftX = Rasterizer2D.leftX;
        }
        if (topY < Rasterizer2D.topY) {
            height -= Rasterizer2D.topY - topY;
            topY = Rasterizer2D.topY;
        }
        if (leftX + width > bottomX)
            width = bottomX - leftX;
        if (topY + height > bottomY)
            height = bottomY - topY;
        int leftOver = Rasterizer2D.width - width;
        int pixelIndex = leftX + topY * Rasterizer2D.width;
        for (int rowIndex = 0; rowIndex < height; rowIndex++) {
            for (int columnIndex = 0; columnIndex < width; columnIndex++)
                //pixels[pixelIndex++] = rgbColour;
                drawAlpha(pixels, pixelIndex++, rgbColour, 0);
            pixelIndex += leftOver;
        }
    }

    /**
     * Draws a transparent box.
     * @param leftX The left edge X-Coordinate of the box.
     * @param topY The top edge Y-Coordinate of the box.
     * @param width The box width.
     * @param height The box height.
     * @param rgbColour The box colour.
     * @param opacity The opacity value ranging from 0 to 256.
     */
    public static void drawTransparentBox(int leftX, int topY, int width, int height, int rgbColour, int opacity){
        if(leftX < Rasterizer2D.leftX){
            width -= Rasterizer2D.leftX - leftX;
            leftX = Rasterizer2D.leftX;
        }
        if(topY < Rasterizer2D.topY){
            height -= Rasterizer2D.topY - topY;
            topY = Rasterizer2D.topY;
        }
        if(leftX + width > bottomX)
            width = bottomX - leftX;
        if(topY + height > bottomY)
            height = bottomY - topY;
        int transparency = 256 - opacity;
        int red = (rgbColour >> 16 & 0xff) * opacity;
        int green = (rgbColour >> 8 & 0xff) * opacity;
        int blue = (rgbColour & 0xff) * opacity;
        int leftOver = Rasterizer2D.width - width;
        int pixelIndex = leftX + topY * Rasterizer2D.width;
        for(int rowIndex = 0; rowIndex < height; rowIndex++){
            for(int columnIndex = 0; columnIndex < width; columnIndex++){
                int otherRed = (pixels[pixelIndex] >> 16 & 0xff) * transparency;
                int otherGreen = (pixels[pixelIndex] >> 8 & 0xff) * transparency;
                int otherBlue = (pixels[pixelIndex] & 0xff) * transparency;
                int transparentColour = ((red + otherRed >> 8) << 16) + ((green + otherGreen >> 8) << 8) + (blue + otherBlue >> 8);
                drawAlpha(pixels, pixelIndex++, transparentColour, opacity);
            }
            pixelIndex += leftOver;
        }
    }

    public static void drawPixels(int height, int posY, int posX, int color, int width) {
        if (posX < leftX) {
            width -= leftX - posX;
            posX = leftX;
        }
        if (posY < topY) {
            height -= topY - posY;
            posY = topY;
        }
        if (posX + width > bottomX) {
            width = bottomX - posX;
        }
        if (posY + height > bottomY) {
            height = bottomY - posY;
        }
        int k1 = Rasterizer2D.width - width;
        int l1 = posX + posY * Rasterizer2D.width;
        for (int i2 = -height; i2 < 0; i2++) {
            for (int j2 = -width; j2 < 0; j2++) {
                pixels[l1++] = color;
            }
            l1 += k1;
        }
    }

    /**
     * Draws a 1 pixel thick box outline in a certain colour.
     * @param leftX The left edge X-Coordinate.
     * @param topY The top edge Y-Coordinate.
     * @param width The width.
     * @param height The height.
     * @param rgbColour The RGB-Colour.
     */
    public static void drawBoxOutline(int leftX, int topY, int width, int height, int rgbColour){
        drawHorizontalLine(leftX, topY, width, rgbColour);
        drawHorizontalLine(leftX, (topY + height) - 1, width, rgbColour);
        drawVerticalLine(leftX, topY, height, rgbColour);
        drawVerticalLine((leftX + width) - 1, topY, height, rgbColour);
    }

    /**
     * Draws a coloured horizontal line in the drawingArea.
     * @param xPosition The start X-Position of the line.
     * @param yPosition The Y-Position of the line.
     * @param width The width of the line.
     * @param rgbColour The colour of the line.
     */
    public static void drawHorizontalLine(int xPosition, int yPosition, int width, int rgbColour){
        if(yPosition < topY || yPosition >= bottomY)
            return;
        if(xPosition < leftX){
            width -= leftX - xPosition;
            xPosition = leftX;
        }
        if(xPosition + width > bottomX)
            width = bottomX - xPosition;
        int pixelIndex = xPosition + yPosition * Rasterizer2D.width;
        for(int i = 0; i < width; i++)
            drawAlpha(pixels, pixelIndex + i, rgbColour, 255);
    }

    /**
     * Draws a coloured vertical line in the drawingArea.
     * @param xPosition The X-Position of the line.
     * @param yPosition The start Y-Position of the line.
     * @param height The height of the line.
     * @param rgbColour The colour of the line.
     */
    public static void drawVerticalLine(int xPosition, int yPosition, int height, int rgbColour){
        if(xPosition < leftX || xPosition >= bottomX)
            return;
        if(yPosition < topY){
            height -= topY - yPosition;
            yPosition = topY;
        }
        if(yPosition + height > bottomY)
            height = bottomY - yPosition;
        int pixelIndex = xPosition + yPosition * width;
        for(int rowIndex = 0; rowIndex < height; rowIndex++)
            drawAlpha(pixels, pixelIndex + rowIndex * width, rgbColour, 255);
    }

    /**
     * Draws a 1 pixel thick transparent box outline in a certain colour.
     * @param leftX The left edge X-Coordinate
     * @param topY The top edge Y-Coordinate.
     * @param width The width.
     * @param height The height.
     * @param rgbColour The RGB-Colour.
     * @param opacity The opacity value ranging from 0 to 256.
     */
    public static void drawTransparentBoxOutline(int leftX, int topY, int width, int height, int rgbColour, int opacity) {
        drawTransparentHorizontalLine(leftX, topY, width, rgbColour, opacity);
        drawTransparentHorizontalLine(leftX, topY + height - 1, width, rgbColour, opacity);
        if(height >= 3) {
            drawTransparentVerticalLine(leftX, topY + 1, height - 2, rgbColour, opacity);
            drawTransparentVerticalLine(leftX + width - 1, topY + 1, height - 2, rgbColour, opacity);
        }
    }

    /**
     * Draws a transparent coloured horizontal line in the drawingArea.
     * @param xPosition The start X-Position of the line.
     * @param yPosition The Y-Position of the line.
     * @param width The width of the line.
     * @param rgbColour The colour of the line.
     * @param opacity The opacity value ranging from 0 to 256.
     */
    public static void drawTransparentHorizontalLine(int xPosition, int yPosition, int width, int rgbColour, int opacity) {
        if(yPosition < topY || yPosition >= bottomY) {
            return;
        }
        if(xPosition < leftX) {
            width -= leftX - xPosition;
            xPosition = leftX;
        }
        if(xPosition + width > bottomX) {
            width = bottomX - xPosition;
        }
        final int transparency = 256 - opacity;
        final int red = (rgbColour >> 16 & 0xff) * opacity;
        final int green = (rgbColour >> 8 & 0xff) * opacity;
        final int blue = (rgbColour & 0xff) * opacity;
        int pixelIndex = xPosition + yPosition * Rasterizer2D.width;
        for(int i = 0; i < width; i++) {
            final int otherRed = (pixels[pixelIndex] >> 16 & 0xff) * transparency;
            final int otherGreen = (pixels[pixelIndex] >> 8 & 0xff) * transparency;
            final int otherBlue = (pixels[pixelIndex] & 0xff) * transparency;
            final int transparentColour = (red + otherRed >> 8 << 16) + (green + otherGreen >> 8 << 8) + (blue + otherBlue >> 8);
            drawAlpha(pixels, pixelIndex, transparentColour, opacity);
        }
    }

    /**
     * Draws a transparent coloured vertical line in the drawingArea.
     * @param xPosition The X-Position of the line.
     * @param yPosition The start Y-Position of the line.
     * @param height The height of the line.
     * @param rgbColour The colour of the line.
     * @param opacity The opacity value ranging from 0 to 256.
     */
    public static void drawTransparentVerticalLine(int xPosition, int yPosition, int height, int rgbColour, int opacity) {
        if(xPosition < leftX || xPosition >= bottomX) {
            return;
        }
        if(yPosition < topY) {
            height -= topY - yPosition;
            yPosition = topY;
        }
        if(yPosition + height > bottomY) {
            height = bottomY - yPosition;
        }
        final int transparency = 256 - opacity;
        final int red = (rgbColour >> 16 & 0xff) * opacity;
        final int green = (rgbColour >> 8 & 0xff) * opacity;
        final int blue = (rgbColour & 0xff) * opacity;
        int pixelIndex = xPosition + yPosition * width;
        for(int i = 0; i < height; i++) {
            final int otherRed = (pixels[pixelIndex] >> 16 & 0xff) * transparency;
            final int otherGreen = (pixels[pixelIndex] >> 8 & 0xff) * transparency;
            final int otherBlue = (pixels[pixelIndex] & 0xff) * transparency;
            final int transparentColour = (red + otherRed >> 8 << 16) + (green + otherGreen >> 8 << 8) + (blue + otherBlue >> 8);
            drawAlpha(pixels, pixelIndex, transparentColour, opacity);
            pixelIndex += width;
        }
    }
    public static int pixels[];
    public static int width;
    public static int height;
    public static int topY;
    public static int bottomY;
    public static int leftX;
    public static int bottomX;
    public static int lastX;
    public static int viewportCenterX;
    public static int viewportCenterY;

    public static Graphics2D createGraphics(boolean renderingHints) {
        Graphics2D g2d = createGraphics(pixels, width, height);
        if (renderingHints) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        return g2d;
    }

    private static final ColorModel COLOR_MODEL = new DirectColorModel(32, 0xff0000, 0xff00, 0xff);

    public static Graphics2D createGraphics(int[] pixels, int width, int height) {
        return new BufferedImage(COLOR_MODEL, Raster.createWritableRaster(COLOR_MODEL.createCompatibleSampleModel(width, height), new DataBufferInt(pixels, width * height), null), false, new Hashtable<Object, Object>()).createGraphics();
    }

    public static Shape createSector(int x, int y, int r, int angle) {
        return new Arc2D.Double(x, y, r, r, 90, -angle, Arc2D.PIE);
    }

    public static Shape createCircle(int x, int y, int r) {
        return new Ellipse2D.Double(x, y, r, r);
    }

    public static void drawFilledCircle(int x, int y, int radius, int color, int alpha) {
        int y1 = y - radius;
        if (y1 < 0) {
            y1 = 0;
        }
        int y2 = y + radius;
        if (y2 >= height) {
            y2 = height - 1;
        }
        int a2 = 256 - alpha;
        int r1 = (color >> 16 & 0xff) * alpha;
        int g1 = (color >> 8 & 0xff) * alpha;
        int b1 = (color & 0xff) * alpha;
        for (int iy = y1; iy <= y2; iy++) {
            int dy = iy - y;
            int dist = (int) Math.sqrt(radius * radius - dy * dy);
            int x1 = x - dist;
            if (x1 < 0) {
                x1 = 0;
            }
            int x2 = x + dist;
            if (x2 >= width) {
                x2 = width - 1;
            }
            int pos = x1 + iy * width;
            for (int ix = x1; ix <= x2; ix++) {
                int r2 = (pixels[pos] >> 16 & 0xff) * a2;
                int g2 = (pixels[pos] >> 8 & 0xff) * a2;
                int b2 = (pixels[pos] & 0xff) * a2;
                drawAlpha(pixels, pos++, ((r1 + r2 >> 8) << 16) + ((g1 + g2 >> 8) << 8) + (b1 + b2 >> 8), alpha);

            }
        }
    }

    public static Shape createRing(Shape sector, Shape innerCircle) {
        Area ring = new Area(sector);
        ring.subtract(new Area(innerCircle));
        return ring;
    }

    private static void drawGradientAlpha(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7)
    {
        final int width = Client.instance.getGraphicsPixelsWidth();
        final int startX = Client.instance.getStartX();
        final int startY = Client.instance.getStartY();
        final int endX = Client.instance.getEndX();
        final int endY = Client.instance.getEndY();
        final int[] pixels = Client.instance.getGraphicsPixels();

        if (!Client.instance.isGpu())
        {
            drawGradientAlpha(var0, var1, var2, var3, var4, var5, var6, var7);
            return;
        }

        if (var2 > 0 && var3 > 0)
        {
            int var8 = 0;
            int var9 = 65536 / var3;
            if (var0 < startX)
            {
                var2 -= startX - var0;
                var0 = startX;
            }

            if (var1 < startY)
            {
                var8 += (startY - var1) * var9;
                var3 -= startY - var1;
                var1 = startY;
            }

            if (var0 + var2 > endX)
            {
                var2 = endX - var0;
            }

            if (var3 + var1 > endY)
            {
                var3 = endY - var1;
            }

            int var10 = width - var2;
            int var11 = var0 + width * var1;

            for (int var12 = -var3; var12 < 0; ++var12)
            {
                int var13 = 65536 - var8 >> 8;
                int var14 = var8 >> 8;
                int var15 = (var13 * var6 + var14 * var7 & 65280) >>> 8;
                if (var15 == 0)
                {
                    var11 += width;
                    var8 += var9;
                }
                else
                {
                    int var16 = (var14 * (var5 & 16711935) + var13 * (var4 & 16711935) & -16711936) + (var14 * (var5 & 65280) + var13 * (var4 & 65280) & 16711680) >>> 8;
                    int var17 = 255 - var15;
                    int var18 = ((var16 & 16711935) * var15 >> 8 & 16711935) + (var15 * (var16 & 65280) >> 8 & 65280);

                    for (int var19 = -var2; var19 < 0; ++var19)
                    {
                        int var20 = pixels[var11];
                        var20 = ((var20 & 16711935) * var17 >> 8 & 16711935) + (var17 * (var20 & 65280) >> 8 & 65280);
                        drawAlpha(pixels, var11++, var18 + var20, var15);
                    }

                    var11 += var10;
                    var8 += var9;
                }
            }
        }
    }


    public static void raster2d7(int var0, int var1, int var2, int var3, int var4, int var5, byte[] var6, int var7)
    {
        final int width = Client.instance.getGraphicsPixelsWidth();
        final int height = Client.instance.getGraphicsPixelsHeight();
        final int[] pixels = Client.instance.getGraphicsPixels();

        if (!Client.instance.isGpu())
        {
            raster2d7(var0, var1, var2, var3, var4, var5, var6, var7);
            return;
        }

        if (var0 + var2 >= 0 && var3 + var1 >= 0)
        {
            if (var0 < width && var1 < height)
            {
                int var8 = 0;
                int var9 = 0;
                if (var0 < 0)
                {
                    var8 -= var0;
                    var2 += var0;
                }

                if (var1 < 0)
                {
                    var9 -= var1;
                    var3 += var1;
                }

                if (var0 + var2 > width)
                {
                    var2 = width - var0;
                }

                if (var3 + var1 > height)
                {
                    var3 = height - var1;
                }

                int var10 = var6.length / var7;
                int var11 = width - var2;
                int var12 = var4 >>> 24;
                int var13 = var5 >>> 24;
                int var14;
                int var15;
                int var16;
                int var17;
                int var18;
                if (var12 == 255 && var13 == 255)
                {
                    var14 = var0 + var8 + (var9 + var1) * width;

                    for (var15 = var9 + var1; var15 < var3 + var9 + var1; ++var15)
                    {
                        for (var16 = var0 + var8; var16 < var0 + var8 + var2; ++var16)
                        {
                            var17 = (var15 - var1) % var10;
                            var18 = (var16 - var0) % var7;
                            if (var6[var18 + var17 * var7] != 0)
                            {
                                pixels[var14++] = var5;
                            }
                            else
                            {
                                pixels[var14++] = var4;
                            }
                        }

                        var14 += var11;
                    }
                }
                else
                {
                    var14 = var0 + var8 + (var9 + var1) * width;

                    for (var15 = var9 + var1; var15 < var3 + var9 + var1; ++var15)
                    {
                        for (var16 = var0 + var8; var16 < var0 + var8 + var2; ++var16)
                        {
                            var17 = (var15 - var1) % var10;
                            var18 = (var16 - var0) % var7;
                            int var19 = var4;
                            if (var6[var18 + var17 * var7] != 0)
                            {
                                var19 = var5;
                            }

                            int var20 = var19 >>> 24;
                            int var21 = 255 - var20;
                            int var22 = pixels[var14];
                            int var23 = ((var19 & 16711935) * var20 + (var22 & 16711935) * var21 & -16711936) + (var20 * (var19 & 65280) + var21 * (var22 & 65280) & 16711680) >> 8;
                            drawAlpha(pixels, var14++, var23, var20);
                        }

                        var14 += var11;
                    }
                }
            }
        }
    }

    public static void clearColorBuffer(int x, int y, int width, int height, int color)
    {
        BufferProvider bp = Client.instance.getBufferProvider();
        int canvasWidth = bp.getWidth();
        int[] pixels = bp.getPixels();

        int pixelPos = y * canvasWidth + x;
        int pixelJump = canvasWidth - width;

        for (int cy = y; cy < y + height; cy++)
        {
            for (int cx = x; cx < x + width; cx++)
            {
                pixels[pixelPos++] = 0;
            }
            pixelPos += pixelJump;
        }
    }

}