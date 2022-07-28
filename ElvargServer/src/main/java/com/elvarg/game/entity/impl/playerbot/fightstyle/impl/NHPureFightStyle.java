package com.elvarg.game.entity.impl.playerbot.fightstyle.impl;

import com.elvarg.game.content.PrayerHandler;
import com.elvarg.game.content.combat.CombatSpecial;
import com.elvarg.game.content.combat.magic.CombatSpells;
import com.elvarg.game.entity.impl.Mobile;
import com.elvarg.game.entity.impl.playerbot.PlayerBot;
import com.elvarg.game.entity.impl.playerbot.fightstyle.PlayerBotFightStyle;
import com.elvarg.game.entity.impl.playerbot.fightstyle.WeaponSwitch;
import com.elvarg.game.model.ItemInSlot;

import static com.elvarg.util.ItemIdentifiers.*;

public class NHPureFightStyle extends PlayerBotFightStyle {

    @Override
    public int getMainWeaponId() {
        return ANCIENT_STAFF;
    }

    @Override
    public WeaponSwitch[] getWeaponSwitches() {
        return new WeaponSwitch[] {

                new WeaponSwitch(CombatSpells.ICE_BARRAGE.getSpell()) {
                    @Override
                    public boolean shouldSwitch(PlayerBot playerBot, Mobile enemy) {
                        // Freeze the player if they can move
                        return enemy.getMovementQueue().canMove();
                    }

                    @Override
                    public void afterSwitch(PlayerBot playerBot) {
                        // TODO: Walk a few sqs away for farcasting
                    }
                },

                new WeaponSwitch(new ItemInSlot(DRAGON_DAGGER_P_PLUS_PLUS_, 5)) {
                    @Override
                    public boolean shouldSwitch(PlayerBot playerBot, Mobile enemy) {
                        return playerBot.getSpecialPercentage() >= 25 &&
                                // Don't switch to Melee if we're frozen
                                playerBot.getMovementQueue().canMove() &&
                                // Switch if the enemy has enabled protect from missles or has low health
                                (enemy.getPrayerActive()[PrayerHandler.PROTECT_FROM_MAGIC] || enemy.getHitpoints() < 50);
                    }

                    @Override
                    public void afterSwitch(PlayerBot playerBot) {
                        CombatSpecial.activate(playerBot);
                    }
                },

                new WeaponSwitch(new ItemInSlot(RUNE_CROSSBOW, 0)) {
                    @Override
                    public boolean shouldSwitch(PlayerBot playerBot, Mobile enemy) {
                        return  // Don't switch if enemy has enabled protect from missles
                                !enemy.getPrayerActive()[PrayerHandler.PROTECT_FROM_MISSILES];
                    }
                },

        };
    }
}
