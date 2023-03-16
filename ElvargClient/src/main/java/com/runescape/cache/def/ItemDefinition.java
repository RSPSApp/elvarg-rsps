package com.runescape.cache.def;

import com.runescape.cache.FileArchive;
import com.runescape.collection.ReferenceCache;
import com.runescape.draw.Rasterizer2D;
import com.runescape.draw.Rasterizer3D;
import com.runescape.entity.model.Model;
import com.runescape.graphics.sprite.Sprite;
import com.runescape.io.Buffer;
import com.runescape.util.BufferExt;
import net.runelite.api.IterableHashTable;
import net.runelite.rs.api.RSItemComposition;
import net.runelite.rs.api.RSIterableNodeHashTable;

import java.util.HashMap;

public final class ItemDefinition implements RSItemComposition {

    public static ReferenceCache sprites = new ReferenceCache(100);
    public static ReferenceCache models = new ReferenceCache(50);
    public static boolean isMembers = true;
    public static int totalItems;
    public static ItemDefinition[] cache;
    private static int cacheIndex;
    private static Buffer item_data;
    private static int[] streamIndices;
    public int cost;
    public int[] colorReplace;
    private short[] textureReplace;
    private short[] textureFind;

    public int id;
    public int[] colorFind;
    public boolean members;
    public int noted_item_id;
    public int weight;
    public int wearPos1;
    public int wearPos2;
    public int wearPos3;
    public int femaleModel1;
    public int maleModel0;
    public String options[];
    public int xOffset2d;
    public String name;
    public int inventory_model;
    public int maleHeadModel;
    public boolean stackable;
    public int unnoted_item_id;
    public int zoom2d;
    public int maleModel1;
    public String actions[];
    public int xan2d;
    public int[] countObj;
    public int yOffset2d;//
    public int femaleHeadModel;
    public int yan2d;
    public int femaleModel0;
    public int[] countCo;
    public int team;
    public int zan2d;
    private byte femaleOffset;
    private int femaleModel2;
    private int maleHeadModel2;
    private int resizeX;
    private int femaleHeadModel2;
    public int contrast;
    private int maleModel2;
    private int resizeZ;
    private int resizeY;
    public int ambient;
    private byte maleOffset;

    private ItemDefinition() {
        id = -1;
    }

    public static void clear() {
        models = null;
        sprites = null;
        streamIndices = null;
        cache = null;
        item_data = null;
    }

    public static void init(FileArchive archive) {
        item_data = new Buffer(archive.readFile("obj.dat"));
        Buffer stream = new Buffer(archive.readFile("obj.idx"));

        totalItems = stream.readUShort();
        streamIndices = new int[totalItems];
        int offset = 2;

        for (int _ctr = 0; _ctr < totalItems; _ctr++) {
            streamIndices[_ctr] = offset;
            offset += stream.readUShort();
        }

        cache = new ItemDefinition[10];

        for (int _ctr = 0; _ctr < 10; _ctr++) {
            cache[_ctr] = new ItemDefinition();
        }

        System.out.println("Loaded: " + totalItems + " items");
    }

