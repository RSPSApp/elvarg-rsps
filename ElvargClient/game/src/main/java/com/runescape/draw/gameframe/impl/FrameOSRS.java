package com.runescape.draw.gameframe.impl;

import com.runescape.Client;
import com.runescape.draw.Rasterizer2D;
import com.runescape.draw.Rasterizer3D;
import com.runescape.draw.gameframe.GameFrame;
import com.runescape.engine.GameEngine;
import com.runescape.engine.impl.MouseHandler;
import com.runescape.graphics.sprite.Sprite;
import com.runescape.graphics.widget.Widget;

import java.util.List;

import static com.runescape.Client.*;

public class FrameOSRS extends GameFrame {

    Client instance;

    public FrameOSRS(Client instance) {
        this.instance = instance;
    }

    @Override
    public String type() {
        return "osrs";
    }

    @Override
    public void drawTabArea() {
        final int xOffset = !instance.isResized() ? 516 : canvasWidth - 241;
        final int yOffset = !instance.isResized() ? 168 : canvasHeight - 336;

        if (!instance.isResized()) {
            spriteCache.draw(21, xOffset, yOffset);
        } else if (!stackSideStones) {
            Rasterizer2D.drawTransparentBox(canvasWidth - 217, canvasHeight - 304, 195, 270, 0x3E3529, transparentTabArea ? 80 : 256);
            spriteCache.draw(47, xOffset, yOffset);
        } else {
            if (canvasWidth >= 1000) {
                if (showTabComponents) {
                    Rasterizer2D.drawTransparentBox(canvasWidth - 197, canvasHeight - 304, 197, 265, 0x3E3529, transparentTabArea ? 80 : 256);
                    spriteCache.draw(50, canvasWidth - 204, canvasHeight - 311);
                }
                for (int x = canvasWidth - 417, y = canvasHeight - 37, index = 0; x <= canvasWidth - 30 && index < 13; x += 32, index++) {
                    spriteCache.draw(46, x, y);
                }
            } else {
                if (showTabComponents) {
                    Rasterizer2D.drawTransparentBox(canvasWidth - 197, canvasHeight - 341, 195, 265, 0x3E3529, transparentTabArea ? 80 : 256);
                    spriteCache.draw(50, canvasWidth - 204, canvasHeight - 348);
                }
                for (int x = canvasWidth - 226, y = canvasHeight - 73, index = 0; x <= canvasWidth - 32 && index < 7; x += 32, index++) {
                    spriteCache.draw(46, x, y);
                }
                for (int x = canvasWidth - 226, y = canvasHeight - 37, index = 0; x <= canvasWidth - 32 && index < 7; x += 32, index++) {
                    spriteCache.draw(46, x, y);
                }
            }
        }
        if (instance.overlayInterfaceId == -1) {
            drawRedStones();
            drawSideIcons();
        }
        if (showTabComponents) {
            int x = !instance.isResized() ? xOffset + 31 : canvasWidth - 215;
            int y = !instance.isResized() ? yOffset + 37 : canvasHeight - 299;
            if (stackSideStones && instance.isResized()) {
                x = canvasWidth - 197;
                y = canvasWidth >= 1000 ? canvasHeight - 303 : canvasHeight - 340;
            }
            try {
                if (instance.overlayInterfaceId != -1) {
                    instance.drawInterface(0, x, Widget.interfaceCache[instance.overlayInterfaceId], y);
                } else if (tabInterfaceIDs[tabId] != -1) {
                    instance.drawInterface(0, x, Widget.interfaceCache[tabInterfaceIDs[tabId]], y);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    final int[] sideIconsX = {17, 49, 83, 114, 146, 180, 214, 16, 49, 82, 116, 148, 184, 217},
            sideIconsY = {9, 7, 7, 5, 2, 3, 7, 303, 306, 306, 302, 305, 303, 304, 306},
            sideIconsId = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13},
            sideIconsTab = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};

    public void drawSideIcons() {
        final int xOffset = !instance.isResized() ? 516 : canvasWidth - 247;
        final int yOffset = !instance.isResized() ? 168 : canvasHeight - 336;
        if (!instance.isResized() || instance.isResized() && !stackSideStones) {
            for (int i = 0; i < sideIconsTab.length; i++) {
                if (tabInterfaceIDs[sideIconsTab[i]] != -1) {
                    if (sideIconsId[i] != -1) {
                        Sprite sprite = instance.sideIcons[sideIconsId[i]];
                        if (i == 13) {
                            spriteCache.draw(360, sideIconsX[i] + xOffset, sideIconsY[i] + yOffset, true);
                        } else {
                            sprite.drawSprite(sideIconsX[i] + xOffset, sideIconsY[i] + yOffset);
                        }

                    }
                }
            }
        } else if (stackSideStones && canvasWidth < 1000) {
            int[] iconId = {0, 1, 2, 3, 4, 5, 6, -1, 8, 9, 7, 11, 12, 13};
            int[] iconX = {219, 189, 156, 126, 94, 62, 30, 219, 189, 156, 124, 92, 59, 28};
            int[] iconY = {67, 69, 67, 69, 72, 72, 69, 32, 29, 29, 32, 30, 33, 31, 32};
            for (int i = 0; i < sideIconsTab.length; i++) {
                if (tabInterfaceIDs[sideIconsTab[i]] != -1) {
                    if (iconId[i] != -1) {
                        Sprite sprite = instance.sideIcons[iconId[i]];
                        if (i == 13) {
                            spriteCache.draw(360, canvasWidth - iconX[i] + 2, canvasHeight - iconY[i] + 1, true);
                        } else {
                            sprite.drawSprite(canvasWidth - iconX[i], canvasHeight - iconY[i]);
                        }
                    }
                }
            }
        } else if (stackSideStones && canvasWidth >= 1000) {
            int[] iconId = {0, 1, 2, 3, 4, 5, 6, -1, 8, 9, 7, 11, 12, 13};
            int[] iconX =
                    {50, 80, 114, 143, 176, 208, 240, 242, 273, 306, 338, 370, 404, 433};
            int[] iconY = {30, 32, 30, 32, 34, 34, 32, 32, 29, 29, 32, 31, 32, 32, 32};
            for (int i = 0; i < sideIconsTab.length; i++) {
                if (tabInterfaceIDs[sideIconsTab[i]] != -1) {
                    if (iconId[i] != -1) {
                        Sprite sprite = instance.sideIcons[iconId[i]];
                        if (i == 13) {
                            spriteCache.draw(360, canvasWidth - 461 + iconX[i] + 2, canvasHeight - iconY[i] + 1, true);
                        } else {
                            sprite.drawSprite(canvasWidth - 461 + iconX[i], canvasHeight - iconY[i]);
                        }
                    }
                }
            }
        }
    }

    private void drawRedStones() {

        final int[] redStonesX =
                {6, 44, 77, 110, 143, 176, 209, 6, 44, 77, 110, 143, 176, 209},
                redStonesY = {0, 0, 0, 0, 0, 0, 0, 298, 298, 298, 298, 298, 298, 298},
                redStonesId = {35, 39, 39, 39, 39, 39, 36, 37, 39, 39, 39, 39, 39, 38};

        final int xOffset = !instance.isResized() ? 516 : canvasWidth - 247;
        final int yOffset = !instance.isResized() ? 168 : canvasHeight - 336;
        if (!instance.isResized() || instance.isResized() && !stackSideStones) {
            if (tabInterfaceIDs[tabId] != -1 && tabId != 15) {
                spriteCache.draw(redStonesId[tabId], redStonesX[tabId] + xOffset,
                        redStonesY[tabId] + yOffset);
            }
        } else if (stackSideStones && canvasWidth < 1000) {
            int[] stoneX = {226, 194, 162, 130, 99, 65, 34, 219, 195, 161, 130, 98, 65, 33};
            int[] stoneY = {73, 73, 73, 73, 73, 73, 73, -1, 37, 37, 37, 37, 37, 37, 37};
            if (tabInterfaceIDs[tabId] != -1 && tabId != 10 && showTabComponents) {
                if (tabId == 7) {
                    spriteCache.draw(39, canvasWidth - 130, canvasHeight - 37);
                }
                spriteCache.draw(39, canvasWidth - stoneX[tabId],
                        canvasHeight - stoneY[tabId]);
            }
        } else if (stackSideStones && canvasWidth >= 1000) {
            int[] stoneX =
                    {417, 385, 353, 321, 289, 256, 224, 129, 193, 161, 130, 98, 65, 33};
            if (tabInterfaceIDs[tabId] != -1 && tabId != 10 && showTabComponents) {
                spriteCache.draw(39, canvasWidth - stoneX[tabId], canvasHeight - 37);
            }
        }
    }

    @Override
    public void drawChatLook(int yOffset) {
        if (instance.chatStateCheck()) {
            showChatComponents = true;
            spriteCache.draw(20, 0, yOffset);
        }
        if (showChatComponents) {
            if ((changeChatArea && instance.isResized()) && !instance.chatStateCheck()) {
                Rasterizer2D.drawHorizontalLine(7, 7 + yOffset, 506, 0x575757);
                Rasterizer2D.drawTransparentGradientBox(7, 7 + yOffset, 510, 130, 0x00000000, 0x5A000000,20);
            } else {
                spriteCache.draw(20, 0, yOffset);
            }
        }
    }

    @Override
    public void processTabAreaClick() {
        if (!stackSideStones || !instance.isResized()) {
            final int yOffset = !instance.isResized() ? 0 : canvasHeight - 503;
            final int xOffset = !instance.isResized() ? 0 : canvasWidth - 765;
            if (MouseHandler.mouseX > 548 + xOffset && MouseHandler.mouseX < 740 + xOffset
                    && MouseHandler.mouseY > 207 + yOffset && MouseHandler.mouseY < 468 + yOffset) {
                if (instance.overlayInterfaceId != -1) {
                    instance.buildInterfaceMenu(548 + xOffset,
                            Widget.interfaceCache[instance.overlayInterfaceId], MouseHandler.mouseX,
                            207 + yOffset, MouseHandler.mouseY, 0);
                } else if (tabInterfaceIDs[tabId] != -1) {
                    instance.buildInterfaceMenu(548 + xOffset,
                            Widget.interfaceCache[tabInterfaceIDs[tabId]],
                            MouseHandler.mouseX, 207 + yOffset, MouseHandler.mouseY, 0);
                }
            }
        } else if (stackSideStones) {
            final int yOffset = canvasWidth >= 1000 ? 37 : 74;
            if (MouseHandler.mouseX > canvasWidth - 197 && MouseHandler.mouseY > canvasHeight - yOffset - 267
                    && MouseHandler.mouseX < canvasWidth - 7
                    && MouseHandler.mouseY < canvasHeight - yOffset - 7 && showTabComponents) {
                if (instance.overlayInterfaceId != -1) {
                    instance.buildInterfaceMenu(canvasWidth - 197,
                            Widget.interfaceCache[instance.overlayInterfaceId], MouseHandler.mouseX,
                            canvasHeight - yOffset - 267, MouseHandler.mouseY, 0);
                } else if (tabInterfaceIDs[tabId] != -1) {
                    instance.buildInterfaceMenu(canvasWidth - 197,
                            Widget.interfaceCache[tabInterfaceIDs[tabId]],
                            MouseHandler.mouseX, canvasHeight - yOffset - 267, MouseHandler.mouseY,
                            0);
                }
            }
        }
    }

    private final int[] tabClickX = {38, 33, 33, 33, 33, 33, 38, 38, 33, 33, 33, 33, 33, 38};
    private final int[] tabClickStart = {522, 560, 593, 625, 659, 692, 724, 522, 560, 593, 625, 659, 692, 724};
    private final int[]  tabClickY = {169, 169, 169, 169, 169, 169, 169, 466, 466, 466, 466, 466, 466, 466};

    @Override
    public void processTabClick() {
        if (MouseHandler.clickMode3 == 1) {
            if (!instance.isResized() || instance.isResized() && !stackSideStones) {
                int xOffset = !instance.isResized() ? 0 : GameEngine.canvasWidth - 765;
                int yOffset = !instance.isResized() ? 0 : GameEngine.canvasHeight - 503;
                for (int i = 0; i < tabClickX.length; i++) {
                    if (MouseHandler.mouseX >= tabClickStart[i] + xOffset && MouseHandler.mouseX <= tabClickStart[i] + tabClickX[i] + xOffset && MouseHandler.mouseY >= tabClickY[i] + yOffset && MouseHandler.mouseY < tabClickY[i] + 37 + yOffset && tabInterfaceIDs[i] != -1) {
                        Client.instance.setInterfaceTab(i);
                        instance.searchingSpawnTab = tabId == 2;

                        break;
                    }
                }
                
            } else if (stackSideStones && GameEngine.canvasWidth < 1000) {
                if (MouseHandler.saveClickX >= GameEngine.canvasWidth - 226
                        && MouseHandler.saveClickX <= GameEngine.canvasWidth - 195
                        && MouseHandler.saveClickY >= GameEngine.canvasHeight - 72
                        && MouseHandler.saveClickY < GameEngine.canvasHeight - 40
                        && tabInterfaceIDs[0] != -1) {
                    if (tabId == 0) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    Client.instance.setInterfaceTab(0);

                }
                if (MouseHandler.saveClickX >= GameEngine.canvasWidth - 194
                        && MouseHandler.saveClickX <= GameEngine.canvasWidth - 163
                        && MouseHandler.saveClickY >= GameEngine.canvasHeight - 72
                        && MouseHandler.saveClickY < GameEngine.canvasHeight - 40
                        && tabInterfaceIDs[1] != -1) {
                    if (tabId == 1) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    Client.instance.setInterfaceTab(1);

                }
                if (MouseHandler.saveClickX >= GameEngine.canvasWidth - 162
                        && MouseHandler.saveClickX <= GameEngine.canvasWidth - 131
                        && MouseHandler.saveClickY >= GameEngine.canvasHeight - 72
                        && MouseHandler.saveClickY < GameEngine.canvasHeight - 40
                        && tabInterfaceIDs[2] != -1) {
                    if (tabId == 2) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    Client.instance.setInterfaceTab(2);

                }
                if (MouseHandler.saveClickX >= GameEngine.canvasWidth - 129
                        && MouseHandler.saveClickX <= GameEngine.canvasWidth - 98
                        && MouseHandler.saveClickY >= GameEngine.canvasHeight - 72
                        && MouseHandler.saveClickY < GameEngine.canvasHeight - 40
                        && tabInterfaceIDs[3] != -1) {
                    if (tabId == 3) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    Client.instance.setInterfaceTab(3);

                }
                if (MouseHandler.saveClickX >= GameEngine.canvasWidth - 97
                        && MouseHandler.saveClickX <= GameEngine.canvasWidth - 66
                        && MouseHandler.saveClickY >= GameEngine.canvasHeight - 72
                        && MouseHandler.saveClickY < GameEngine.canvasHeight - 40
                        && tabInterfaceIDs[4] != -1) {
                    if (tabId == 4) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    Client.instance.setInterfaceTab(4);

                }
                if (MouseHandler.saveClickX >= GameEngine.canvasWidth - 65
                        && MouseHandler.saveClickX <= GameEngine.canvasWidth - 34
                        && MouseHandler.saveClickY >= GameEngine.canvasHeight - 72
                        && MouseHandler.saveClickY < GameEngine.canvasHeight - 40
                        && tabInterfaceIDs[5] != -1) {
                    if (tabId == 5) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    Client.instance.setInterfaceTab(5);

                }
                if (MouseHandler.saveClickX >= GameEngine.canvasWidth - 33 && MouseHandler.saveClickX <= GameEngine.canvasWidth
                        && MouseHandler.saveClickY >= GameEngine.canvasHeight - 72
                        && MouseHandler.saveClickY < GameEngine.canvasHeight - 40
                        && tabInterfaceIDs[6] != -1) {
                    if (tabId == 6) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    Client.instance.setInterfaceTab(6);

                }

                if (MouseHandler.saveClickX >= GameEngine.canvasWidth - 194
                        && MouseHandler.saveClickX <= GameEngine.canvasWidth - 163
                        && MouseHandler.saveClickY >= GameEngine.canvasHeight - 37
                        && MouseHandler.saveClickY < GameEngine.canvasHeight
                        && tabInterfaceIDs[8] != -1) {
                    if (tabId == 8) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    Client.instance.setInterfaceTab(8);

                }
                if (MouseHandler.saveClickX >= GameEngine.canvasWidth - 162
                        && MouseHandler.saveClickX <= GameEngine.canvasWidth - 131
                        && MouseHandler.saveClickY >= GameEngine.canvasHeight - 37
                        && MouseHandler.saveClickY < GameEngine.canvasHeight
                        && tabInterfaceIDs[9] != -1) {
                    if (tabId == 9) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    Client.instance.setInterfaceTab(9);

                }
                if (MouseHandler.saveClickX >= GameEngine.canvasWidth - 129
                        && MouseHandler.saveClickX <= GameEngine.canvasWidth - 98
                        && MouseHandler.saveClickY >= GameEngine.canvasHeight - 37
                        && MouseHandler.saveClickY < GameEngine.canvasHeight
                        && tabInterfaceIDs[10] != -1) {
                    if (tabId == 7) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    Client.instance.setInterfaceTab(7);

                }
                if (MouseHandler.saveClickX >= GameEngine.canvasWidth - 97
                        && MouseHandler.saveClickX <= GameEngine.canvasWidth - 66
                        && MouseHandler.saveClickY >= GameEngine.canvasHeight - 37
                        && MouseHandler.saveClickY < GameEngine.canvasHeight
                        && tabInterfaceIDs[11] != -1) {
                    if (tabId == 11) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    Client.instance.setInterfaceTab(11);

                }
                if (MouseHandler.saveClickX >= GameEngine.canvasWidth - 65
                        && MouseHandler.saveClickX <= GameEngine.canvasWidth - 34
                        && MouseHandler.saveClickY >= GameEngine.canvasHeight - 37
                        && MouseHandler.saveClickY < GameEngine.canvasHeight
                        && tabInterfaceIDs[12] != -1) {
                    if (tabId == 12) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    Client.instance.setInterfaceTab(12);

                }
                if (MouseHandler.saveClickX >= GameEngine.canvasWidth - 33 && MouseHandler.saveClickX <= GameEngine.canvasWidth
                        && MouseHandler.saveClickY >= GameEngine.canvasHeight - 37
                        && MouseHandler.saveClickY < GameEngine.canvasHeight
                        && tabInterfaceIDs[13] != -1) {
                    if (tabId == 13) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    Client.instance.setInterfaceTab(13);

                }
            } else if (stackSideStones && GameEngine.canvasWidth >= 1000) {
                if (MouseHandler.mouseY >= GameEngine.canvasHeight - 37 && MouseHandler.mouseY <= GameEngine.canvasHeight) {
                    if (MouseHandler.mouseX >= GameEngine.canvasWidth - 417
                            && MouseHandler.mouseX <= GameEngine.canvasWidth - 386) {
                        if (tabId == 0) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        Client.instance.setInterfaceTab(0);
                    }
                    if (MouseHandler.mouseX >= GameEngine.canvasWidth - 385
                            && MouseHandler.mouseX <= GameEngine.canvasWidth - 354) {
                        if (tabId == 1) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        Client.instance.setInterfaceTab(1);
                    }
                    if (MouseHandler.mouseX >= GameEngine.canvasWidth - 353
                            && MouseHandler.mouseX <= GameEngine.canvasWidth - 322) {
                        if (tabId == 2) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        Client.instance.setInterfaceTab(2);
                    }
                    if (MouseHandler.mouseX >= GameEngine.canvasWidth - 321
                            && MouseHandler.mouseX <= GameEngine.canvasWidth - 290) {
                        if (tabId == 3) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        Client.instance.setInterfaceTab(3);
                    }
                    if (MouseHandler.mouseX >= GameEngine.canvasWidth - 289
                            && MouseHandler.mouseX <= GameEngine.canvasWidth - 258) {
                        if (tabId == 4) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        Client.instance.setInterfaceTab(4);
                    }
                    if (MouseHandler.mouseX >= GameEngine.canvasWidth - 257
                            && MouseHandler.mouseX <= GameEngine.canvasWidth - 226) {
                        if (tabId == 5) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        Client.instance.setInterfaceTab(5);
                    }
                    if (MouseHandler.mouseX >= GameEngine.canvasWidth - 225
                            && MouseHandler.mouseX <= GameEngine.canvasWidth - 194) {
                        if (tabId == 6) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        Client.instance.setInterfaceTab(6);
                    }
                    if (MouseHandler.mouseX >= GameEngine.canvasWidth - 193
                            && MouseHandler.mouseX <= GameEngine.canvasWidth - 163) {
                        if (tabId == 8) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        Client.instance.setInterfaceTab(8);
                    }
                    if (MouseHandler.mouseX >= GameEngine.canvasWidth - 162
                            && MouseHandler.mouseX <= GameEngine.canvasWidth - 131) {
                        if (tabId == 9) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        Client.instance.setInterfaceTab(9);
                    }
                    if (MouseHandler.mouseX >= GameEngine.canvasWidth - 130
                            && MouseHandler.mouseX <= GameEngine.canvasWidth - 99) {
                        if (tabId == 7) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        Client.instance.setInterfaceTab(7);
                    }
                    if (MouseHandler.mouseX >= GameEngine.canvasWidth - 98
                            && MouseHandler.mouseX <= GameEngine.canvasWidth - 67) {
                        if (tabId == 11) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        Client.instance.setInterfaceTab(11);
                    }
                    if (MouseHandler.mouseX >= GameEngine.canvasWidth - 66
                            && MouseHandler.mouseX <= GameEngine.canvasWidth - 45) {
                        if (tabId == 12) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        Client.instance.setInterfaceTab(12);
                    }
                    if (MouseHandler.mouseX >= GameEngine.canvasWidth - 31 && MouseHandler.mouseX <= GameEngine.canvasWidth) {
                        if (tabId == 13) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        Client.instance.setInterfaceTab(13);
                    }
                }
            }
        }
    }

    @Override
    public Sprite setCompass() {
        return Client.instance.compass;
    }

    @Override
    public boolean oldScroll() {
        return false;
    }

    @Override
    public Boolean processTabHover() {
        return null;
    }

    @Override
    public List<Integer> chatButtons() {
        return List.of(49,15,16,17,18);
    }


}
