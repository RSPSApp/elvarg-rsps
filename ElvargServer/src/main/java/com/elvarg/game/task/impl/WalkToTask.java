package com.elvarg.game.task.impl;

import com.elvarg.game.collision.RegionManager;
import com.elvarg.game.entity.Entity;
import com.elvarg.game.entity.impl.Mobile;
import com.elvarg.game.entity.impl.grounditem.ItemOnGround;
import com.elvarg.game.entity.impl.npc.NPC;
import com.elvarg.game.entity.impl.object.GameObject;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Location;
import com.elvarg.game.model.movement.MovementQueue;
import com.elvarg.game.model.movement.path.PathFinder;
import com.elvarg.game.task.Task;
import com.elvarg.game.task.TaskManager;

public class WalkToTask extends Task {

    // The MovementQueue this task is related to
    private MovementQueue movement;

    // The player which this task belongs to
    private Player player;

    // The entity we are walking to
    private Entity entity;

    // The runnable which will execute after pathing completes
    private Runnable action;

    // The initial position of the entity
    private int destX, destY;

    private Location position;

    // The destination calculated by the PathFinder
    private int finalDestinationX, finalDestinationY;

    // Whether the player has reached their destination
    private boolean reachedDestination = false;

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
        this.destination(entity);
        // Always reset the movement queue when the player intents to walk towards an entity
        movement.reset();
        if (player.getLocation().getDistance(position) == 0 && action != null) {
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
        if (!movement.checkDestination(position)) {
            // Destination is not valid
            return;
        }
        movement.walkToReset();
        calculateWalkRoute(entity);
        this.finalDestinationX = movement.pathX;
        this.finalDestinationY = movement.pathY;
    }

    private void destination(Entity entity) {
        if (entity instanceof GameObject) {
            GameObject obj = (GameObject) entity;
            position = obj.getLocation();
        }
        if (entity instanceof ItemOnGround) {
            ItemOnGround obj = (ItemOnGround) entity;
            position = obj.getLocation();
        }
        if (entity instanceof Mobile) {
            position = entity.getLocation();
        }
        this.destX = position.getX();
        this.destY = position.getY();
    }

    @Override
    protected void execute() {
        player.setPositionToFace(position);
        if (entity instanceof Entity) {
            player.setMobileInteraction((Mobile) entity);
        }
        /** Required LOS for interaction purposes **/
        if (RegionManager.hasLineOfSight(player.getLocation(), new Location(destX, destY, player.getLocation().getZ()), 1, 1)) {
            if (reachedDestination || withinInteractionDistance()) {
                // Executes the runnable and stops the task. Player will still path to the destination.
                if (action != null) {
                    action.run();
                }
                stop();
                return;
            }
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
    private void calculateWalkRoute(Object entity) {
        if (entity instanceof Entity) {
            PathFinder.calculateEntityRoute(player, destX, destY, 1, true);
        } else if (entity instanceof GameObject) {
            PathFinder.calculateObjectRoute(player, (GameObject) entity);
        } else if (entity instanceof ItemOnGround) {
            PathFinder.calculateWalkRoute(player, destX, destY);
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
            return player.getLocation().getDistance(position) <= 0;
        }
        if (entity instanceof NPC || entity instanceof Player) {
            return movement.hasRoute() && movement.points().size() <= MovementQueue.NPC_INTERACT_RADIUS && player.getLocation().getDistance(position) <= MovementQueue.NPC_INTERACT_RADIUS;
        }
        return false;
    }

    private void checkForMovement() {
        if (!(entity instanceof Entity)) {
            return;
        }
        if (destX != position.getX() || destY != position.getY()) {
            // Entity has moved, update the entity path to the updated position
            Entity follow = (Entity) entity;
            destX = follow.getLocation().getX();
            destY = follow.getLocation().getY();
            calculateWalkRoute(entity);
        }
    }
}