    public static ItemDefinition lookup(int itemId) {
        for (int count = 0; count < 10; count++)
            if (cache[count].id == itemId)
                return cache[count];

        cacheIndex = (cacheIndex + 1) % 10;
        ItemDefinition itemDef = cache[cacheIndex];
        if (itemId > 0)
            item_data.currentPosition = streamIndices[itemId];
        itemDef.id = itemId;
        itemDef.setDefaults();
        itemDef.decode(item_data);

        if (itemDef.noted_item_id != -1)
            itemDef.toNote();

        if (itemId == 4724 || itemId == 4726 || itemId == 4728 || itemId == 4730 || itemId == 4753 || itemId == 4755 || itemId == 4757 || itemId == 4759 || itemId == 4745 || itemId == 4747 || itemId == 4749 || itemId == 4751 ||
                itemId == 4708 || itemId == 4710 || itemId == 4712 || itemId == 4714 || itemId == 4732 || itemId == 4734 || itemId == 4736 || itemId == 4738 || itemId == 4716 || itemId == 4718 || itemId == 4720 || itemId == 4722) {
            itemDef.actions[2] = "Set";
        }

        /**
         * Place customs here
         */
        switch (itemId) {
            case 13302:
                itemDef.name = "Crate key";
                itemDef.colorFind = new int[]{8128};
                itemDef.colorReplace = new int[]{5231};
                break;
            case 5022:
                itemDef.name = "Spin ticket";
                itemDef.colorFind = new int[]{100};
                itemDef.colorReplace = new int[]{10562};
                //[10458,0,0],"newModelColor":[10562
                break;
            case 5509:
            case 5510:
            case 5512:
            case 5514:
                itemDef.actions = new String[5];
                itemDef.actions[0] = "Fill";
                itemDef.actions[1] = null;
                itemDef.actions[2] = "Empty";
                itemDef.actions[3] = "Check";
                break;
            case 12881:
            case 12875:
            case 12873:
            case 12883:
            case 12879:
            case 12877:
                itemDef.actions = new String[5];
                itemDef.actions[0] = "Open";
                break;

            case 8013:
                itemDef.name = "Home teleport";
                break;
            case 2542:
                itemDef.copy(lookup(1505));
                itemDef.actions = new String[5];
                itemDef.actions[0] = "Read";
                itemDef.name = "Preserve scroll";
                itemDef.stackable = false;
                break;
            case 2543:
                itemDef.copy(lookup(1505));
                itemDef.actions = new String[5];
                itemDef.actions[0] = "Read";
                itemDef.name = "Rigour scroll";
                itemDef.stackable = false;
                break;
            case 2544:
                itemDef.copy(lookup(1505));
                itemDef.actions = new String[5];
                itemDef.actions[0] = "Read";
                itemDef.name = "Augury scroll";
                itemDef.stackable = false;
                break;
            case 2545:
                itemDef.copy(lookup(12846));
                itemDef.actions = new String[5];
                itemDef.actions[0] = "Read";
                itemDef.name = "Target-teleport scroll";
                itemDef.stackable = false;
                break;
            case 12006:
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                break;
            case 12926:
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.actions[2] = "Check";
                break;
        }
        return itemDef;
    }

