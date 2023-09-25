package com.elvarg.game.content.itemteleports.handlers;

import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Item;
import com.elvarg.game.model.Location;
import com.elvarg.game.model.container.ItemContainer;
import com.elvarg.game.model.teleportation.TeleportHandler;
import com.elvarg.game.model.teleportation.TeleportType;
import com.elvarg.util.Misc;

public class Tablet extends ItemType {

    private int itemId;

    private Location destination;

    public Tablet(int itemId, Location destination) {
        this.itemId = itemId;
        this.destination = destination;
    }

    public int getItemId() {
        return itemId;
    }


    @Override
    TeleportType teleportType() {
        return TeleportType.TELE_TAB;
    }

    public void handle(Player player, int itemId, ItemContainer container) {
        if (!container.contains(itemId) || !TeleportHandler.checkReqs(player, destination)) {
            return;
        }

        player.sendMessage(getMessage(new Item(itemId)));
        container.delete(itemId, 1);
        TeleportHandler.teleport(player, destination, teleportType(),false);
    }

    @Override
    String getMessage(Item item) {
        String name = Misc.formatName(item.getDefinition().getName());
        return "You break the " + name + "...";
    }
}