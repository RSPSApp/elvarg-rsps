package com.elvarg.game.definition;

import java.util.HashMap;
import java.util.Map;

public class AnimationDefinition {

	public static Map<Integer, AnimationDefinition> definitions = new HashMap<>();

	private static final AnimationDefinition DEFAULT = new AnimationDefinition(808, 819, 824, 820, 822, 821, 823, 823);

	/**
	 * Attempts to get the {@link AnimationDefinition} for the given item.
	 *
	 * @param item
	 * @return
	 */
	public static AnimationDefinition forId(int item) {
		return definitions.getOrDefault(item, DEFAULT);
	}

	public int stand;
	public int walk;

	public int run;
	public int walkR180;
	public int walkRRight;
	public int walkRLeft;
	public int standRRight;//TODO check if is always same as standRLeft
	public int standRLeft;//TODO check if is always same as standRRight

	public AnimationDefinition(int stand, int walk, int run, int walkR180, int walkRRight, int walkRLeft,
			int standRRight, int standRLeft) {
		this.stand = stand;
		this.walk = walk;
		this.run = run;
		this.walkR180 = walkR180;
		this.walkRRight = walkRRight;
		this.walkRLeft = walkRLeft;
		this.standRRight = standRRight;
		this.standRLeft = standRLeft;
	}

	public int getStand() {
		return stand;
	}

	public int getWalk() {
		return walk;
	}

	public int getRun() {
		return run;
	}

	public int getWalkR180() {
		return walkR180;
	}

	public int getWalkRRight() {
		return walkRRight;
	}

	public int getWalkRLeft() {
		return walkRLeft;
	}

	public int getStandRRight() {
		return standRRight;
	}

	public int getStandRLeft() {
		return standRLeft;
	}
}
