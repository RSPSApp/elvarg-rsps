package com.runescape.draw.gameframe.impl;

import com.runescape.Client;
import com.runescape.draw.Rasterizer2D;
import com.runescape.draw.gameframe.GameFrame;
import com.runescape.engine.GameEngine;
import com.runescape.engine.impl.MouseHandler;
import com.runescape.graphics.sprite.Sprite;
import com.runescape.graphics.widget.Widget;
import java.util.List;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

import static com.runescape.Client.*;
import static com.runescape.engine.GameEngine.canvasHeight;
import static com.runescape.engine.GameEngine.canvasWidth;

public class Frame317 extends GameFrame {
    
    private int[] sideIconsX;
    private int[] sideIconsY;
    private Client instance;

    @NotNull
    public String type() {
        return "317";
    }

    public void drawTabArea() {
        int xOffset = !instance.isResized() ? 516 : canvasWidth - 241;
        int yOffset = !instance.isResized() ? 168 : canvasHeight - 336;
        if (!instance.isResized()) {
            Client.spriteCache.lookup(676).drawSprite(xOffset, yOffset);
        } else if (instance.isResized() && !stackSideStones) {
            Client.spriteCache.lookup(677).drawTransparentSprite(canvasWidth - 209, canvasHeight - 301, Client.transparentTabArea ? 235 : 255);
            Client.spriteCache.lookup(678).drawTransparentSprite(xOffset + 11, yOffset + 4, Client.transparentTabArea ? 235 : 255);
        } else if (canvasWidth >= 1000) {
            if (Client.showTabComponents) {
                Rasterizer2D.drawTransparentBox(canvasWidth - 197, canvasHeight - 306, 195, 265, 4076841, stackSideStones ? 80 : 256);
                Client.spriteCache.lookup(50).drawTransparentSprite(canvasWidth - 204, canvasHeight - 314, Client.transparentTabArea ? 160 : 255);
            }

            Client.spriteCache.lookup(679).drawAdvancedSprite(canvasWidth - 467, canvasHeight - 39);
        } else if (canvasWidth < 1000) {
            if (Client.showTabComponents) {
                Rasterizer2D.drawTransparentBox(canvasWidth - 197, canvasHeight - 354, 195, 265, 4076841, stackSideStones ? 80 : 256);
                Client.spriteCache.lookup(50).drawSprite(canvasWidth - 204, canvasHeight - 360);
            }

            Client.spriteCache.lookup(680).drawAdvancedSprite(canvasWidth - 230, canvasHeight - 84);
        }

        if (instance.overlayInterfaceId == -1) {
            drawRedStones();
            drawSideIcons();
        }

        if (Client.showTabComponents) {
            int x = !instance.isResized() ? xOffset + 38 : canvasWidth - 211;
            int y = !instance.isResized() ? yOffset + 37 : canvasHeight - 299;
            if (instance.isResized() && stackSideStones) {
                x = canvasWidth - 197;
                y = canvasWidth >= 1000 ? canvasHeight - 307 : canvasHeight - 353;
            }

            try {
                if (instance.overlayInterfaceId != -1) {
                    instance.drawInterface(0, x, Widget.interfaceCache[instance.overlayInterfaceId], y);
                } else if (tabInterfaceIDs[tabId] != -1) {
                    instance.drawInterface(0, x, Widget.interfaceCache[tabInterfaceIDs[tabId]], y);
                }
            } catch (Exception var6) {
            }
        }

    }

