package com.elvarg.game.model;

import com.elvarg.game.model.container.impl.Equipment;

public enum EquipmentType {
    HOODED_CAPE(Equipment.CAPE_SLOT),//hides the player hair/head
    CAPE(Equipment.CAPE_SLOT),

    SHIELD(Equipment.SHIELD_SLOT),

    GLOVES(Equipment.HANDS_SLOT),

    BOOTS(Equipment.FEET_SLOT),

    AMULET(Equipment.AMULET_SLOT),

    RING(Equipment.RING_SLOT),

    ARROWS(Equipment.AMMUNITION_SLOT),

    COIF(Equipment.HEAD_SLOT),//hide hair
    HAT(Equipment.HEAD_SLOT),//hide nothing
    MASK(Equipment.HEAD_SLOT),//not used? based on name hide beard/show hair?
    MED_HELMET(Equipment.HEAD_SLOT),//not used? based on name hide hair/show beard? REMOVE DUE TO same as coif?
    FULL_HELMET(Equipment.HEAD_SLOT),//hide hair+beard
    //TODO, add type that just hides beard (mime etc)
    
    BODY(Equipment.BODY_SLOT),//TORSO REMOVAL
    PLATEBODY(Equipment.BODY_SLOT),

    LEGS(Equipment.LEG_SLOT),//REMOVES BOTTOM HALF OF BODY TO FEET IF ITEM HAS NO LEG DATA

    WEAPON(Equipment.WEAPON_SLOT),
    MINIGAME(Equipment.JAW_SLOT),//used by cwars, soul wars, and barbarian assault to allow capes.
    DOUBLE_HANDED(Equipment.WEAPON_SLOT),
    NONE(-1);//DEFAULT/NOTHING IN SLOT

    private final int slot;

    private EquipmentType(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }
}
