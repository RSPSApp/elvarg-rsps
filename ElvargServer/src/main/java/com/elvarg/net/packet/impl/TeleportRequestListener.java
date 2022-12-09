package com.elvarg.net.packet.impl;

import com.elvarg.game.World;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.PlayerRelations;
import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketExecutor;
import com.elvarg.util.Misc;

import java.util.List;
import java.util.Optional;

/**
 * @author Ynneh | 08/12/2022 - 11:16
 * <https://github.com/drhenny>
 */
public class TeleportRequestListener implements PacketExecutor {

    @Override
    public void execute(Player player, Packet packet) {

        Long usernameHash = packet.readLong();

        if (usernameHash == null)
            return;

        String username = Misc.longToString(usernameHash);

        if (username == null)
            return;

        Optional<Player> searchedPlayer = World.getPlayers().stream().filter(p -> p != null).filter(p -> p.getUsername().equalsIgnoreCase(username)).findFirst();

        if (searchedPlayer == null) {
            return;
        }

        Player friend = searchedPlayer.get();

        if (friend == null) {
            return;
        }

        boolean teleportToRequest = packet.readByte() == 1;

        if (friend.getRelations().getStatus().equals(PlayerRelations.PrivateChatStatus.OFF)) {
            /** Should only appear for the requester a.k.a player **/
            return;
        }
        player.teleportRequestManager.request(player, friend, teleportToRequest);
    }
}