    public static Sprite getSprite(int itemId, int stackSize, int outlineColor) {
        if (outlineColor == 0) {
            Sprite sprite = (Sprite) sprites.get(itemId);
            if (sprite != null && sprite.maxHeight != stackSize && sprite.maxHeight != -1) {

                sprite.unlink();
                sprite = null;
            }
            if (sprite != null)
                return sprite;
        }
        ItemDefinition itemDef = lookup(itemId);
        if (itemDef.countObj == null)
            stackSize = -1;
        if (stackSize > 1) {
            int stack_item_id = -1;
            for (int j1 = 0; j1 < 10; j1++)
                if (stackSize >= itemDef.countCo[j1] && itemDef.countCo[j1] != 0)
                    stack_item_id = itemDef.countObj[j1];

            if (stack_item_id != -1)
                itemDef = lookup(stack_item_id);
        }
        Model model = itemDef.getModel(1);
        if (model == null)
            return null;
        Sprite sprite = null;
        if (itemDef.noted_item_id != -1) {
            sprite = getSprite(itemDef.unnoted_item_id, 10, -1);
            if (sprite == null)
                return null;
        }
        Sprite enabledSprite = new Sprite(32, 32);
        int centerX = Rasterizer3D.originViewX;
        int centerY = Rasterizer3D.originViewY;
        int lineOffsets[] = Rasterizer3D.scanOffsets;
        int pixels[] = Rasterizer2D.pixels;
        int width = Rasterizer2D.width;
        int height = Rasterizer2D.height;
        int vp_left = Rasterizer2D.leftX;
        int vp_right = Rasterizer2D.bottomX;
        int vp_top = Rasterizer2D.topY;
        int vp_bottom = Rasterizer2D.bottomY;
        Rasterizer3D.world = false;
        Rasterizer3D.aBoolean1464 = false;
        Rasterizer2D.initDrawingArea(32, 32, enabledSprite.myPixels);
        Rasterizer2D.drawItemBox(0, 0, 32, 32, 0);
        Rasterizer3D.useViewport();
        int k3 = itemDef.zoom2d;
        if (outlineColor == -1)
            k3 = (int) ((double) k3 * 1.5D);
        if (outlineColor > 0)
            k3 = (int) ((double) k3 * 1.04D);
        int l3 = Rasterizer3D.SINE[itemDef.xan2d] * k3 >> 16;
        int i4 = Rasterizer3D.COSINE[itemDef.xan2d] * k3 >> 16;
        Rasterizer3D.renderOnGpu = true;
        model.renderModel(itemDef.yan2d, itemDef.zan2d, itemDef.xan2d, itemDef.xOffset2d,
                l3 + model.modelBaseY / 2 + itemDef.yOffset2d, i4 + itemDef.yOffset2d);
        Rasterizer3D.renderOnGpu = false;

        enabledSprite.outline(1);
        if (outlineColor > 0) {
            enabledSprite.outline(16777215);
        }
        if (outlineColor == 0) {
            enabledSprite.shadow(3153952);
        }

        Rasterizer2D.initDrawingArea(32, 32, enabledSprite.myPixels);

        if (itemDef.noted_item_id != -1) {
            int old_w = sprite.maxWidth;
            int old_h = sprite.maxHeight;
            sprite.maxWidth = 32;
            sprite.maxHeight = 32;
            sprite.drawSprite(0, 0);
            sprite.maxWidth = old_w;
            sprite.maxHeight = old_h;
        }
        if (outlineColor == 0)
            sprites.put(enabledSprite, itemId);
        Rasterizer2D.initDrawingArea(height, width, pixels);
        Rasterizer2D.setDrawingArea(vp_bottom, vp_left, vp_right, vp_top);
        Rasterizer3D.originViewX = centerX;
        Rasterizer3D.originViewY = centerY;
        Rasterizer3D.scanOffsets = lineOffsets;
        Rasterizer3D.aBoolean1464 = true;
        Rasterizer3D.world = true;
        if (itemDef.stackable)
            enabledSprite.maxWidth = 33;
        else
            enabledSprite.maxWidth = 32;
        enabledSprite.maxHeight = stackSize;
        return enabledSprite;
    }

