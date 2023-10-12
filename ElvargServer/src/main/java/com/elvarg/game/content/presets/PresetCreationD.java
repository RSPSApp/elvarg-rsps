package com.elvarg.game.content.presets;

import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Item;
import com.elvarg.game.model.Skill;
import com.elvarg.game.model.dialogues.builders.DynamicDialogueBuilder;
import com.elvarg.game.model.dialogues.entries.impl.OptionDialogue;
import com.elvarg.util.Misc;

import java.util.stream.IntStream;

import static com.elvarg.game.content.presets.PresetManager.loadoutToPreset;
import static com.elvarg.game.content.presets.PresetManager.open;

/**
 * @author Ynneh | 07/09/2023 - 17:14
 * <https://github.com/drhenny>
 */
public class PresetCreationD extends DynamicDialogueBuilder {

    private Player player;

    private int index;

    public PresetCreationD(Player player, int index) {
        this.player = player;
        this.index = index;
    }

    @Override
    public void build(Player player) {
        add(new OptionDialogue(0, (option) -> {
            switch (option) {
                case FIRST_OPTION -> {
                    createPreset();
                }
                case SECOND_OPTION -> {
                    close();
                }
            }
        }, "Create Preset #"+(index + 1), "Never mind"));
    }

    private void update(String name) {
        Item[] inventory = player.getInventory().copyValidItemsArray();
        Item[] equipment = player.getEquipment().copyValidItemsArray();
        for (Item t : Misc.concat(inventory, equipment)) {
            if (t.getDefinition().isNoted()) {
                player.getPacketSender()
                        .sendMessage("You cannot create presets which contain noted items.");
                return;
            }
        }
        int[] stats = new int[7];
        for (int i = 0; i < stats.length; i++) {
            stats[i] = player.getSkillManager().getMaxLevel(Skill.values()[i]);
        }
        createPreset(index, name, stats, inventory, equipment);
    }

    public void createPreset() {
            player.setEnteredSyntaxAction((input) -> {
                input = Misc.formatText(input);

                if(!Misc.isValidName(input)) {
                    player.getPacketSender().sendMessage("Invalid name for preset. Please enter characters only.");
                    player.setCurrentPreset(null);
                    open(player);
                    return;
                }
                update(input);
            });
            player.getPacketSender().sendEnterInputPrompt("What would you like to call your preset?");
            close();
    }
    private void createPreset(int index, String name, int[] stats, Item[] inventory, Item[] equipment) {
        Presetable newPreset = new Presetable();
        newPreset.setName(name == null ? ("Preset #"+index) : name);
        newPreset.setStats(stats);
        newPreset.setInventory(inventory);
        newPreset.setEquipment(equipment);
        newPreset.setSpellbook(player.getSpellbook());
        player.setPreset(index, newPreset);
        player.getPacketSender().sendMessage("Your new preset has been added and named by default: "+newPreset.getName());
        player.getDialogueManager().reset();
        open(player, newPreset);
    }

    public void delete() {
        if (player.getPresets()[index] == null) {
            return;
        }
        player.getPresets()[index] = null;
        player.setCurrentPreset(null);
        player.getPacketSender().sendMessage("The preset has been deleted.");
        open(player);
    }

    public void close() {
        player.getPacketSender().closeDialogue();
    }
}
