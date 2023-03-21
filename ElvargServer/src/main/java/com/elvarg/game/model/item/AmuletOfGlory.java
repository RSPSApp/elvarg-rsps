package com.elvarg.game.model.item;

import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Item;
import com.elvarg.game.model.Location;
import com.elvarg.game.model.dialogues.DialogueOption;
import com.elvarg.game.model.dialogues.builders.DynamicDialogueBuilder;
import com.elvarg.game.model.dialogues.entries.impl.OptionDialogue;
import com.elvarg.game.model.teleportation.TeleportHandler;
import com.elvarg.game.model.teleportation.TeleportType;

/**
 * @author Ynneh | 21/03/2023 - 16:37
 * <https://github.com/drhenny>
 */
public class AmuletOfGlory extends DynamicDialogueBuilder {

    private int slot;
    private boolean inventory;

    public AmuletOfGlory(int slot, boolean inventory) {
        this.slot = slot;
        this.inventory = inventory;
    }

    @Override
    public void build(Player player) {
        player.sendMessage("You rub the amulet...");//TODO add delay of 1-2ticks from message to options.
        add(new OptionDialogue(0, (option) -> handleOption(player, inventory ? player.getInventory().get(slot) : player.getEquipment().get(slot), option), "Edgeville", "Karamja", "Draynor Village", "Al Kharid", "Nowehre"));
    }

    private void handleOption(Player player, Item item, DialogueOption option) {
        Location tileDest = null;
        switch (option) {
            case FIRST_OPTION -> tileDest = new Location(3088, 3501, 0);
            case SECOND_OPTION -> tileDest = new Location(2917, 3177, 0);
            case THIRD_OPTION -> tileDest = new Location(3091, 3248, 0);
            case FOURTH_OPTION -> tileDest = new Location(3292, 3169, 0);
        }
        if (tileDest != null) {
            item.setId(getNewGloryId(item.getId()));
            if (inventory)
                player.getInventory().refreshItems();
            else
                player.getEquipment().refreshItems();
            String charges_remaining = getChargeCount(item.getId());
            String charges_message = charges_remaining == null ? "You use your amulet's last charge." : "Your amulet has " + charges_remaining + " charges left.";
            player.sendMessage("<col=8113b9>" + charges_message);
            TeleportHandler.teleport(player, tileDest, TeleportType.NORMAL, false);
        }
    }

    public static boolean isGlory(int item) {
        return item >= 1706 && item <= 1712 || item >= 11976 && item <= 11978;
    }

    private int getNewGloryId(int id) {
        if (id == 11976)
            return 1712;
        return id - 2;
    }

    private String getChargeCount(int item) {
        switch (item) {
            case 11976:
                return "five";
            case 1712:
                return "four";
            case 1710:
                return "three";
            case 1708:
                return "two";
            case 1706:
                return "one";
        }
        return null;
    }


}