    public static Sprite getSprite(int itemId, int stackSize, int zoom, int outlineColor) {
        ItemDefinition itemDef = lookup(itemId);
        if (itemDef.countObj == null)
            stackSize = -1;
        if (stackSize > 1) {
            int stack_item_id = -1;
            for (int j1 = 0; j1 < 10; j1++)
                if (stackSize >= itemDef.countCo[j1] && itemDef.countCo[j1] != 0)
                    stack_item_id = itemDef.countObj[j1];

            if (stack_item_id != -1)
                itemDef = lookup(stack_item_id);
        }
        Model model = itemDef.getModel(1);
        if (model == null)
            return null;
        Sprite sprite = new Sprite(90, 90);
        int centerX = Rasterizer3D.originViewX;
        int centerY = Rasterizer3D.originViewY;
        int lineOffsets[] = Rasterizer3D.scanOffsets;
        int pixels[] = Rasterizer2D.pixels;
        int width = Rasterizer2D.width;
        int height = Rasterizer2D.height;
        int vp_left = Rasterizer2D.leftX;
        int vp_right = Rasterizer2D.bottomX;
        int vp_top = Rasterizer2D.topY;
        int vp_bottom = Rasterizer2D.bottomY;
        Rasterizer3D.world = false;
        Rasterizer3D.aBoolean1464 = false;
        Rasterizer2D.initDrawingArea(90, 90, sprite.myPixels);
        Rasterizer2D.drawItemBox(0, 0, 90, 90, 0);
        Rasterizer3D.useViewport();
        int l3 = Rasterizer3D.SINE[itemDef.xan2d] * zoom >> 15;
        int i4 = Rasterizer3D.COSINE[itemDef.xan2d] * zoom >> 15;
        Rasterizer3D.renderOnGpu = true;

        model.renderModel(itemDef.yan2d, itemDef.zan2d, itemDef.xan2d, itemDef.xOffset2d,
                l3 + model.modelBaseY / 2 + itemDef.yOffset2d, i4 + itemDef.yOffset2d);
        sprite.outline(1);
        Rasterizer3D.renderOnGpu = false;

        if (outlineColor > 0) {
            sprite.outline(16777215);
        }
        if (outlineColor == 0) {
            sprite.shadow(3153952);
        }
        Rasterizer2D.initDrawingArea(90, 90, sprite.myPixels);
        Rasterizer2D.initDrawingArea(height, width, pixels);
        Rasterizer2D.setDrawingArea(vp_bottom, vp_left, vp_right, vp_top);
        Rasterizer3D.originViewX = centerX;
        Rasterizer3D.originViewY = centerY;
        Rasterizer3D.scanOffsets = lineOffsets;
        Rasterizer3D.aBoolean1464 = true;
        Rasterizer3D.world = true;
        if (itemDef.stackable)
            sprite.maxWidth = 33;
        else
            sprite.maxWidth = 32;
        sprite.maxHeight = stackSize;
        return sprite;
    }

    public boolean isDialogueModelCached(int gender) {
        int model_1 = maleHeadModel;
        int model_2 = maleHeadModel2;
        if (gender == 1) {
            model_1 = femaleHeadModel;
            model_2 = femaleHeadModel2;
        }
        if (model_1 == -1)
            return true;
        boolean cached = true;
        if (!Model.isCached(model_1))
            cached = false;
        if (model_2 != -1 && !Model.isCached(model_2))
            cached = false;
        return cached;
    }

    public Model getChatEquipModel(int gender) {
        int dialogueModel = maleHeadModel;
        int dialogueHatModel = maleHeadModel2;
        if (gender == 1) {
            dialogueModel = femaleHeadModel;
            dialogueHatModel = femaleHeadModel2;
        }
        if (dialogueModel == -1)
            return null;
        Model dialogueModel_ = Model.getModel(dialogueModel);
        if (dialogueHatModel != -1) {
            Model hatModel_ = Model.getModel(dialogueHatModel);
            Model models[] = {dialogueModel_, hatModel_};
            dialogueModel_ = new Model(2, models);
        }
        if (colorReplace != null) {
            for (int i1 = 0; i1 < colorReplace.length; i1++)
                dialogueModel_.recolor(colorReplace[i1], colorFind[i1]);

        }
        return dialogueModel_;
    }

    public boolean isEquippedModelCached(int gender) {
        int primaryModel = maleModel0;
        int secondaryModel = maleModel1;
        int emblem = maleModel2;
        if (gender == 1) {
            primaryModel = femaleModel0;
            secondaryModel = femaleModel1;
            emblem = femaleModel2;
        }
        if (primaryModel == -1)
            return true;
        boolean cached = true;
        if (!Model.isCached(primaryModel))
            cached = false;
        if (secondaryModel != -1 && !Model.isCached(secondaryModel))
            cached = false;
        if (emblem != -1 && !Model.isCached(emblem))
            cached = false;
        return cached;
    }

