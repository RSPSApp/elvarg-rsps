package com.elvarg.game.task.impl;

import com.elvarg.game.entity.Entity;
import com.elvarg.game.entity.impl.Mobile;
import com.elvarg.game.entity.impl.grounditem.ItemOnGround;
import com.elvarg.game.entity.impl.npc.NPC;
import com.elvarg.game.entity.impl.object.GameObject;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.movement.MovementQueue;
import com.elvarg.game.model.movement.path.PathFinder;
import com.elvarg.game.task.Task;
import com.elvarg.game.task.TaskManager;

import static com.elvarg.game.model.movement.MovementQueue.NPC_INTERACT_RADIUS;

public class WalkToTask extends Task {

    // The MovementQueue this task is related to
    MovementQueue movement;

    // The player which this task belongs to
    Player player;

    // The entity we are walking to
    Entity entity;

    // The runnable which will execute after pathing completes
    Runnable action;

    // The initial location of the entity
    int destX, destY;

    // The destination calculated by the PathFinder
    int finalDestinationX, finalDestinationY;

    // Whether the player has reached their destination
    boolean reachedDestination = false;

    public static void submit(Player player, Entity entity, Runnable action) {
        resetMovement(player);
        TaskManager.submit(new WalkToTask(player, entity, action));
    }

    public static void resetMovement(Player player) {
        player.setMobileInteraction(null);
        player.getMovementQueue().reset();
        TaskManager.cancelTasks(player.getIndex());
    }

    public WalkToTask(Player player, Entity entity, Runnable action) {
        super(0, player.getIndex(), true);
        this.player = player;
        this.movement = player.getMovementQueue();
        this.entity = entity;
        this.action = action;
        this.destX = entity.getLocation().getX();
        this.destY = entity.getLocation().getY();
        // Always reset the movement queue when the player intents to walk towards an entity
        movement.reset();
        if (player.getLocation().getDistance(entity.getLocation()) == 0 && action != null) {
            // If player is already standing on top of the entity, run the action now
            action.run();
            return;
        }
        MovementQueue.Mobility mobility = movement.getMobility();
        if (!mobility.canMove()) {
            // Player can not currently move
            mobility.sendMessage(movement.getPlayer());
            return;
        }
        if (!movement.checkDestination(entity.getLocation())) {
            // Destination is not valid
            return;
        }
        movement.walkToReset();
        calculateWalkRoute(entity);
        this.finalDestinationX = movement.pathX;
        this.finalDestinationY = movement.pathY;
    }

    @Override
    protected void execute() {
        player.setPositionToFace(entity.getLocation());
        if (entity instanceof Mobile) {
            player.setMobileInteraction((Mobile) entity);
        }
        if (reachedDestination || withinInteractionDistance()) {
            // Executes the runnable and stops the task. Player will still path to the destination.
            if (action != null) {
                action.run();
            }
            stop();
            return;
        }
        checkForMovement();
        // If the target has moved, update the movement queue
        if (!inRange()) {
            return;
        }
    }

    private boolean inRange() {
        boolean hasRoute = player.getMovementQueue().hasRoute();
        if (entity instanceof GameObject) {
            if (!hasRoute || player.getLocation().getX() != finalDestinationX || player.getLocation().getY() != finalDestinationY) {
                return false;
            }
            reachedDestination = true;
            return true;
        }
        return false;
    }

    /**
     * Invokes the PathFinder to calculate the best route to the entity.
     *
     * @param entity The entity to walk to
     */
    private void calculateWalkRoute(Entity entity) {
        if (entity instanceof Mobile /* Players and NPCs */) {
            findRoute();
        } else if (entity instanceof GameObject) {
            PathFinder.calculateObjectRoute(player, (GameObject) entity);
        } else if (entity instanceof ItemOnGround) {
            PathFinder.calculateWalkRoute(player, destX, destY);
        }
    }

    private void findRoute() {
        if (PathFinder.calculateEntityRoute(player, destX, destY) == -1) {
            int size = entity.size();
            if (entity instanceof NPC) {
                NPC npc = (NPC) entity;
                /** Ignores clipping of bank booths ect **/
                if (npc.ignoreClipping()) {
                    PathFinder.findWalkableRoute(player, destX, destY, size == 0 ? 1 : size);
                }
            }
        }
    }

    /**
     * Determines whether the player is within interaction distance of the entity.
     *
     * @return
     */
    private boolean withinInteractionDistance() {
        if (entity instanceof ItemOnGround) {
            /** On Top Of **/
            return player.getLocation().getDistance(entity.getLocation()) <= 0;
        }
        if (entity instanceof NPC || entity instanceof Player) {
            return movement.points().size() <= NPC_INTERACT_RADIUS && player.getLocation().getDistance(entity.getLocation()) <= NPC_INTERACT_RADIUS;
        }
        return false;
    }

    private void checkForMovement() {
        if (!(entity instanceof Mobile)) {
            return;
        }
        if (destX != entity.getLocation().getX() || destY != entity.getLocation().getY()) {
            // Entity has moved, update the entity path to the updated location
            destX = entity.getLocation().getX();
            destY = entity.getLocation().getY();
            calculateWalkRoute(entity);
        }
    }
}
