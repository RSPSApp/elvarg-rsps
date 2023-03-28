package com.elvarg.game.content.itemteleports;

import com.elvarg.game.content.itemteleports.handlers.Jewellery;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Item;
import com.elvarg.game.model.Location;
import com.elvarg.game.model.container.ItemContainer;
import com.elvarg.game.model.dialogues.DialogueOption;
import com.elvarg.game.model.dialogues.builders.DynamicDialogueBuilder;
import com.elvarg.game.model.dialogues.entries.impl.OptionDialogue;
import com.elvarg.game.model.teleportation.TeleportHandler;

import java.util.ArrayList;

public class JewelleryDialogue extends DynamicDialogueBuilder {

    private int slot;

    private boolean inventory;

    private Jewellery jewellery;

    public JewelleryDialogue(int slot, boolean inventory, Jewellery jewellery) {
        this.slot = slot;
        this.jewellery = jewellery;
        this.inventory = inventory;
    }

    @Override
    public void build(Player player) {
        player.sendMessage("You rub the amulet...");
        ArrayList<String> locations = new ArrayList<>();
        jewellery.getDestinations().forEach(jewellery -> locations.add(jewellery.getLeft()));
        add(new OptionDialogue(0, (option) -> handleOption(player, slot,inventory ? player.getInventory().get(slot) : player.getEquipment().get(slot), option), locations.toArray(new String[0])));
    }

    private void handleOption(Player player, int slot, Item item, DialogueOption option) {
        int index = option.ordinal();

        Location destination = jewellery.getDestinations().get(index).getRight();

        if(TeleportHandler.checkReqs(player, destination)) {
            int nextItemId = jewellery.getCharges().getNextItem(item.getId());
            ItemContainer container = inventory ? player.getInventory() : player.getEquipment();
            if(nextItemId  == -1) {
                player.sendMessage("<col=800080>Your " + item.getDefinition().getName() + " crumbles to dust.");
                container.delete(item);
            } else {
                container.setItem(slot, new Item(nextItemId));
                container.refreshItems();
            }
            TeleportHandler.teleport(player, destination, jewellery.teleportType(),false);
            if(nextItemId != -1) {
                player.sendMessage(jewellery.getMessage(item));
            }
        }

    }


}