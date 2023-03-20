package com.elvarg.game.content.combat.method.impl.specials;

import com.elvarg.game.content.combat.CombatFactory;
import com.elvarg.game.content.combat.CombatSpecial;
import com.elvarg.game.content.combat.hit.PendingHit;
import com.elvarg.game.content.combat.method.impl.RangedCombatMethod;
import com.elvarg.game.content.combat.ranged.RangedData.RangedWeapon;
import com.elvarg.game.entity.impl.Mobile;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Animation;
import com.elvarg.game.model.Priority;
import com.elvarg.game.model.Projectile;

public class BlowpipeCombatMethod extends RangedCombatMethod {

    private static final Animation ANIMATION = new Animation(5061, Priority.HIGH);

    @Override
    public PendingHit[] hits(Mobile character, Mobile target) {
        return new PendingHit[] { new PendingHit(character, target, this, 2) };
    }

    @Override
    public boolean canAttack(Mobile character, Mobile target) {
        Player player = character.getAsPlayer();
        if (player.getCombat().getRangedWeapon() != RangedWeapon.TOXIC_BLOWPIPE) {
            return false;
        }
        return true;
    }

    @Override
    public void start(Mobile character, Mobile target) {
        final Player player = character.getAsPlayer();        
        CombatSpecial.drain(player, CombatSpecial.TOXIC_BLOWPIPE.getDrainAmount());
        player.performAnimation(ANIMATION);
        new Projectile(character, target, 1043, 32, 65, 35, 36).sendProjectile();
    }
}