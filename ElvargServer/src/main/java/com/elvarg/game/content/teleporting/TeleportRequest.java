package com.elvarg.game.content.teleporting;

import com.elvarg.game.entity.impl.player.Player;

/**
 * @author Ynneh | 08/12/2022 - 12:34
 * <https://github.com/drhenny>
 */
public class TeleportRequest {

    public Player player;

    public Player requester;

    public long requestedAt;

    public RequestType requestType;

    public TeleportRequest(Player player, Player requester, RequestType requestType) {
        this.player = player;
        this.requester = requester;
        this.requestType = requestType;
        this.requestedAt = System.currentTimeMillis();
    }
}
