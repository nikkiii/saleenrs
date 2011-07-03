package org.saleen.rs2.content.chat;

import java.util.LinkedList;

import org.saleen.rs2.model.Player;

/**
 * A class handling a <code>Player</code>'s ignore list
 * 
 * @author Nikki
 * 
 */
public class IgnoreList {

	/**
	 * A list containing all of the ignores for this player
	 */
	private LinkedList<Long> ignores = new LinkedList<Long>();

	/**
	 * The player this list belongs to
	 */
	private Player player;

	/**
	 * Class constructor
	 * 
	 * @param player
	 *            The player this list belongs to
	 */

	public IgnoreList(Player player) {
		this.player = player;
	}

	/**
	 * Add a name to this list
	 * 
	 * @param name
	 *            The name to add
	 */
	public void add(long name) {
		ignores.add(name);
	}

	/**
	 * Remove a name from this list
	 * 
	 * @param name
	 *            The name to remove
	 */
	public void remove(long name) {
		ignores.remove(name);
	}

	/**
	 * Check if the list contains a name
	 * 
	 * @param name
	 *            The name to check
	 * @return True, if found
	 */
	public boolean contains(long name) {
		return ignores.contains(name);
	}

	/**
	 * Update this list to the client
	 */
	public void update() {
		player.getActionSender().sendIgnores(ignores);
	}
}
