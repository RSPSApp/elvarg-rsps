package com.elvarg.game.content.presets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.elvarg.game.GameConstants;
import com.elvarg.game.content.PrayerHandler;
import com.elvarg.game.content.PrayerHandler.PrayerData;
import com.elvarg.game.content.combat.CombatFactory;
import com.elvarg.game.content.combat.CombatSpecial;
import com.elvarg.game.content.combat.bountyhunter.BountyHunter;
import com.elvarg.game.content.combat.magic.Autocasting;
import com.elvarg.game.content.skill.SkillManager;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.entity.impl.playerbot.PlayerBot;
import com.elvarg.game.entity.impl.playerbot.fightstyle.impl.F2PMeleeFighterPreset;
import com.elvarg.game.model.Flag;
import com.elvarg.game.model.Item;
import com.elvarg.game.model.Skill;
import com.elvarg.game.model.areas.impl.WildernessArea;
import com.elvarg.game.model.container.impl.Bank;
import com.elvarg.game.model.rights.PlayerRights;
import com.elvarg.util.Misc;

/**
 * A class for handling {@code Presetable} sets.
 * <p>
 * Holy, this became messy quickly. Sorry about that.
 *
 * @author Professor Oak
 */
public class PresetManager {

    /**
     * The max amount of premade/custom presets.
     */
    public static final int MAX_PRESETS = 10;

    /**
     * The presets interface id.
     */
    private static final int INTERFACE_ID = 45000;

    /**
     * Pre-made sets by the server which everyone can use.
     */
    public static final Presetable[] GLOBAL_PRESETS = new Presetable[]{
            PredefinedPresets.OBBY_MAULER_57,
            PredefinedPresets.G_MAULER_70,
            PredefinedPresets.DDS_PURE_M_73,
            PredefinedPresets.DDS_PURE_R_73,
            PredefinedPresets.NH_PURE_83,
            F2PMeleeFighterPreset.PRESETABLE,
            PredefinedPresets.ATT_70_ZERKER_97,
            PredefinedPresets.MAIN_RUNE_126,
            PredefinedPresets.MAIN_HYBRID_126,
            PredefinedPresets.MAIN_TRIBRID_126,
    };

    /**
     * Opens the presets interface for a player.
     *
     * @param player
     */
    public static void open(Player player) {
        open(player, player.getCurrentPreset());
    }

    /**
     * Opens the specified preset for a player.
     *
     * @param player
     * @param preset
     */
    public static void open(Player player, Presetable preset) {
        if (preset != null) {

            // Send name
            player.getPacketSender().sendString(45002, "Presets - " + preset.getName());

            // Send stats
            player.getPacketSender().sendString(45007, Integer.toString(preset.getStats()[3])); // Hitpoints
            player.getPacketSender().sendString(45008, Integer.toString(preset.getStats()[0])); // Attack
            player.getPacketSender().sendString(45009, Integer.toString(preset.getStats()[2])); // Strength
            player.getPacketSender().sendString(45010, Integer.toString(preset.getStats()[1])); // Defence
            player.getPacketSender().sendString(45011, Integer.toString(preset.getStats()[4])); // Ranged
            player.getPacketSender().sendString(45012, Integer.toString(preset.getStats()[5])); // Prayer
            player.getPacketSender().sendString(45013, Integer.toString(preset.getStats()[6])); // Magic

            // Send spellbook
            player.getPacketSender().sendString(45014,
                    "@yel@" + Misc.formatText(preset.getSpellbook().name().toLowerCase()));
        } else {

            // Reset name
            player.getPacketSender().sendString(45002, "Presets");

            // Reset stats
            for (int i = 0; i <= 6; i++) {
                player.getPacketSender().sendString(45007 + i, "");
            }

            // Reset spellbook
            player.getPacketSender().sendString(45014, "");
        }

        // Send inventory
        for (int i = 0; i < 28; i++) {

            // Get item..
            Item item = null;
            if (preset != null) {
                if (i < preset.getInventory().length) {
                    item = preset.getInventory()[i];
                }
            }

            // If it isn't null, send it. Otherwise, send empty slot.
            if (item != null) {
                player.getPacketSender().sendItemOnInterface(45015 + i, item.getId(), item.getAmount());
            } else {
                player.getPacketSender().sendItemOnInterface(45015 + i, -1, 1);
            }
        }

        // Send equipment
        for (int i = 0; i < 14; i++) {
            player.getPacketSender().sendItemOnInterface(45044 + i, -1, 1);
        }
        if (preset != null) {
            Arrays.stream(preset.getEquipment()).filter(t -> !Objects.isNull(t) && t.isValid())
                    .forEach(t -> player.getPacketSender().sendItemOnInterface(
                            45044 + t.getDefinition().getEquipmentType().getSlot(), t.getId(), t.getAmount()));
        }

        // Send all available global presets
        for (int i = 0; i < MAX_PRESETS; i++) {
            player.getPacketSender().sendString(45070 + i,
                    GLOBAL_PRESETS[i] == null ? "Empty" : GLOBAL_PRESETS[i].getName());
        }

        refreshCustomPresetNames(player);

        // Send on death toggle
        player.getPacketSender().sendConfig(987, player.isOpenPresetsOnDeath() ? 0 : 1);

        // Send interface
        player.getPacketSender().sendInterface(INTERFACE_ID);

        // Update current preset
        player.setCurrentPreset(preset);
    }

