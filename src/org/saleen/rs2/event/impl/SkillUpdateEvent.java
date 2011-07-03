package org.saleen.rs2.event.impl;

import org.saleen.rs2.event.Event;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.Skills;

/**
 * An event which normalizes skill levels
 * 
 * @author Nikki
 * 
 */
public class SkillUpdateEvent extends Event {

	/**
	 * The delay, 1 tick
	 */
	private static final int DELAY = 600000;

	/**
	 * The player this event belongs to
	 */
	private Player player;

	public SkillUpdateEvent(Player player) {
		super(DELAY);
		this.player = player;
	}

	@Override
	public void execute() {
		if (player.isDead()) {
			return;
		}
		for (int i = 0; i < Skills.SKILL_COUNT; i++) {
			if (player.getSkills().getLevel(i) != player.getSkills()
					.getLevelForExperience(i)) {
				player.getSkills().normalizeLevel(i);
			}
		}
	}

}
