package com.elvarg.net.packet.impl;

import com.elvarg.game.World;
import com.elvarg.game.content.teleporting.RequestType;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketExecutor;
import com.elvarg.util.Misc;

import java.util.Optional;

/**
 * @author Ynneh | 09/12/2022 - 11:02
 * <https://github.com/drhenny>
 */
public class TeleportAcceptListener implements PacketExecutor {

    @Override
    public void execute(Player player, Packet packet) {
        int typeIndex = packet.readByte();

        if (typeIndex > RequestType.values().length)
            return;

        RequestType type = RequestType.values()[typeIndex - 1];

        if (type == null) {
            return;
        }

        Long nameHash = packet.readLong();

        if (nameHash == null) {
            System.err.println("invalid namehash..");
            return;
        }

        String name = Misc.longToString(nameHash);

        if (name == null)
            return;

        Optional<Player> p = World.getPlayers().stream().filter(n -> n != null).filter(n -> n.getUsername().equalsIgnoreCase(name)).findFirst();

        if (p == null || !p.isPresent()) {
            player.teleportRequestManager.remove(name, type);
            return;
        }

        Player requester = p.get();

        if (requester == null)
            return;

        player.teleportRequestManager.onAccept(player, requester, type);

    }
}
