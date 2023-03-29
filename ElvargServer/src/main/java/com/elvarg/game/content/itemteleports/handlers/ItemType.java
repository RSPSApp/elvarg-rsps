package com.elvarg.game.content.itemteleports.handlers;

import com.elvarg.game.model.Item;
import com.elvarg.game.model.teleportation.TeleportType;

abstract class ItemType {
    abstract TeleportType teleportType();
    abstract String getMessage(Item item);
}
