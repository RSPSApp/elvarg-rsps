package com.elvarg.game.definition;

import com.elvarg.game.definition.loader.impl.NpcDefinitionLoader.NPCAnimSet;
import com.elvarg.game.definition.loader.impl.NpcDefinitionLoader.OSRSBoxNPCDefinition;
import com.elvarg.util.ElvargNpcDefinitions;
import com.elvarg.util.SuppliedHashMap;

/**
 * Represents an npc's definition. Holds its information, such as name and
 * combat level.
 *
 * @author Professor Oak
 */
public class NpcDefinition {

    public static final int ATTACK_LEVEL = 0, STRENGTH_LEVEL = 1, DEFENCE_LEVEL = 2, RANGED_LEVEL = 3, MAGIC_LEVEL = 4;
    public static final int ATTACK_MELEE = 5;

    /*
     * public static final int ATTACK_STAB = 0, ATTACK_SLASH = 1, ATTACK_CRUSH = 2,
     * ATTACK_MAGIC = 3, ATTACK_RANGE = 4,
     * 
     * DEFENCE_STAB = 10, DEFENCE_SLASH = 11, DEFENCE_CRUSH = 12, DEFENCE_MAGIC =
     * 13, DEFENCE_RANGE = 14,
     * 
     * STRENGTH = 0, RANGED_STRENGTH = 1, MAGIC_STRENGTH = 2, PRAYER = 3
     */
    ;

    /**
     * The map containing all our {@link NpcDefinition}s.
     */
    public static final SuppliedHashMap<Integer, NpcDefinition> definitions = new SuppliedHashMap<>(NpcDefinition::new);

    /**
     * The default {@link ItemDefinition} that will be used.
     */
    private static final NpcDefinition DEFAULT = new NpcDefinition();

    /**
     * A fallback set of stats for NPCs.
     */
    private static final int[] DEFAULT_STATS = new int[] { 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    public boolean canTileStack = true;

    public boolean canTileStack() {
        return canTileStack;
    }

    private int attackAnim;
    private int defenceAnim;
    private int deathAnim;

    // VALUES from OSRSBOX
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

    // VALUES Calculated from OSRSBOX definitions
    private boolean attackable;
    private boolean retreats;
    private boolean aggressiveTolerance = true;
    private boolean fightsBack = true;
    private int respawn;
    private int combatFollowDistance;

    /**
     * Attempts to get the {@link ItemDefinition} for the given item.
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
     * Whether this NPC gain tolerance towards players after being around them for a
     * given length of time.
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
        /* TODO extrapolated from data, should be from cache if has attack tooltip. */
        // right now set to if hitpoints > 0. There are no exceptions in old elvarg data
        // to this approach.
        this.attackable = o.hitpoints > 0;

        // REAL
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

        /* TODO placeholder values */
        this.combatFollowDistance = ElvargNpcDefinitions.combatFollowDistance(id, attackable);
        this.respawn = ElvargNpcDefinitions.respawnTime(id, attackable);
        this.aggressiveTolerance = ElvargNpcDefinitions.aggressionTolerance(id);
        this.retreats = ElvargNpcDefinitions.retreats(id, attackable);
        this.fightsBack = ElvargNpcDefinitions.fightsBack(id, attackable);
    }

    public void update(int id, NPCAnimSet set) {
        this.id = id;
        this.attackAnim = set.attackAnim;
        this.defenceAnim = set.defenceAnim;
        this.deathAnim = set.deathAnim;
    }
}
