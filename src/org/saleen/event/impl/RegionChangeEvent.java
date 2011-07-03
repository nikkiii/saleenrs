package org.saleen.event.impl;

import org.saleen.event.Event;
import org.saleen.rs2.model.Player;

/**
 * An event which is called when a player changes regions.
 * 
 * @author Nikki
 * 
 */
public class RegionChangeEvent implements Event {

	/**
	 * The player this event belongs to
	 */
	private Player player;

	/**
	 * Create an event for the player
	 * 
	 * @param player
	 *            The player
	 */
	public RegionChangeEvent(Player player) {
		this.player = player;
	}

	/**
	 * Get the player
	 * 
	 * @return The player
	 */
	public Player getPlayer() {
		return player;
	}
}
