package com.runescape.entity;

import com.runescape.Client;
import com.runescape.cache.anim.Animation;
import com.runescape.cache.anim.Frame;
import com.runescape.cache.anim.Graphic;
import com.runescape.cache.def.NpcDefinition;
import com.runescape.entity.model.Model;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.rs.api.*;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class Npc extends Mob implements RSNPC {

    public NpcDefinition desc;
    public int headIcon = -1;
    public int ownerIndex = -1;

    public boolean showActions() {
        if (ownerIndex == -1) {
            return true;
        }
        return (Client.instance.localPlayerIndex == ownerIndex);
    }

    public int getHeadIcon() {
        if (headIcon == -1) {
            if (desc != null) {
                return desc.headIcon;
            }
        }
        return headIcon;
    }

    private Model getAnimatedModel() {
        if (super.emoteAnimation >= 0 && super.animationDelay == 0) {
            int emote = Animation.animations[super.emoteAnimation].primaryFrames[super.displayedEmoteFrames];
            int movement = -1;
            if (super.movementAnimation >= 0 && super.movementAnimation != super.idleAnimation)
                movement = Animation.animations[super.movementAnimation].primaryFrames[super.displayedMovementFrames];
            return desc.getAnimatedModel(movement, emote,
                    Animation.animations[super.emoteAnimation].interleaveOrder);
        }
        int movement = -1;
        if (super.movementAnimation >= 0) {
            movement = Animation.animations[super.movementAnimation].primaryFrames[super.displayedMovementFrames];
        }
        return desc.getAnimatedModel(-1, movement, null);
    }

    public Model getRotatedModel() {
        if (desc == null)
            return null;
        Model animatedModel = getAnimatedModel();
        if (animatedModel == null)
            return null;
        super.height = animatedModel.modelBaseY;
        if (super.graphic != -1 && super.currentAnimation != -1) {
            Graphic spotAnim = Graphic.cache[super.graphic];
            Model graphicModel = spotAnim.getModel();
            if (graphicModel != null) {
                int frame = spotAnim.animationSequence.primaryFrames[super.currentAnimation];
                Model model = new Model(true, Frame.noAnimationInProgress(frame),
                        false, graphicModel);
                model.offsetBy(0, -super.graphicHeight, 0);
                model.generateBones();
                model.animate(frame);
                model.faceGroups = null;
                model.vertexGroups = null;
                if (spotAnim.resizeXY != 128 || spotAnim.resizeZ != 128)
                    model.scale(spotAnim.resizeXY, spotAnim.resizeXY,
                            spotAnim.resizeZ);
                model.light(64 + spotAnim.modelBrightness,
                        850 + spotAnim.modelShadow, -30, -50, -30, true);
                Model models[] = {animatedModel, model};
                animatedModel = new Model(models);
            }
        }
        if (desc.size == 1)
            animatedModel.singleTile = true;
        return animatedModel;
    }

    public boolean isVisible() {
        return desc != null;
    }

    @Override
    public int getRSInteracting() {
        return interactingEntity;
    }

    @Override
    public String getOverheadText() {
        return "";
    }

    @Override
    public void setOverheadText(String overheadText) {

    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int[] getPathX() {
        return pathX;
    }

    @Override
    public int[] getPathY() {
        return pathY;
    }

    @Override
    public int getAnimation() {
        return 0;
    }

    @Override
    public void setAnimation(int animation) {

    }

    @Override
    public int getAnimationFrame() {
        return 0;
    }

    @Override
    public int getActionFrame() {
        return 0;
    }

    @Override
    public void setAnimationFrame(int frame) {
    }

    @Override
    public void setActionFrame(int frame) {
    }

    @Override
    public int getActionFrameCycle() {
        return 0;
    }

    @Override
    public int getGraphic() {
        return graphic;
    }

    @Override
    public void setGraphic(int id) {
        graphic = id;
    }

    @Override
    public int getSpotAnimFrame() {
        return 0;
    }

    @Override
    public void setSpotAnimFrame(int id) {
    }

    @Override
    public int getSpotAnimationFrameCycle() {
        return 0;
    }

    @Override
    public int getIdlePoseAnimation() {
        return 0;
    }

    @Override
    public void setIdlePoseAnimation(int animation) {
    }

    @Override
    public int getPoseAnimation() {
        return movementAnimation;
    }

    @Override
    public void setPoseAnimation(int animation) {
        movementAnimation = animation;
    }

    @Override
    public int getPoseFrame() {
        return 0;
    }

    @Override
    public void setPoseFrame(int frame) {
    }

    @Override
    public int getPoseFrameCycle() {
        return 0;
    }

    @Override
    public int getLogicalHeight() {
        return height;
    }

    @Override
    public int getOrientation() {
        return orientation;
    }

    @Override
    public int getCurrentOrientation() {
        return orientation;
    }

    @Override
    public RSIterableNodeDeque getHealthBars() {
        return null;
    }

    @Override
    public int[] getHitsplatValues() {
        return null;
    }

    @Override
    public int[] getHitsplatTypes() {
        return null;
    }

    @Override
    public int[] getHitsplatCycles() {
        return null;
    }

    @Override
    public int getIdleRotateLeft() {
        return 0;
    }

    @Override
    public int getIdleRotateRight() {
        return 0;
    }

    @Override
    public int getWalkAnimation() {
        return 0;
    }

    @Override
    public int getWalkRotate180() {
        return 0;
    }

    @Override
    public int getWalkRotateLeft() {
        return 0;
    }

    @Override
    public int getWalkRotateRight() {
        return 0;
    }

    @Override
    public int getRunAnimation() {
        return 0;
    }

    @Override
    public void setDead(boolean dead) {
    }

    @Override
    public int getPathLength() {
        return 0;
    }

    @Override
    public int getOverheadCycle() {
        return 0;
    }

    @Override
    public void setOverheadCycle(int cycle) {
    }

    @Override
    public int getPoseAnimationFrame() {
        return 0;
    }

    @Override
    public void setPoseAnimationFrame(int frame) {

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

    }

    @Override
    public boolean isHidden() {
        return false;
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
    public int getCombatLevel() {
        return desc.combatLevel;
    }

    @Override
    public String getName() {
        return desc.getName();
    }

    @Override
    public Actor getInteracting() {
        int index = getRSInteracting();
        if (index == -1 || index == 65535)
        {
            return null;
        }
        Client client = Client.instance;

        if (index < 32768)
        {
            NPC[] npcs = client.getCachedNPCs();
            return npcs[index];
        }

        index -= 32768;
        Player[] players = client.players;
        return players[index];
    }

    @Override
    public int getHealthRatio() {
        return Math.round(this.currentHealth / this.maxHealth);
    }

    @Override
    public int getHealthScale() {
        return currentHealth;
    }

    @Override
    public WorldPoint getWorldLocation() {
        return WorldPoint.fromLocal(Client.instance, getLocalLocation());
    }

    @Override
    public LocalPoint getLocalLocation() {
        return new LocalPoint(this.x, this.y);
    }

    @Override
    public void setIdleRotateLeft(int animationID) {
    }

    @Override
    public void setIdleRotateRight(int animationID) {
    }

    @Override
    public void setWalkAnimation(int animationID) {
    }

    @Override
    public void setWalkRotateLeft(int animationID) {
    }

    @Override
    public void setWalkRotateRight(int animationID) {
    }

    @Override
    public void setWalkRotate180(int animationID) {
    }

    @Override
    public void setRunAnimation(int animationID) {
    }

    @Override
    public Polygon getCanvasTilePoly() {
        return Perspective.getCanvasTilePoly(Client.instance, this.getLocalLocation());
    }

    @Override
    public Point getCanvasTextLocation(Graphics2D graphics, String text, int zOffset) {
        return Perspective.getCanvasTextLocation(Client.instance, graphics, getLocalLocation(), text, zOffset);
    }

    @Override
    public Point getCanvasImageLocation(BufferedImage image, int zOffset) {
        return Perspective.getCanvasImageLocation(Client.instance, getLocalLocation(), image, zOffset);
    }

    @Override
    public Point getCanvasSpriteLocation(SpritePixels sprite, int zOffset) {
        return null;
    }

    @Override
    public Point getMinimapLocation() {
        return Perspective.localToMinimap(Client.instance, getLocalLocation());
    }

    @Override
    public Shape getConvexHull() {
        RSModel model = getModel();
        if (model == null)
        {
            return null;
        }

        int size = getComposition().getSize();
        LocalPoint tileHeightPoint = new LocalPoint(
                size * Perspective.LOCAL_HALF_TILE_SIZE - Perspective.LOCAL_HALF_TILE_SIZE + getX(),
                size * Perspective.LOCAL_HALF_TILE_SIZE - Perspective.LOCAL_HALF_TILE_SIZE + getY());

        int tileHeight = Perspective.getTileHeight(Client.instance, tileHeightPoint, Client.instance.getPlane());

        return model.getConvexHull(getX(), getY(), getOrientation(), tileHeight);
    }

    @Override
    public WorldArea getWorldArea() {
        int size = 1;
        if (this instanceof NPC)
        {
            NPCComposition composition = ((NPC)this).getComposition();
            if (composition != null && composition.getConfigs() != null)
            {
                composition = composition.transform();
            }
            if (composition != null)
            {
                size = composition.getSize();
            }
        }

        return new WorldArea(this.getWorldLocation(), size, size);
        //return new WorldArea(getWorldLocation(), desc.scaleXZ, desc.scaleY);
    }

    @Override
    public boolean isDead() {
        return currentHealth <= 0 && maxHealth > 0;
    }

    @Override
    public boolean isMoving() {
        return false;
    }

    @Override
    public int getId() {
        return desc.id;
    }

    @Override
    public NPCComposition getTransformedComposition() {
        RSNPCComposition composition = getComposition();
        if (composition != null && composition.getConfigs() != null)
        {
            composition = composition.transform();
        }
        return composition;
    }

    @Override
    public void onDefinitionChanged(NPCComposition composition) {
        if (composition == null)
        {
            Client.instance.getCallbacks().post(new NpcDespawned(this));
        }
        else if (this.getId() != -1)
        {
            RSNPCComposition oldComposition = getComposition();
            if (oldComposition == null)
            {
                return;
            }

            if (composition.getId() == oldComposition.getId())
            {
                return;
            }

            Client.instance.getCallbacks().postDeferred(new NpcChanged(this, oldComposition));
        }
    }

    @Override
    public RSNPCComposition getComposition() {
        return desc;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int id) {
        this.index = id;
    }

}
