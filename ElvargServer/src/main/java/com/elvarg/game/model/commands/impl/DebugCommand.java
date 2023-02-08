package com.elvarg.game.model.commands.impl;

import com.elvarg.game.collision.RegionManager;
import com.elvarg.game.definition.PlayerBotDefinition;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.entity.impl.playerbot.PlayerBot;
import com.elvarg.game.entity.impl.playerbot.fightstyle.FighterPreset;
import com.elvarg.game.entity.impl.playerbot.fightstyle.impl.DDSPureMFighterPreset;
import com.elvarg.game.entity.impl.playerbot.fightstyle.impl.F2PMeleeFighterPreset;
import com.elvarg.game.entity.impl.playerbot.minigame.CastlewarsBot;
import com.elvarg.game.model.Location;
import com.elvarg.game.model.commands.Command;
import com.elvarg.game.model.dialogues.builders.impl.NieveDialogue;
import com.elvarg.game.model.rights.PlayerRights;

public class DebugCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {

        for (int i = 0; i < 10; i++) {
            System.err.println("addded bot #"+i);
            CastlewarsBot.create(new PlayerBot(new PlayerBotDefinition("cwarsbot"+i, new Location(2440, 3090, 0), new DDSPureMFighterPreset())));
        }
        //System.out.println(RegionManager.wallsExist(player.getLocation().clone(), player.getPrivateArea()));
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
