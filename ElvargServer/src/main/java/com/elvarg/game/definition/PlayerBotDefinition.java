package com.elvarg.game.definition;

import com.elvarg.game.content.presets.Presetable;
import com.elvarg.game.entity.impl.playerbot.PlayerBot;
import com.elvarg.game.model.Location;

public class PlayerBotDefinition {

    private String username;

    private Location spawnLocation;

    private Presetable preset;

    public PlayerBotDefinition(String _username, Location _spawnLocation, Presetable _preset) {
        this.username = _username;
        this.spawnLocation = _spawnLocation;
        this.preset = _preset;
    }

    public String getUsername() {
        return this.username;
    }

    public Location getSpawnLocation() {
        return this.spawnLocation;
    }

    public Presetable getPreset() { return this.preset; }
}
