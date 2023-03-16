package com.runescape.cache.anim;

import com.runescape.cache.FileArchive;
import com.runescape.collection.ReferenceCache;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;

public final class SpotAnimationDefinition {

    public static SpotAnimationDefinition[] cache;
    public static ReferenceCache models = new ReferenceCache(30);
    private int[] originalModelColours;
    private int[] modifiedModelColours;
    public SequenceDefinition sequenceDefinitionSequence;
    public int resizeXY;
    public int resizeZ;
    public int rotation;
    public int modelBrightness;
    public int modelShadow;
    public int id;
    private int modelId;
    private int animationId;

    private SpotAnimationDefinition() {
        animationId = -1;
        originalModelColours = new int[6];
        modifiedModelColours = new int[6];
        resizeXY = 128;
        resizeZ = 128;
    }

    public static void init(FileArchive archive) {
        Buffer stream = new Buffer(archive.readFile("spotanim.dat"));
        int length = stream.readUShort();
        if (cache == null)
            cache = new SpotAnimationDefinition[length + 1];
        for (int index = 0; index < length; index++) {
            if (cache[index] == null)
                cache[index] = new SpotAnimationDefinition();
            cache[index].id = index;
            cache[index].readValues(stream);
        }

        System.out.println("Loaded: " + length + " graphics");
    }

    public void readValues(Buffer buffer) {
        while(true) {
            final int opcode = buffer.readUnsignedByte();

            if (opcode == 0) {
                return;
            } else if (opcode == 1) {
                modelId = buffer.readUShort();
            } else if (opcode == 2) {
                animationId = buffer.readUShort();

                if (SequenceDefinition.sequenceDefinitions != null)
                    sequenceDefinitionSequence = SequenceDefinition.sequenceDefinitions[animationId];
            } else if (opcode == 4) {
                resizeXY = buffer.readUShort();
            } else if (opcode == 5) {
                resizeZ = buffer.readUShort();
            } else if (opcode == 6) {
                rotation = buffer.readUShort();
            } else if (opcode == 7) {
                modelBrightness = buffer.readUnsignedByte();
            } else if (opcode == 8) {
                modelShadow = buffer.readUnsignedByte();
            } else if (opcode == 40) {
                int len = buffer.readUnsignedByte();
                originalModelColours = new int[len];
                modifiedModelColours = new int[len];
                for (int i = 0; i < len; i++) {
                    originalModelColours[i] = buffer.readUShort();
                    modifiedModelColours[i] = buffer.readUShort();
                }
            } else if (opcode == 41) { // re-texture
                int len = buffer.readUnsignedByte();

                for (int i = 0; i < len; i++) {
                    buffer.readUShort();
                    buffer.readUShort();
                }
            } else {
                System.out.println("gfx invalid opcode: " + opcode);
            }
        }
    }

    public Model getModel() {
        Model model = (Model) models.get(id);
        if (model != null)
            return model;
        model = Model.getModel(modelId);
        if (model == null)
            return null;
        if (originalModelColours == null ||
                originalModelColours.length == 0 ||
                originalModelColours.length != modifiedModelColours.length) {
            return model;
        }

        for (int i = 0; i < originalModelColours.length; i++)
            if (originalModelColours[0] != 0)
                model.recolor(originalModelColours[i], modifiedModelColours[i]);

        models.put(model, id);
        return model;
    }

    public Model getTransformedModel(int frameindex) {
        Model model = (Model) models.get(id);
        if(model == null) {
            model = Model.getModel(modelId);
            if (model == null)
                return null;
            for (int i = 0; i < originalModelColours.length; i++)
                if (originalModelColours[0] != 0) //default frame id
                    model.recolor(originalModelColours[i], modifiedModelColours[i]);
           
            models.put(model, id);
        }
        Model var6;
        if (animationId != -1 && frameindex != -1) {
            var6 = sequenceDefinitionSequence.animateSpotanim(model, frameindex);
        } else {
            var6 = model.bakeSharedModel(true);
        }


        var6.faceGroups = null;
        var6.vertexGroups = null;
        if (resizeXY != 128 || resizeZ != 128)
            var6.scale(resizeXY, resizeXY, resizeZ);
        var6.light(64 + modelBrightness, 850 + modelShadow, -30, -50, -30, true);

        return var6;
    }

}
