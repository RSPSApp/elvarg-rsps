package com.elvarg.game.content.combat.method.impl.specials;

import com.elvarg.game.content.combat.CombatFactory;
import com.elvarg.game.content.combat.CombatSpecial;
import com.elvarg.game.content.combat.hit.PendingHit;
import com.elvarg.game.content.combat.method.impl.MeleeCombatMethod;
import com.elvarg.game.entity.impl.Mobile;
import com.elvarg.game.model.*;
import com.elvarg.game.task.impl.CombatPoisonEffect.PoisonType;
import com.elvarg.util.Misc;
import com.elvarg.util.TileUtils;
import com.elvarg.util.timers.Timer;
import com.elvarg.util.timers.TimerKey;

public class ZamorakspearCombatMethod extends MeleeCombatMethod {

    private static final Animation ANIMATION = new Animation(1064, Priority.HIGH);
    private static final Graphic GRAPHIC = new Graphic(253, GraphicHeight.HIGH, Priority.HIGH);

    @Override
    public void start(Mobile character, Mobile target) {
        CombatSpecial.drain(character, CombatSpecial.ZAMMY_SPEAR.getDrainAmount());
        character.performAnimation(ANIMATION);
        character.getTimers().register(new Timer(TimerKey.COMBAT_ATTACK, 2));
        character.getCombat().setTarget(target);
        //The special attack can only be used on targets that take up one square.
        if(character.isPlayer() && target.isNpc()) {
            if(target.size() > 1) {
                character.getAsPlayer().getPacketSender().sendMessage("You can't spear this monster!");
                return;
            }
        }

        //The effects of this special are non-stackable, meaning that players cannot use the spear's special attack on a target who is already stunned.
        if (target.getTimers().has(TimerKey.STUN)) {
            character.getAsPlayer().getPacketSender().sendMessage("They are already stunned!");
            return;
        }
        // Since this weapon doesn't deal damage, manually extend the in-combat timer.
        target.getTimers().extendOrRegister(TimerKey.COMBAT_ATTACK, 16);
        target.getMovementQueue().reset(); // clears any pre-existing movement. spear replaces their movement
        target.getMovementQueue().resetFollow();
        //It pushes an opponent back and stuns them for three seconds.
        Location targTile = target.getLocation().transform(-1,0);
        boolean legal = target.getMovementQueue().canWalk(-1, 0);
        if (!legal) {
            targTile = target.getLocation().transform(-1,0);
            legal = target.getMovementQueue().canWalk(1, 0);
            if (!legal) {
                return; // No valid move to go!
            }
        }

        //  @Override
  //  public void handleAfterHitEffects(PendingHit hit) {
   //     Mobile target = hit.getTarget();

        if (target.getHitpoints() <= 0) {
            return;
        }
        // Block if our movement is locked.
        if (target.getMovementQueue().getMobility().canMove()) {
            return;
        }

        target.performGraphic(GRAPHIC);
        target.getTimers().register(TimerKey.STUN, 5);
        target.getTimers().register(TimerKey.FREEZE, 5);
        target.getMovementQueue().addStep(targTile);
        }
    }
