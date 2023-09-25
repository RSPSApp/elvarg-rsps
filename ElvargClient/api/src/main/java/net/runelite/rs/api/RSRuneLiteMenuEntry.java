package net.runelite.rs.api;

import net.runelite.api.MenuEntry;

import java.util.function.Consumer;

public interface RSRuneLiteMenuEntry extends MenuEntry
{
	Consumer getConsumer();
	void setConsumer(Consumer consumer);

	int getIdx();
	void setIdx(int idx);
}
