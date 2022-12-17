package com.elvarg.game.content.morphing;

import com.elvarg.game.content.skill.slayer.SlayerTask;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Flag;
import com.elvarg.game.model.UpdateFlag;
import com.elvarg.game.model.dialogues.builders.DynamicDialogueBuilder;
import com.elvarg.game.model.dialogues.entries.impl.NpcDialogue;
import com.elvarg.game.model.dialogues.entries.impl.OptionDialogue;
import com.elvarg.util.NpcIdentifiers;

/**
 * @author Ynneh | 13/12/2022 - 10:02
 * <https://github.com/drhenny>
 */
public class MorphSelection extends DynamicDialogueBuilder {

    @Override
    public void build(Player player) {
        add(new OptionDialogue(0, (option) -> {
            player.getPacketSender().sendInterfaceRemoval();
            switch (option) {
                case FIRST_OPTION:
                    RingOfMorphing.transform(player, MorphInfo.GHOSTS);
                    break;
                case SECOND_OPTION:
                    RingOfMorphing.transform(player, MorphInfo.HILL_GIANT);
                    break;
                case THIRD_OPTION:
                    if (player.slayerMorph == null)
                        return;
                    if (player.getCombat().getTarget() != null) {
                        player.getPacketSender().sendMessage("You cannot unmorph while in combat!");
                        return;
                    }
                    player.slayerMorph = null;
                    player.setNpcTransformationId(-1);
                    player.getUpdateFlag().flag(Flag.APPEARANCE);
                    break;
                default:
                    player.getPacketSender().sendInterfaceRemoval();
                    break;
            }
        }, "Ghost", "Hill Giant", "Un-morph", "Never mind.."));
    }
}