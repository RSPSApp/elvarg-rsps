package com.elvarg.game.definition.loader;

import com.elvarg.Server;
import com.elvarg.game.definition.loader.impl.OSBoxNPCDrops;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author Ynneh | 04/07/2023 - 13:27
 * <https://github.com/drhenny>
 */
public class NPCDefs {

    private static Map<Integer, NPCDefs> definitions = Maps.newConcurrentMap();

    public static void addDefinition(int npcId, NPCDefs defs) {
        definitions.put(npcId, defs);
    }

    public static NPCDefs forId(int npcId) {
        return definitions.get(npcId);
    }

    public static Map<Integer, NPCDefs> getDefs() {
        return definitions;
    }

    public int id;
    public String name;
    public String last_updated;
    public boolean incomplete;
    public boolean members;
    public String release_date;
    public int combat_level;
    public int size;
    public int hitpoints;
    public int max_hit;
    public List<String> attack_type = Lists.newArrayList();
    public int attack_speed;
    public boolean aggressive;
    public boolean poisonous;
    public boolean venomous;
    public boolean immune_poison;
    public boolean immune_venom;
    public List<String> attributes = Lists.newArrayList();
    public List<String> category = Lists.newArrayList();
    public boolean slayer_monster;
    public int slayer_level;
    public double slayer_xp;
    public List<String> slayer_masters = Lists.newArrayList();
    public boolean duplicate;
    public String examine;
    public String wiki_name;
    public String wiki_url;
    public int attack_level;
    public int strength_level;
    public int defence_level;
    public int magic_level;
    public int ranged_level;
    public int attack_bonus;
    public int strength_bonus;
    public int attack_magic;
    public int magic_bonus;
    public int attack_ranged;
    public int ranged_bonus;
    public int defence_stab;
    public int defence_slash;
    public int defence_crush;
    public int defence_magic;
    public int defence_ranged;
    public List<OSBoxNPCDrops> drops = Lists.newArrayList();

    public void setDrops(List<OSBoxNPCDrops> drops) {
        for (OSBoxNPCDrops item : drops) {
            if (item.rarity > 1.0D || item.rarity <= 0D) {
                Server.getLogger().log(Level.INFO, "WARNING.. npcId="+id+" Name="+name+" dropItem="+item.id+" itemName="+item.name+" has an invalid rarity of "+item.rarity);
                continue;
            }
            this.drops.add(item);
        }
    }
}
