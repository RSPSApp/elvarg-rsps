package com.elvarg.game.entity.impl.npc.impl;

import com.elvarg.game.entity.impl.npc.NPC;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Ids;
import com.elvarg.game.model.Location;
import com.elvarg.game.model.dialogues.builders.impl.BankerDialogue;
import com.elvarg.game.entity.impl.npc.NPCInteraction;

import static com.elvarg.util.NpcIdentifiers.*;

@Ids({BANKER, BANKER_2, BANKER_3, BANKER_4, BANKER_5, BANKER_6, BANKER_7, BANKER_8, BANKER_9, BANKER_10, BANKER_11,
    BANKER_12, BANKER_13, BANKER_14, BANKER_15, BANKER_16, BANKER_17, BANKER_18, BANKER_19, BANKER_20, BANKER_21,
    BANKER_22, BANKER_23, BANKER_24, BANKER_25, BANKER_26, BANKER_27, BANKER_28, BANKER_29, BANKER_30, BANKER_31,
    BANKER_32, BANKER_33, BANKER_34, BANKER_35, BANKER_36, BANKER_37, BANKER_38, BANKER_39, BANKER_40, BANKER_41,
    BANKER_42, BANKER_43, BANKER_44, BANKER_45, BANKER_46, BANKER_47, BANKER_48, BANKER_49, BANKER_50, BANKER_51,
    BANKER_52, BANKER_53, BANKER_54, BANKER_55, BANKER_56, BANKER_57, BANKER_58, BANKER_59, BANKER_60, BANKER_61,
    BANKER_62, BANKER_63, BANKER_64, BANKER_65, BANKER_66, BANKER_67, BANKER_68, BANKER_69})
public class Banker extends NPC implements NPCInteraction {

    /**
     * Constructs a Banker.
     *
     * @param id       The npc id.
     * @param position
     */
    public Banker(int id, Location position) {
        super(id, position);
    }

    @Override
    public int getWalkRadius() {
        return 0;
    }

    @Override
    public void firstOptionClick(Player player, NPC npc) {
        player.getDialogueManager().start(new BankerDialogue());
    }

    @Override
    public void secondOptionClick(Player player, NPC npc) {
        player.getBank(player.getCurrentBankTab()).open();
    }

    @Override
    public void thirdOptionClick(Player player, NPC npc) {

    }

    @Override
    public void forthOptionClick(Player player, NPC npc) {

    }

    @Override
    public void useItemOnNpc(Player player, NPC npc, int itemId, int slot) {

    }

}
