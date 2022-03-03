package com.elvarg.game.entity.impl.object;

import com.elvarg.game.collision.RegionManager;
import com.elvarg.game.definition.ObjectDefinition;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Location;
import com.elvarg.game.model.dialogues.entries.impl.OptionDialogue;
import com.elvarg.game.model.teleportation.TeleportHandler;
import com.elvarg.game.model.teleportation.TeleportType;
import com.elvarg.game.task.Task;
import com.elvarg.game.task.TaskManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;




public class LadderHandler {

    /**
     * The {@link ObjectDefinition} to get Ladder data.
     */
    private final ObjectDefinition ladderData;
    /**
     * The Location of our ladder to climb.
     */
    private final Location location;
    private Ladders ladder;


    public LadderHandler( Player player, ObjectDefinition ladderData , Location location, int clickType, Ladders... ladder) {
        this.ladderData = ladderData;
        this.location = location;
        this.ladder = ladder[0];
        start(player, clickType);
    }

    public void start(Player player, int clickType) {
        if (ladderData.interactions == null) {
            return;
        }

        String direction = (ladderData.interactions[clickType].equals("Climb-up")) ? "up" : "down";
        int currHeight = player.getLocation().getZ();
        int newHeight = (direction.equals("up")) ? (currHeight + 1) : (currHeight - 1);
        player.getMovementQueue().reset();

        if (hasRequirements(player)) {
            climbLadder(player, newHeight, direction);
        }
    }


    public void climbLadder(Player player, int newHeight, String direction) {
        TaskManager.submit(new Task(1) {
            @Override
            public void execute() {

                TeleportType teleType = (direction.equals("up")) ? TeleportType.LADDER_UP : TeleportType.LADDER_DOWN;
                Location teleportLoc;
                if(ladder == null) {
                    teleportLoc = new Location(player.getLocation().getX(),
                            player.getLocation().getY(),
                            newHeight);
                } else {
                    teleportLoc = ladder.getNewLoc();
                }
                player.getPacketSender().sendMessage("You climb " + direction + " the " + ladderData.getName() + ".");
                TeleportHandler.teleport(
                        player,
                        teleportLoc,
                        teleType, false);
                stop();
            }
        });

    }

    public void ladderDialogue(Player player) {
      //To-do
    }

    public boolean hasRequirements(Player player) {
        if ((Math.abs(player.getLocation().getX() - location.getX()) <= 1) ||
                (Math.abs(player.getLocation().getY() - location.getY()) <= 1)) {

            return true;
        }
        player.getPacketSender().sendMessage("I can't reach that!");
        return false;
    }




    /**
     * Holds data related to the ladders
     */
    public enum Ladders {
        MILL_DOWN(new Location(2208,4839,0), new Location(3789,2825,0) ),
        ZOMBIES_UP(new Location(3788,2825,0), new Location(2208,4840,0) ),
        TRAINING_UP(new Location(3812,2837,0), new Location(1760,5164,0) ),
        TRAINING_DOWN(new Location(1760,5163,0), new Location(3812,2838,0) );


        private static final Map<Location, Ladders> ladders = new HashMap<>();

        static {
            for (Ladders ladderData : Ladders.values()) {
                ladders.put(ladderData.getNewLoc(), ladderData);
                ladders.put(ladderData.getLoc(), ladderData);
            }
        }

        private final Location newLoc;
        private final Location ladderLoc;


        Ladders(Location newLoc, Location ladderLoc) {
            this.newLoc = newLoc;
            this.ladderLoc = ladderLoc;

        }

        // Put the object ID in here if the object definition does not have "Ladder as its name"
        public static int[] LADDER_IDS = { 22173 };

        public static Optional<Ladders> forObjectLoc(Location loc) {
            return Optional.ofNullable(ladders.get(loc));
        }

        public Location getLoc() {
            return ladderLoc;
        }

        public Location getNewLoc() {
            return newLoc;
        }

    }

}

