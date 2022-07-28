package com.elvarg.game.content.presets;

import com.elvarg.game.entity.impl.playerbot.fightstyle.PlayerBotFightStyle;
import com.elvarg.game.model.Item;
import com.elvarg.game.model.MagicSpellbook;
import com.elvarg.util.Misc;

/**
 * A class which represents a presetable
 * set.
 * @author Professor Oak
 */
public class Presetable {
	
	/**
	 * This set's name.
	 */
	protected String name;
	
	/**
	 * This set's inventory.
	 */
	private Item[] inventory;
	
	/**
	 * This set's equipment.
	 */
	private Item[] equipment;
	
	/**
	 * This set's skill levels.
	 */
	private int[] stats;
	
	/**
	 * This set's magic spellbook.
	 */
	private MagicSpellbook spellbook;
	
	/**
	 * This set's index
	 */
	private final int index;
	
	/**
	 * Is this a global preset?
	 */
	private final boolean isGlobal;

	public Presetable() {
		this.index = 0;
		this.isGlobal = true;
	}

	/**
	 * Constructs a new {@link Presetable}.
	 * @param name			The set's name.
	 * @param index			The set's index.
	 * @param inventory		The set's inventory items.
	 * @param equipment		The set's equipment items.
	 * @param stats			The set's skill levels.
	 * @param spellbook		The set's magic spellbook.
	 */
	public Presetable(String name, int index, Item[] inventory, Item[] equipment, int[] stats, MagicSpellbook spellbook, boolean isGlobal) {
		this.name = name;
		this.inventory = inventory;
		this.equipment = equipment;
		this.stats = stats;
		this.spellbook = spellbook;
		this.index = index;
		this.isGlobal = isGlobal;
	}
	
	public int getAmount(int itemId) {
		int count = 0;
		for(Item item : Misc.concat(inventory, equipment)) {
			if(item == null)
				continue;
			if(item.getId() == itemId) {
				count += item.getAmount();
			}
		}
		return count;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Item[] getInventory() {
		return inventory;
	}
	
	public void setInventory(Item[] inventory) {
		this.inventory = inventory;
	}

	public Item[] getEquipment() {
		return equipment;
	}
	
	public void setEquipment(Item[] equipment) {
		this.equipment = equipment;
	}

	public int[] getStats() {
		return stats;
	}
	
	public void setStats(int[] stats) {
		this.stats = stats;
	}

	public MagicSpellbook getSpellbook() {
		return spellbook;
	}
	
	public void setSpellbook(MagicSpellbook spellbook) {
		this.spellbook = spellbook;
	}

	public boolean isGlobal() {
		return isGlobal;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public int hashCode() {
		// For better HashMap performance
		return this.getIndex();
	}

	@Override
	public boolean equals(Object o) {
		// For better HashMap performance
		if (o == this)
			return true;
		return (((Presetable) o).getIndex() == this.getIndex());
	}
}