package com.runescape.scene;

import com.runescape.Client;
import com.runescape.cache.anim.Animation;
import com.runescape.cache.anim.SpotAnimationDefinition;
import com.runescape.entity.Renderable;
import com.runescape.entity.model.Model;
import net.runelite.api.Actor;
import net.runelite.rs.api.RSModel;
import net.runelite.rs.api.RSNode;
import net.runelite.rs.api.RSProjectile;

public final class Projectile extends Renderable implements RSProjectile {

    public final int startCycle;
    public final int stopCycle;
    private double xIncrement;
    private double yIncrement;
    private double diagonalIncrement;
    private double heightIncrement;
    private double aDouble1578;
    private boolean started;
    private final int projectileX;
    private final int projectileY;
    private final int startHeight;
    public final int endHeight;
    public double xPos;
    public double yPos;
    public double cnterHeight;
    private final int initialSlope;
    private final int initialDistance;
    public final int target;
    private final SpotAnimationDefinition projectileGFX;
    private int currentFrameIndex;
    private int frameLoop;
    public int turnValue;
    private int tiltAngle;
    public final int projectileZ;

    public void calculateIncrements(int currentCycle, int targetY, int targetCenterHeight, int targetX) {
        if (!started) {
            double xToGo = targetX - projectileX;
            double yToGo = targetY - projectileY;
            double distanceToGo = Math.sqrt(xToGo * xToGo + yToGo * yToGo);
            xPos = (double) projectileX + (xToGo * (double) initialDistance) / distanceToGo;
            yPos = (double) projectileY + (yToGo * (double) initialDistance) / distanceToGo;
            cnterHeight = startHeight;
        }
        double cyclesLeft = (stopCycle + 1) - currentCycle;
        xIncrement = ((double) targetX - xPos) / cyclesLeft;
        yIncrement = ((double) targetY - yPos) / cyclesLeft;
        diagonalIncrement = Math.sqrt(xIncrement * xIncrement + yIncrement * yIncrement);
        if (!started) {
            heightIncrement = -diagonalIncrement * Math.tan((double) initialSlope * 0.02454369D);
        }
        aDouble1578 = (2D * ((double) targetCenterHeight - cnterHeight - heightIncrement * cyclesLeft)) / (cyclesLeft * cyclesLeft);
    }

    public Model getRotatedModel() {
        Model model = projectileGFX.getModel();
        if (model == null)
            return null;
        int frame = -1;
        Model projectileModel = new Model(true, Animation.noAnimationInProgress(frame), false, model);

        if (projectileGFX.sequenceDefinitionSequence != null) {
            frame = currentFrameIndex;
        }
        if (frame != -1) {
            projectileModel.generateBones();
            projectileModel.animate(projectileGFX.sequenceDefinitionSequence, frame);
            projectileModel.faceGroups = null;
            projectileModel.vertexGroups = null;
        }
        if (projectileGFX.resizeXY != 128 || projectileGFX.resizeZ != 128)
            projectileModel.scale(projectileGFX.resizeXY, projectileGFX.resizeXY,
                    projectileGFX.resizeZ);
        projectileModel.rotateZ(tiltAngle);
        projectileModel.light(64 + projectileGFX.modelBrightness, 850 + projectileGFX.modelShadow, -30, -50, -30, true);
        return projectileModel;
    }

    public Projectile(int initialSlope, int endHeight, int creationCycle, int destructionCycle, int initialDistance, int startZ, int startHeight, int y, int x, int target, int gfxMoving) {
        projectileGFX = SpotAnimationDefinition.cache[gfxMoving];
        projectileZ = startZ;
        projectileX = x;
        projectileY = y;
        this.startHeight = startHeight;
        startCycle = creationCycle;
        stopCycle = destructionCycle;
        this.initialSlope = initialSlope;
        this.initialDistance = initialDistance;
        this.target = target;
        this.endHeight = endHeight;
        started = false;
    }

    public void progressCycles(int cyclesMissed) {
        started = true;
        xPos += xIncrement * (double) cyclesMissed;
        yPos += yIncrement * (double) cyclesMissed;
        cnterHeight += heightIncrement * (double) cyclesMissed + 0.5D * aDouble1578 * (double) cyclesMissed * (double) cyclesMissed;
        heightIncrement += aDouble1578 * (double) cyclesMissed;
        //noinspection SuspiciousNameCombination
        turnValue = (int) (Math.atan2(xIncrement, yIncrement) * 325.94900000000001D) + 1024 & 0x7ff;
        tiltAngle = (int) (Math.atan2(heightIncrement, diagonalIncrement) * 325.94900000000001D) & 0x7ff;
        if (projectileGFX.sequenceDefinitionSequence != null) {
            if (projectileGFX.sequenceDefinitionSequence.usingKeyframes()) {
                this.currentFrameIndex += cyclesMissed;
                int var3 = projectileGFX.sequenceDefinitionSequence.getKeyframeDuration();
                if (this.currentFrameIndex >= var3) {
                    this.currentFrameIndex = var3 - projectileGFX.sequenceDefinitionSequence.loopFrameCount;
                }
            } else {
                for (frameLoop += cyclesMissed; frameLoop > projectileGFX.sequenceDefinitionSequence.durations[currentFrameIndex]; ) {
                    frameLoop -= projectileGFX.sequenceDefinitionSequence.durations[currentFrameIndex] + 1;
                    currentFrameIndex++;
                    if (currentFrameIndex >= projectileGFX.sequenceDefinitionSequence.frameCount)
                        currentFrameIndex = 0;
                }
            }
        }
    }

    public int endX, endY;

    public void stillFocus(int endX, int endY) {
        this.endX = endX;
        this.endY = endY;
    }

    @Override
    public Actor getInteracting() {
        if (target < 0) {
            return Client.instance.players[-target - 1];
        } else if (target > 0) {
            return Client.instance.getNpcs().get(target - 1);
        }
        return null;
    }

    @Override
    public int getRemainingCycles() {
        return stopCycle - Client.instance.getTickCount();
    }

    @Override
    public int getId() {
        return projectileGFX.id;
    }

    @Override
    public int getRsInteracting() {
        return target;
    }

    @Override
    public int getHeight() {
        return (int) cnterHeight;
    }

    @Override
    public int getEndHeight() {
        return endHeight;
    }

    @Override
    public int getX1() {
        return endX;
    }

    @Override
    public int getY1() {
        return endY;
    }

    @Override
    public int getFloor() {
        return (int) cnterHeight;
    }

    @Override
    public int getStartMovementCycle() {
        return startCycle;
    }

    @Override
    public int getEndCycle() {
        return stopCycle;
    }

    @Override
    public int getSlope() {
        return initialSlope;
    }

    @Override
    public int getStartHeight() {
        return startHeight;
    }

    @Override
    public double getX() {
        return xPos;
    }

    @Override
    public double getY() {
        return yPos;
    }

    @Override
    public double getZ() {
        return Client.instance.getPlane();
    }

    @Override
    public double getScalar() {
        return this.cnterHeight;
    }

    @Override
    public double getVelocityX() {
        return xIncrement;
    }

    @Override
    public double getVelocityY() {
        return yIncrement;
    }

    @Override
    public double getVelocityZ() {
        return diagonalIncrement;
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

    @Override
    public void draw(int orientation, int pitchSin, int pitchCos, int yawSin, int yawCos, int x, int y, int z, long hash) {
        renderAtPoint(orientation, pitchSin, pitchCos, yawSin, yawCos, x, y, z, hash);
    }
}