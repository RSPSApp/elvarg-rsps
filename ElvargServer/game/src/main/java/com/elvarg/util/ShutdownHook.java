package com.elvarg.util;

import com.elvarg.game.World;
import com.elvarg.plugin.event.EventManager;
import com.elvarg.plugin.event.impl.ServerStoppedEvent;

import java.util.logging.Logger;

public class ShutdownHook extends Thread {

    /**
     * The ShutdownHook logger to print out information.
     */
    private static final Logger logger = Logger.getLogger(ShutdownHook.class.getName());

    @Override
    public void run() {
        EventManager.INSTANCE.post(new ServerStoppedEvent());
        logger.info("The shutdown hook is processing all required actions...");
        World.savePlayers();
        logger.info("The shudown hook actions have been completed, shutting the server down...");
    }
}
