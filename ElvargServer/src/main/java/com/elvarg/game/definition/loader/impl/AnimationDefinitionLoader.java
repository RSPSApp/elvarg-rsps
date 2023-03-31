package com.elvarg.game.definition.loader.impl;

import com.elvarg.game.definition.*;

import com.elvarg.game.GameConstants;
import com.elvarg.game.definition.loader.DefinitionLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AnimationDefinitionLoader extends DefinitionLoader {

	@Override
	public void load() throws Throwable {
		//TODO temporary until the definitions dumped from OSRS are fully filled.
		//These are the items that had animations that were not the default values.
		//This is to ensure feature parity with the original system.
		load(GameConstants.DEFINITIONS_DIRECTORY + "item-animations-elvarg.json");
		load(file());
	}

	private void load(String file) throws Throwable {
		FileReader reader = new FileReader(file);
		_AnimationDefinition[] defs = new Gson().fromJson(reader, _AnimationDefinition[].class);
		for (_AnimationDefinition def : defs) {
			AnimationDefinition d = new AnimationDefinition(def.stand, def.walk, def.run, def.walkR180, def.walkRRight, def.walkRLeft, def.standRRight, def.standRLeft);
			for (int i : def.ids) {
				AnimationDefinition.definitions.put(i, d);
			}
		}
	}

	@Override
	public String file() {
		return GameConstants.DEFINITIONS_DIRECTORY + "item-animations.json";
	}

	private static class _AnimationDefinition {
		public int[] ids;

		public int stand;
		public int walk;

		public int run;
		public int walkR180;
		public int walkRRight;
		public int walkRLeft;
		public int standRRight;
		public int standRLeft;
	}

}