    private void drawRedStones() {


        final int xOffset = !instance.isResized() ? 516 : canvasWidth - 247;
        final int yOffset = !instance.isResized() ? 168 : canvasHeight - 336;

        if (!instance.isResized() || instance.isResized() && !stackSideStones) {
            int[] redStonesX =  new int[]{22, 54, 82, 110, 154, 182, 209, 22, 54, 81, 110, 154, 182, 209};
            int[] redStonesY =  new int[]{3, 1, 1, 0, 1, 1, 2, 298, 299, 297, 299, 298, 298, 298};
            int[] redStonesId =  new int[]{702, 707, 707, 704, 709, 709, 703, 705, 710, 710, 708, 711, 711, 706};

            if (tabInterfaceIDs[tabId] != -1 && tabId != 15) {
                Client.spriteCache.lookup(redStonesId[tabId]).drawSprite(redStonesX[tabId] + xOffset, redStonesY[tabId] + yOffset);
            }
        } else if (stackSideStones && canvasWidth < 1000) {
            int[] stoneX = new int[]{230, 196, 164, 131, 97, 65, 30, 230, 196, 163, 130, 98, 65, 31 };
            int[] stoneY = new int[]{85, 85, 83, 83, 82, 80, 82, 37, 40, 40, 40, 40, 40, 35, 26 };
            int[] stoneId = new int[]{702, 712, 712, 712, 712, 712, 703, 705, 713, 713, 713, 713, 713, 706, 706};
            if (tabInterfaceIDs[tabId] != 65535 && tabId != 15 && showTabComponents) {
                Client.spriteCache.lookup(stoneId[tabId]).drawSprite(canvasWidth - stoneX[tabId], canvasHeight - stoneY[tabId]);
            }
        } else if (stackSideStones && canvasWidth >= 1000) {
            int[] stoneX = new int[]{467, 433, 399, 366, 321, 333, 300, 267, 234, 201, 135, 102, 69, 36};
            int[] stoneId = new int[]{702, 707, 707, 707, 707, 707, 707, 707, 707, 707, 707, 707, 707, 707, 703};
            if (tabInterfaceIDs[tabId] != -1 && tabId != 15 && showTabComponents) {
                Client.spriteCache.lookup(stoneId[tabId]).drawSprite(canvasWidth - stoneX[tabId], canvasHeight - 39);
            }
        }
    }

    @NotNull
    public final int[] getSideIconsX() {
        return sideIconsX;
    }

    public final void setSideIconsX(int[] var1) {
        sideIconsX = var1;
    }

    @NotNull
    public final int[] getSideIconsY() {
        return sideIconsY;
    }

    public final void setSideIconsY(int[] var1) {
        sideIconsY = var1;
    }

    public final void drawSideIcons() {
        Sprite[] sideIcons = new Sprite[]{Client.spriteCache.lookup(681), Client.spriteCache.lookup(682), Client.spriteCache.lookup(683), Client.spriteCache.lookup(stackSideStones && instance.isResized() ? 685 : 684), Client.spriteCache.lookup(686), Client.spriteCache.lookup(687), Client.spriteCache.lookup(689), Client.spriteCache.lookup(692), Client.spriteCache.lookup(694), Client.spriteCache.lookup(695), Client.spriteCache.lookup(stackSideStones && instance.isResized() ? 697 : 696), Client.spriteCache.lookup(698), Client.spriteCache.lookup(699), Client.spriteCache.lookup(700)};
        int xOffset = !instance.isResized() ? 516 : canvasWidth - 247;
        int yOffset = !instance.isResized() ? 168 : canvasHeight - 336;
        int index;
        byte var5;
        if (!instance.isResized()) {
            index = 0;

            for(var5 = 13; index <= var5; ++index) {
                if (tabInterfaceIDs[index] != -1 && (instance.flashingSidebarId != index || Client.instance.tickDelta % 20 < 10)) {
                    sideIcons[index].drawAdvancedSprite(sideIconsX[index] + xOffset, sideIconsY[index] + yOffset);
                }
            }
        } else if (instance.isResized() && !stackSideStones) {
            index = 0;

            for(var5 = 13; index <= var5; ++index) {
                if (tabInterfaceIDs[index] != -1 && (instance.flashingSidebarId != index || Client.instance.tickDelta % 20 < 10)) {
                    sideIcons[index].drawAdvancedSprite(sideIconsX[index] + xOffset + 1, sideIconsY[index] + yOffset + 2);
                }
            }
        } else {
            int index1;
            byte var7;
            int[] iconX;
            int[] iconY;
            if (instance.isResized() && stackSideStones && canvasWidth < 1000) {
                iconX = new int[]{220, 191, 156, 126, 92, 59, 29, 223, 191, 158, 126, 92, 55, 28};
                iconY = new int[]{68, 70, 70, 68, 71, 70, 68, 25, 25, 25, 25, 27, 27, 25, 25};
                index1 = 0;

                for(var7 = 13; index1 <= var7; ++index1) {
                    if (tabInterfaceIDs[index1] != 65535 && (instance.flashingSidebarId != index1 || Client.instance.tickDelta % 20 < 10)) {
                        sideIcons[index1].drawAdvancedSprite(canvasWidth - iconX[index1], canvasHeight - iconY[index1] - 5);
                    }
                }
            } else if (instance.isResized() && stackSideStones && canvasWidth >= 1000) {
                iconX = new int[]{35, 65, 102, 133, 167, 200, 233, 267, 301, 333, 365, 400, 435, 463};
                iconY = new int[]{30, 32, 32, 30, 32, 34, 32, 32, 31, 31, 32, 31, 33, 32, 32};
                index1 = 0;

                for(var7 = 13; index1 <= var7; ++index1) {
                    if (tabInterfaceIDs[index1] != -1 && (instance.flashingSidebarId != index1 || Client.instance.tickDelta % 20 < 10)) {
                        sideIcons[index1].drawAdvancedSprite(canvasWidth - 493 + iconX[index1], canvasHeight - iconY[index1]);
                    }
                }
            }
        }

    }

