package com.elvarg.game.entity.impl.playerbot.minigame;

import com.elvarg.game.World;
import com.elvarg.game.content.minigames.impl.CastleWars;
import com.elvarg.game.entity.impl.object.GameObject;
import com.elvarg.game.entity.impl.object.MapObjects;
import com.elvarg.game.entity.impl.playerbot.PlayerBot;
import com.elvarg.game.model.Location;
import com.elvarg.game.model.container.impl.Equipment;
import com.elvarg.game.model.movement.path.PathFinder;
import com.elvarg.game.task.Task;
import com.elvarg.game.task.TaskManager;
import com.elvarg.net.packet.impl.ObjectActionPacketListener;

/**
 * @author Ynneh | 06/02/2023 - 23:16
 * <https://github.com/drhenny>
 */
public class CastlewarsBot {

    public static void create(PlayerBot bot) {
        begin(bot);
    }

    private static void begin(PlayerBot bot) {

        bot.startEvent(2, () -> {
            bot.getEquipment().set(Equipment.CAPE_SLOT, Equipment.NO_ITEM);
            bot.getEquipment().set(Equipment.HEAD_SLOT, Equipment.NO_ITEM);
            bot.getInventory().resetItems();
            bot.updateLocalPlayers();
        });

        bot.startEvent(5, () -> CastleWars.addToWaitingRoom(bot, CastleWars.Team.GUTHIX));

        bot.setCastlewarsBotState(CastlewarsBotState.LEAVING_RESPAWN_AREA);

        CastleWars.Team team = CastleWars.Team.getTeamForPlayer(bot);

        if (team == null) {
            return;
        }

        boolean sara = team == CastleWars.Team.SARADOMIN;

        Task task = new Task(-1, bot.getIndex(), true) {

            int ticks;

            @Override
            protected void execute() {
                ticks++;

                switch (bot.getCastleWarsState()) {

                    case LEAVING_RESPAWN_AREA: {
                        if (team.respawn_area_bounds.inside(bot.getLocation())) {

                            GameObject barrier = sara ? MapObjects.get(bot, 4469, new Location(2422, 3076, 0)) : MapObjects.get(bot, 4470, new Location(2377, 3131, 0));

                            if (barrier == null) {
                                return;
                            }
                            ObjectActionPacketListener.objectInteract(bot, barrier.getId(), barrier.getLocation().getX(), barrier.getLocation().getY(), 1);
                            return;
                        }
                        break;
                    }

                    case STAIRS_TO_MAIN_AREA: {
                        int height = bot.getLocation().getZ();

                        if (height == 1) {

                            GameObject barrier = sara ? MapObjects.get(bot, 4415, new Location(2419, 3080, 0)) : MapObjects.get(bot, 4415, new Location(2380, 3127, 0));

                            if (barrier == null) {
                                return;
                            }
                            ObjectActionPacketListener.objectInteract(bot, barrier.getId(), barrier.getLocation().getX(), barrier.getLocation().getY(), 1);
                        }
                        break;
                    }

                }



            }
        };
        TaskManager.submit(task);
    }
}
