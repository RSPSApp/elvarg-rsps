package com.elvarg.game.model.areas.impl;

import com.elvarg.game.content.combat.bountyhunter.BountyHunter;
import com.elvarg.game.entity.impl.Mobile;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Boundary;
import com.elvarg.game.model.areas.Area;

import java.util.Arrays;
import java.util.Optional;

public class ResourceArea extends Area {




    public ResourceArea() {
        super(Arrays.asList(new Boundary(3895, 3900, 2866, 2876)));
    }

    @Override
    public void enter(Mobile character) {

    }

    @Override
    public void leave(Mobile character, boolean logout) {

    }

    @Override
    public void process(Mobile character) {


    }

    @Override
    public boolean canTeleport(Player player) {
        return true;
    }

    @Override
    public boolean canAttack(Mobile attacker, Mobile target) {
//            Player a = attacker.getAsPlayer();
//            Player t = target.getAsPlayer();
//            if (!(t.getArea() instanceof WildernessArea)) {
//                a.getPacketSender()
//                        .sendMessage("That player cannot be attacked, because they are not in the Wilderness.");
//                a.getMovementQueue().reset();
//            }

        return false;
    }

    @Override
    public boolean canTrade(Player player, Player target) {
        return true;
    }

    @Override
    public boolean isMulti(Mobile character) {
        return false;
    }

    @Override
    public boolean canEat(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean canDrink(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean dropItemsOnDeath(Player player, Optional<Player> killer) {
        return true;
    }

    @Override
    public boolean handleDeath(Player player, Optional<Player> killer) {
        return false;
    }

    @Override
    public void onPlayerRightClick(Player player, Player rightClicked, int option) {
    }

    @Override
    public void defeated(Player player, Mobile character) {
        if (character.isPlayer()) {
            BountyHunter.onDeath(player, character.getAsPlayer());
        }
    }

    @Override
    public boolean overridesNpcAggressionTolerance(Player player, int npcId) {
        return true;
    }

    @Override
    public boolean handleObjectClick(Player player, int objectId, int type) {
        return false;
    }
}