    public Model getEquippedModel(int gender) {
        int primaryModel = maleModel0;
        int secondaryModel = maleModel1;
        int emblem = maleModel2;

        if (gender == 1) {
            primaryModel = femaleModel0;
            secondaryModel = femaleModel1;
            emblem = femaleModel2;
        }

        if (primaryModel == -1)
            return null;
        Model primaryModel_ = Model.getModel(primaryModel);
        if (secondaryModel != -1)
            if (emblem != -1) {
                Model secondaryModel_ = Model.getModel(secondaryModel);
                Model emblemModel = Model.getModel(emblem);
                Model models[] = {primaryModel_, secondaryModel_, emblemModel};
                primaryModel_ = new Model(3, models);
            } else {
                Model model_2 = Model.getModel(secondaryModel);
                Model models[] = {primaryModel_, model_2};
                primaryModel_ = new Model(2, models);
            }
        if (gender == 0 && maleOffset != 0)
            primaryModel_.offsetBy(0, maleOffset, 0);
        if (gender == 1 && femaleOffset != 0)
            primaryModel_.offsetBy(0, femaleOffset, 0);

        if (colorReplace != null) {
            for (int i1 = 0; i1 < colorReplace.length; i1++)
                primaryModel_.recolor(colorReplace[i1], colorFind[i1]);

        }
        return primaryModel_;
    }

    private void setDefaults() {
        inventory_model = 0;
        name = null;
        colorReplace = null;
        colorFind = null;
        textureReplace = null;
        textureFind = null;

        zoom2d = 2000;
        xan2d = 0;
        yan2d = 0;
        zan2d = 0;
        xOffset2d = 0;
        yOffset2d = 0;
        stackable = false;
        cost = 1;
        members = false;
        options = null;
        actions = null;
        maleModel0 = -1;
        maleModel1 = -1;
        maleOffset = 0;
        femaleModel0 = -1;
        femaleModel1 = -1;
        femaleOffset = 0;
        maleModel2 = -1;
        femaleModel2 = -1;
        maleHeadModel = -1;
        maleHeadModel2 = -1;
        femaleHeadModel = -1;
        femaleHeadModel2 = -1;
        countObj = null;
        countCo = null;
        unnoted_item_id = -1;
        noted_item_id = -1;
        resizeX = 128;
        resizeY = 128;
        resizeZ = 128;
        ambient = 0;
        contrast = 0;
        team = 0;
    }

    private void copy(ItemDefinition copy) {
        yan2d = copy.yan2d;
        xan2d = copy.xan2d;
        zan2d = copy.zan2d;
        resizeX = copy.resizeX;
        resizeY = copy.resizeY;
        resizeZ = copy.resizeZ;
        zoom2d = copy.zoom2d;
        xOffset2d = copy.xOffset2d;
        yOffset2d = copy.yOffset2d;
        inventory_model = copy.inventory_model;
        stackable = copy.stackable;

    }

