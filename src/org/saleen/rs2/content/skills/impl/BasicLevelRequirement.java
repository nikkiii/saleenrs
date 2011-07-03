package org.saleen.rs2.content.skills.impl;

import org.saleen.rs2.model.Player;

/**
 * An implementation of a <code>LevelRequirement</code> which represents a
 * single skill requirement
 * 
 * @author Nikki
 * 
 */
public class BasicLevelRequirement implements LevelRequirement {

	/**
	 * The skill of requirement
	 */
	private int skillId;

	/**
	 * The level requirement
	 */
	private int levelRequirement;

	/**
	 * If this level requirement is a real level, or the current level
	 */
	private boolean realLevel = true;

	/**
	 * Create a level requirement
	 * 
	 * @param skillId
	 *            The skill of this requirement
	 * @param levelRequirement
	 *            The level requirement
	 */
	public BasicLevelRequirement(int skillId, int levelRequirement) {
		this.skillId = skillId;
		this.levelRequirement = levelRequirement;
	}

	/**
	 * Create a level requirement which depends on the current level of the
	 * player
	 */
	public BasicLevelRequirement(int skillId, int levelRequirement,
			boolean realLevel) {
		this.skillId = skillId;
		this.levelRequirement = levelRequirement;
		this.realLevel = realLevel;
	}

	@Override
	public boolean hasRequirements(Player player) {
		if (realLevel) {
			return player.getSkills().getLevelForExperience(skillId) >= levelRequirement;
		}
		return player.getSkills().getLevel(skillId) >= levelRequirement;
	}

	/**
	 * Get the requirement's skill id
	 * 
	 * @return The skillid
	 */
	public int getSkillId() {
		return skillId;
	}

	/**
	 * Get the required level
	 * 
	 * @return The level
	 */
	public int getRequiredLevel() {
		return levelRequirement;
	}
}