    static void refreshCustomPresetNames(Player player) {
        // Send all available player presets
        for (int i = 0; i < MAX_PRESETS; i++) {
            player.getPacketSender().sendString(45082 + i,
                    player.getPresets()[i] == null ? "Empty" : player.getPresets()[i].getName());
        }
    }

    static Presetable loadoutToPreset(String name, Player player) {
        return new Presetable(name,
                player.getInventory().getItems().clone(),
                player.getEquipment().getItems().clone(),
                /* atk, def, str, hp, range, pray, mage */
                new int[]{
                        player.getSkillManager().getMaxLevel(Skill.ATTACK),
                        player.getSkillManager().getMaxLevel(Skill.DEFENCE),
                        player.getSkillManager().getMaxLevel(Skill.STRENGTH),
                        player.getSkillManager().getMaxLevel(Skill.HITPOINTS),
                        player.getSkillManager().getMaxLevel(Skill.RANGED),
                        player.getSkillManager().getMaxLevel(Skill.PRAYER),
                        player.getSkillManager().getMaxLevel(Skill.MAGIC),
                },
                player.getSpellbook(),
                false
        );
    }

    /**
     * Edits a preset.
     *
     * @param player The player.
     * @param index  The preset(to edit)'s index
     */
    private static void edit(Player player, final int index) {
        // Check if we can edit..
        if (player.getArea() instanceof WildernessArea) {
            player.getPacketSender().sendMessage("You can't edit a preset in the wilderness!");
            return;
        }
        if (player.getDueling().inDuel()) {
            player.getPacketSender().sendMessage("You can't edit a preset during a duel!");
            return;
        }
        if (CombatFactory.inCombat(player)) {
            player.getPacketSender().sendMessage("You cannot do that right now.");
            return;
        }
        if (index == -1) {
            /**
             * Invalid number.
             */
            System.err.println("invalid index.. " + index);
            return;
        }
        Integer ri = (Integer) player.getAttribute("presetIndex");
        if (ri == null) {
            player.sendMessage("You need to select a preset to edit!");
            return;
        }
        if (ri < 0 || ri > 10) {
            player.sendMessage("You cannot edit this preset!");
            return;
        }
        player.getDialogueManager().start(new PresetDialogue(player, (Integer) player.getAttribute("presetIndex")));
    }

    /**
     * Loads a preset.
     *
     * @param player The player.
     * @param preset The preset to load.
     */
    public static void load(Player player, final Presetable preset) {
        final int oldCbLevel = player.getSkillManager().getCombatLevel();

        // Check if we can load...
        if (player.getArea() instanceof WildernessArea) {
            if (!(player instanceof PlayerBot) && player.getRights() != PlayerRights.DEVELOPER) {
                player.getPacketSender().sendMessage("You can't load a preset in the wilderness!");
                return;
            }
        }
        if (player.getDueling().inDuel()) {
            player.getPacketSender().sendMessage("You can't load a preset during a duel!");
            return;
        }

        // Send valuable items in inventory/equipment to bank
        boolean sent = false;
        for (Item item : Misc.concat(player.getInventory().getCopiedItems(), player.getEquipment().getCopiedItems())) {
            if (!item.isValid()) {
                continue;
            }
            boolean spawnable = GameConstants.ALLOWED_SPAWNS.contains(item.getId());
            if (!spawnable) {
                player.getBank(Bank.getTabForItem(player, item.getId())).add(item, false);
                sent = true;
            }
        }
        if (sent) {
            player.getPacketSender().sendMessage("The non-spawnable items you had on you have been sent to your bank.");
        }

        player.getInventory().resetItems().refreshItems();
        player.getEquipment().resetItems().refreshItems();

        // Check for the preset's valuable items and see if the player has them.
        if (!preset.isGlobal()) {
            List<Item> nonSpawnables = new ArrayList<Item>();

            // Get all the valuable items in this preset and check if player has them..
            for (Item item : Misc.concat(preset.getInventory(), preset.getEquipment())) {
                if (item == null)
                    continue;

                boolean spawnable = GameConstants.ALLOWED_SPAWNS.contains(item.getId());

                if (!spawnable) {
                    nonSpawnables.add(item);

                    int inventoryAmt = player.getInventory().getAmount(item.getId());
                    int equipmentAmt = player.getEquipment().getAmount(item.getId());
                    int bankAmt = player.getBank(Bank.getTabForItem(player, item.getId())).getAmount(item.getId());
                    int totalAmt = inventoryAmt + equipmentAmt + bankAmt;

                    int preset_amt = preset.getAmount(item.getId());

                    if (totalAmt < preset_amt) {
                        player.getPacketSender().sendMessage("You don't have the non-spawnable item "
                                        + item.getDefinition().getName() + " in your inventory, equipment")
                                .sendMessage("or bank.");
                        return;
                    }
                }
            }

            // Delete valuable items from the proper place
            // Not from inventory/equipment, they will be reset anyway.
            for (Item item : nonSpawnables) {
                if (player.getInventory().contains(item)) {
                    player.getInventory().delete(item);
                } else if (player.getEquipment().contains(item)) {
                    player.getEquipment().delete(item);
                } else {
                    player.getBank(Bank.getTabForItem(player, item.getId())).delete(item);
                }
            }
        }

        // Add inventory
        Arrays.stream(preset.getInventory()).filter(t -> !Objects.isNull(t) && t.isValid())
                .forEach(t -> player.getInventory().add(t));

        // Set equipment
        Arrays.stream(preset.getEquipment()).filter(t -> !Objects.isNull(t) && t.isValid())
                .forEach(t -> player.getEquipment().setItem(t.getDefinition().getEquipmentType().getSlot(), t.clone()));

        // Set magic spellbook
        player.setSpellbook(preset.getSpellbook());
        Autocasting.setAutocast(player, null);

        // Set levels
        long totalExp = 0;
        for (int i = 0; i < preset.getStats().length; i++) {
            Skill skill = Skill.values()[i];
            int level = preset.getStats()[i];
            int exp = SkillManager.getExperienceForLevel(level);
            player.getSkillManager().setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill, exp);
            totalExp += exp;
        }

