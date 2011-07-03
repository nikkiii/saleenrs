package org.saleen.commandplugin;

import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.Player.Rights;

/**
 * A class which is implemented for each command
 * 
 * @author Nikki
 *
 */
public interface Command {
	
	/**
	 * Handle a command
	 * @param player
	 * 			The player to handle ofr
	 * @param params
	 * 			The params
	 */
	public void handle(Player player, String params);

	/**
	 * Returns the command rank needed.
	 */
	public Rights getRights();
}
