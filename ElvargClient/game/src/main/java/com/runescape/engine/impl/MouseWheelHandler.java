package com.runescape.engine.impl;

import com.runescape.Client;
import com.runescape.graphics.widget.SettingsWidget;
import com.runescape.graphics.widget.Widget;
import net.runelite.rs.api.RSMouseWheelHandler;

import java.awt.*;
import java.awt.event.*;

import static com.runescape.engine.impl.MouseHandler.mouseX;
import static com.runescape.engine.impl.MouseHandler.mouseY;

public class MouseWheelHandler implements MouseWheelListener, RSMouseWheelHandler {

    int rotation;
    public static boolean mouseWheelDown;
    public static int mouseWheelX;
    public static int mouseWheelY;

    public synchronized int useRotation() {
        int rotation = this.rotation;
        this.rotation = 0;
        return rotation;
    }

    public void addTo(Component component) {
        component.addMouseWheelListener(this);
    }

    public void removeFrom(Component component) {
        component.removeMouseWheelListener(this);
    }

    public boolean canZoom = true;

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        MouseWheelEvent event = e;
        if (Client.instance.getDrawCallbacks() != null) {
            event = Client.instance.getCallbacks().mouseWheelMoved(e);
        }
        if (!event.isConsumed()) {
            int rotation = e.getWheelRotation();
            handleInterfaceScrolling(e);
            if (Client.showChatComponents && mouseX > 0 && mouseX < 512 && mouseY > Client.canvasHeight - 165 && mouseY < Client.canvasHeight - 25) {
                int scrollPos = Client.anInt1089;
                scrollPos -= rotation * 30;
                if (scrollPos < 0)
                    scrollPos = 0;
                if (scrollPos > Client.anInt1211 - 110)
                    scrollPos = Client.anInt1211 - 110;
                if (Client.anInt1089 != scrollPos) {
                    Client.anInt1089 = scrollPos;
                    Client.updateChatbox = true;
                }
            }

            if (Client.openInterfaceId == -1) {
                if (canZoom) {
                    if (Client.showChatComponents && mouseX > 0 && mouseX < 512 && mouseY > Client.canvasHeight - 165 && mouseY < Client.canvasHeight - 25) {
                        return;
                    }
                    /** ZOOMING **/
                    boolean zoom = !Client.instance.isResized() ? (mouseX < 512) : (mouseX < Client.canvasWidth - 200);
                    if (zoom) {
                        int zoom_in = !Client.instance.isResized() ? 195 : 240;
                        int zoom_out = !Client.instance.isResized() ? 1105 : 1220;

                        if (event.getWheelRotation() != -1) {
                            if (Client.cameraZoom > zoom_in) {
                                Client.cameraZoom -= 45;
                            }
                        } else {
                            if (Client.cameraZoom < zoom_out) {
                                Client.cameraZoom += 45;
                            }
                        }
                    }

                    int setting = 0;

                    if (Client.cameraZoom > 1000) {
                        setting = 4;
                    } else if (Client.cameraZoom > 800) {
                        setting = 3;
                    } else if (Client.cameraZoom > 600) {
                        setting = 2;
                    } else if (Client.cameraZoom > 400) {
                        setting = 1;
                    }
                    Client.instance.settings[168] = setting;
                    Widget.interfaceCache[SettingsWidget.ZOOM_SLIDER].slider.setValue(Client.cameraZoom);
                }
            }
        }
    }

    public void handleInterfaceScrolling(MouseWheelEvent event) {
        int rotation = event.getWheelRotation();
        int positionX = 0;
        int positionY = 0;
        int width = 0;
        int height = 0;
        int offsetX = 0;
        int offsetY = 0;
        int childID = 0;
        int tabInterfaceID = Client.tabInterfaceIDs[Client.tabId];
        boolean fixed = !Client.instance.isResized();
        if (tabInterfaceID != -1) {
            Widget tab = Widget.interfaceCache[tabInterfaceID];
            if (tab != null) {
                offsetX = fixed ? 558 : Client.canvasWidth - 217;
                offsetY = fixed ? 205 : Client.canvasHeight - 298;
                if (!fixed && Client.stackSideStones && Client.canvasWidth <= 1000) {
                    offsetX = Client.canvasWidth - 198;
                    offsetY = Client.canvasHeight - 335;
                }
                for (int index = 0; index < tab.children.length; index++) {
                    if (Widget.interfaceCache[tab.children[index]].scrollMax > 0) {
                        childID = index;
                        positionX = tab.childX[index];
                        positionY = tab.childY[index];
                        width = Widget.interfaceCache[tab.children[index]].width;
                        height = Widget.interfaceCache[tab.children[index]].height;
                        break;
                    }
                }
                if (Client.showTabComponents && mouseX > offsetX + positionX && mouseY > offsetY + positionY && mouseX < offsetX + positionX + width
                        && mouseY < offsetY + positionY + height) {
                    canZoom = false;
                    Widget.interfaceCache[tab.children[childID]].scrollPosition += rotation * 30;
                } else {
                    canZoom = true;
                }
            }
        }
        if (Client.openInterfaceId != -1) {
            int w = 512, h = 334;
            int x = (Client.canvasWidth / 2) - 240;
            int y = (Client.canvasHeight / 2) - 167;
            int count = Client.stackSideStones ? 3 : 4;
            if (Client.instance.isResized()) {
                for (int i = 0; i < count; i++) {
                    if (x + w > (Client.canvasWidth - 225)) {
                        x = x - 30;
                        if (x < 0) {
                            x = 0;
                        }
                    }
                    if (y + h > (Client.canvasHeight - 182)) {
                        y = y - 30;
                        if (y < 0) {
                            y = 0;
                        }
                    }
                }
            }
            Widget rsi = Widget.interfaceCache[Client.openInterfaceId];
            if (rsi != null) {
                offsetX = !Client.instance.isResized() ? 4 : x;
                offsetY = !Client.instance.isResized() ? 4 : y;
                for (int index = 0; index < rsi.children.length; index++) {
                    if (Widget.interfaceCache[rsi.children[index]].scrollMax > 0) {
                        childID = index;
                        positionX = rsi.childX[index];
                        positionY = rsi.childY[index];
                        width = Widget.interfaceCache[rsi.children[index]].width;
                        height = Widget.interfaceCache[rsi.children[index]].height;
                        break;
                    }
                }
                if (mouseX > offsetX + positionX && mouseY > offsetY + positionY && mouseX < offsetX + positionX + width
                        && mouseY < offsetY + positionY + height) {
                    canZoom = false;
                    Widget.interfaceCache[rsi.children[childID]].scrollPosition += rotation * 30;
                } else {
                    canZoom = true;
                }
            }
        }
    }

}
