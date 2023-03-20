package com.elvarg.game.content.combat.method.impl.specials;

import com.elvarg.game.content.combat.CombatSpecial;
import com.elvarg.game.content.combat.method.impl.MeleeCombatMethod;
import com.elvarg.game.entity.impl.Mobile;
import com.elvarg.game.model.Animation;
import com.elvarg.game.model.Graphic;
import com.elvarg.game.model.GraphicHeight;
import com.elvarg.game.model.Priority;

public class VestaLongswordCombatMethod extends MeleeCombatMethod {

    private static final Animation ANIMATION = new Animation(8145, Priority.HIGH);

    @Override
    public void start(Mobile character, Mobile target) {
        CombatSpecial.drain(character, CombatSpecial.VESTA_LONGSWORD.getDrainAmount());
        character.performAnimation(ANIMATION);
    }
}