    private void toNote() {
        ItemDefinition itemDef = lookup(noted_item_id);
        inventory_model = itemDef.inventory_model;
        zoom2d = itemDef.zoom2d;
        xan2d = itemDef.xan2d;
        yan2d = itemDef.yan2d;

        zan2d = itemDef.zan2d;
        xOffset2d = itemDef.xOffset2d;
        yOffset2d = itemDef.yOffset2d;

        ItemDefinition itemDef_1 = lookup(unnoted_item_id);
        name = itemDef_1.name;
        members = itemDef_1.members;
        cost = itemDef_1.cost;
        stackable = true;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public int getNote() {
        return 0;
    }

    @Override
    public int getLinkedNoteId() {
        return 0;
    }

    @Override
    public int getPlaceholderId() {
        return 0;
    }

    @Override
    public int getPlaceholderTemplateId() {
        return 0;
    }

    @Override
    public int getPrice() {
        return 0;
    }

    @Override
    public boolean isMembers() {
        return false;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public void setTradeable(boolean yes) {

    }

    @Override
    public int getIsStackable() {
        return 0;
    }

    @Override
    public int getMaleModel() {
        return 0;
    }

    @Override
    public String[] getInventoryActions() {
        return new String[0];
    }

    @Override
    public String[] getGroundActions() {
        return new String[0];
    }

    @Override
    public int getShiftClickActionIndex() {
        return 0;
    }

    public Model getModel(int stack_size) {
        if (countObj != null && stack_size > 1) {
            int stack_item_id = -1;
            for (int k = 0; k < 10; k++)
                if (stack_size >= countCo[k] && countCo[k] != 0)
                    stack_item_id = countObj[k];

            if (stack_item_id != -1)
                return lookup(stack_item_id).getModel(1);
        }
        Model model = (Model) models.get(id);
        if (model != null)
            return model;
        model = Model.getModel(inventory_model);
        if (model == null)
            return null;
        if (resizeX != 128 || resizeY != 128 || resizeZ != 128)
            model.scale(resizeX, resizeZ, resizeY);
        if (colorReplace != null) {
            for (int l = 0; l < colorReplace.length; l++)
                model.recolor(colorReplace[l], colorFind[l]);

        }
        int lightInt = 64 + ambient;
        int lightMag = 768 + contrast;
        model.light(lightInt, lightMag, -50, -10, -50, true);
        model.singleTile = true;
        models.put(model, id);
        return model;
    }

    @Override
    public int getInventoryModel() {
        return 0;
    }

    @Override
    public short[] getColorToReplaceWith() {
        return new short[0];
    }

    @Override
    public short[] getTextureToReplaceWith() {
        return new short[0];
    }

    @Override
    public RSIterableNodeHashTable getParams() {
        return null;
    }

    @Override
    public void setParams(IterableHashTable params) {

    }

    @Override
    public void setParams(RSIterableNodeHashTable params) {

    }

    public Model getUnshadedModel(int stack_size) {
        if (countObj != null && stack_size > 1) {
            int stack_item_id = -1;
            for (int count = 0; count < 10; count++)
                if (stack_size >= countCo[count] && countCo[count] != 0)
                    stack_item_id = countObj[count];

            if (stack_item_id != -1)
                return lookup(stack_item_id).getUnshadedModel(1);
        }
        Model model = Model.getModel(inventory_model);
        if (model == null)
            return null;
        if (colorReplace != null) {
            for (int colorPtr = 0; colorPtr < colorReplace.length; colorPtr++)
                model.recolor(colorReplace[colorPtr], colorFind[colorPtr]);

        }
        return model;
    }

    private int shiftClickIndex = -2;
    private int category;
    private boolean tradeable;
    private int bought_id;
    private int bought_template_id;
    private int placeholder_id;
    private int placeholder_template_id;
    public HashMap<Integer,Object> params;

    private void decode(Buffer buffer) {
        while (true) {
            int opcode = buffer.readUnsignedByte();
            if (opcode == 0)
                return;
            if (opcode == 1)
                inventory_model = buffer.readUShort();
            else if (opcode == 2)
                name = buffer.readJagexString();
            else if (opcode == 4)
                zoom2d = buffer.readUShort();
            else if (opcode == 5)
                xan2d = buffer.readUShort();
            else if (opcode == 6)
                yan2d = buffer.readUShort();
            else if (opcode == 7) {
                xOffset2d = buffer.readUShort();
                if (xOffset2d > 32767)
                    xOffset2d -= 0x10000;
            } else if (opcode == 8) {
                yOffset2d = buffer.readUShort();
                if (yOffset2d > 32767)
                    yOffset2d -= 0x10000;
            } else if (opcode == 11)
                stackable = true;
            else if (opcode == 12)
                cost = buffer.readInt();
            else if (opcode == 13)
                wearPos1 = buffer.readUnsignedByte();
            else if (opcode == 14)
                wearPos2 = buffer.readUnsignedByte();
            else if (opcode == 16)
                members = true;
            else if (opcode == 23) {
                maleModel0 = buffer.readUShort();
                maleOffset = buffer.readSignedByte();
            } else if (opcode == 24)
                maleModel1 = buffer.readUShort();
            else if (opcode == 25) {
                femaleModel0 = buffer.readUShort();
                femaleOffset = buffer.readSignedByte();
            } else if (opcode == 27) {
                wearPos3 = buffer.readUnsignedByte();
            } else if (opcode == 26)
                femaleModel1 = buffer.readUShort();
            else if (opcode >= 30 && opcode < 35) {
                if (options == null)
                    options = new String[5];
                options[opcode - 30] = buffer.readString();
                if (options[opcode - 30].equalsIgnoreCase("hidden"))
                    options[opcode - 30] = null;
            } else if (opcode >= 35 && opcode < 40) {
                if (actions == null)
                    actions = new String[5];
                actions[opcode - 35] = buffer.readString();
            } else if (opcode == 40) {
                int length = buffer.readUnsignedByte();
                colorReplace = new int[length];
                colorFind = new int[length];
                for (int index = 0; index < length; index++) {
                    colorReplace[index] = buffer.readUShort();
                    colorFind[index] = buffer.readUShort();
                }
            } else if (opcode == 41) {
                int length = buffer.readUnsignedByte();
                textureFind = new short[length];
                textureReplace = new short[length];
                for (int index = 0; index < length; index++) {
                    textureFind[index] = (short) buffer.readUShort();
                    textureReplace[index] = (short) buffer.readUShort();
                }
            } else if (opcode == 42) {
                shiftClickIndex = buffer.readUnsignedByte();
            } else if (opcode == 65) {
                tradeable = true;
            } else if (opcode == 75)
                weight = buffer.readUShort();
            else if (opcode == 78)
                maleModel2 = buffer.readUShort();
            else if (opcode == 79)
                femaleModel2 = buffer.readUShort();
            else if (opcode == 90)
                maleHeadModel = buffer.readUShort();
            else if (opcode == 91)
                femaleHeadModel = buffer.readUShort();
            else if (opcode == 92)
                maleHeadModel2 = buffer.readUShort();
            else if (opcode == 93)
                femaleHeadModel2 = buffer.readUShort();
            else if (opcode == 94)
                category = buffer.readUShort();
            else if (opcode == 95)
                zan2d = buffer.readUShort();
            else if (opcode == 97)
                unnoted_item_id = buffer.readUShort();
            else if (opcode == 98)
                noted_item_id = buffer.readUShort();
            else if (opcode == 110)
                resizeX = buffer.readUShort();
            else if (opcode == 111)
                resizeY = buffer.readUShort();
            else if (opcode == 112)
                resizeZ = buffer.readUShort();
            else if (opcode == 113)
                ambient = buffer.readSignedByte();
            else if (opcode == 114)
                contrast = buffer.readSignedByte() * 5;
            else if (opcode >= 100 && opcode < 110) {
                if (countObj == null) {
                    countObj = new int[10];
                    countCo = new int[10];
                }
                countObj[opcode - 100] = buffer.readUShort();
                countCo[opcode - 100] = buffer.readUShort();
            } else if (opcode == 115)
                team = buffer.readUnsignedByte();
            else if (opcode == 139)
                bought_id = buffer.readUShort();
            else if (opcode == 140)
                bought_template_id = buffer.readUShort();
            else if (opcode == 148)
                placeholder_id = buffer.readUShort();
            else if (opcode == 149) {
                placeholder_template_id = buffer.readUShort();
            } else if (opcode == 249) {
                params = BufferExt.readStringIntParameters(buffer);
            } else {
                System.err.printf("Error unrecognised {Items} opcode: %d%n%n", opcode);
            }
        }
    }

    @Override
    public int getHaPrice() {
        return 0;
    }

    @Override
    public boolean isStackable() {
        return false;
    }

    @Override
    public void setShiftClickActionIndex(int shiftClickActionIndex) {

    }

    @Override
    public void resetShiftClickActionIndex() {

    }

    @Override
    public int getIntValue(int paramID) {
        return 0;
    }

    @Override
    public void setValue(int paramID, int value) {

    }

    @Override
    public String getStringValue(int paramID) {
        return null;
    }

    @Override
    public void setValue(int paramID, String value) {

    }
}
