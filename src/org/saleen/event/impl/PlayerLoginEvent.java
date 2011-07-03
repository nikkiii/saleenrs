package org.saleen.event.impl;

import org.saleen.event.Event;
import org.saleen.rs2.model.Player;

/**
 * An event for when players login/logout
 * 
 * @author Nikki
 * 
 */
public class PlayerLoginEvent implements Event {

	/**
	 * The subject player
	 */
	private Player player;

	/**
	 * A boolean to check whether it's a login
	 */
	private boolean playerLogin;

	/**
	 * The constructor
	 * 
	 * @param player
	 *            The player this event belongs to
	 */
	public PlayerLoginEvent(Player player, boolean playerLogin) {
		this.player = player;
		this.playerLogin = playerLogin;
	}

	/**
	 * Get the player
	 * 
	 * @return The associated player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Get whether this is a player login or logout
	 * 
	 * @return true, if login, or false if logout
	 */
	public boolean isPlayerLogin() {
		return playerLogin;
	}

	/**
	 * A sub-class of PlayerLoginEvent which is for Player logins
	 * 
	 * @author Nikki
	 * 
	 */
	public static class PlayerLogin extends PlayerLoginEvent {
		public PlayerLogin(Player player) {
			super(player, true);
		}
	}

	/**
	 * A sub-class of PlayerLoginEvent which is for Player logouts
	 * 
	 * @author Nikki
	 * 
	 */
	public static class PlayerLogout extends PlayerLoginEvent {
		public PlayerLogout(Player player) {
			super(player, false);
		}
	}
}
