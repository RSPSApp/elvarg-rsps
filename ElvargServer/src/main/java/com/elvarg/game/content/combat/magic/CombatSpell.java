package com.elvarg.game.content.combat.magic;

import com.elvarg.game.content.sound.Sound;
import com.elvarg.game.content.combat.hit.PendingHit;
import com.elvarg.game.entity.impl.Mobile;
import com.elvarg.game.entity.impl.npc.NPC;
import com.elvarg.game.model.Animation;
import com.elvarg.game.model.Graphic;
import com.elvarg.game.model.Projectile;

import java.util.Optional;

/**
 * A {@link Spell} implementation used for combat related spells.
 *
 * @author lare96
 */
public abstract class CombatSpell extends Spell {

    @Override
    public void startCast(Mobile cast, Mobile castOn) {

        int castAnimation = -1;

        NPC npc = cast.isNpc() ? ((NPC) cast) : null;
        /*if(npc != null) {
			if(npc.getId() == 3496 || npc.getId() == 6278 || npc.getId() == 2000 || npc.getId() == 109 || npc.getId() == 3580 || npc.getId() == 2007) {
				castAnimation = npc.getDefinition().getAttackAnim();
			}
		}*/
        
        if (castAnimation().isPresent() && castAnimation == -1) {
            castAnimation().ifPresent(cast::performAnimation);
        } else {
            cast.performAnimation(new Animation(castAnimation));
        }

        // Then send the starting graphic.
        if (npc != null) {
            if (npc.getId() != 2000 && npc.getId() != 109 && npc.getId() != 3580 && npc.getId() != 2007) {
                startGraphic().ifPresent(cast::performGraphic);
            }
        } else {
            startGraphic().ifPresent(cast::performGraphic);
        }

        // Finally send the projectile
        if(projectile().getProjectileId() != -1) {
        	Projectile.sendProjectile(cast, castOn, projectile());
        }
    }

    public int getAttackSpeed() {
        int speed = 6;
        final CombatSpell spell = this;
        if (spell instanceof CombatAncientSpell) {

            if (spell == CombatSpells.SMOKE_RUSH.getSpell() || spell == CombatSpells.SHADOW_RUSH.getSpell()
                    || spell == CombatSpells.BLOOD_RUSH.getSpell() || spell == CombatSpells.ICE_RUSH.getSpell()
                    || spell == CombatSpells.SMOKE_BLITZ.getSpell() || spell == CombatSpells.SHADOW_BLITZ.getSpell()
                    || spell == CombatSpells.SANGUINESTI_STAFF.getSpell() || spell == CombatSpells.TRIDENT_OF_THE_SWAMP.getSpell()
                    || spell == CombatSpells.BLOOD_BLITZ.getSpell() || spell == CombatSpells.ICE_BLITZ.getSpell() || spell == CombatSpells.TRIDENT_OF_THE_SEAS.getSpell()) {
                speed = 5;
            }

        }
        return speed;
    }

    /**
     * The fixed ID of the spell implementation as recognized by the protocol.
     *
     * @return the ID of the spell, or <tt>-1</tt> if there is no ID for this
     * spell.
     */
    public abstract int spellId();

    /**
     * The maximum hit an {@link Mobile} can deal with this spell.
     *
     * @return the maximum hit able to be dealt with this spell implementation.
     */
    public abstract int maximumHit();

    /**
     * The animation played when the spell is cast.
     *
     * @return the animation played when the spell is cast.
     */
    public abstract Optional<Animation> castAnimation();

    /**
     * The starting graphic played when the spell is cast.
     *
     * @return the starting graphic played when the spell is cast.
     */
    public abstract Optional<Graphic> startGraphic();

    /**
     * The projectile played when this spell is cast.
     *
     * @return the projectile played when this spell is cast.
     */
    public abstract Projectile projectile();

    /**
     * The ending graphic played when the spell hits the victim.
     *
     * @return the ending graphic played when the spell hits the victim.
     */
    public abstract Optional<Graphic> endGraphic();

    /**
     * Fired when the spell hits the victim.
     *
     * @param cast     the entity casting the spell.
     * @param castOn   the entity targeted by the spell.
     * @param accurate if the spell was accurate.
     * @param damage   the amount of damage inflicted by this spell.
     */
    public abstract void finishCast(Mobile cast, Mobile castOn,
                                    boolean accurate, int damage);

    /**
     * Fired when hit is rolled
     */
    public void onHitCalc(PendingHit hit) {
        if (!hit.isAccurate()) {
            return;
        }
        this.spellEffectOnHitCalc(hit.getAttacker(), hit.getTarget(), hit.getTotalDamage());
    }


    /**
     * The effect this spell has on the target when hit is calculated
     *
     * @param cast   the entity casting this spell.
     * @param castOn the person being hit by this spell.
     * @param damage the damage inflicted.
     */
    public void spellEffectOnHitCalc(Mobile cast, Mobile castOn, int damage) {};

    /**
     * Gets the impact sound for a given spell
     *
     * @return {Sound} impactSound
     */
    public Sound impactSound() {
        return null;
    }
}