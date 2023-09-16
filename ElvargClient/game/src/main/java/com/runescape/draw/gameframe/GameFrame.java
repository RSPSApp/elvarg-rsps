package com.runescape.draw.gameframe;

import com.runescape.graphics.sprite.Sprite;

import java.util.List;

public abstract class GameFrame {

    /**
     * Contains information about the Game frame, revision type.
     * @return the information about the game frame, revision, e.g 474.
     */
    public abstract String type();

    /**
     * Draws the tab area for the revision type.
     * @return the tab area for revision.
     */
    public abstract void drawTabArea();

    /**
     * Draws the Chat area for the revision type.
     * @return the Chat area for revision.
     */
    public abstract void drawChatLook(int yOffset);

    /**
     * Click regions for the tab the revision type.
     * @return the click tab areas for revision.
     */
    public abstract void processTabAreaClick();

    /**
     * Click regions for the tab the revision type.
     * @return the click tab areas for revision.
     */
    public abstract void processTabClick();

    /**
     * Set Compass Image.
     * @return the Compass for map revision.
     */
    public abstract Sprite setCompass();

    /**
     * Scroll bar for the revision type.
     * @return state for the revision.
     */
    public abstract boolean oldScroll();


    /**
     * User Hovering Tab.
     * @return state for the revision.
     */
    public abstract Boolean processTabHover();

    /**
     * Chat Buttons for the revision type.
     * @return list of chat Buttons.
     */
    public abstract List<Integer> chatButtons();

}
