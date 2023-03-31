package com.runescape.draw.teleports;

import com.runescape.Client;
import com.runescape.Client.ScreenMode;
import com.runescape.engine.impl.MouseHandler;
import com.runescape.graphics.RSFont;
import com.runescape.draw.Rasterizer2D;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeleportChatBox {


	public static ArrayList<HierarchyOption> hierarchyOptions = new ArrayList<>();



	public static void addCategory(String name, int key, String descriptions) {
		ParentHierarchyOption parentHierarchyOption = new ParentHierarchyOption() {
			@Override
			public Dimension getDimension() {
				return new Dimension(SUPER_WIDTH, SUPER_HEIGHT);
			}

			@Override
			public String getName() {
				return name;
			}

			@Override
			public int getShortcutKey() {
				return key;
			}

			@Override
			public String getDescription() {
				return descriptions;
			}

			@Override
			public ArrayList<HierarchyOption> getOptions() {
				return new ArrayList<HierarchyOption>(20);
			}
		};
		hierarchyOptions.add(parentHierarchyOption);
	}

	public static void addTeleport(String category, int shortcutKey, String name) {
		HierarchyOption option = new HierarchyOption() {

			@Override
			public Dimension getDimension() {
				return new Dimension(160, SUPER_HEIGHT);
			}

			@Override
			public String getName() {
				return name;
			}

			@Override
			public int getShortcutKey() {
				return shortcutKey;
			}

			@Override
			public String getDescription() {
				return "sdfsd";
			}

			@Override
			public int[] getIndex() {
				return new int[0];
			}

			@Override
			public ArrayList<HierarchyOption> getOptions() {
				return null;
			}
		};
		hierarchyOptions.stream().filter(it-> it.getName() == category).findFirst().get().getOptions().add(option);
	}

	public static final int PVP_TELEPORT_TYPE = 0;
	public static final int MINIGAMES_TELEPORT_TYPE = 1;
	public static final int BOSSES_TELEPORT_TYPE = 2;
	public static final int SKILLS_TELEPORT_TYPE = 3;


	private static final int SUPER_WIDTH = 98;
	private static final int SUPER_HEIGHT = 13;

	private static HierarchyOption selectedHierarchy;
	private static int clickedIndex = -1;

	public static void draw(int offsetX, int offsetY) {
		int mouseX = MouseHandler.mouseX;
		int mouseY = MouseHandler.mouseY - (Client.canvasHeight - 165) + offsetY;
		int chatboxWidth = !Client.instance.isResized() ? 501 : 498;
		int chatboxHeight = 166;
		boolean click = MouseHandler.clickMode3 == 1;
		Rasterizer2D.drawTransparentHorizontalLine(10, 24 + offsetY, chatboxWidth, 0x847963, 255);
		Rasterizer2D.drawTransparentHorizontalLine(10, 23 + offsetY, chatboxWidth, 0x847963, 255);

		// Draw and handle close button..
		int spriteDrawX = (offsetX + 7);
		int spriteDrawY = (offsetY + 7);
		boolean closeHover = mouseX >= spriteDrawX && mouseX <= (spriteDrawX + 15) && mouseY >= spriteDrawY && mouseY <= (spriteDrawY + 15);
		Client.spriteCache.lookup(137).drawHoverSprite(spriteDrawX,spriteDrawY,Client.spriteCache.lookup(138));
		Rasterizer2D.drawBox(spriteDrawX, spriteDrawY + 16,chatboxWidth + 4,2,0x847963);

		if (click && closeHover) {
			close();
			return;
		}

		final RSFont font = Client.instance.newRegularFont;
		int optionX = 13 + offsetX;
		int optionY = 36 + offsetY;
		int index = 0;
		int hoveredIndex = -1;
		int hoveredOptionX = -1;
		int hoveredOptionY = -1;
		List<HierarchyOption> options = hierarchyOptions;
		for (HierarchyOption hierarchyOption : options) {

			if(clickedIndex == index) {
				Rasterizer2D.drawTransparentBox(optionX, optionY, hierarchyOption.getDimension().width - 6, 13, 0x56733E, 50);
			}

			if (hierarchyOption.getShortcutKey() != -1) {
				font.drawBasicString(KeyEvent.getKeyText(hierarchyOption.getShortcutKey()) + ".", optionX, optionY, 0x696969, -1);
			}

			font.drawBasicString(hierarchyOption.getName(), 15 + optionX, optionY, 0x000000, -1);

			if (hierarchyOption.getIndex() == null) {
				int textWidth = font.getTextWidth(hierarchyOption.getName());
				Client.spriteCache.lookup(615).drawAdvancedSprite(20 + textWidth + optionX, optionY - 9);
			}

			if (mouseX >= optionX && mouseX <= optionX + hierarchyOption.getDimension().width && mouseY >= optionY - 10 && mouseY <= optionY - 10 + hierarchyOption.getDimension().height) {
				hoveredIndex = index;
				hoveredOptionX = optionX;
				hoveredOptionY = optionY - hierarchyOption.getDimension().height;
			}

			optionY += font.baseCharacterHeight;
			index++;
		}

		if (hoveredIndex >= 0 && hoveredIndex < options.size()) {
			HierarchyOption hierarchyOption = options.get(hoveredIndex);
			Rasterizer2D.drawTransparentBox(5 + (hoveredOptionX > 10 ? hoveredOptionX - 5 : 0), hoveredOptionY + 1, hierarchyOption.getDimension().width - 6, 13, 0x56733E, 150);

			if (click) {
				if (hierarchyOption.getIndex() == null) {
					clickedIndex = hoveredIndex;
					selectedHierarchy = hierarchyOption;
				} else {
					sendTeleportPacket(hierarchyOption);
				}
			}
		}

		if (selectedHierarchy != null) {
			Rasterizer2D.drawBox(108, 25 + offsetY,2,chatboxHeight - 55,0x847963);

			optionX = 2 + optionX + 100;
			optionY = 36 + offsetY;
			index = 0;
			hoveredIndex = -1;
			hoveredOptionX = -1;
			hoveredOptionY = -1;
			options = selectedHierarchy.getOptions();
			if (options == null)
				return;
			for (HierarchyOption hierarchyOption : options) {
				if (optionY >= chatboxHeight - 23 + offsetY) {
					optionY = 36 + offsetY;
					optionX += hierarchyOption.getDimension().width;
				}
				String shortcutKey = null;
				if (hierarchyOption.getShortcutKey() != -1) {
					shortcutKey = KeyEvent.getKeyText(hierarchyOption.getShortcutKey()) + ".";
					font.drawBasicString(shortcutKey, optionX, optionY, 0x696969, -1);
				}
				font.drawBasicString(hierarchyOption.getName(), 15 + optionX, optionY, 0x000000, -1);

				if (hierarchyOption.getIndex() == null) {
					int textWidth = font.getTextWidth(hierarchyOption.getName());
					Client.spriteCache.draw(615, 20 + textWidth + optionX, optionY - 10, true);
				}

				if (mouseX >= optionX
						&& mouseX <= optionX + hierarchyOption.getDimension().width
						&& mouseY >= optionY - 10
						&& mouseY <= optionY - 10 + hierarchyOption.getDimension().height) {
					hoveredIndex = index;
					hoveredOptionX = optionX;
					hoveredOptionY = optionY - hierarchyOption.getDimension().height;
				}

				optionY += font.baseCharacterHeight;
				index++;
			}

			if (hoveredIndex >= 0 && hoveredIndex < options.size()) {
				HierarchyOption hierarchyOption = options.get(hoveredIndex);
				int pixelsLength = hierarchyOption.getDimension().width;
				if ((hoveredOptionX + pixelsLength) > 509) {
					pixelsLength = (509 - hoveredOptionX);
				}
				Rasterizer2D.drawTransparentBox(hoveredOptionX,
						hoveredOptionY + 1,
						pixelsLength, 13, 0, 50);

				if (click) {
					if (hierarchyOption.getIndex() == null)
						selectedHierarchy = hierarchyOption;
					else
						sendTeleportPacket(hierarchyOption);
				}
			}
		}

		boolean mainTitleHover = mouseX >= 28 && mouseX <= 136 && mouseY >= offsetY + 4 && mouseY <= offsetY + 21;
		if (mainTitleHover) {
			// Clicking main tab title should close current selection
			if (click) {
				selectedHierarchy = null;
			}

			Rasterizer2D.drawTransparentBox(29, 10 + offsetY, 108, 11, 0, 50);
		}
		font.drawBasicString("Teleportation menu" + (selectedHierarchy != null ? " -> " + selectedHierarchy.getName() : ""), 28, 20 + offsetY, 0x000080, -1);
	}

	public static void pressKey(int key) {
		if (key == KeyEvent.VK_DELETE || key == KeyEvent.VK_BACK_SPACE) {
			selectedHierarchy = null;
			return;
		}

		boolean found = false;
		if (selectedHierarchy != null
				&& selectedHierarchy.getOptions() != null) {

			List<HierarchyOption> options = selectedHierarchy.getOptions();
			for (HierarchyOption hierarchyOption : options) {

				if (hierarchyOption.getShortcutKey() == key) {

					if (hierarchyOption.getIndex() != null) {
						sendTeleportPacket(hierarchyOption);
					} else {
						selectedHierarchy = hierarchyOption;
					}
					return;
				}
			}
		}

		if (!found) {
			List<HierarchyOption> options = hierarchyOptions;
			for (HierarchyOption hierarchyOption : options) {
				if (hierarchyOption.getShortcutKey() == key) {
					if (hierarchyOption.getIndex() != null) {
						sendTeleportPacket(hierarchyOption);
					} else {
						selectedHierarchy = hierarchyOption;
					}
					return;
				}
			}
		}
	}

	public static void sendTeleportPacket(HierarchyOption teleport) {
		if (isOpen()) {

			// Send packet
			final int[] index = teleport.getIndex();
			if (index.length == 2) {
				Client.instance.packetSender.sendTeleportSelection(index[0], index[1]);
			}

			// Reset selected teleport
			selectedHierarchy = null;

			// Close interface
			close();
		}
	}

	public static void open(int index) {
		if (index < 0 || index >= hierarchyOptions.size()) {
			selectedHierarchy = null;
		} else {
			selectedHierarchy = hierarchyOptions.get(index);
		}
		open();
	}

	public static boolean isOpen() {
		return Client.instance.inputDialogState == 3;
	}

	public static void open() {
		Client.instance.inputDialogState = 3;
		Client.updateChatbox = true;
	}

	public static void close() {
		Client.instance.inputDialogState = 0;
		Client.updateChatbox = true;
	}

}
