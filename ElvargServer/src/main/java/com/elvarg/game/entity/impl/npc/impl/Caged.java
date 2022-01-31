package com.elvarg.game.entity.impl.npc.impl;

import com.elvarg.game.entity.impl.npc.NPC;
import com.elvarg.game.model.Location;
import com.elvarg.game.model.areas.AreaManager;

public class Caged extends NPC {

    public Caged(int id, Location position) {
        super(id, position);
        setHitpoints(250);
    }

    @Override
    public void process(){

        getTimers().process();

        // Handles random walk and retreating from fights
        getMovementQueue().process();


        getCombat().processNoAttack();

        // Process areas..
        AreaManager.process(this);

    }


}
