package com.elvarg.game.definition;

import com.elvarg.game.content.combat.WeaponInterfaces.WeaponInterface;
import com.elvarg.game.definition.loader.impl.ItemDefinitionLoader.OSRSBoxItemDefinition;
import com.elvarg.game.model.EquipmentType;
import com.elvarg.game.model.container.shop.currency.impl.BloodMoneyCurrency;
import com.elvarg.util.SuppliedHashMap;

import static com.elvarg.util.ItemIdentifiers.*;

/**
 * Represents the definition of an item.
 *
 * @author Professor Oak
 */
public class ItemDefinition {

    /**
     * The map containing all our {@link ItemDefinition}s.
     */
    public static final SuppliedHashMap<Integer, ItemDefinition> definitions = new SuppliedHashMap<>(ItemDefinition::new);

    /**
     * The default {@link ItemDefinition} that will be used.
     */
    public static final ItemDefinition DEFAULT = new ItemDefinition();
    public transient WeaponInterface weaponInterface;
    public EquipmentType equipmentType = EquipmentType.NONE;
    public boolean doubleHanded;//TODO is this needed?
    public boolean dropable;//TODO get from cache. If it doesnt have destroy.
    public boolean sellable;//Noot sure about this one.
    public int value;// GE PRICE
    
    //VALUES from OSRSBOX
    public int id;
    public String name = "";
    public String examine = "";
    public boolean stackable;
    public boolean tradeable;
    public boolean noted;
    public int highAlch;
    public int lowAlch;
    public int dropValue;
    public int noteId = -1;
    public double weight;
    public int[] bonuses;
    public int[] requirements;
    public String weapon_type;

    /**
     * Attempts to get the {@link ItemDefinition} for the
     * given item.
     *
     * @param item
     * @return
     */
    public static ItemDefinition forId(int item) {
        return definitions.getOrDefault(item, DEFAULT);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getExamine() {
        return examine;
    }

    public int getValue() {
        return value;
    }

    public int getHighAlchValue() {
        return highAlch;
    }

    public int getLowAlchValue() {
        return lowAlch;
    }

    public int getDropValue() {
        return dropValue;
    }

    public boolean isStackable() {
        return stackable;
    }

    public boolean isTradeable() {
        return tradeable;
    }

    public boolean isSellable() {
        return sellable;
    }

    public boolean isDropable() {
        return dropable;
    }

    public boolean isNoted() {
        return noted;
    }

    public int getNoteId() {
        return noteId;
    }

    public boolean isDoubleHanded() {
        return doubleHanded;
    }

    public double getWeight() {
        return weight;
    }

    public int[] getBonuses() {
        return bonuses;
    }

    public int[] getRequirements() {
        return requirements;
    }

    public WeaponInterface getWeaponInterface() {
        return weaponInterface;
    }

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public boolean isBarrows() {
        return id != BOLT_RACK && (id >= AHRIMS_HOOD && id <= VERACS_PLATESKIRT || id >= AHRIMS_HOOD_100 && id <= VERACS_PLATESKIRT_0_2);
    }

    /**
     * Attempts to get the unnoted version of the given item.
     *
     * @param id
     * @return
     */
    public int unNote() {
        return ItemDefinition.forId(id - 1).getName().equals(name) ? id - 1 : id;
    }

    public void update(OSRSBoxItemDefinition o) {
        // ELVARG max id is 26562 and it is missing the following:
/*
OSRSBoxItemDefinition [id=25484, name=Webweaver bow (u)] mismatch with Elvarg [id=0, name=]
OSRSBoxItemDefinition [id=25485, name=Webweaver bow] mismatch with Elvarg [id=0, name=]
OSRSBoxItemDefinition [id=25486, name=Ursine chainmace (u)] mismatch with Elvarg [id=0, name=]
OSRSBoxItemDefinition [id=25487, name=Ursine chainmace] mismatch with Elvarg [id=0, name=]
OSRSBoxItemDefinition [id=25488, name=Accursed sceptre (u)] mismatch with Elvarg [id=0, name=]
OSRSBoxItemDefinition [id=25489, name=Accursed sceptre] mismatch with Elvarg [id=0, name=]
OSRSBoxItemDefinition [id=25490, name=Voidwaker] mismatch with Elvarg [id=0, name=]
OSRSBoxItemDefinition [id=25491, name=Accursed sceptre (au)] mismatch with Elvarg [id=0, name=]
OSRSBoxItemDefinition [id=25492, name=Accursed sceptre (a)] mismatch with Elvarg [id=0, name=]
 */

        //Check to ensure that the names match. This should throw an error on shuffled ids.
        //if(!this.name.equalsIgnoreCase(o.name))
        //System.out.println("OSRSBoxItemDefinition [id=" + o.id + ", name=" + o.name + "] mismatch with Elvarg [id=" + this.id + ", name=" + this.name + "]");

        this.id = o.id;
        this.name = o.name;
        this.examine = o.examine;
        this.stackable = o.stackable;
        this.tradeable = o.tradeable;
        this.noted = o.noted;
        this.highAlch = o.highalch;
        this.lowAlch = o.lowalch;
        this.dropValue = o.cost;
        this.noteId = o.noted ? o.linked_id_item : o.linked_id_noted;
        this.weight = o.weight;
        this.bonuses = o.equipment != null? o.getBonuses() : null;
        this.requirements = o.equipment != null && o.equipment.requirements != null ? o.getRequirements() : null;		
        this.weapon_type = o.weapon != null ? o.weapon.weapon_type : null;
        if(this.weapon_type != null)
            this.weaponInterface = WeaponInterfaces.get(this.weapon_type, this.id);
    }
    
    public static int getBlockAnimation(int shield, int weapon) {
        if(shield > 0)
            return getOffhandBlock(shield);
        WeaponInterface def = ItemDefinition.forId(weapon).getWeaponInterface();
        if(def != null)
            return def.getBlockAnim();
        return 424;
    }

    private static int getOffhandBlock(int itemId) {
        switch (itemId) {
        case 1189: // Bronze kiteshield
        case 1191: // Iron kiteshield
        case 1193: // Steel kiteshield
        case 1195: // Black kiteshield
        case 1197: // Mithril kiteshield
        case 1199: // Adamant kiteshield
        case 1201: // Rune kiteshield
        case 6894: // Adamant kiteshield
        case 11283: // Dragonfire shield
        case 11284: // Dragonfire shield
        case 11285: // Dragonfire shield
        case 12817: // Elysian spirit shield
        case 12821: // Spectral spirit shield
        case 12825: // Arcane spirit shield
            return 1156;
        case 8844: // Bronze defender
        case 8845: // Iron defender
        case 8846: // Steel defender
        case 8847: // Black defender
        case 8848: // Mithril defender
        case 8849: // Adamant defender
        case 8850: // Rune defender
        case 12954: // Dragon defender
            return 4177;
        }
        return 424;
    }
}
