package com.elvarg.game.content;

import com.elvarg.game.World;
import com.elvarg.game.entity.impl.npc.impl.AnimatedArmor;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Location;
import com.elvarg.game.task.Task;
import com.elvarg.game.task.TaskManager;
import com.elvarg.util.NpcIdentifiers;

public class ArmorAnimator {

    private static final Location ARMOR_SPAWN_POS = new Location(3807, 2835);

    public static void animateArmor(Player player){
        int level = player.getSkillManager().getTotalLevel();
        int npcId;
        if(!checkAll(player))
            return;

        if(level <= 40){
            npcId = NpcIdentifiers.ANIMATED_BRONZE_ARMOUR;
        } else if(level <= 50){
            npcId = NpcIdentifiers.ANIMATED_IRON_ARMOUR;
        } else if(level <= 60){
            npcId = NpcIdentifiers.ANIMATED_BLACK_ARMOUR;
        } else if(level <= 70){
            npcId = NpcIdentifiers.ANIMATED_STEEL_ARMOUR;
        } else if(level <= 80){
            npcId = NpcIdentifiers.ANIMATED_MITHRIL_ARMOUR;
        } else if(level <= 90){
            npcId = NpcIdentifiers.ANIMATED_ADAMANT_ARMOUR;
        } else {
            npcId = NpcIdentifiers.ANIMATED_RUNE_ARMOUR;
        }

        TaskManager.submit(new Task(0, player, true) {
            @Override
            protected void execute() {
                stop();
                World.getAddNPCQueue().add(new AnimatedArmor(player, npcId, ARMOR_SPAWN_POS));
                stop();
            }
        });

    }

    public static boolean checkAll(Player player){
        if (player.busy()) {
            player.getPacketSender().sendMessage("You cannot do that right now.");
            return false;
        }
        return !player.getIsAnimated();
    }

}
