package com.runescape.entity;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;

import com.runescape.Client;
import com.runescape.cache.def.ObjectDefinition;

import com.runescape.util.ObjectKeyUtil;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.Angle;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.rs.api.RSGameObject;
import net.runelite.rs.api.RSModel;
import net.runelite.rs.api.RSRenderable;

/**
 * ObjectGenre = 2
 */
public final class GameObject implements RSGameObject {

    public int zLoc;
    public int tileHeight;
    public int xPos;
    public int yPos;
    public Renderable renderable;
    public int turnValue;
    public int xLocLow;
    public int xLocHigh;
    public int yLocHigh;
    public int yLocLow;
    public int anInt527;
    public int anInt528;
    public long uid;
    /**
     * mask = (byte)((objectRotation << 6) + objectType);
     */
    public byte mask;

    public RSModel getModel() {
        RSRenderable renderable = getRenderable();
        if (renderable == null)
        {
            return null;
        }

        if (renderable instanceof RSModel)
        {
            return (RSModel) renderable;
        }
        else
        {
            return renderable.getModel();
        }
    }

    @Override
    public int sizeX() {
        return xLocHigh - xLocLow;
    }

    @Override
    public int sizeY() {
        return yLocHigh - yLocLow;
    }

    @Override
    public Point getSceneMinLocation() {
        return new Point(this.getStartX(), this.getStartY());
    }

    @Override
    public Point getSceneMaxLocation() {
        return new Point(this.getEndX(), this.getEndY());
    }

    @Override
    public Shape getConvexHull() {
        RSModel model = getModel();
        if (model == null)
        {
            return null;
        }
        int tileHeight = Perspective.getTileHeight(Client.instance, new LocalPoint(getX(), getY()), Client.instance.getPlane());
        return model.getConvexHull(getX(), getY(), getModelOrientation(), tileHeight);
    }

    @Override
    public Angle getOrientation() {
        int orientation = this.getModelOrientation();
        int face = this.getFlags() >> 6 & 3;
        return new Angle(orientation + face * 512);
    }

    @Override
    public int getId() {
        return ObjectKeyUtil.getObjectId(uid);
    }

    @Override
    public Point getCanvasLocation() {
        return Perspective.localToCanvas(Client.instance, getLocalLocation(), getPlane(), 0);
    }

    @Override
    public Point getCanvasLocation(int zOffset) {
        return Perspective.localToCanvas(Client.instance, this.getLocalLocation(), this.getPlane(), zOffset);
    }

    @Override
    public Polygon getCanvasTilePoly() {
        return Perspective.getCanvasTilePoly(Client.instance, this.getLocalLocation());
    }

    @Override
    public Point getCanvasTextLocation(Graphics2D graphics, String text, int zOffset) {
        return Perspective.getCanvasTextLocation(Client.instance, graphics, this.getLocalLocation(), text, zOffset);
    }

    @Override
    public Point getMinimapLocation() {
        return Perspective.localToMinimap(Client.instance, getLocalLocation());
    }

    @Override
    public Shape getClickbox() {
        return Perspective.getClickbox(Client.instance, this.getModel(), this.getModelOrientation(), getLocalLocation());
    }

    @Override
    public String getName() { return ObjectDefinition.lookup(getId()).name; }

    @Override
    public String[] getActions() {
        return null;
    }

    @Override
    public WorldPoint getWorldLocation() {
        return WorldPoint.fromLocal(Client.instance, this.getX(), this.getY(), this.getPlane());
    }

    @Override
    public LocalPoint getLocalLocation() {
        return new LocalPoint(this.getX(), this.getY());
    }

    @Override
    public RSRenderable getRenderable() {
        return renderable;
    }

    @Override
    public int getStartX() {
        return xLocLow;
    }

    @Override
    public int getStartY() {
        return yLocLow;
    }

    @Override
    public int getEndX() {
        return xLocHigh;
    }

    @Override
    public int getEndY() {
        return yLocHigh;
    }

    @Override
    public int getX() {
        return xPos;
    }

    @Override
    public int getY() {
        return yPos;
    }

    @Override
    public int getHeight() {
        return tileHeight;
    }

    @Override
    public int getModelOrientation() {
        return turnValue;
    }

    @Override
    public long getHash() {
        return uid;
    }

    @Override
    public int getFlags() {
        return mask;
    }

    @Override
    public int getPlane() {
        return zLoc;
    }

    @Override
    public void setPlane(int plane) {
        zLoc = plane;
    }
}
