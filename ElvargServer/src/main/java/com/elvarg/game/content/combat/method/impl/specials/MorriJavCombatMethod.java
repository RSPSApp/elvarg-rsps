package com.elvarg.game.content.combat.method.impl.specials;

import com.elvarg.game.content.combat.CombatFactory;
import com.elvarg.game.content.combat.CombatSpecial;
import com.elvarg.game.content.combat.CombatType;
import com.elvarg.game.content.combat.hit.HitDamage;
import com.elvarg.game.content.combat.hit.HitMask;
import com.elvarg.game.content.combat.hit.PendingHit;
import com.elvarg.game.content.combat.method.impl.RangedCombatMethod;
import com.elvarg.game.content.combat.ranged.RangedData.RangedWeapon;
import com.elvarg.game.entity.impl.Mobile;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Animation;
import com.elvarg.game.model.Priority;
import com.elvarg.game.model.Projectile;
import com.elvarg.util.Misc;

public class MorriJavCombatMethod extends RangedCombatMethod {

    private static final Animation ANIMATION = new Animation(806, Priority.HIGH);

    @Override
    public PendingHit[] hits(Mobile character, Mobile target) {
        return new PendingHit[] { new PendingHit(character, target, this, 2) };
    }

    @Override
    public boolean canAttack(Mobile character, Mobile target) {
        Player player = character.getAsPlayer();
        if (player.getCombat().getRangedWeapon() != RangedWeapon.MORRIGANS_JAVELIN) {
            return false;
        }
        return true;
    }


    @Override
    public void start(Mobile character, Mobile target) {
        final Player player = character.getAsPlayer();        
        CombatSpecial.drain(player, CombatSpecial.MORRIJAVS.getDrainAmount());
        player.performAnimation(ANIMATION);
        new Projectile(character, target, 1622, 30, 60, 40, 36).sendProjectile();
        target.sendMessage("You start to bleed as a result of the javelin strike");
        target.getCombat().getHitQueue().addPendingHit(new PendingHit(character, target,this,8));
        target.getCombat().getHitQueue().addPendingHit(new PendingHit(character, target,this,16));
        target.getCombat().getHitQueue().addPendingHit(new PendingHit(character, target,this,12));
      //  target.getCombat().getHitQueue().addPendingHit(new HitDamage(Misc.getRandom(10), HitMask.YELLOW));
    }
}