package org.saleen.event.impl;

import org.saleen.event.Event;
import org.saleen.rs2.model.Player;

public class CommandEvent implements Event {

	/**
	 * The player this event belongs to
	 */
	private Player player;

	/**
	 * The string containing the command and args
	 */
	private String commandString;

	/**
	 * Create a command event
	 * 
	 * @param player
	 * @param commandString
	 */
	public CommandEvent(Player player, String commandString) {
		this.player = player;
		this.commandString = commandString;
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
	 * Get the command string
	 * 
	 * @return The string
	 */
	public String getCommandString() {
		return commandString;
	}
}
