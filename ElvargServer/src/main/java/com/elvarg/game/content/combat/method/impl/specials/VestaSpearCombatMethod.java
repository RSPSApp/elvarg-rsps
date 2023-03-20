package com.elvarg.game.content.combat.method.impl.specials;

import com.elvarg.game.content.combat.CombatSpecial;
import com.elvarg.game.content.combat.hit.HitDamage;
import com.elvarg.game.content.combat.hit.HitMask;
import com.elvarg.game.content.combat.method.impl.MeleeCombatMethod;
import com.elvarg.game.entity.impl.Mobile;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.*;
import com.elvarg.util.Misc;

import java.util.ArrayList;
import java.util.List;

public class VestaSpearCombatMethod extends MeleeCombatMethod {

    private static final Animation ANIMATION = new Animation(8184, Priority.HIGH);

    private static final Graphic GRAPHIC = new Graphic(1627, GraphicHeight.HIGH, Priority.HIGH);

    @Override
    public void start(Mobile character, Mobile target) {
        CombatSpecial.drain(character, CombatSpecial.VESTA_SPEAR.getDrainAmount());
        character.performAnimation(ANIMATION);
        Location targetPos = target.getLocation();
        List<Location> attackPositions = new ArrayList<>();
        attackPositions.add(targetPos);
        for (int i = 0; i < 2; i++) {
            attackPositions.add(new Location((targetPos.getX() - 1) ,
                    (targetPos.getY() - 1)));
        }
            if (target.getLocation().equals(targetPos)) {
                target.getCombat().getHitQueue()
                        .addPendingDamage(new HitDamage(Misc.getRandom(30), HitMask.RED));
            }
            if (target.getLocation().equals(targetPos)) {
                target.getCombat().getHitQueue()
                        .addPendingDamage(new HitDamage(Misc.getRandom(30), HitMask.RED));
        }
        }
    }