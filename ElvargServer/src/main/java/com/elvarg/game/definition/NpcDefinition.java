package com.elvarg.game.definition;

import java.util.HashMap;
import java.util.Map;

import com.elvarg.game.definition.loader.impl.NpcDefinitionLoader.OSRSBoxNPCDefinition;

/**
 * Represents an npc's definition.
 * Holds its information, such as
 * name and combat level.
 *
 * @author Professor Oak
 */
public class NpcDefinition {

    /**
     * The map containing all our {@link ItemDefinition}s.
     */
    public static final Map<Integer, NpcDefinition> definitions = new HashMap<Integer, NpcDefinition>();

    /**
     * The default {@link ItemDefinition} that will be used.
     */
    private static final NpcDefinition DEFAULT = new NpcDefinition();

    /**
     * A fallback set of stats for NPCs.
     */
    private static final int[] DEFAULT_STATS = new int[] { 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    private boolean attackable;
    private boolean retreats;
    private boolean aggressiveTolerance = true;
    private boolean fightsBack = true;
    private int respawn;
    private int attackAnim;
    private int defenceAnim;
    private int deathAnim;
    private int combatFollowDistance;
    
    //VALUES from OSRSBOX
    private int id;
    private String name;
    private String examine;
    private int size;
    private boolean aggressive;
    private boolean poisonous;
    private int maxHit;
    private int hitpoints = 10;
    private int attackSpeed;
    private int combatLevel;
    private int[] stats;
    private int slayerLevel;

    /**
     * Attempts to get the {@link ItemDefinition} for the
     * given item.
     *
     * @param item
     * @return
     */
    public static NpcDefinition forId(int item) {
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

    public int getSize() {
        return size;
    }

    public boolean isAttackable() {
        return attackable;
    }

    public boolean doesRetreat() {
        return retreats;
    }

    public boolean isAggressive() {
        return aggressive;
    }

    /**
     * Whether this NPC gain tolerance towards players after being around them for a given length of time.
     *
     * @return {boolean}
     */
    public boolean buildsAggressionTolerance() {
        return aggressiveTolerance;
    }

    public boolean isPoisonous() {
        return poisonous;
    }

    public boolean doesFightBack() {
        return fightsBack;
    }

    public int getRespawn() {
        return respawn;
    }

    public int getMaxHit() {
        return maxHit;
    }

    public int getHitpoints() {
        return hitpoints;
    }

    public void setMaxHitpoints(int hitpoints) {
        this.hitpoints = hitpoints;
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }

    public int getAttackAnim() {
        return attackAnim;
    }

    public int getDefenceAnim() {
        return defenceAnim;
    }

    public int getDeathAnim() {
        return deathAnim;
    }

    public int getCombatLevel() {
        return combatLevel;
    }

    public int[] getStats() {
        if (stats == null) {
            return DEFAULT_STATS;
        }

        return stats;
    }

    public int getSlayerLevel() {
        return slayerLevel;
    }

    public int getCombatFollowDistance() {
        return combatFollowDistance;
    }
    
	public void update(OSRSBoxNPCDefinition o) {
		/*TODO extrapolated from data, should be from cache if has attack tooltip. */
		this.attackable = o.hitpoints > 0;
		
		//REAL
		this.id = o.id;
		this.name = o.name;
		this.examine = o.examine;
		this.size = o.size;
		this.aggressive = o.aggressive;
		this.poisonous = o.poisonous;
		this.maxHit = o.max_hit;
		this.hitpoints = o.hitpoints;
		this.attackSpeed = o.attack_speed;
		this.combatLevel = o.combat_level;
		this.stats = o.getStats();
		this.slayerLevel = o.slayer_level;
	}
}
