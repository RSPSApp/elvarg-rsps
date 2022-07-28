package com.elvarg.game.entity.impl.playerbot.fightstyle;

public abstract class PlayerBotFightStyle {

    // The weapon the PlayerBot switches back to once none of the weapon switches apply
    public abstract int getMainWeaponId();

    // The list of weapon switches and conditions under which they happen
    public abstract WeaponSwitch[] getWeaponSwitches();

}
