package com.elvarg.game.content.morphing;

/**
 * @author Ynneh | 13/12/2022 - 11:53
 * <https://github.com/drhenny>
 */
public enum MorphInfo {

    GHOSTS(85, 1, 5539, 5538, 1),
    HILL_GIANT(2098, 1, 4649, 4650, 2),

    ;

    public int npcId;
    public int slayerLevelRequired;
    public int walkAnim, standAnim;

    public int size;
    MorphInfo(int npcId, int slayerLevelRequired, int walkAnim, int standAnim, int size) {
        this.npcId = npcId;
        this.slayerLevelRequired = slayerLevelRequired;
        this.walkAnim = walkAnim;
        this.standAnim = standAnim;
        this.size = size;
    }
}
