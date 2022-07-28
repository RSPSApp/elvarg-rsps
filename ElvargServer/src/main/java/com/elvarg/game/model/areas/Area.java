package com.elvarg.game.model.areas;

import com.elvarg.game.entity.impl.Mobile;
import com.elvarg.game.entity.impl.npc.NPC;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.entity.impl.playerbot.PlayerBot;
import com.elvarg.game.model.Boundary;

import java.util.*;

public abstract class Area {

    private final List<Boundary> boundaries;

    private final Map<Integer, NPC> npcs;
    private final Map<Integer, Player> players;
    private final Map<Integer, PlayerBot> playerBots;

    public Area(List<Boundary> boundaries) {
        this.boundaries = boundaries;
        this.npcs = new HashMap<Integer, NPC>();
        this.players = new HashMap<Integer, Player>();
        this.playerBots = new HashMap<Integer, PlayerBot>();
    }

    public void enter(Mobile character) {

    }

    public void postEnter(Mobile character) {
        if (character.isPlayerBot()) {
            this.playerBots.put(character.getIndex(), character.getAsPlayerBot());
            return;
        }

        if (character.isPlayer()) {
            this.players.put(character.getIndex(), character.getAsPlayer());
            return;
        }

        if (character.isNpc()) {
            this.npcs.put(character.getIndex(), character.getAsNpc());
        }
    }

    public void leave(Mobile character, boolean logout) {
    }

    public void postLeave(Mobile character) {
        if (character.isPlayerBot()) {
            this.playerBots.remove(character.getIndex());
            return;
        }

        if (character.isPlayer()) {
            this.players.remove(character.getIndex());
            return;
        }

        if (character.isNpc()) {
            this.npcs.remove(character.getIndex());
        }
    }
    public abstract void process(Mobile character);

    public abstract boolean canTeleport(Player player);

    public abstract boolean canAttack(Mobile attacker, Mobile target);

    public abstract void defeated(Player player, Mobile character);

    public abstract boolean canTrade(Player player, Player target);

    public abstract boolean isMulti(Mobile character);

    public abstract boolean canEat(Player player, int itemId);

    public abstract boolean canDrink(Player player, int itemId);

    public abstract boolean dropItemsOnDeath(Player player, Optional<Player> killer);

    public abstract boolean handleDeath(Player player, Optional<Player> killer);

    public abstract void onPlayerRightClick(Player player, Player rightClicked, int option);

    public abstract boolean handleObjectClick(Player player, int objectId, int type);
    
    public abstract boolean overridesNpcAggressionTolerance(Player player, int npcId);

    public List<Boundary> getBoundaries() {
        return boundaries;
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public Map<Integer, NPC> getNpcs() {
        return this.npcs;
    }

    public Map<Integer, Player> getPlayers() {
        return this.players;
    }

    public Map<Integer, PlayerBot> getPlayerBots() {
        return this.playerBots;
    }
}
