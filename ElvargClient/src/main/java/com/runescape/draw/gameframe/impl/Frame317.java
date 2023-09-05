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
        Sprite[] sideIcons = new Sprite[]{Client.spriteCache.lookup(681), Client.spriteCache.lookup(682), Client.spriteCache.lookup(683), Client.spriteCache.lookup(stackSideStones && instance.isResized() ? 685 : 684), Client.spriteCache.lookup(686), Client.spriteCache.lookup(687), Client.spriteCache.lookup(689), Client.spriteCache.lookup(692), Client.spriteCache.lookup(694), Client.spriteCache.lookup(695), Client.spriteCache.lookup(stackSideStones && instance.isResized() ? 697 : 696), Client.spriteCache.lookup(698), Client.spriteCache.lookup(699), Client.spriteCache.lookup(700)};

        if (MouseHandler.clickMode3 == 1) {
            int xOffset = !instance.isResized() ? 516 : canvasWidth - 247;
            int yOffset = !instance.isResized() ? 168 : canvasHeight - 336;
            int index;
            byte var5;
            if (!instance.isResized()) {
                index = 0;

                for(var5 = 13; index <= var5; ++index) {
                    if(instance.clickInRegion(sideIconsX[index] + xOffset, sideIconsY[index] + yOffset,sideIcons[index])) {
                        switchTab(index);
                    }
                }
            } else if (instance.isResized() && !stackSideStones) {
                index = 0;

                for(var5 = 13; index <= var5; ++index) {
                    if(instance.clickInRegion(sideIconsX[index] + xOffset + 1, sideIconsY[index] + yOffset + 2,sideIcons[index])) {
                        switchTab(index);
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
                        if(instance.clickInRegion(canvasWidth - iconX[index1], canvasHeight - iconY[index1] - 5,sideIcons[index1])) {
                            switchTab(index1);
                        }
                    }
                } else if (instance.isResized() && stackSideStones && canvasWidth >= 1000) {
                    iconX = new int[]{35, 65, 102, 133, 167, 200, 233, 267, 301, 333, 365, 400, 435, 463};
                    iconY = new int[]{30, 32, 32, 30, 32, 34, 32, 32, 31, 31, 32, 31, 33, 32, 32};
                    index1 = 0;

                    for(var7 = 13; index1 <= var7; ++index1) {
                        if(instance.clickInRegion(canvasWidth - 493 + iconX[index1], canvasHeight - iconY[index1],sideIcons[index1])) {
                            switchTab(index1);
                        }
                    }
                }
            }
        }

    }

    public void switchTab(int index) {
        if(instance.isResized()) {
            if (tabId == index) {
                showTabComponents = !showTabComponents;
            } else {
                showTabComponents = true;
            }
        }
        Client.instance.setInterfaceTab(index);
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
