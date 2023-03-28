package com.runescape.scene;

import com.runescape.Client;
import com.runescape.cache.anim.SequenceDefinition;
import com.runescape.cache.config.VariableBits;
import com.runescape.cache.def.ObjectDefinition;
import com.runescape.entity.Renderable;
import com.runescape.entity.model.Model;

public final class SceneObject extends Renderable {

    public static Client clientInstance;
    private final int[] anIntArray1600;
    private final int anInt1601;
    private final int anInt1602;
    private final int anInt1603;
    private final int anInt1604;
    private final int anInt1605;
    private final int anInt1606;
    private final int anInt1610;
    private final int anInt1611;
    private final int anInt1612;
    private int currentFrameindex;
    private SequenceDefinition seqtype;
    private int currentTick;

    public SceneObject(int i, int j, int k, int l, int i1, int j1, int k1, int l1, boolean flag) {
        anInt1610 = i;
        anInt1611 = k;
        anInt1612 = j;
        anInt1603 = j1;
        anInt1604 = l;
        anInt1605 = i1;
        anInt1606 = k1;

        if (l1 != -1) {
            seqtype = SequenceDefinition.sequenceDefinitions[l1];
            currentFrameindex = 0;
            currentTick = Client.tick;
            if (flag && seqtype.loopFrameCount != -1) {
                if (seqtype.usingKeyframes()) {
                    currentFrameindex = (int) (Math.random() * (double) seqtype.getKeyframeDuration());
                } else {
                    currentFrameindex = (int) (Math.random() * seqtype.frameCount);
                    currentTick -= (int) (Math.random() * seqtype.durations[currentFrameindex]);
                }
            }
        }

        ObjectDefinition objectDef = ObjectDefinition.lookup(anInt1610);
        anInt1601 = objectDef.varbitID;
        anInt1602 = objectDef.varpID;
        anIntArray1600 = objectDef.configs;
    }

    private ObjectDefinition method457() {
        int i = -1;
        if (anInt1601 != -1) {
            try {
                VariableBits varBit = VariableBits.varbits[anInt1601];
                int k = varBit.getSetting();
                int l = varBit.getLow();
                int i1 = varBit.getHigh();
                int j1 = Client.BIT_MASKS[i1 - l];
                i = clientInstance.settings[k] >> l & j1;
            } catch (Exception ex) {
            }
        } else if (anInt1602 != -1 && anInt1602 < clientInstance.settings.length) {
            i = clientInstance.settings[anInt1602];
        }
        if (i < 0 || i >= anIntArray1600.length || anIntArray1600[i] == -1) {
            return null;
        } else {
            return ObjectDefinition.lookup(anIntArray1600[i]);
        }
    }

    public Model getRotatedModel() {
        int frame = -1;
        if (seqtype != null) {
            int processTime = Client.tick - currentTick;
            if (processTime > 100 && seqtype.loopFrameCount > 0)
                processTime = 100;
            if (seqtype.usingKeyframes()) {
                int frameDuration = this.seqtype.getKeyframeDuration();
                this.currentFrameindex += processTime;
                processTime = 0;
                if (this.currentFrameindex >= frameDuration) {
                    this.currentFrameindex = frameDuration - this.seqtype.loopFrameCount;
                    if (this.currentFrameindex < 0 || this.currentFrameindex > frameDuration) {
                        this.seqtype = null;
                    }
                }
            } else {
                while (processTime > seqtype.durations[currentFrameindex]) {
                    processTime -= seqtype.durations[currentFrameindex];
                    currentFrameindex++;
                    if (currentFrameindex < seqtype.frameCount)
                        continue;
                    currentFrameindex -= seqtype.loopFrameCount;
                    if (currentFrameindex >= 0 && currentFrameindex < seqtype.frameCount)
                        continue;
                    seqtype = null;
                    break;
                }
            }
            currentTick = Client.tick - processTime;
            if (seqtype != null) {
                frame = currentFrameindex;
            }
        }
        ObjectDefinition definition;
        if (anIntArray1600 != null)
            definition = method457();
        else
            definition = ObjectDefinition.lookup(anInt1610);
        if (definition == null) {
            return null;
        } else {
            return definition.modelAt(anInt1611, anInt1612, anInt1603, anInt1604, anInt1605, anInt1606, frame, seqtype);
        }
    }
}