package com.runescape.entity.model;

import net.runelite.rs.api.RSVertexNormal;

public final class VertexNormal implements RSVertexNormal {

    public int x;
    public int y;
    public int z;
    public int magnitude;

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getZ() {
        return z;
    }
}
