package com.elvarg.game.entity.impl.playerbot.interaction;

import com.elvarg.game.collision.RegionManager;
import com.elvarg.game.content.combat.CombatFactory;
import com.elvarg.game.entity.impl.playerbot.PlayerBot;
import com.elvarg.game.model.Location;
import com.elvarg.game.model.areas.impl.DuelArenaArea;
import com.elvarg.game.model.movement.MovementQueue;
import com.elvarg.game.model.teleportation.TeleportHandler;
import com.elvarg.game.model.teleportation.TeleportType;
import com.elvarg.util.Misc;

public class MovementInteraction {

    // The PlayerBot this interaction belongs to
    private PlayerBot playerBot;

    public MovementInteraction(PlayerBot _playerBot) {
        this.playerBot = _playerBot;
    }

    public void process() {
        if (!playerBot.getMovementQueue().getMobility().canMove() || playerBot.busy()) {
            return;
        }

        switch (playerBot.getCurrentState()) {
            case COMMAND:
                // Player Bot is currently busy, do nothing
                return;

            case IDLE:
                if (CombatFactory.inCombat(playerBot) || playerBot.getDueling().inDuel()) {
                    return;
                }

                this.randomWalk(Misc.random(1,6));

                if (playerBot.getArea() != null && playerBot.getArea().canPlayerBotIdle(playerBot)) {
                    break;
                }

                if (playerBot.getLocation().getDistance(playerBot.getDefinition().getSpawnLocation()) > 20) {
                    // Bot is far away, teleport back to original location
                    TeleportHandler.teleport(playerBot, playerBot.getDefinition().getSpawnLocation(), TeleportType.NORMAL, false);
                }
                break;
        }
    }

    /**
     * Moves randomly within {radius} of the PlayerBots original spawn location.
     *
     * @param radius
     */
    public void randomWalk(int radius) {
        if (this.playerBot.getMovementQueue().isMoving() || !this.playerBot.getMovementQueue().getMobility().canMove() || this.playerBot.busy()) {
            return;
        }

        if (CombatFactory.inCombat(this.playerBot) || this.playerBot.getDueling().inDuel()) {
            return;
        }

        if (Misc.getRandom(Misc.random(30,60)) > 1) {
            return;
        }

        Location destination;
        do {
            // Generate a location within radius of the original spawn location
            destination = this.playerBot.getDefinition().getSpawnLocation().clone().transform(Misc.random(-radius, radius), Misc.random(-radius, radius));
        } while (!RegionManager.canMove(this.playerBot.getLocation(), destination, this.playerBot.size(), this.playerBot.size(), this.playerBot.getPrivateArea()));

        this.playerBot.getMovementQueue().addStep(destination);
    }
}
