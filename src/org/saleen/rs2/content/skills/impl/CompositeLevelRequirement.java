package org.saleen.rs2.content.skills.impl;

import java.util.LinkedList;
import java.util.List;

import org.saleen.rs2.model.Player;

/**
 * An implementation of a <code>LevelRequirement</code> which checks multiple
 * skills
 * 
 * @author Nikki
 * 
 */
public class CompositeLevelRequirement implements LevelRequirement {

	/**
	 * A list of requirements
	 */
	private LinkedList<BasicLevelRequirement> requirements = new LinkedList<BasicLevelRequirement>();

	/**
	 * Create a requirement for the specified skills and add to the list
	 * 
	 * @param skillIds
	 *            The list of skill ids
	 * @param skillLevels
	 *            The list of skill levels
	 */
	public CompositeLevelRequirement(List<Integer> skillIds,
			List<Integer> skillLevels) {
		for (int i = 0; i < skillIds.size(); i++) {
			requirements.add(new BasicLevelRequirement(skillIds.get(i),
					skillLevels.get(i)));
		}
	}

	@Override
	public boolean hasRequirements(Player player) {
		for (BasicLevelRequirement requirement : requirements) {
			if (!requirement.hasRequirements(player)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Get the missing requirement
	 * 
	 * @param player
	 *            The player
	 * @return The requirement
	 */
	public BasicLevelRequirement getMissingRequirement(Player player) {
		for (BasicLevelRequirement requirement : requirements) {
			if (!requirement.hasRequirements(player)) {
				return requirement;
			}
		}
		return null;
	}
}