    public void drawChatLook(int yOffset) {
        if (instance.chatStateCheck()) {
            Client.showChatComponents = true;
            Client.spriteCache.draw(667, 0, yOffset);
        }

        if (Client.showChatComponents) {
            if (Client.changeChatArea && instance.isResized() && !instance.chatStateCheck()) {
                Client.spriteCache.lookup(668).drawAdvancedSprite(-5, yOffset - 10);
            } else {
                Client.spriteCache.draw(667, 0, yOffset);
            }
        }

    }

    public void processTabAreaClick() {
        if (!instance.isResized()) {
            if (instance.getMouseX() > 540 && instance.getMouseY() > 214 && instance.getMouseX() < 765 && instance.getMouseY() < 466) {
                if (instance.overlayInterfaceId != -1) {
                    instance.buildInterfaceMenu(547, Widget.interfaceCache[instance.overlayInterfaceId], instance.getMouseX(), 205, instance.getMouseY(), 0);
                } else if (tabInterfaceIDs[tabId] != -1) {
                    instance.buildInterfaceMenu(547, Widget.interfaceCache[tabInterfaceIDs[tabId]], instance.getMouseX(), 205, instance.getMouseY(), 0);
                }
            }
        } else {
            int yOffset;
            if (!stackSideStones) {
                yOffset = !instance.isResized() ? 0 : canvasHeight - 503;
                int xOffset = !instance.isResized() ? 0 : canvasWidth - 765;
                if (instance.getMouseX() > 548 + xOffset && instance.getMouseX() < 740 + xOffset && instance.getMouseY() > 207 + yOffset && instance.getMouseY() < 468 + yOffset) {
                    if (Client.instance.overlayInterfaceId != -1) {
                        instance.buildInterfaceMenu(548 + xOffset, Widget.interfaceCache[Client.instance.overlayInterfaceId], instance.getMouseX(), 207 + yOffset, instance.getMouseY(), 0);
                    } else if (tabInterfaceIDs[tabId] != -1) {
                        instance.buildInterfaceMenu(548 + xOffset, Widget.interfaceCache[tabInterfaceIDs[tabId]], instance.getMouseX(), 207 + yOffset, instance.getMouseY(), 0);
                    }
                }
            } else if (stackSideStones) {
                yOffset = canvasWidth >= 1000 ? 37 : 74;
                if (instance.getMouseX() > canvasWidth - 197 && instance.getMouseY() > canvasHeight - yOffset - 267 && instance.getMouseX() < canvasWidth - 7 && instance.getMouseY() < canvasHeight - yOffset - 7 && Client.showTabComponents) {
                    if (Client.instance.overlayInterfaceId != -1) {
                        instance.buildInterfaceMenu(canvasWidth - 197, Widget.interfaceCache[Client.instance.overlayInterfaceId], instance.getMouseX(), canvasHeight - yOffset - 267, instance.getMouseY(), 0);
                    } else if (tabInterfaceIDs[tabId] != -1) {
                        instance.buildInterfaceMenu(canvasWidth - 197, Widget.interfaceCache[tabInterfaceIDs[tabId]], instance.getMouseX(), canvasHeight - yOffset - 267, instance.getMouseY(), 0);
                    }
                }
            }
        }

    }

