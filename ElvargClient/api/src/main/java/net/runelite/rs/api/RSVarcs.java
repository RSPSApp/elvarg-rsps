package net.runelite.rs.api;

import net.runelite.mapping.Import;

import java.util.Map;

public interface RSVarcs
{
	@Import("map")
	Map<Integer, Object> getVarcMap();
}
