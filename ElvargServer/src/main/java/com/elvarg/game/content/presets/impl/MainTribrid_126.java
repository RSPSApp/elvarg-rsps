package com.elvarg.game.content.presets.impl;

import com.elvarg.game.content.presets.Presetable;
import com.elvarg.game.model.Item;
import com.elvarg.game.model.MagicSpellbook;

import static com.elvarg.util.ItemIdentifiers.*;

public class MainTribrid_126 {

    public static Presetable preset = new Presetable("Main Tribrid", 9,
            new Item[]{
                    new Item(AVAS_ACCUMULATOR), new Item(BLACK_DHIDE_BODY), new Item(ABYSSAL_WHIP), new Item(SARADOMIN_BREW_4_),
                    new Item(RUNE_CROSSBOW), new Item(RUNE_PLATELEGS), new Item(RUNE_DEFENDER), new Item(SARADOMIN_BREW_4_),
                    new Item(COOKED_KARAMBWAN), new Item(COOKED_KARAMBWAN), new Item(DRAGON_DAGGER_P_PLUS_PLUS_), new Item(SUPER_RESTORE_4_),
                    new Item(SHARK), new Item(SHARK), new Item(SHARK), new Item(SUPER_RESTORE_4_),
                    new Item(SHARK), new Item(SHARK), new Item(SHARK), new Item(SUPER_STRENGTH_4_),
                    new Item(SHARK), new Item(SHARK), new Item(SHARK), new Item(SUPER_ATTACK_4_),
                    new Item(WATER_RUNE, 6000), new Item(BLOOD_RUNE, 2000), new Item(DEATH_RUNE, 4000), new Item(RANGING_POTION_4_),
            },
            new Item[]{
                    new Item(HELM_OF_NEITIZNOT),
                    new Item(SARADOMIN_CAPE),
                    new Item(ANCIENT_STAFF),
                    new Item(AMULET_OF_GLORY),
                    new Item(MYSTIC_ROBE_TOP),
                    new Item(SPIRIT_SHIELD),
                    new Item(MYSTIC_ROBE_BOTTOM),
                    new Item(BARROWS_GLOVES),
                    new Item(CLIMBING_BOOTS),
                    new Item(RING_OF_RECOIL),
                    new Item(DRAGONSTONE_BOLTS_E_, 500),
            },
            /* atk, def, str, hp, range, pray, mage */
            new int[]{99, 99, 99, 99, 99, 99, 99},
            MagicSpellbook.ANCIENT,
            true
    );

}