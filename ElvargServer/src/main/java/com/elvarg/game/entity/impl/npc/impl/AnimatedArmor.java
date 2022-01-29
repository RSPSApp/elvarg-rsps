package com.elvarg.game.entity.impl.npc.impl;

import com.elvarg.game.World;
import com.elvarg.game.entity.impl.npc.NPC;
import com.elvarg.game.entity.impl.npc.NPCDropGenerator;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Location;
import com.elvarg.game.model.areas.impl.FightCavesArea;

public class AnimatedArmor extends NPC {

    public AnimatedArmor(Player player, int id, Location position) {
        super(id, position);
        setOwner(player);
        setHeadIcon(2);
        player.setIsAnimated(true);
        setMobileInteraction(player);
        setFollowing(player);
    }

    @Override
    public void process(){
        Player player = this.getOwner();
        if(isOutOfArea(player) || !World.getPlayers().contains(player)){
            World.getRemoveNPCQueue().add(this);
        }
        super.process();
    }

    public boolean isOutOfArea(Player player){
        return this.getLocation().getDistance(player.getLocation()) >= 20;
    }

    @Override
    public void appendDeath() {
        Player player = this.getOwner();
        player.setIsAnimated(false);
        World.getRemoveNPCQueue().add(this);
        NPCDropGenerator.start(player, this);
    }

}
