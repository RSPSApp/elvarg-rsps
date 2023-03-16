package com.runescape.scene;

import com.runescape.cache.anim.Animation;
import com.runescape.cache.anim.SpotAnimationDefinition;
import com.runescape.entity.Renderable;
import com.runescape.entity.model.Model;

public final class AnimableObject extends Renderable {

    public final int anInt1560;
    public final int anInt1561;
    public final int anInt1562;
    public final int anInt1563;
    public final int anInt1564;
    private final SpotAnimationDefinition spotanim;
    public boolean finishedAnimating;
    private int currentFrameIndex;
    private int frameLoop;

    public AnimableObject(int i, int j, int l, int i1, int j1, int k1, int l1) {
        finishedAnimating = false;
        spotanim = SpotAnimationDefinition.cache[i1];
        anInt1560 = i;
        anInt1561 = l1;
        anInt1562 = k1;
        anInt1563 = j1;
        anInt1564 = j + l;
        finishedAnimating = false;
    }

    public Model getRotatedModel() {
        Model model = spotanim.getModel();
        if (model == null)
            return null;
        int frame = currentFrameIndex;

        Model model_1 = new Model(true, Animation.noAnimationInProgress(frame), false, model);
        if (!finishedAnimating) {
            model_1.generateBones();
            model_1.animate(spotanim.sequenceDefinitionSequence, frame);
            model_1.faceGroups = null;
            model_1.vertexGroups = null;
        }
        if (spotanim.resizeXY != 128 || spotanim.resizeZ != 128)
            model_1.scale(spotanim.resizeXY, spotanim.resizeXY,
                    spotanim.resizeZ);
        if (spotanim.rotation != 0) {
            if (spotanim.rotation == 90)
                model_1.rotate90Degrees();
            if (spotanim.rotation == 180) {
                model_1.rotate90Degrees();
                model_1.rotate90Degrees();
            }
            if (spotanim.rotation == 270) {
                model_1.rotate90Degrees();
                model_1.rotate90Degrees();
                model_1.rotate90Degrees();
            }
        }
        model_1.light(64 + spotanim.modelBrightness, 850 + spotanim.modelShadow, -30, -50, -30, true);
        return model_1;
    }

    public void advance_anim(int i) {
        if(spotanim.sequenceDefinitionSequence.usingKeyframes()) {
            frameLoop += i;
            this.currentFrameIndex += i;
            if (this.currentFrameIndex >= spotanim.sequenceDefinitionSequence.getKeyframeDuration()) {
                this.finishedAnimating = true;
            }
        } else {

            for (frameLoop += i; frameLoop > spotanim.sequenceDefinitionSequence.durations[currentFrameIndex]; ) {
                frameLoop -= spotanim.sequenceDefinitionSequence.durations[currentFrameIndex] + 1;
                currentFrameIndex++;
                if (currentFrameIndex >= spotanim.sequenceDefinitionSequence.frameCount && (currentFrameIndex < 0 || currentFrameIndex >= spotanim.sequenceDefinitionSequence.frameCount)) {
                    currentFrameIndex = 0;
                    finishedAnimating = true;
                }
            }
        }
    }
}