        // Update prayer tab with prayer info
        player.getPacketSender().sendString(687, player.getSkillManager().getCurrentLevel(Skill.PRAYER) + "/"
                + player.getSkillManager().getMaxLevel(Skill.PRAYER));

        // Send total level
        player.getPacketSender().sendString(31200, "" + player.getSkillManager().getTotalLevel());

        // Send combat level
        final int newCbLevel = player.getSkillManager().getCombatLevel();
        final String combatLevel = "Combat level: " + newCbLevel;
        player.getPacketSender().sendString(19000, combatLevel).sendString(5858, combatLevel);

        if (newCbLevel != oldCbLevel) {
            BountyHunter.unassign(player);
        }

        // Send new spellbook
        player.getPacketSender().sendTabInterface(6, player.getSpellbook().getInterfaceId());
        player.getPacketSender().sendConfig(709, PrayerHandler.canUse(player, PrayerData.PRESERVE, false) ? 1 : 0);
        player.getPacketSender().sendConfig(711, PrayerHandler.canUse(player, PrayerData.RIGOUR, false) ? 1 : 0);
        player.getPacketSender().sendConfig(713, PrayerHandler.canUse(player, PrayerData.AUGURY, false) ? 1 : 0);
        player.resetAttributes();
        player.getPacketSender().sendMessage("Preset loaded!");
        player.getPacketSender().sendTotalExp(totalExp);

        // Restore special attack
        player.setSpecialPercentage(100);
        CombatSpecial.updateBar(player);

        player.getUpdateFlag().flag(Flag.APPEARANCE);
    }

    /**
     * Handles a clicked button on the interface.
     *
     * @param player
     * @param button
     * @param action
     * @return
     */
    public static boolean handleButton(Player player, int button, int action) {
        if (player.getInterfaceId() != INTERFACE_ID && !(player instanceof PlayerBot)) {
            return false;
        }
        /** My presets index **/
        if (button >= 45082 && button <= 45091) {
            button = (button - 45082);
            player.getPacketSender().closeDialogue();
            if (player.getPresets()[button] == null) {
                createPreset(player, button);
                return true;
            }
            if (player.getPresets()[button] == null) {
                open(player, null);
                return true;
            }
            // Check if already in set, no need to re-open
            if (player.getCurrentPreset() != null && player.getCurrentPreset() == player.getPresets()[button]) {
                return true;
            }
            open(player, player.getPresets()[button]);
        }
        if (button != 45061)//EDIT
            player.setAttribute("presetIndex", button);

        switch (button) {
            case 45060: // Toggle on death show
                player.setOpenPresetsOnDeath(!player.isOpenPresetsOnDeath());
                player.getPacketSender().sendConfig(987, player.isOpenPresetsOnDeath() ? 0 : 1);
                return true;
            case 45061: // Edit preset
                edit(player, button);
                return true;
            case 45064: // Load preset
                if (player.getCurrentPreset() == null) {
                    player.getPacketSender().sendMessage("You haven't selected any preset yet.");
                    return true;
                }
                load(player, player.getCurrentPreset());
                return true;
        }

        // Global presets selection
        if (button >= 45070 && button <= 45079) {
            final int index = button - 45070;
            if (GLOBAL_PRESETS.length <= index || GLOBAL_PRESETS[index] == null) {
                player.getPacketSender().sendMessage("That preset is currently unavailable.");
                return true;
            }

            // Check if already in set, no need to re-open
            if (player.getCurrentPreset() != null && player.getCurrentPreset() == GLOBAL_PRESETS[index]) {
                return true;
            }

            open(player, GLOBAL_PRESETS[index]);
            return true;
        }
        return false;
    }

    private static void createPreset(Player player, int index) {
        player.getDialogueManager().start(new PresetCreationD(player, index));
    }
}
