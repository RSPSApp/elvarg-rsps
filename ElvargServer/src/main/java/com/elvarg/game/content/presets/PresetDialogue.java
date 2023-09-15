package com.elvarg.game.content.presets;

import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Item;
import com.elvarg.game.model.Skill;
import com.elvarg.game.model.dialogues.builders.DynamicDialogueBuilder;
import com.elvarg.game.model.dialogues.entries.impl.OptionDialogue;
import com.elvarg.util.Misc;

import java.util.stream.IntStream;

import static com.elvarg.game.content.presets.PresetManager.*;

/**
 * @author Ynneh | 07/09/2023 - 13:21
 * <https://github.com/drhenny>
 */
public class PresetDialogue extends DynamicDialogueBuilder {

    private Player player;

    private int index;

    private Presetable editingPreset;

    public PresetDialogue(Player player, int index) {
        this.player = player;
        this.index = index;
        this.editingPreset = player.getPresets()[index];
    }

    @Override
    public void build(Player player) {
        add(new OptionDialogue(0, (option) -> {
            switch (option) {
                case FIRST_OPTION -> {
                    changeName();
                }
                case SECOND_OPTION -> {
                    update();
                }
                case THIRD_OPTION -> {
                    delete();
                }
                case FOURTH_OPTION -> {
                    close();
                }
            }
        }, "Change Preset Name", "Update Equipment / Inventory", "Delete Preset ("+editingPreset.getName()+") ?", "Never mind"));
        player.tickAction(1, () -> open(player));
    }

    private void changeName() {
        Presetable currentPreset = player.getCurrentPreset();
        if (currentPreset != null && currentPreset.isGlobal()) {
            player.getPacketSender().sendMessage("You can't edit this preset!");
            return;
        }

        player.setEnteredSyntaxAction((input) -> {
            input = Misc.formatText(input);

            if(!Misc.isValidName(input)) {
                player.getPacketSender().sendMessage("Invalid name for preset. Please enter characters only.");
                player.setCurrentPreset(null);
                open(player);
                return;
            }

            int changeIndex = IntStream.range(0, player.getPresets().length)
                    .filter(i -> player.getPresets()[i] == player.getCurrentPreset())
                    .findFirst()
                    .orElse(-1);

            if (changeIndex == -1) {
                player.getPacketSender().sendMessage("You don't have free space left!");
                return;
            }
            Presetable preset = player.getPresets()[changeIndex];
            preset.setName(input);
            player.getPresets()[changeIndex] = preset;
            refreshCustomPresetNames(player);
        });
        player.getPacketSender().sendEnterInputPrompt("What would you like to call your preset?");
    }

    private void update() {
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

        Presetable currentPreset = player.getPresets()[index];

        if (currentPreset == null) {
            createPreset(index, stats, inventory, equipment);
            return;
        }
        // Update stats
        player.getPresets()[index].setStats(stats);
        // Update spellbook
        player.getPresets()[index].setSpellbook(player.getSpellbook());
        player.getPresets()[index].setInventory(inventory);
        player.getPresets()[index].setEquipment(equipment);
        player.getPacketSender().sendMessage("The preset has been saved.");
        open(player);
    }

    private void createPreset(int index, int[] stats, Item[] inventory, Item[] equipment) {
        Presetable newPreset = new Presetable();
        newPreset.setName("Preset #"+index);
        newPreset.setStats(stats);
        newPreset.setInventory(inventory);
        newPreset.setEquipment(equipment);
        newPreset.setSpellbook(player.getSpellbook());
        player.setPreset(index, newPreset);
        player.getPacketSender().sendMessage("Your new preset has been added and named by default: "+newPreset.getName());
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
