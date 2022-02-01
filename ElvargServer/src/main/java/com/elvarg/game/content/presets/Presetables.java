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
import com.elvarg.game.model.Item;
import com.elvarg.game.model.Skill;
import com.elvarg.game.model.areas.impl.WildernessArea;
import com.elvarg.game.model.container.impl.Bank;
import com.elvarg.game.model.rights.PlayerRights;
import com.elvarg.util.Misc;

/**
 * A class for handling {@code Presetable} sets.
 * 
 * Holy, this became messy quickly. Sorry about that.
 * 
 * @author Professor Oak
 */
public class Presetables {

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
	public static final Presetable[] GLOBAL_PRESETS = new Presetable[MAX_PRESETS];

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

		// Send all available player presets
		for (int i = 0; i < MAX_PRESETS; i++) {
			player.getPacketSender().sendString(45082 + i,
					player.getPresets()[i] == null ? "Empty" : player.getPresets()[i].getName());
		}

		// Send on death toggle
		player.getPacketSender().sendConfig(987, player.isOpenPresetsOnDeath() ? 0 : 1);

		// Send interface
		player.getPacketSender().sendInterface(INTERFACE_ID);

		// Update current preset
		player.setCurrentPreset(preset);
	}

	/**
	 * Edits a preset.
	 * 
	 * @param player
	 *            The player.
	 * @param index
	 *            The preset(to edit)'s index
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
		if (player.getPresets()[index] == null) {
			player.getPacketSender().sendMessage("This preset cannot be edited.");
			return;
		}

		/*DialogueManager.start(player, 11);
		player.setDialogueOptions(new DialogueOptions() {
			@Override
			public void handleOption(Player player, int option) {
				switch (option) {
				case 1: // Change name
					player.setEnteredSyntaxAction((input) -> {
					    player.getPacketSender().sendInterfaceRemoval();
				        
				        input = Misc.formatText(input);
				        
				        if(!Misc.isValidName(input)) {
				            player.getPacketSender().sendMessage("Invalid name for preset. Please enter characters only.");
				            player.setCurrentPreset(null);
				            Presetables.open(player);
				            return;
				        }
				        
				        if(player.getPresets()[index] != null) {
				            
				            player.getPresets()[index].setName(input);
				            player.getPacketSender().sendMessage("The preset's name has been updated.");
				            
				            Presetables.open(player);
				        } 
					});
					player.getPacketSender().sendEnterInputPrompt("Enter a new name for your preset below.");

					break;
				case 2: // Update items

					// Update items
					Item[] inventory = player.getInventory().copyValidItemsArray();
					Item[] equipment = player.getEquipment().copyValidItemsArray();
					for (Item t : Misc.concat(inventory, equipment)) {
						if (t.getDefinition().isNoted()) {
							player.getPacketSender()
									.sendMessage("You cannot create presets which contain noted items.");
							return;
						}
					}

					player.getPresets()[index].setInventory(inventory);
					player.getPresets()[index].setEquipment(equipment);

					player.getPacketSender()
							.sendMessage("The preset's items have been updated to match your current setup.");
					open(player);
					break;
				case 3: // Update stats

					// Fetch stats
					int[] stats = new int[7];
					for (int i = 0; i < stats.length; i++) {
						stats[i] = player.getSkillManager().getMaxLevel(Skill.values()[i]);
					}

					// Update stats
					player.getPresets()[index].setStats(stats);

					// Update spellbook
					player.getPresets()[index].setSpellbook(player.getSpellbook());

					player.getPacketSender()
							.sendMessage("The preset's stats have been updated to match your current setup.");
					open(player);
					break;
				case 4: // Delete preset

					// Delete
					player.getPresets()[index] = null;
					player.setCurrentPreset(null);

					player.getPacketSender().sendMessage("The preset has been deleted.");
					open(player);
					break;
				case 5: // Cancel
					player.getPacketSender().sendInterfaceRemoval();
					break;
				}
			}
		});*/
	}

	/**
	 * Loads a preset.
	 * 
	 * @param player
	 *            The player.
	 * @param preset
	 *            The preset to load.
	 */
	private static void load(Player player, final Presetable preset) {
		final int oldCbLevel = player.getSkillManager().getCombatLevel();

		// Always use the temporary skill manager for presets
		SkillManager tempSkillManager = player.getTempSkillManager();

		// Close!
		player.getPacketSender().sendInterfaceRemoval();

		// Check if we can load..
		if (player.getArea() instanceof WildernessArea) {
			if (player.getRights() != PlayerRights.DEVELOPER) {
				player.getPacketSender().sendMessage("You can't load a preset in the wilderness!");
				return;
			}
		}
		if (player.getDueling().inDuel()) {
			player.getPacketSender().sendMessage("You can't load a preset during a duel!");
			return;
		}

		boolean sent = false;
		if (!player.hasUsedPreset) {
			// Player is loading preset for first time, bank all their items
			for (Item item : Misc.concat(player.getInventory().getCopiedItems(), player.getEquipment().getCopiedItems())) {
				if (!item.isValid()) {
					continue;
				}

				player.getBank(Bank.getTabForItem(player, item.getId())).add(item, false);
				sent = true;
			}
			if (sent) {
				player.getPacketSender().sendMessage("The items/equipment you had on you have been sent to your bank.");
			}
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

				boolean spawnable = false;
				for (int i : GameConstants.ALLOWED_SPAWNS) {
					if (item.getId() == i) {
						spawnable = true;
						break;
					}
				}

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
			tempSkillManager.setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill, exp);
			totalExp += exp;
		}

		// Update prayer tab with prayer info
		player.getPacketSender().sendString(687, tempSkillManager.getCurrentLevel(Skill.PRAYER) + "/"
				+ tempSkillManager.getMaxLevel(Skill.PRAYER));

		// Send total level
		player.getPacketSender().sendString(31200, "" + tempSkillManager.getTotalLevel());

		// Send combat level
		final int newCbLevel = tempSkillManager.getCombatLevel();
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
		player.hasUsedPreset = true;
		player.getPacketSender().sendMessage("Preset loaded!");
		player.getPacketSender().sendTotalExp(totalExp);

		// Restore special attack
		player.setSpecialPercentage(100);
		CombatSpecial.updateBar(player);
	}

	/**
	 * Handles a clicked button on the interface.
	 * 
	 * @param player
	 * @param button
	 * @return
	 */
	public static boolean handleButton(Player player, int button) {
		if (player.getInterfaceId() != INTERFACE_ID) {
			return false;
		}
		switch (button) {
		case 45060: // Toggle on death show
			player.setOpenPresetsOnDeath(!player.isOpenPresetsOnDeath());
			player.getPacketSender().sendConfig(987, player.isOpenPresetsOnDeath() ? 0 : 1);
			return true;
		case 45061: // Edit preset
			player.getPacketSender().sendMessage("This feature is currently disabled.");
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
			if (GLOBAL_PRESETS[index] == null) {
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

		// Custom presets selection
		if (button >= 45082 && button <= 45091) {
			final int index = button - 45082;

			if (player.getPresets()[index] == null) {
				/*DialogueManager.start(player, 10);
				player.setDialogueOptions(new DialogueOptions() {
					@Override
					public void handleOption(Player player, int option) {
						if (option == 1) {
							player.setEnteredSyntaxAction((input) -> {
							    player.getPacketSender().sendInterfaceRemoval();

						        input = Misc.formatText(input);

						        if(!Misc.isValidName(input)) {
						            player.getPacketSender().sendMessage("Invalid name for preset.");
						            player.setCurrentPreset(null);
						            Presetables.open(player);
						            return;
						        }

						        if(player.getPresets()[index] == null) {

						            //Get stats..
						            int[] stats = new int[7];
						            for(int i = 0; i < stats.length; i++) {
						                stats[i] = player.getSkillManager().getMaxLevel(Skill.values()[i]);
						            }
						            
						            Item[] inventory = player.getInventory().copyValidItemsArray();
						            Item[] equipment = player.getEquipment().copyValidItemsArray();
						            for(Item t : Misc.concat(inventory, equipment)) {
						                if(t.getDefinition().isNoted()) {
						                    player.getPacketSender().sendMessage("You cannot create presets which contain noted items.");
						                    return;
						                }
                                    }
                                    player.getPresets()[index] = new Presetable(input, index, inventory, equipment,
                                            stats, player.getSpellbook(), false);
                                    player.setCurrentPreset(player.getPresets()[index]);

						            Presetables.open(player);
						        }
							});
							player.getPacketSender().sendEnterInputPrompt("Enter a name for your preset below.");
						} else {
							player.getPacketSender().sendInterfaceRemoval();
						}
					}
				});*/
				return true;
			}

			// Check if already in set, no need to re-open
			if (player.getCurrentPreset() != null && player.getCurrentPreset() == player.getPresets()[index]) {
				return true;
			}

			open(player, player.getPresets()[index]);
		}
		return false;
	}
}
