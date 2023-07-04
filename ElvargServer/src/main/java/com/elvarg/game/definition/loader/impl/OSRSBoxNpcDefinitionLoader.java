package com.elvarg.game.definition.loader.impl;

import com.elvarg.Server;
import com.elvarg.game.definition.loader.NPCDefs;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.util.logging.Level;

/**
 * @author Ynneh | 04/07/2023 - 13:27
 * <https://github.com/drhenny>
 */
public class OSRSBoxNpcDefinitionLoader implements Runnable {

    public void run() {
        long start = System.currentTimeMillis();
        for (File file : new File("data/definitions/monsters-json").listFiles()) {
            if (file.getName().endsWith(".json")) {
                try {
                    FileReader reader = new FileReader(file);
                    NPCDefs definition = new Gson().fromJson(reader, NPCDefs.class);
                    NPCDefs npcDefs = new NPCDefs();
                    npcDefs.id = definition.id;
                    npcDefs.name = definition.name;
                    npcDefs.last_updated = definition.last_updated;
                    npcDefs.incomplete = definition.incomplete;
                    npcDefs.members = definition.members;
                    npcDefs.release_date = definition.release_date;
                    npcDefs.combat_level = definition.combat_level;
                    npcDefs.size = definition.size;
                    npcDefs.hitpoints = definition.hitpoints;
                    npcDefs.max_hit = definition.max_hit;
                    npcDefs.attack_type = definition.attack_type;
                    npcDefs.attack_speed = definition.attack_speed;
                    npcDefs.aggressive = definition.aggressive;
                    npcDefs.poisonous = definition.poisonous;
                    npcDefs.venomous = definition.venomous;
                    npcDefs.immune_poison = definition.immune_poison;
                    npcDefs.immune_venom = definition.immune_venom;
                    npcDefs.attributes = definition.attributes;
                    npcDefs.category = definition.category;
                    npcDefs.slayer_monster = definition.slayer_monster;
                    npcDefs.slayer_level = Integer.valueOf(definition.slayer_level);
                    npcDefs.slayer_xp = definition.slayer_xp;
                    npcDefs.slayer_masters = definition.slayer_masters;
                    npcDefs.duplicate = definition.duplicate;
                    npcDefs.examine = definition.examine;
                    npcDefs.wiki_name = definition.wiki_name;
                    npcDefs.wiki_url = definition.wiki_url;
                    npcDefs.attack_level = definition.attack_level;
                    npcDefs.strength_level = definition.strength_level;
                    npcDefs.defence_level = definition.defence_level;
                    npcDefs.magic_level = definition.magic_level;
                    npcDefs.ranged_level = definition.ranged_level;
                    npcDefs.attack_bonus = definition.attack_bonus;
                    npcDefs.strength_bonus = definition.strength_bonus;
                    npcDefs.attack_magic = definition.attack_magic;
                    npcDefs.magic_bonus = definition.magic_bonus;
                    npcDefs.attack_ranged = definition.attack_ranged;
                    npcDefs.ranged_bonus = definition.ranged_bonus;
                    npcDefs.defence_stab = definition.defence_stab;
                    npcDefs.defence_slash = definition.defence_slash;
                    npcDefs.defence_crush = definition.defence_crush;
                    npcDefs.defence_magic = definition.defence_magic;
                    npcDefs.defence_ranged = definition.defence_ranged;
                    npcDefs.setDrops(definition.drops);
                    NPCDefs.addDefinition(npcDefs.id, npcDefs);
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        long elapsed = System.currentTimeMillis() - start;
        Server.getLogger().log(Level.INFO, "Loaded "+NPCDefs.getDefs().size()+" OSRS NPC Definitions. It took " + elapsed + " milliseconds.");
    }
}