    public Boolean processTabHover() {
        if (!instance.isResized()) {
            if (instance.getMouseX() > 540 && instance.getMouseY() > 214 && instance.getMouseX() < 765 && instance.getMouseY() < 466) {
                return true;
            }
        } else {
            int yOffset;
            if (!stackSideStones) {
                yOffset = !instance.isResized() ? 0 : canvasHeight - 503;
                int xOffset = !instance.isResized() ? 0 : canvasWidth - 765;
                if (instance.getMouseX() > 548 + xOffset && instance.getMouseX() < 740 + xOffset && instance.getMouseY() > 207 + yOffset && instance.getMouseY() < 468 + yOffset) {
                    return true;
                }
            } else if (stackSideStones) {
                yOffset = canvasWidth >= 1000 ? 37 : 74;
                if (instance.getMouseX() > canvasWidth - 197 && instance.getMouseY() > canvasHeight - yOffset - 267 && instance.getMouseX() < canvasWidth - 7 && instance.getMouseY() < canvasHeight - yOffset - 7 && Client.showTabComponents) {
                    return true;
                }
            }
        }

        return false;
    }

    public void processTabClick() {
        int[] tabClickX = new int[]{38, 33, 33, 33, 33, 33, 38, 38, 33, 33, 33, 33, 33, 38};
        int[] tabClickStart = new int[]{522, 560, 593, 625, 659, 692, 724, 522, 560, 593, 625, 659, 692, 724};
        int[] tabClickY = new int[]{169, 169, 169, 169, 169, 169, 169, 466, 466, 466, 466, 466, 466, 466};
        if (MouseHandler.clickMode3 == 1) {
            int x;
            int yOffset;
            int index;
            if (instance.isResized() && (!instance.isResized() || !instance.isResized() || !stackSideStones)) {
                if (instance.isResized() && stackSideStones && canvasWidth < 1000) {
                    if (tabInterfaceIDs[0] != 65535 && MouseHandler.saveClickX >= canvasWidth - 226 && MouseHandler.saveClickX <= canvasWidth - 195 && MouseHandler.saveClickY >= canvasHeight - 72 && MouseHandler.saveClickY < canvasHeight - 40 && tabInterfaceIDs[0] != -1) {
                        if (tabId == 0) {
                            Client.showTabComponents = !Client.showTabComponents;
                        } else {
                            Client.showTabComponents = true;
                        }

                        tabId = 0;
                        Client.tabAreaAltered = true;
                    }

                    if (tabInterfaceIDs[1] != 65535 && MouseHandler.saveClickX >= canvasWidth - 194 && MouseHandler.saveClickX <= canvasWidth - 163 && MouseHandler.saveClickY >= canvasHeight - 72 && MouseHandler.saveClickY < canvasHeight - 40 && tabInterfaceIDs[1] != -1) {
                        if (tabId == 1) {
                            Client.showTabComponents = !Client.showTabComponents;
                        } else {
                            Client.showTabComponents = true;
                        }

                        tabId = 1;
                        Client.tabAreaAltered = true;
                    }

                    if (tabInterfaceIDs[2] != 65535 && MouseHandler.saveClickX >= canvasWidth - 162 && MouseHandler.saveClickX <= canvasWidth - 131 && MouseHandler.saveClickY >= canvasHeight - 72 && MouseHandler.saveClickY < canvasHeight - 40 && tabInterfaceIDs[2] != -1) {
                        if (tabId == 2) {
                            Client.showTabComponents = !Client.showTabComponents;
                        } else {
                            Client.showTabComponents = true;
                        }

                        tabId = 2;
                        Client.tabAreaAltered = true;
                    }

                    if (tabInterfaceIDs[3] != 65535 && MouseHandler.saveClickX >= canvasWidth - 129 && MouseHandler.saveClickX <= canvasWidth - 98 && MouseHandler.saveClickY >= canvasHeight - 72 && MouseHandler.saveClickY < canvasHeight - 40 && tabInterfaceIDs[3] != -1) {
                        if (tabId == 3) {
                            Client.showTabComponents = !Client.showTabComponents;
                        } else {
                            Client.showTabComponents = true;
                        }

                        tabId = 3;
                        Client.tabAreaAltered = true;
                    }

                    if (tabInterfaceIDs[4] != 65535 && MouseHandler.saveClickX >= canvasWidth - 97 && MouseHandler.saveClickX <= canvasWidth - 66 && MouseHandler.saveClickY >= canvasHeight - 72 && MouseHandler.saveClickY < canvasHeight - 40 && tabInterfaceIDs[4] != -1) {
                        if (tabId == 4) {
                            Client.showTabComponents = !Client.showTabComponents;
                        } else {
                            Client.showTabComponents = true;
                        }

                        tabId = 4;
                        Client.tabAreaAltered = true;
                    }

                    if (tabInterfaceIDs[5] != 65535 && MouseHandler.saveClickX >= canvasWidth - 65 && MouseHandler.saveClickX <= canvasWidth - 34 && MouseHandler.saveClickY >= canvasHeight - 72 && MouseHandler.saveClickY < canvasHeight - 40 && tabInterfaceIDs[5] != -1) {
                        if (tabId == 5) {
                            Client.showTabComponents = !Client.showTabComponents;
                        } else {
                            Client.showTabComponents = true;
                        }

                        tabId = 5;
                        Client.tabAreaAltered = true;
                    }

                    if (tabInterfaceIDs[6] != 65535 && MouseHandler.saveClickX >= canvasWidth - 33 && MouseHandler.saveClickX <= canvasWidth && MouseHandler.saveClickY >= canvasHeight - 72 && MouseHandler.saveClickY < canvasHeight - 40 && tabInterfaceIDs[6] != -1) {
                        if (tabId == 6) {
                            Client.showTabComponents = !Client.showTabComponents;
                        } else {
                            Client.showTabComponents = true;
                        }

                        tabId = 6;
                        Client.tabAreaAltered = true;
                    }

                    if (tabInterfaceIDs[7] != 65535 && MouseHandler.saveClickX >= canvasWidth - 226 && MouseHandler.saveClickX <= canvasWidth - 195 && MouseHandler.saveClickY >= canvasHeight - 27 && MouseHandler.saveClickY < canvasHeight - 0 && tabInterfaceIDs[7] != -1) {
                        if (tabId == 7) {
                            Client.showTabComponents = !Client.showTabComponents;
                        } else {
                            Client.showTabComponents = true;
                        }

                        tabId = 7;
                        Client.tabAreaAltered = true;
                    }

                    if (tabInterfaceIDs[8] != 65535 && MouseHandler.saveClickX >= canvasWidth - 194 && MouseHandler.saveClickX <= canvasWidth - 163 && MouseHandler.saveClickY >= canvasHeight - 37 && MouseHandler.saveClickY < canvasHeight - 0 && tabInterfaceIDs[8] != -1) {
                        if (tabId == 8) {
                            Client.showTabComponents = !Client.showTabComponents;
                        } else {
                            Client.showTabComponents = true;
                        }

                        tabId = 8;
                        Client.tabAreaAltered = true;
                    }

                    if (tabInterfaceIDs[9] != 65535 && MouseHandler.saveClickX >= canvasWidth - 162 && MouseHandler.saveClickX <= canvasWidth - 131 && MouseHandler.saveClickY >= canvasHeight - 37 && MouseHandler.saveClickY < canvasHeight - 0 && tabInterfaceIDs[9] != -1) {
                        if (tabId == 9) {
                            Client.showTabComponents = !Client.showTabComponents;
                        } else {
                            Client.showTabComponents = true;
                        }

                        tabId = 9;
                        Client.tabAreaAltered = true;
                    }

                    if (tabInterfaceIDs[10] != 65535 && MouseHandler.saveClickX >= canvasWidth - 129 && MouseHandler.saveClickX <= canvasWidth - 98 && MouseHandler.saveClickY >= canvasHeight - 37 && MouseHandler.saveClickY < canvasHeight - 0 && tabInterfaceIDs[10] != -1) {
                        if (tabId == 10) {
                            Client.showTabComponents = !Client.showTabComponents;
                        } else {
                            Client.showTabComponents = true;
                        }

                        tabId = 10;
                        Client.tabAreaAltered = true;
                    }

                    if (tabInterfaceIDs[11] != 65535 && MouseHandler.saveClickX >= canvasWidth - 97 && MouseHandler.saveClickX <= canvasWidth - 66 && MouseHandler.saveClickY >= canvasHeight - 37 && MouseHandler.saveClickY < canvasHeight - 0 && tabInterfaceIDs[11] != -1) {
                        if (tabId == 11) {
                            Client.showTabComponents = !Client.showTabComponents;
                        } else {
                            Client.showTabComponents = true;
                        }

                        tabId = 11;
                        Client.tabAreaAltered = true;
                    }

                    if (tabInterfaceIDs[12] != 65535 && MouseHandler.saveClickX >= canvasWidth - 65 && MouseHandler.saveClickX <= canvasWidth - 34 && MouseHandler.saveClickY >= canvasHeight - 37 && MouseHandler.saveClickY < canvasHeight - 0 && tabInterfaceIDs[12] != -1) {
                        if (tabId == 12) {
                            Client.showTabComponents = !Client.showTabComponents;
                        } else {
                            Client.showTabComponents = true;
                        }

                        tabId = 12;
                        Client.tabAreaAltered = true;
                    }

                    if (tabInterfaceIDs[13] != 65535 && MouseHandler.saveClickX >= canvasWidth - 33 && MouseHandler.saveClickX <= canvasWidth && MouseHandler.saveClickY >= canvasHeight - 37 && MouseHandler.saveClickY < canvasHeight - 0 && tabInterfaceIDs[13] != -1) {
                        if (tabId == 13) {
                            Client.showTabComponents = !Client.showTabComponents;
                        } else {
                            Client.showTabComponents = true;
                        }

                        tabId = 13;
                        Client.tabAreaAltered = true;
                    }
                } else if (instance.isResized() && stackSideStones && canvasWidth >= 1000 && instance.getMouseY() >= canvasHeight - 68 && instance.getMouseY() <= canvasHeight) {
                    x = canvasWidth - 449;
                    yOffset = canvasHeight - 37;

                    for(index = 0; x <= canvasWidth - 30 && index < 14; ++index) {
                        if (tabInterfaceIDs[index] != 65535 && MouseHandler.clickMode3 == 1) {
                            if (tabId == index) {
                                Client.showTabComponents = !Client.showTabComponents;
                            } else {
                                Client.showTabComponents = true;
                            }

                            tabId = index;
                            Client.tabAreaAltered = true;
                        }

                        x += 32;
                    }
                }
            } else {
                x = !instance.isResized() ? 0 : canvasWidth - 765;
                yOffset = !instance.isResized() ? 0 : canvasHeight - 503;
                index = 0;

                for(int var7 = tabClickX.length; index < var7; ++index) {
                    if (tabInterfaceIDs[index] != 65535 && instance.getMouseX() >= tabClickStart[index] + x && instance.getMouseX() <= tabClickStart[index] + tabClickX[index] + x && instance.getMouseY() >= tabClickY[index] + yOffset && instance.getMouseY() < tabClickY[index] + 37 + yOffset && tabInterfaceIDs[index] != -1) {
                        tabId = index;
                        Client.tabAreaAltered = true;
                        break;
                    }
                }
            }
        }

    }
    
    public Sprite setCompass() {
        return Client.spriteCache.lookup(675);
    }

    public boolean oldScroll() {
        return true;
    }

    
    public List chatButtons() {
        return List.of(670, 671, 672, 673, 674);
    }

    @NotNull
    public Client getInstance() {
        return instance;
    }

    public Frame317(Client instance) {
        Intrinsics.checkParameterIsNotNull(instance, "instance");
        this.instance = instance;
        sideIconsX = new int[]{34, 57, 87, 115, 157, 184, 212, 32, 58, 85, 117, 156, 188, 212};
        sideIconsY = new int[]{11, 8, 9, 5, 5, 5, 11, 303, 303, 306, 306, 304, 306, 300, 300};
    }
}
