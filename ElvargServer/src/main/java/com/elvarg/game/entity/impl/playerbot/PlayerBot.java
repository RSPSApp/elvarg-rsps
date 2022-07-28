package com.elvarg.game.entity.impl.playerbot;

import com.elvarg.game.GameConstants;
import com.elvarg.game.World;
import com.elvarg.game.content.presets.Presetable;
import com.elvarg.game.content.presets.Presetables;
import com.elvarg.game.content.presets.impl.*;
import com.elvarg.game.definition.PlayerBotDefinition;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.entity.impl.playerbot.commands.BotCommand;
import com.elvarg.game.entity.impl.playerbot.commands.FollowPlayer;
import com.elvarg.game.entity.impl.playerbot.commands.HoldItems;
import com.elvarg.game.entity.impl.playerbot.commands.LoadPreset;
import com.elvarg.game.entity.impl.playerbot.fightstyle.PlayerBotFightStyle;
import com.elvarg.game.entity.impl.playerbot.fightstyle.impl.*;
import com.elvarg.game.entity.impl.playerbot.interaction.*;
import com.elvarg.game.entity.updating.PlayerUpdating;
import com.elvarg.game.model.ChatMessage;
import com.elvarg.game.model.Location;
import com.elvarg.net.PlayerBotSession;
import com.elvarg.net.PlayerSession;
import com.elvarg.util.Misc;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static com.elvarg.game.entity.impl.playerbot.commands.LoadPreset.LOAD_PRESET_BUTTON_ID;

public class PlayerBot extends Player {

    private Location spawnPosition = GameConstants.DEFAULT_LOCATION;

    public Location getSpawnPosition() {
        return this.spawnPosition;
    }

    // The current interaction of this PlayerBot
    private InteractionState currentState = InteractionState.IDLE;

    /**
     * Pre-made sets which are defined for Player Bots to use.
     */
    public static final Map<Presetable, PlayerBotFightStyle> PLAYER_BOT_PRESETS  = new HashMap<Presetable, PlayerBotFightStyle>() {{
        put(ObbyMauler_57.preset, new ObbyMaulerFightStyle());
        put(Elvemage_86.preset, new ElvemageFightStyle());
        put(GMauler_70.preset, new ObbyMaulerFightStyle());
        put(DDSPure_M_73.preset, new DDSPureMFightStyle());
        put(DDSPure_R_73.preset, new DDSPureRFightStyle());
        put(NHPure_83.preset, new NHPureFightStyle());
    }};

    private static final BotCommand[] chatCommands = new BotCommand[] {
            new FollowPlayer(), new HoldItems(), new LoadPreset()
    };

    private BotCommand activeCommand;

    public enum InteractionState {
        IDLE,
        COMMAND;
    }

    public BotCommand getActiveCommand() { return this.activeCommand; }

    public void stopCommand() {
        if (this.getActiveCommand() != null) {
            this.getActiveCommand().stop(this);
        }

        this.setInteractingWith(null);
        this.activeCommand = null;
        this.setCurrentState(PlayerBot.InteractionState.IDLE);
    }

    public void startCommand(BotCommand _command, Player _player, String[] args) {
        this.setInteractingWith(_player);
        this.activeCommand = _command;
        this.setCurrentState(InteractionState.COMMAND);
        _command.start(this, args);
    }

    private PlayerBotDefinition definition;

    private Player interactingWith;

    public Player getInteractingWith() { return this.interactingWith; }

    public void setInteractingWith(Player _interact) { this.interactingWith = _interact; }

    private MovementInteraction movementInteraction;

    private ChatInteraction chatInteraction;

    private TradingInteraction tradingInteraction;

    private CombatInteraction combatInteraction;

    public PlayerBotDefinition getDefinition() { return this.definition; }
    public InteractionState getCurrentState() {
        return this.currentState;
    }

    public void setCurrentState(InteractionState interactionState) {
        this.currentState = interactionState;
    }

    public BotCommand[] getChatCommands() {
        return this.chatCommands;
    }

    public ChatInteraction getChatInteraction() {
        return this.chatInteraction;
    }

    public TradingInteraction getTradingInteraction() {
        return this.tradingInteraction;
    }

    public MovementInteraction getMovementInteraction() {
        return this.movementInteraction;
    }

    public CombatInteraction getCombatInteraction() {
        return this.combatInteraction;
    }

    /**
     * Creates this player bot from a given definition.
     *
     */
    public PlayerBot(PlayerBotDefinition _definition) {
        super(new PlayerBotSession(), _definition.getSpawnLocation());

        this.setUsername(_definition.getUsername()).setLongUsername(Misc.stringToLong(_definition.getUsername()))
                .setPasswordHashWithSalt(GameConstants.PLAYER_BOT_PASSWORD).setHostAddress("127.0.0.1");

        this.definition = _definition;
        this.tradingInteraction = new TradingInteraction(this);
        this.chatInteraction = new ChatInteraction(this);
        this.movementInteraction = new MovementInteraction(this);
        this.combatInteraction = new CombatInteraction(this);

        if (!World.getAddPlayerQueue().contains(this)) {
            World.getAddPlayerQueue().add(this);
        }
    }

    // Send a regular chat from this PlayerBot
    public void sendChat(String message) {
        this.getChatMessageQueue().add(new ChatMessage(0, 0, Misc.textPack(message)));
    }

    // Manually update the players local to this PlayerBot
    public void updateLocalPlayers() {
        if (this.getLocalPlayers().size() == 0) {
            return;
        }

        for (Player localPlayer : this.getLocalPlayers()) {
            PlayerUpdating.update(localPlayer);
        }
    }

    @Override
    public boolean autoRetaliate() {
        // PlayerBots always retaliate
        return true;
    }

    @Override
    public void onLogin() {
        super.onLogin();

        this.setCurrentPreset(this.getDefinition().getPreset());
        Presetables.handleButton(this, LOAD_PRESET_BUTTON_ID);
    }

    @Override
    public void resetAttributes() {
        super.resetAttributes();

        stopCommand();
    }
}
