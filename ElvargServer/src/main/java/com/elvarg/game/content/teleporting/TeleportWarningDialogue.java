package com.elvarg.game.content.teleporting;

import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.dialogues.builders.DynamicDialogueBuilder;
import com.elvarg.game.model.dialogues.entries.impl.OptionDialogue;

/**
 * @author Ynneh | 09/12/2022 - 12:47
 * <https://github.com/drhenny>
 */
public class TeleportWarningDialogue extends DynamicDialogueBuilder {

    private RequestType requestType;
    private TeleportRequest req;

    private Player requester;

    public TeleportWarningDialogue(Player requester, RequestType requestType, TeleportRequest req) {
        super();
        this.requester = requester;
        this.requestType = requestType;
        this.req = req;
    }

    @Override
    public void build(Player player) {
        add(new OptionDialogue(0, "Teleport to Wilderness?", (option) -> {
            switch (option) {
                case FIRST_OPTION:
                    player.teleportRequestManager.afterDialogue(player, requester, requestType, req);
                    break;
                default:

                    break;
            }
        }, "Yes, let's go.", "No, bruv."));
    }
}
