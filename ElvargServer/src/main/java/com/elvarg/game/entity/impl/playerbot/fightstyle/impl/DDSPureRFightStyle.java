package com.elvarg.game.entity.impl.playerbot.fightstyle.impl;

import com.elvarg.game.content.combat.CombatSpecial;
import com.elvarg.game.entity.impl.Mobile;
import com.elvarg.game.entity.impl.playerbot.PlayerBot;
import com.elvarg.game.entity.impl.playerbot.fightstyle.PlayerBotFightStyle;
import com.elvarg.game.entity.impl.playerbot.fightstyle.WeaponSwitch;
import com.elvarg.game.model.ItemInSlot;

import static com.elvarg.util.ItemIdentifiers.*;

public class DDSPureRFightStyle extends PlayerBotFightStyle {

    private WeaponSwitch[] weaponSwitches = new WeaponSwitch[] {

        new WeaponSwitch(new ItemInSlot(DRAGON_DAGGER_P_PLUS_PLUS_, 0)) {
            @Override
            public boolean shouldSwitch(PlayerBot playerBot, Mobile enemy) {
                return playerBot.getSpecialPercentage() >= 25 &&
                        // Switch if the enemy has lowish health
                        enemy.getHitpoints() < 60;
            }

            @Override
            public void afterSwitch(PlayerBot playerBot) {
                CombatSpecial.activate(playerBot);
            }
        },

    };

    @Override
    public int getMainWeaponId() {
        return MAGIC_SHORTBOW;
    }

    @Override
    public WeaponSwitch[] getWeaponSwitches() {
        return this.weaponSwitches;
    }

}
