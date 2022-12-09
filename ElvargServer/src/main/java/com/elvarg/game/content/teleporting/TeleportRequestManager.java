package com.elvarg.game.content.teleporting;

import com.elvarg.game.World;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.dialogues.builders.impl.BankerDialogue;
import com.elvarg.game.model.teleportation.TeleportHandler;
import io.netty.util.internal.ConcurrentSet;

import java.util.Optional;
import java.util.Set;

/**
 * @author Ynneh | 08/12/2022 - 12:37
 * <https://github.com/drhenny>
 */
public class TeleportRequestManager {

    public Set<TeleportRequest> requests = new ConcurrentSet<>();

    private Player player;

    public TeleportRequestManager(Player player) {
        this.player = player;
    }

    public void onAccept(Player player, Player requester, RequestType requestType) {

        if (requester == null) {
            return;
        }

        Optional<TeleportRequest> request = requests.stream().filter(r -> r.requestType.equals(requestType)).findAny();

        if (request == null || !request.isPresent()) {
            player.getPacketSender().sendMessage("This teleport request has expired.");
            return;
        }

        TeleportRequest req = request.get();

        if (req == null) {
            return;
        }
        if (player.getWildernessLevel() > 0) {
            requester.getDialogueManager().start(new TeleportWarningDialogue(player, requestType, req));
            return;
        }
        if (requestType == RequestType.TELE_TO_ME) {
            TeleportHandler.teleport(player, requester.getLocation().clone(), player.getSpellbook().getTeleportType(), false);
        } else {
            TeleportHandler.teleport(requester, player.getLocation().clone(), requester.getSpellbook().getTeleportType(), false);
        }
        requests.remove(req);
    }


    public void request(Player player, Player friend, boolean teleportToRequest) {

        if (friend == null)
            return;

        TeleportRequest request = new TeleportRequest(player, friend, teleportToRequest ? RequestType.TELE_TO : RequestType.TELE_TO_ME);

        if (friend.teleportRequestManager.requests.contains(request)) {
            player.getPacketSender().sendMessage("You already have a "+(teleportToRequest ? "tele to" : "tele to me")+" request pending for "+friend.getUsername());
            return;
        }
        friend.teleportRequestManager.requests.add(request);
        friend.getPacketSender().sendMessage(player.getUsername()+ (teleportToRequest ? ":teletoreq:" : ":teletomereq:"));
        player.getPacketSender().sendMessage("<col=0000ff>Sent teleport request to "+friend.getUsername()+"...");
    }

    public void remove(String name, RequestType type) {
        /**
         * Called when trying to accept requests from OFFLINE PLAYERS!
         */
        requests.stream().filter(n -> n.player.getUsername().equalsIgnoreCase(name)).filter(t -> t.requestType.equals(type)).forEach(t -> requests.remove(t));
    }

    public void afterDialogue(Player player, Player requester, RequestType requestType, TeleportRequest req) {
        if (requestType == RequestType.TELE_TO_ME) {
            TeleportHandler.teleport(requester, player.getLocation().clone(), requester.getSpellbook().getTeleportType(), false);
        } else {
            TeleportHandler.teleport(player, requester.getLocation().clone(), player.getSpellbook().getTeleportType(), false);
        }
        requests.remove(req);
    }
}

