package com.elvarg.game.content.morphing;

import com.elvarg.game.definition.NpcDefinition;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Flag;
import com.elvarg.game.model.Skill;

/**
 * @author Ynneh | 13/12/2022 - 09:57
 * <https://github.com/drhenny>
 */
public class RingOfMorphing {

    private Player player;

    private int lastDrainTick = 2;

    public RingOfMorphing(Player player) {
        this.player = player;
    }

    public void tick() {
        /**
         * Doesn't execute IF no morph
         */
        if (player.slayerMorph == null)
            return;
        /**
         * Processes formula for slayer level.
         */
        if (lastDrainTick > 0) {
            lastDrainTick--;
            if (lastDrainTick == 0) {
                /**
                 * Executes formula
                 */
                int tickDelay = 1;
                int level = player.getSkillManager().getMaxLevel(Skill.SLAYER);
                tickDelay += (level * 6) / 100;
                lastDrainTick = tickDelay;
                drain(1);
                if (level == 0) {
                    player.slayerMorph = null;
                    player.setNpcTransformationId(-1);
                    player.getUpdateFlag().flag(Flag.APPEARANCE);
                    player.getPacketSender().sendMessage("Oh no! you ran out of Slayer Points!");
                }
            }
        }
    }

    private void drain(int amount) {
        /**
         * Drains based on amount
         */
        int currentLevel = player.getSkillManager().getCurrentLevel(Skill.SLAYER);
        player.getSkillManager().setCurrentLevel(Skill.SLAYER, currentLevel - amount);
    }

    public static void transform(Player player, MorphInfo morph) {

        final int levelRequired = morph.slayerLevelRequired;

        if (player.getSkillManager().getMaxLevel(Skill.SLAYER) < levelRequired) {
            player.getPacketSender().sendMessage("You need a Slayer Level of "+levelRequired+" to Morph into this NPC.");
            return;
        }

        String npcName = morph.name().toLowerCase().replaceAll("_", " ");

        if (npcName == null)
            return;

        NpcDefinition def = NpcDefinition.forId(morph.npcId);

        if (def == null) {
            /**
             * No defs for npc..
             */
            player.getPacketSender().sendMessage("This NPC doesn't have definitions!");
            return;
        }

        if (player.getSkillManager().getCurrentLevel(Skill.SLAYER) <= 0) {
            /**
             * Slayer level as prayer points
             */
            player.getPacketSender().sendMessage("You don't have enough Slayer Points to Morph into this NPC.");
            return;
        }
        player.setSlayerMorph(morph);
    }
}
