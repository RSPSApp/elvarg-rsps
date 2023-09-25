package com.runescape.entity;

import com.runescape.Client;
import com.runescape.cache.anim.SequenceDefinition;
import com.runescape.cache.anim.SpotAnimationDefinition;
import com.runescape.cache.def.ItemDefinition;
import com.runescape.cache.def.NpcDefinition;
import com.runescape.collection.ReferenceCache;
import com.runescape.entity.model.IdentityKit;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;
import com.runescape.util.StringUtils;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.kit.KitType;
import net.runelite.rs.api.*;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public final class Player extends Mob implements RSPlayer,RSPlayerComposition {


    public static ReferenceCache models = new ReferenceCache(260);
    public final int[] appearanceColors = new int[5];
    public final int[] equipment = new int[12];
    public NpcDefinition npcDefinition;
    public boolean aBoolean1699;
    public int team;
    public String name;
    public int combatLevel;
    public int headIcon;
    public int skullIcon;
    public int hintIcon;
    public int objectModelStart;
    public int objectModelStop;
    public int anInt1709;
    public boolean visible;
    public int objectXPos;
    public int objectCenterHeight;
    public int objectYPos;
    public Model playerModel;
    public int objectAnInt1719LesserXLoc;
    public int objectAnInt1720LesserYLoc;
    public int objectAnInt1721GreaterXLoc;
    public int objectAnInt1722GreaterYLoc;
    public int skill;
    public String clanName = "None";
    public int rights, donatorRights;
    private long cachedModel = -1L;
    private int gender;
    private long appearanceOffset;

    public Model getRotatedModel() {

        if (!visible) {
            return null;
        }

        Model animatedModel = getAnimatedModel();

        if (animatedModel == null) {
            return null;
        }

        super.height = animatedModel.modelBaseY;
        animatedModel.singleTile = true;

        if (aBoolean1699) {
            return animatedModel;
        }

        if (super.graphic != -1 && super.currentAnimation != -1) {
            SpotAnimationDefinition spotAnim = SpotAnimationDefinition.cache[super.graphic];
            Model model_3 = spotAnim.getTransformedModel(super.currentAnimation);
            if(model_3 != null) {
                model_3.offsetBy(0, -super.graphicHeight, 0);
                Model aclass30_sub2_sub4_sub6_1s[] = {animatedModel, model_3};
                animatedModel = new Model(aclass30_sub2_sub4_sub6_1s);
            }
        }

        if (playerModel != null) {
            if (Client.tick >= objectModelStop)
                playerModel = null;
            if (Client.tick >= objectModelStart && Client.tick < objectModelStop) {
                Model model_1 = playerModel;
                model_1.offsetBy(objectXPos - super.x, objectCenterHeight - anInt1709, objectYPos - super.y);
                if (super.nextStepOrientation == 512) {
                    model_1.rotate90Degrees();
                    model_1.rotate90Degrees();
                    model_1.rotate90Degrees();
                } else if (super.nextStepOrientation == 1024) {
                    model_1.rotate90Degrees();
                    model_1.rotate90Degrees();
                } else if (super.nextStepOrientation == 1536)
                    model_1.rotate90Degrees();
                Model models[] = {animatedModel, model_1};
                animatedModel = new Model(models);
                if (super.nextStepOrientation == 512)
                    model_1.rotate90Degrees();
                else if (super.nextStepOrientation == 1024) {
                    model_1.rotate90Degrees();
                    model_1.rotate90Degrees();
                } else if (super.nextStepOrientation == 1536) {
                    model_1.rotate90Degrees();
                    model_1.rotate90Degrees();
                    model_1.rotate90Degrees();
                }
                model_1.offsetBy(super.x - objectXPos, anInt1709 - objectCenterHeight, super.y - objectYPos);
            }
        }
        animatedModel.singleTile = true;
        return animatedModel;
    }

    public void updateAppearance(Buffer buffer) {
        buffer.currentPosition = 0;

        gender = buffer.readUnsignedByte();
        headIcon = buffer.readUnsignedByte();
        skullIcon = buffer.readUnsignedByte();
        hintIcon = buffer.readUnsignedByte();
        npcDefinition = null;
        team = 0;

        for (int bodyPart = 0; bodyPart < 12; bodyPart++) {

            int reset = buffer.readUnsignedByte();

            if (reset == 0) {
                equipment[bodyPart] = 0;
                continue;
            }

            int id = buffer.readUnsignedByte();

            equipment[bodyPart] = (reset << 8) + id;

            if (bodyPart == 0 && equipment[0] == 65535) {
                npcDefinition = NpcDefinition.lookup(buffer.readUShort());
                break;
            }

            if (equipment[bodyPart] >= 512 && equipment[bodyPart] - 512 < ItemDefinition.totalItems) {
                int team = ItemDefinition.lookup(equipment[bodyPart] - 512).team;

                if (team != 0) {
                    this.team = team;
                }

            }

        }

        for (int part = 0; part < 5; part++) {
            int color = buffer.readUnsignedByte();
            if (color < 0 || color >= Client.PLAYER_BODY_RECOLOURS[part].length) {
                color = 0;
            }
            appearanceColors[part] = color;
        }

        super.idleAnimation = buffer.readUShort();
        if (super.idleAnimation == 65535) {
            super.idleAnimation = -1;
        }

        super.standTurnAnimIndex = buffer.readUShort();
        if (super.standTurnAnimIndex == 65535) {
            super.standTurnAnimIndex = -1;
        }
        super.readyanim_r = super.standTurnAnimIndex;
        super.walkAnimIndex = buffer.readUShort();
        if (super.walkAnimIndex == 65535) {
            super.walkAnimIndex = -1;
        }

        super.turn180AnimIndex = buffer.readUShort();
        if (super.turn180AnimIndex == 65535) {
            super.turn180AnimIndex = -1;
        }

        super.turn90CWAnimIndex = buffer.readUShort();
        if (super.turn90CWAnimIndex == 65535) {
            super.turn90CWAnimIndex = -1;
        }

        super.turn90CCWAnimIndex = buffer.readUShort();
        if (super.turn90CCWAnimIndex == 65535) {
            super.turn90CCWAnimIndex = -1;
        }

        super.runAnimIndex = buffer.readUShort();
        if (super.runAnimIndex == 65535) {
            super.runAnimIndex = -1;
        }

        name = StringUtils.formatText(StringUtils.decodeBase37(buffer.readLong()));
        combatLevel = buffer.readUnsignedByte();
        rights = buffer.readUnsignedByte();

        //skill = buffer.readUShort();
        visible = true;
        appearanceOffset = 0L;

        for (int index = 0; index < 12; index++) {
            appearanceOffset <<= 4;

            if (equipment[index] >= 256) {
                appearanceOffset += equipment[index] - 256;
            }

        }

        if (equipment[0] >= 256) {
            appearanceOffset += equipment[0] - 256 >> 4;
        }

        if (equipment[1] >= 256) {
            appearanceOffset += equipment[1] - 256 >> 8;
        }

        for (int index = 0; index < 5; index++) {
            appearanceOffset <<= 3;
            appearanceOffset += appearanceColors[index];
        }

        appearanceOffset <<= 1;
        appearanceOffset += gender;
    }

    public Model getAnimatedModel() {

        SequenceDefinition sequenceDefinition = super.emoteAnimation != -1 && super.animationDelay == 0 ? SequenceDefinition.sequenceDefinitions[super.emoteAnimation] : null;
        SequenceDefinition walkSequenceDefinition = (super.movementAnimation == -1 || (super.movementAnimation == super.idleAnimation && sequenceDefinition != null)) ? null : SequenceDefinition.sequenceDefinitions[super.movementAnimation];

        if (npcDefinition != null) {
            Model model = npcDefinition.getAnimatedModel(super.displayedEmoteFrames, sequenceDefinition, null, super.secondaryanimFrameindex, walkSequenceDefinition);
            return model;
        }

        long uid = appearanceOffset;


        int[] equipment = this.equipment;
        if (sequenceDefinition != null && (sequenceDefinition.rightHandOverride >= 0 || sequenceDefinition.leftHandOverride >= 0)) {
            equipment = new int[12];

            for(int i = 0; i < 12; ++i) {
                equipment[i] = this.equipment[i];
            }

            if (sequenceDefinition.rightHandOverride >= 0) {
                uid += (long)(sequenceDefinition.rightHandOverride - this.equipment[5] << 40);
                equipment[5] = sequenceDefinition.rightHandOverride;
            }

            if (sequenceDefinition.leftHandOverride >= 0) {
                uid += (long)(sequenceDefinition.leftHandOverride - this.equipment[3] << 48);
                equipment[3] = sequenceDefinition.leftHandOverride;
            }
        }
        Model model_1 = (Model) models.get(uid);
        if (model_1 == null) {
            boolean modelsInvalid = false;
            for (int wearpos = 0; wearpos < 12; wearpos++) {
                int wear = equipment[wearpos];
                if (wear >= 0x100 && wear < 512 && !IdentityKit.kits[wear - 0x100].bodyLoaded()) {
                    modelsInvalid = true;
                }
                if (wear >= 0x200 && !ItemDefinition.lookup(wear - 0x200).isEquippedModelCached(gender)) {
                    modelsInvalid = true;
                }
            }

            if (modelsInvalid) {
                if (cachedModel != -1L)
                    model_1 = (Model) models.get(cachedModel);
                if (model_1 == null)
                    return null;
            }
        }
        if (model_1 == null) {
            Model kitMeshes[] = new Model[12];
            int worn = 0;
            for (int slot = 0; slot < 12; slot++) {
                int part = equipment[slot];
                if (part >= 0x100 && part < 0x200) {
                    Model model_3 = IdentityKit.kits[part - 0x100].bodyModel();
                    if (model_3 != null)
                        kitMeshes[worn++] = model_3;
                }
                if (part >= 0x200) {
                    Model model_4 = ItemDefinition.lookup(part - 0x200).getEquippedModel(gender);
                    if (model_4 != null)
                        kitMeshes[worn++] = model_4;
                }
            }

            model_1 = new Model(worn, kitMeshes);
            for (int j3 = 0; j3 < 5; j3++)
                if (appearanceColors[j3] != 0) {
                    model_1.recolor(Client.PLAYER_BODY_RECOLOURS[j3][0],
                            Client.PLAYER_BODY_RECOLOURS[j3][appearanceColors[j3]]);
                    if (j3 == 1)
                        model_1.recolor(Client.anIntArray1204[0], Client.anIntArray1204[appearanceColors[j3]]);
                }

            model_1.light(64, 850, -30, -50, -30, true);
            models.put(model_1, uid);
            cachedModel = uid;
        }
        if (aBoolean1699)
            return model_1;


        if (sequenceDefinition == null && walkSequenceDefinition == null) {
            return model_1;
        } else {
            Model animatedModel;
            if (sequenceDefinition != null && walkSequenceDefinition != null) {
                animatedModel = sequenceDefinition.animateMultiple(model_1, super.displayedEmoteFrames, walkSequenceDefinition, super.secondaryanimFrameindex);
            } else if (sequenceDefinition == null) {
                animatedModel = walkSequenceDefinition.animateEither(model_1, super.secondaryanimFrameindex);
            } else {
                animatedModel = sequenceDefinition.animateEither(model_1, super.displayedEmoteFrames);
            }
            
            return animatedModel;
        }
    }

    public Model getHeadModel() {
        if (!visible) {
            return null;
        }

        if (npcDefinition != null) {
            return npcDefinition.model();
        }

        boolean cached = false;

        for (int index = 0; index < 12; index++) {
            int appearanceId = equipment[index];

            if (appearanceId >= 256 && appearanceId < 512 && !IdentityKit.kits[appearanceId - 256].headLoaded()) {
                cached = true;
            }

            if (appearanceId >= 512 && !ItemDefinition.lookup(appearanceId - 512).isDialogueModelCached(gender)) {
                cached = true;
            }
        }

        if (cached) {
            return null;
        }

        Model headModels[] = new Model[12];

        int headModelsOffset = 0;

        for (int modelIndex = 0; modelIndex < 12; modelIndex++) {
            int appearanceId = equipment[modelIndex];

            if (appearanceId >= 256 && appearanceId < 512) {

                Model subModel = IdentityKit.kits[appearanceId - 256].headModel();

                if (subModel != null) {
                    headModels[headModelsOffset++] = subModel;
                }

            }
            if (appearanceId >= 512) {
                Model subModel = ItemDefinition.lookup(appearanceId - 512).getChatEquipModel(gender);

                if (subModel != null) {
                    headModels[headModelsOffset++] = subModel;
                }

            }
        }

        Model headModel = new Model(headModelsOffset, headModels);

        for (int index = 0; index < 5; index++) {
            if (appearanceColors[index] != 0) {
                headModel.recolor(Client.PLAYER_BODY_RECOLOURS[index][0],
                        Client.PLAYER_BODY_RECOLOURS[index][appearanceColors[index]]);
                if (index == 1) {
                    headModel.recolor(Client.anIntArray1204[0], Client.anIntArray1204[appearanceColors[index]]);
                }
            }
        }

        return headModel;
    }

    public boolean isVisible() {
        return visible;
    }

    @Nullable
    @Override
    public String getName() {
        return null;
    }

    @Override
    public Actor getInteracting() {
        return null;
    }

    @Override
    public int getHealthRatio() {
        return 0;
    }

    @Override
    public int getHealthScale() {
        return 0;
    }

    @Override
    public WorldPoint getWorldLocation() {
        return WorldPoint.fromLocal(Client.instance,
                this.getPathX()[0] * Perspective.LOCAL_TILE_SIZE + Perspective.LOCAL_TILE_SIZE / 2,
                this.getPathY()[0] * Perspective.LOCAL_TILE_SIZE + Perspective.LOCAL_TILE_SIZE / 2,
                Client.instance.getPlane());
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
        return null;
    }

    @Nullable
    @Override
    public Point getCanvasTextLocation(Graphics2D graphics, String text, int zOffset) {
        return null;
    }

    @Override
    public Point getCanvasImageLocation(BufferedImage image, int zOffset) {
        return null;
    }

    @Override
    public Point getCanvasSpriteLocation(SpritePixels sprite, int zOffset) {
        return null;
    }

    @Override
    public Point getMinimapLocation() {
        return null;
    }

    @Override
    public Shape getConvexHull()
    {
        RSModel model = getModel();
        if (model == null)
        {
            return null;
        }

        int tileHeight = Perspective.getTileHeight(Client.instance, new LocalPoint(getX(), getY()), Client.instance.getPlane());

        return model.getConvexHull(getX(), getY(), getOrientation(), tileHeight);
    }

    @Override
    public WorldArea getWorldArea() {
        return new WorldArea(getWorldLocation(), 1, 1);
    }

    @Override
    public boolean isDead() {
        return false;
    }

    @Override
    public boolean isMoving() {
        return false;
    }

    @Override
    public Polygon[] getPolygons()
    {
        RSModel model = getModel();

        if (model == null)
        {
            return null;
        }

        int[] x2d = new int[model.getVerticesCount()];
        int[] y2d = new int[model.getVerticesCount()];

        int localX = getX();
        int localY = getY();

        final int tileHeight = Perspective.getTileHeight(Client.instance, new LocalPoint(localX, localY), Client.instance.getPlane());

        Perspective.modelToCanvas(Client.instance, model.getVerticesCount(), localX, localY, tileHeight, getOrientation(), model.getVerticesX(), model.getVerticesZ(), model.getVerticesY(), x2d, y2d);
        ArrayList polys = new ArrayList(model.getFaceCount());

        int[] trianglesX = model.getFaceIndices1();
        int[] trianglesY = model.getFaceIndices2();
        int[] trianglesZ = model.getFaceIndices3();

        for (int triangle = 0; triangle < model.getFaceCount(); ++triangle)
        {
            int[] xx =
                    {
                            x2d[trianglesX[triangle]], x2d[trianglesY[triangle]], x2d[trianglesZ[triangle]]
                    };

            int[] yy =
                    {
                            y2d[trianglesX[triangle]], y2d[trianglesY[triangle]], y2d[trianglesZ[triangle]]
                    };

            polys.add(new Polygon(xx, yy, 3));
        }

        return (Polygon[]) polys.toArray(new Polygon[0]);
    }


    @Nullable
    @Override
    public HeadIcon getOverheadIcon() {
        return null;
    }

    @Nullable
    @Override
    public SkullIcon getSkullIcon() {
        return null;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public int getRSInteracting() {
        return 0;
    }

    @Override
    public String getOverheadText() {
        return null;
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
        return 0;
    }

    @Override
    public void setGraphic(int id) {

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
        return 0;
    }

    @Override
    public void setPoseAnimation(int animation) {

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
        return 0;
    }

    @Override
    public int getOrientation() {
        return 0;
    }

    @Override
    public int getCurrentOrientation() {
        return 0;
    }

    @Override
    public RSIterableNodeDeque getHealthBars() {
        return null;
    }

    @Override
    public int[] getHitsplatValues() {
        return new int[0];
    }

    @Override
    public int[] getHitsplatTypes() {
        return new int[0];
    }

    @Override
    public int[] getHitsplatCycles() {
        return new int[0];
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
    public RSNode getNext() {
        return null;
    }

    @Override
    public long getHash() {
        return 0;
    }

    @Override
    public int[] getEquipmentIds() {
        return new int[0];
    }

    @Override
    public void setTransformedNpcId(int id) {

    }

    @Override
    public void setHash() {

    }

    @Override
    public RSNode getPrevious() {
        return null;
    }

    @Override
    public void onUnlink() {

    }

    @Override
    public RSUsername getRsName() {
        return null;
    }

    @Override
    public int getPlayerId() {
        return 0;
    }

    @Override
    public RSPlayerComposition getPlayerComposition() {
        return null;
    }

    @Override
    public int getCombatLevel() {
        return 0;
    }

    @Override
    public int getTotalLevel() {
        return 0;
    }

    @Override
    public int getTeam() {
        return 0;
    }

    @Override
    public boolean isFriendsChatMember() {
        return false;
    }

    @Override
    public boolean isClanMember() {
        return false;
    }

    @Override
    public boolean isFriend() {
        return false;
    }

    @Override
    public boolean isFriended() {
        return false;
    }

    @Override
    public int getRsOverheadIcon() {
        return 0;
    }

    @Override
    public int getRsSkullIcon() {
        return 0;
    }

    @Override
    public int getRSSkillLevel() {
        return 0;
    }

    @Override
    public String[] getActions() {
        return new String[0];
    }

    @Override
    public int getModelHeight() {
        return modelBaseY;
    }

    @Override
    public void setModelHeight(int modelHeight) {
        this.modelBaseY = modelHeight;
    }

    @Override
    public RSModel getModel() {
        return getRotatedModel();
    }

    @Override
    public void draw(int orientation, int pitchSin, int pitchCos, int yawSin, int yawCos, int x, int y, int z, long hash) {

    }

    public boolean isFemale()
    {
        return gender == 1;
    }

    @Override
    public int[] getColors() {
        return new int[0];
    }

    @Override
    public int getEquipmentId(KitType type)
    {
        int id = getEquipmentIds()[type.getIndex()];
        if (id < 512)
        {
            return -1; // not an item
        }
        return id - 512;
    }

    @Override
    public int getKitId(KitType type)
    {
        int id = getEquipmentIds()[type.getIndex()];
        if (id < 256 || id >= 512)
        {
            return -1; // not a kit
        }
        return id - 256;
    }

}
