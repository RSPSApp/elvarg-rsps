package com.elvarg.game.definition.loader.impl;

import com.elvarg.game.GameConstants;
import com.elvarg.game.content.combat.WeaponInterfaces.WeaponInterface;
import com.elvarg.game.definition.ItemDefinition;
import com.elvarg.game.definition.loader.DefinitionLoader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Map;

public class ItemDefinitionLoader extends DefinitionLoader {

    @Override
    public void load() throws Throwable {
    	ItemDefinition.definitions.clear();
        FileReader reader = new FileReader(file());
        ItemDefinition[] defs = new Gson().fromJson(reader, ItemDefinition[].class);
        for (ItemDefinition def : defs) {
            ItemDefinition.definitions.put(def.getId(), def);
        }
        
        Gson gson = new Gson();
        Type type = new TypeToken<Map<Integer, OSRSBoxItemDefinition>>() {}.getType();
        Map<Integer, OSRSBoxItemDefinition> boxDefs = gson.fromJson(new FileReader(GameConstants.DEFINITIONS_DIRECTORY + "items-complete.json"), type);
        for (Integer key: boxDefs.keySet()) {
        	ItemDefinition.definitions.getAndCreate(key).update(boxDefs.get(key));
        }
        reader.close();
    }

    @Override
    public String file() {
        return GameConstants.DEFINITIONS_DIRECTORY + "items.json";
    }
    
    public static class OSRSBoxItemDefinition {
        
        @Override
        public String toString() {
            return "OSRSBoxItemDefinition [id=" + id + ", name=" + name + ", examine=" + examine + ", tradeable="
                    + tradeable + ", stackable=" + stackable + ", noted=" + noted + ", highalch=" + highalch + ", lowalch="
                    + lowalch + ", weight=" + weight + ", cost=" + cost + ", equipment=" + equipment + ", weapon=" + weapon
                    + "]";
        }

        public int id;
        public String name = "";
        public String examine = "";
        public boolean tradeable;
        public boolean stackable;
        public boolean noted;
        public int highalch;//this is known as highAlch;
        public int lowalch;//this is known as lowAlch;

        public double weight;
        public int cost;//this is known as dropValue.
        public Equipment equipment;
        public Weapon weapon;
        
        
        
        /*
         * TODO the server has a value right now which is getNoteId which is unused, right now server just adds/subtracts 1.
         * 
         */
        //public int linked_id_noted and linked_id_item are used to convert back and forth.
        public int linked_id_noted;
        public int linked_id_item;
        
        //private int noteId = -1; this is in elvarg but not used.
        
        //end TODO
    /*

        private int[] bonuses;
        private int[] requirements;
         */
        public int[] getBonuses() {
            return equipment.array();
        }
        
        public int[] getRequirements() {
            return equipment.requirements.array();
        }
        
        public static class Weapon {

            /**
             * Perhaps use this data to get the {@link WeaponInterface}
             */
            public String weapon_type;

            /*
             * axe
             * blunt
             * bludgeon
             * bulwark
             * claw
             * polearm
             * partisan
             * pickaxe
             * scythe
             * slash_sword
             * spear
             * spiked
             * stab_sword
             * 2h_sword
             * whip
             * bow
             * chinchompas
             * crossbow
             * thrown
             * staff
             * bladed_staff
             * powered_staff
             * banner
             * blaster
             * gun
             * polestaff
             * salamander
             * unarmed
             */
            @Override
            public String toString() {
                return "Weapon [weapon_type=" + weapon_type + "]";
            } 
        }
        
        public static class Equipment {
            public int attack_stab;
            public int attack_slash;
            public int attack_crush;
            public int attack_magic;
            public int attack_ranged;
            public int defence_stab;
            public int defence_slash;
            public int defence_crush;
            public int defence_magic;
            public int defence_ranged;
            public int melee_strength;
            public int ranged_strength;
            public int magic_damage;
            public int prayer;
            
            public int[] array() {
                return new int[] {
                        attack_stab,
                        attack_slash,
                        attack_crush,
                        attack_magic,
                        attack_ranged,
                        defence_stab,
                        defence_slash,
                        defence_crush,
                        defence_magic,
                        defence_ranged,
                        melee_strength,
                        ranged_strength,
                        magic_damage,
                        prayer
                };
             }
            
            public String slot;//TODO THIS WILL NEED SOME ADDITIONAL CRAP TO SEPARATE BODY from PLATEBODY. WE CAN GET THIS FROM CACHE.
            public Requirements requirements;
            
            @Override
            public String toString() {
                return "Equipment [attack_stab=" + attack_stab + ", attack_slash=" + attack_slash + ", attack_crush="
                        + attack_crush + ", attack_magic=" + attack_magic + ", attack_ranged=" + attack_ranged
                        + ", defence_stab=" + defence_stab + ", defence_slash=" + defence_slash + ", defence_crush="
                        + defence_crush + ", defence_magic=" + defence_magic + ", defence_ranged=" + defence_ranged
                        + ", melee_strength=" + melee_strength + ", ranged_strength=" + ranged_strength + ", magic_damage="
                        + magic_damage + ", prayer=" + prayer + ", slot=" + slot + ", requirements=" + requirements + "]";
            }

            public static class Requirements {
                public int attack;
                public int defence;
                public int strength;
                public int hitpoints;
                public int ranged;
                public int prayer;
                public int magic;
                public int cooking;
                public int woodcutting;
                public int fletching;
                public int fishing;
                public int firemaking;
                public int crafting;
                public int smithing;
                public int mining;
                public int herblore;
                public int agility;
                public int thieving;
                public int slayer;
                public int farming;
                public int runecraft;
                public int construction;
                public int hunter;
                
                public int[] array() {
                    return new int[] {
                            attack,
                            defence,
                            strength,
                            hitpoints,
                            ranged,
                            prayer,
                            magic,
                            cooking,
                            woodcutting,
                            fletching,
                            fishing,
                            firemaking,
                            crafting,
                            smithing,
                            mining,
                            herblore,
                            agility,
                            thieving,
                            slayer,
                            farming,
                            runecraft,
                            construction,
                            hunter
                    };
                 }
                
                @Override
                public String toString() {
                    return "Requirements [attack=" + attack + ", defence=" + defence + ", strength=" + strength
                            + ", hitpoints=" + hitpoints + ", ranged=" + ranged + ", prayer=" + prayer + ", magic=" + magic
                            + ", cooking=" + cooking + ", woodcutting=" + woodcutting + ", fletching=" + fletching
                            + ", fishing=" + fishing + ", firemaking=" + firemaking + ", crafting=" + crafting
                            + ", smithing=" + smithing + ", mining=" + mining + ", herblore=" + herblore + ", agility="
                            + agility + ", thieving=" + thieving + ", slayer=" + slayer + ", farming=" + farming
                            + ", runecraft=" + runecraft + ", construction=" + construction + ", hunter=" + hunter + "]";
                }


            }
        }
    }
}
