package com.elvarg.game.model.container.shop.currency.impl;

import static com.elvarg.util.ItemIdentifiers.*;

public class BloodMoneyCurrency extends ItemCurrency {

    /**
     * Shop currency for Blood Money.
     */
    public BloodMoneyCurrency() {
        super(BLOOD_MONEY);
    }
    
    public static int getBloodMoneyValue(int itemId) {
        switch (itemId) {
        case FIRE_CAPE:
            return 4500;
        case AMULET_OF_FURY:
            return 2600;
        case TELEPORT_TO_HOUSE:
            return 200;
        case VOID_KNIGHT_TOP:
            return 5000;
        case VOID_KNIGHT_ROBE:
            return 3000;
        case VOID_KNIGHT_GLOVES:
            return 2000;
        case FIGHTER_TORSO:
            return 6000;
        case BERSERKER_NECKLACE:
            return 1200;
        case VOID_MAGE_HELM:
            return 2000;
        case VOID_RANGER_HELM:
            return 2000;
        case VOID_MELEE_HELM:
            return 2000;
        case SEERS_RING_I_:
            return 6000;
        case ARCHERS_RING_I_:
            return 5000;
        case BERSERKER_RING_I_:
            return 14000;
        case ARMADYL_CROSSBOW:
            return 15000;
        case STAFF_OF_THE_DEAD:
            return 15000;
        case ARMADYL_GODSWORD:
            return 20000;
        case BANDOS_GODSWORD:
            return 8000;
        case SARADOMIN_GODSWORD:
            return 9000;
        case ZAMORAK_GODSWORD:
            return 4000;
        case ARMADYL_HELMET:
            return 8000;
        case ARMADYL_CHESTPLATE:
            return 8000;
        case ARMADYL_CHAINSKIRT:
            return 7000;
        case BANDOS_CHESTPLATE:
            return 12000;
        case BANDOS_TASSETS:
            return 11000;
        case BANDOS_BOOTS:
            return 1000;
        case SARADOMIN_SWORD:
            return 6000;
        case SUPER_COMBAT_POTION_4_:
            return 200;
        case ELYSIAN_SPIRIT_SHIELD:
            return 34000;
        case SPECTRAL_SPIRIT_SHIELD:
            return 6000;
        case ARCANE_SPIRIT_SHIELD:
            return 10000;
        case DRAGON_DEFENDER:
            return 4000;
        case ABYSSAL_BLUDGEON:
            return 15000;
        case ABYSSAL_DAGGER_P_PLUS_PLUS_:
            return 22000;
        case DRAGON_WARHAMMER:
            return 12000;
        case DRAGON_CLAWS:
            return 25000;
        case LIGHT_BALLISTA:
            return 500;
        case HEAVY_BALLISTA:
            return 10000;
        case AMULET_OF_TORTURE:
            return 12200;
        case ELDER_MAUL_3:
            return 20000;
        case INFERNAL_CAPE:
            return 18500;
        case AVERNIC_DEFENDER:
            return 13500;
        case GHRAZI_RAPIER:
            return 35000;
        case AVERNIC_DEFENDER_HILT:
            return 10000;
        }
        return 0;
    }
}
