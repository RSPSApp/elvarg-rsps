package com.elvarg.game.model.areas.impl;
import com.elvarg.game.entity.impl.Mobile;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Boundary;
import com.elvarg.game.model.ForceMovement;
import com.elvarg.game.model.Location;
import com.elvarg.game.model.areas.Area;
import com.elvarg.game.task.TaskManager;
import com.elvarg.game.task.impl.ForceMovementTask;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class CombatRingArea extends Area {

    public static final Boundary BOUNDARY = new Boundary(3793, 3799, 2841, 2847);

    public CombatRingArea() {
        super(Arrays.asList(BOUNDARY));
    }

    public static void handleRingFirstClick(Player player) {
        int destX = 0, destY = 0, direction = 0;

        if (player.getArea() instanceof CombatRingArea) {

            // Player is standing inside the Combat Ring

            if (player.getCombat().getAttacker() != null) {
                player.getPacketSender()
                        .sendMessage("You cannot leave the Combat Ring while in combat, pussy!");
                return;
            }

            if (player.getLocation().getX() == CombatRingArea.BOUNDARY.getX()) {
                destX = -1;
                direction = 3;
            } else if (player.getLocation().getX() == CombatRingArea.BOUNDARY.getX2()) {
                destX = 1;
                direction = 1;
            }

            if (player.getLocation().getY() == CombatRingArea.BOUNDARY.getY()) {
                destY = -1;
                direction = 2;
            } else if (player.getLocation().getY() == CombatRingArea.BOUNDARY.getY2()) {
                destY = 1;
                direction = 0;
            }

        } else {

            // Player is standing outside the combat ring

            if (player.getLocation().getX() == (CombatRingArea.BOUNDARY.getX() - 1)) {
                destX = 1;
                direction = 1;
            } else if (player.getLocation().getX() == (CombatRingArea.BOUNDARY.getX2() + 1)) {
                destX = -1;
                direction = 3;
            }

            if (player.getLocation().getY() == (CombatRingArea.BOUNDARY.getY() - 1)) {
                destY = 1;
                direction = 0;
            } else if (player.getLocation().getY() == (CombatRingArea.BOUNDARY.getY2() + 1)) {
                destY = -1;
                direction = 2;
            }

        }

        player.getMovementQueue().reset();
        if (player.getForceMovement() == null && player.getClickDelay().elapsed(2000)) {
            final Location jumpOverRopes = new Location(destX, destY);
            TaskManager.submit(new ForceMovementTask(player, 3,
                    new ForceMovement(player.getLocation().clone(), jumpOverRopes, 0, 70, direction, 6132)));
            player.getClickDelay().reset();
        }
    }

    @Override
    public void enter(Mobile character) {
        character.getAsPlayer().getPacketSender()
                .sendMessage("You enter the combat ring.");

        // TODO: If there's no players in the combat ring, spawn a PVP bot
    }

    @Override
    public void leave(Mobile character, boolean logout) {
        character.getAsPlayer().getPacketSender()
                .sendMessage("You leave the combat ring.");
    }

    @Override
    public void process(Mobile character) {

    }

    @Override
    public boolean canTeleport(Player player) {
        return false;
    }

    @Override
    public boolean canAttack(Mobile attacker, Mobile target) {
        // TODO: Return true unless the player is choosing a preset
        return true;
    }

    @Override
    public void defeated(Player player, Mobile character) {
        // Give player some reward for kill
    }

    @Override
    public boolean canTrade(Player player, Player target) {
        return false;
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
        return false;
    }

    @Override
    public boolean handleDeath(Player player, Optional<Player> killer) {
        return false;
    }

    @Override
    public void onPlayerRightClick(Player player, Player rightClicked, int option) {

    }

    public boolean useTemporarySkills() {
        return true;
    }

    @Override
    public boolean handleObjectClick(Player player, int objectId, int type) {
        return false;
    }

    @Override
    public boolean overridesNpcAggressionTolerance(Player player, int npcId) {
        return false;
    }
}
