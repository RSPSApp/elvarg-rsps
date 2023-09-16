package com.runescape.engine.impl;

import com.runescape.Client;
import com.runescape.Configuration;
import com.runescape.draw.Console;
import com.runescape.model.content.Keybinding;
import net.runelite.api.GameState;
import net.runelite.api.KeyCode;
import net.runelite.rs.api.RSKeyHandler;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public final class KeyHandler implements KeyListener, FocusListener, RSKeyHandler {

    public static KeyHandler instance;

    static  {
        instance = new KeyHandler();
    }

    public final int[] keyArray = new int[128];
    private final int[] charQueue = new int[128];
    private int readIndex;
    private int writeIndex;
    int keyPressed = 0;
    public static volatile int idleCycles = 0;

    @Override
    public void focusGained(FocusEvent e) {}

    @Override
    public void focusLost(FocusEvent e) {
        for (int i = 0; i < 128; i++) {
            keyArray[i] = 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (Client.instance.getCallbacks() != null) {
            Client.instance.getCallbacks().keyTyped(e);
        }
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (Client.instance.getCallbacks() != null) {
            Client.instance.getCallbacks().keyPressed(event);
        }
        idleCycles = 0;
        int i = event.getKeyCode();

        if(i == KeyEvent.VK_SHIFT) {
            keyPressed = KeyCode.KC_SHIFT;
            Client.shiftDown = true;
        } else {
            keyPressed = i;
        }
        int keyChar = event.getKeyChar();

        if (event.isShiftDown()) {
            Client.shiftDown = true;
        }

        if (dialogueSkipKeys(i)) {
            return;
        }

        if (Keybinding.isBound(i))
            return;

        if (i == KeyEvent.VK_ESCAPE && Configuration.escapeCloseInterface) {
            if (Client.loggedIn && Client.openInterfaceId != -1) {
                Client.instance. packetSender.sendInterfaceClear();
                return;
            }
        }

        if (keyChar < 30)
            keyChar = 0;
        if (i == 37)
            keyChar = 1;
        if (i == 39)
            keyChar = 2;
        if (i == 38)
            keyChar = 3;
        if (i == 40)
            keyChar = 4;
        if (i == 17)
            keyChar = 5;
        if (i == 8)
            keyChar = 8;
        if (i == 127)
            keyChar = 8;
        if (i == 9)
            keyChar = 9;
        if (i == 10)
            keyChar = 10;
        if (i >= 112 && i <= 123)
            keyChar = (1008 + i) - 112;
        if (i == 36)
            keyChar = 1000;
        if (i == 35)
            keyChar = 1001;
        if (i == 33)
            keyChar = 1002;
        if (i == 34)
            keyChar = 1003;
        if (keyChar > 0 && keyChar < 128)
            keyArray[keyChar] = 1;
        if (keyChar > 4) {
            charQueue[writeIndex] = keyChar;
            writeIndex = writeIndex + 1 & 0x7f;
        }
    }

    public int readChar() {
        int k = -1;
        if (writeIndex != readIndex) {
            k = charQueue[readIndex];
            readIndex = readIndex + 1 & 0x7f;
        }
        return k;
    }


    @Override
    public void keyReleased(KeyEvent event) {
        if (Client.instance.getCallbacks() != null) {
            Client.instance.getCallbacks().keyReleased(event);
        }
        idleCycles = 0;
        int i = event.getKeyCode();
        char c = event.getKeyChar();

        if (i == KeyEvent.VK_SHIFT) {
            Client.shiftDown = false;
        }

        if (c < '\036')
            c = '\0';
        if (i == 37)
            c = '\001';
        if (i == 39)
            c = '\002';
        if (i == 38)
            c = '\003';
        if (i == 40)
            c = '\004';
        if (i == 17)
            c = '\005';
        if (i == 8)
            c = '\b';
        if (i == 127)
            c = '\b';
        if (i == 9)
            c = '\t';
        if (i == 10)
            c = '\n';
        if (c > 0 && c < '\200')
            keyArray[c] = 0;
    }

    public boolean dialogueSkipKeys(int key) {
        if (Client.instance.backDialogueId != -1 && !Client.instance.continuedDialogue
                && Client.instance.inputDialogState == 0 && !Client.instance.messagePromptRaised
                && !Console.consoleOpen) {
            int select = -1;
            if (key >= 49 && key <= 53) {
                int option = key - 49;
                switch (Client.instance.backDialogueId) {
                    case 2459:
                        select = 2461 + option;
                        break;
                    case 2469:
                        select = 2471 + option;
                        break;
                    case 2480:
                        select = 2482 + option;
                        break;
                    case 2492:
                        select = 2494 + option;
                        break;
                }
            }
            if (select != -1) {
                Client.instance.packetSender.sendButtonClick(select);
                return true;
            }
        }
        return false;
    }

}
