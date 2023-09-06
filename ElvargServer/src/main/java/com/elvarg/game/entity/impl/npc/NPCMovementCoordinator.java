package com.elvarg.game.entity.impl.npc;

import com.elvarg.game.collision.RegionManager;
import com.elvarg.game.content.combat.CombatFactory;
import com.elvarg.game.model.Location;
import com.elvarg.game.model.movement.path.PathFinder;
import com.elvarg.util.Misc;

/**
 * Will make all {@link NPC}s set to coordinate, pseudo-randomly move within a
 * specified radius of their original position.
 *
 * @author lare96
 */
public class NPCMovementCoordinator {

    /**
     * The npc we are coordinating movement for.
     */
    private NPC npc;

    /**
     * The coordinate state this npc is in.
     */
    private CoordinateState coordinateState;

    public NPCMovementCoordinator(NPC npc) {
        this.npc = npc;
        this.coordinateState = CoordinateState.HOME;
    }

    public void process() {

        // If walk radius is 0, that means the npc shouldn't walk around.
        // HOWEVER: Only if npc is home. Because the npc might be retreating
        // from a fight.
        if (npc.getWalkRadius() == 0) {
            if (coordinateState == CoordinateState.HOME) {
                return;
            }
        }
        
        if (!npc.getMovementQueue().getMobility().canMove()) {
        	return;
        }

        updateCoordinator();

        switch (coordinateState) {
            case HOME:

                if (CombatFactory.inCombat(npc)) {
                    return;
                }

                if (npc.getInteractingMobile() != null) {
                    return;
                }

                if (!npc.getMovementQueue().isMoving()) {
                    if (Misc.getRandom(9) <= 1) {
                        Location pos = generateLocalPosition();
                        if (pos != null) {
                            npc.getMovementQueue().walkStep(pos.getX(), pos.getY());
                        }
                    }
                }

                break;
            case RETREATING:
            case AWAY:
                PathFinder.calculateWalkRoute(npc, npc.getSpawnPosition().getX(), npc.getSpawnPosition().getY());
                break;
        }
    }

    public void updateCoordinator() {

        /**
         * Handle retreating from combat.
         */

        if (CombatFactory.inCombat(npc)) {
            if (coordinateState == CoordinateState.AWAY) {
                coordinateState = CoordinateState.RETREATING;
            }
            if (coordinateState == CoordinateState.RETREATING) {
                if (npc.getLocation().equals(npc.getSpawnPosition())) {
                    coordinateState = CoordinateState.HOME;
                }
                npc.getCombat().reset();
            }
            return;
        }

        int deltaX;
        int deltaY;

        if (npc.getSpawnPosition().getX() > npc.getLocation().getX()) {
            deltaX = npc.getSpawnPosition().getX() - npc.getLocation().getX();
        } else {
            deltaX = npc.getLocation().getX() - npc.getSpawnPosition().getX();
        }

        if (npc.getSpawnPosition().getY() > npc.getLocation().getY()) {
            deltaY = npc.getSpawnPosition().getY() - npc.getLocation().getY();
        } else {
            deltaY = npc.getLocation().getY() - npc.getSpawnPosition().getY();
        }

        if ((deltaX > npc.getWalkRadius()) || (deltaY > npc.getWalkRadius())) {
            coordinateState = CoordinateState.AWAY;
        } else {
            coordinateState = CoordinateState.HOME;
        }
    }

    private Location generateLocalPosition() {
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        int random = Misc.getRandom(3);
        int x = directions[random][0];
        int y = directions[random][1];
        int walkRadius = npc.getWalkRadius(); // Define walk radius here
        for (int i = 0; i < directions.length; i++) {
            if (!RegionManager.blocked(npc.getLocation().translate(directions[i][0], directions[i][1]), npc.getPrivateArea())) {
                x = directions[i][0];
                y = directions[i][1];
                break;
            }
        }
        if (x == 0 && y == 0) {
            return null;
        }
        int spawnX = npc.getSpawnPosition().getX();
        int spawnY = npc.getSpawnPosition().getY();
        if (x == 1 && npc.getLocation().getX() + x > spawnX + walkRadius || x == -1 && npc.getLocation().getX() - x < spawnX - walkRadius || y == 1 && npc.getLocation().getY() + y > spawnY + walkRadius || y == -1 && npc.getLocation().getY() - y < spawnY - walkRadius) {
            return null;
        }
        return new Location(x, y);
    }


    public CoordinateState getCoordinateState() {
        return coordinateState;
    }

    public void setCoordinateState(CoordinateState coordinateState) {
        this.coordinateState = coordinateState;
    }

    public enum CoordinateState {
        HOME,
        AWAY,
        RETREATING;
    }
}