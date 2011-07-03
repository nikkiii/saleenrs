package org.saleen.event.impl;

import org.saleen.event.Event;
import org.saleen.rs2.model.Player;

/**
 * An event which is called when an actionbutton is called
 * 
 * @author Nikki
 * 
 */
public class ActionButtonEvent implements Event {

	/**
	 * The player this event belongs to
	 */
	private Player player;

	/**
	 * The button of this event
	 */
	private int button;

	/**
	 * Event constructor
	 * 
	 * @param player
	 * @param button
	 */
	public ActionButtonEvent(Player player, int button) {
		this.player = player;
		this.button = button;
	}

	/**
	 * Get the player
	 * 
	 * @return The player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Get the button
	 * 
	 * @return The button
	 */
	public int getButton() {
		return button;
	}
}
