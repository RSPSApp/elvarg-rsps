package com.runescape.entity;

import com.runescape.Client;
import com.runescape.cache.def.ItemDefinition;
import com.runescape.entity.model.Model;
import net.runelite.api.Tile;
import net.runelite.rs.api.RSTileItem;

public final class Item extends Renderable implements RSTileItem {

    public int ID;
    public int x;
    public int y;
    public int itemCount;

    public final Model getRotatedModel() {
        ItemDefinition itemDef = ItemDefinition.lookup(ID);
        return itemDef.getModel(itemCount);
    }

    @Override
    public int getSpawnTime() {
        return 0;
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public void setId(int id) {
        ID = id;
    }

    @Override
    public int getQuantity() {
        return itemCount;
    }

    @Override
    public void setQuantity(int quantity) {
        itemCount = quantity;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public Tile getTile() {
        if (x == -1 || y == -1) {
            return null;
        }

        Tile[][][] tiles = Client.instance.getScene().getTiles();
        return tiles[Client.instance.getPlane()][x][y];
    }

}
