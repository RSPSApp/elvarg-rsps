package com.elvarg.game.model.rights;

import com.elvarg.game.entity.impl.player.Player;

public enum PlayerRights {
    NONE(-1, ""),
    MODERATOR(618, "[<col=0000ff>Moderator@bla@]"),
    ADMINISTRATOR(619, "[@yel@Administrator@bla@]"),
    OWNER(620, "[@red@Owner@bla@]"),
    DEVELOPER(621, "[<col=7900FF>Developer@bla@]"),
    ;
	
	private final int spriteId;
	private final String yellTag;
	
	PlayerRights(int spriteId, String yellTag) {
		this.spriteId = spriteId;
		this.yellTag = yellTag;
	}

	public int getSpriteId() {
		return spriteId;
	}

	public String getYellTag() {
		return yellTag;
	}

    public static boolean isAdmin(Player player) {
		return player.getRights().ordinal() >= ADMINISTRATOR.ordinal();
    }
}
