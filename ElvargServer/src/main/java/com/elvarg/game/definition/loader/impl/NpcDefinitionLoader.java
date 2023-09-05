package com.elvarg.game.definition.loader.impl;

import com.elvarg.game.GameConstants;
import com.elvarg.game.definition.NpcDefinition;
import com.elvarg.game.definition.loader.DefinitionLoader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Map;

public class NpcDefinitionLoader extends DefinitionLoader {

    @Override
    public void load() throws Throwable {
        NpcDefinition.definitions.clear();
        Gson gson = new Gson();

        FileReader reader = new FileReader(GameConstants.DEFINITIONS_DIRECTORY + "npc_anims.json");
        NPCAnimSet[] anims = gson.fromJson(reader, NPCAnimSet[].class);

        //Arrays.stream(anims).forEach(anim -> Arrays.stream(anim.ids).forEach(id -> NpcDefinition.definitions.getAndCreate(id).update(anim)));
        for (NPCAnimSet anim : anims) {
            for (int id : anim.ids) {
                NpcDefinition.definitions.getAndCreate(id).update(id, anim);
            }
        }

        Type type = new TypeToken<Map<Integer, OSRSBoxNPCDefinition>>() {}.getType();
        Map<Integer, OSRSBoxNPCDefinition> boxDefs = gson.fromJson(new FileReader(GameConstants.DEFINITIONS_DIRECTORY + "monsters-complete.json"), type);
        boxDefs.keySet().forEach(key -> NpcDefinition.definitions.getAndCreate(key).update(boxDefs.get(key)));
        Map<Integer, OSRSBoxNPCDefinition> custom = gson.fromJson(new FileReader(GameConstants.DEFINITIONS_DIRECTORY + "npc_overrides.json"), type);
        custom.keySet().forEach(key -> NpcDefinition.definitions.getAndCreate(key).update(custom.get(key)));
        reader.close();
    }

    @Override
    public String file() {
        return "Npcs";
    }

    public static class NPCAnimSet {
        int[] ids;
        public int attackAnim;
        public int defenceAnim;
        public int deathAnim;
    }

    public static class OSRSBoxNPCDefinition {
        public int id;
        public String name;
        public String examine;
        public int size;
        public boolean aggressive;
        public boolean poisonous;
        public int max_hit;
        public int hitpoints;
        public int attack_speed;
        public int combat_level;
        public int slayer_level;
        private int attack_level = 1;
        private int strength_level = 1;
        private int defence_level = 1;
        private int magic_level = 1;
        private int ranged_level = 1;
        private int attack_bonus = 0;
        private int strength_bonus = 0;
        private int attack_magic = 0;
        private int magic_bonus = 0;
        private int attack_ranged = 0;
        private int ranged_bonus = 0;
        private int defence_stab = 0;
        private int defence_slash = 0;
        private int defence_crush = 0;
        private int defence_magic = 0;
        private int defence_ranged = 0;

        public int[] getStats() {
            return new int[] { 
                    // 0 = attack
                    // 1 = strength
                    // 2 = defence
                    // 3 = range
                    // 4 = magic

                    attack_level, strength_level, defence_level, ranged_level, magic_level, 

                    0, 0, 0, 0, 0, //index 5->9, combat bonuses?

                    // 10,11,12 = melee
                    // 13 = magic
                    // 14 = range
                    defence_stab, 0, 0, defence_magic, defence_ranged,
                    //idk?
                    0, 0, 0 };
        }

        public OSRSBoxNPCDefinition(int id, String name, String examine, int size, boolean aggressive, boolean poisonous,
                int maxHit, int hitpoints, int attackSpeed, int combatLevel, int[] stats, int slayerLevel) {
            this.id = id;
            this.name = name;
            this.examine = examine;
            this.size = size;
            this.aggressive = aggressive;
            this.poisonous = poisonous;
            this.max_hit = maxHit;
            this.hitpoints = hitpoints;
            this.attack_speed = attackSpeed;
            this.combat_level = combatLevel;
            this.slayer_level = slayerLevel;
        }
    }
}
