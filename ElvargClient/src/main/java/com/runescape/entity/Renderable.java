package com.runescape.entity;

import com.runescape.collection.Cacheable;
import com.runescape.entity.model.Model;
import com.runescape.entity.model.VertexNormal;
import net.runelite.rs.api.RSModel;
import net.runelite.rs.api.RSNode;
import net.runelite.rs.api.RSRenderable;

public class Renderable extends Cacheable implements RSRenderable {

    public int modelBaseY;
    public VertexNormal normals[];

    public void renderAtPoint(int i, int j, int k, int l, int i1, int j1, int k1, int l1, long i2) {
        Model model = getRotatedModel();
        if(model != null) {
            modelBaseY = model.modelBaseY;

            model.renderAtPoint(i, j, k, l, i1, j1, k1, l1, i2);
        }
    }

    @Override
    public void draw(int orientation, int pitchSin, int pitchCos, int yawSin, int yawCos, int x, int y, int z, long hash) {
        renderAtPoint(orientation,pitchSin,pitchCos,yawSin,yawCos,x,y,z,hash);
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    public Model getRotatedModel() {
        return null;
    }

    public Renderable() {
        modelBaseY = 1000;
    }

    @Override
    public RSNode getNext() {
        return null;
    }

    @Override
    public long getHash() {
        return 0;
    }

    @Override
    public RSNode getPrevious() {
        return null;
    }

    @Override
    public void onUnlink() {
    }

    @Override
    public int getModelHeight() {
        return modelBaseY;
    }

    @Override
    public void setModelHeight(int modelHeight) {
        modelBaseY = modelHeight;
    }

    @Override
    public RSModel getModel() {
        return getRotatedModel();
    }


}