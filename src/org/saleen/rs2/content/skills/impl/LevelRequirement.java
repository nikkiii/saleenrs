package org.saleen.rs2.content.skills.impl;

import org.saleen.rs2.model.Player;

/**
 * A simple class which represents a level requirement of a skill to use an
 * object or equip an item
 * 
 * @author Nikki
 * 
 */
public interface LevelRequirement {
	/**
	 * Check whether the player has the requirements needed
	 * 
	 * @param player
	 *            The player to check
	 * @return True, if the player has the requirements
	 */
	public boolean hasRequirements(Player player);
}
