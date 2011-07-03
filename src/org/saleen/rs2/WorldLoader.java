package org.saleen.rs2;

import org.saleen.ls.PlayerData;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.PlayerDetails;

/**
 * An interface which describes the methods for loading persistent world
 * information such as players.
 * 
 * @author Graham Edgecombe
 * 
 */
public interface WorldLoader {

	/**
	 * Represents the result of a login request.
	 * 
	 * @author Graham Edgecombe
	 * 
	 */
	public static class LoginResult {

		/**
		 * The return code.
		 */
		private int returnCode;

		/**
		 * The player object, or <code>null</code> if the login failed.
		 */
		private Player player;

		private PlayerData playerData;

		/**
		 * Creates a login result that failed.
		 * 
		 * @param returnCode
		 *            The return code.
		 */
		public LoginResult(int returnCode) {
			this.returnCode = returnCode;
			this.player = null;
		}

		/**
		 * Creates a login result that succeeded.
		 * 
		 * @param returnCode
		 *            The return code.
		 * @param player
		 *            The player object.
		 */
		public LoginResult(int returnCode, Player player) {
			this.returnCode = returnCode;
			this.player = player;
		}

		public LoginResult(int code, PlayerData playerData) {
			this.returnCode = code;
			this.playerData = playerData;
		}

		/**
		 * Gets the return code.
		 * 
		 * @return The return code.
		 */
		public int getReturnCode() {
			return returnCode;
		}

		/**
		 * Gets the player.
		 * 
		 * @return The player.
		 */
		public Player getPlayer() {
			return player;
		}

		public PlayerData getPlayerData() {
			return playerData;
		}

	}

	/**
	 * Checks if a set of login details are correct. If correct, creates but
	 * does not load, the player object.
	 * 
	 * @param pd
	 *            The login details.
	 * @return The login result.
	 */
	public LoginResult checkLogin(PlayerDetails pd);

	/**
	 * Loads player information.
	 * 
	 * @param player
	 *            The player object.
	 * @return <code>true</code> on success, <code>false</code> on failure.
	 */
	public boolean loadPlayer(Player player);

	/**
	 * Saves player information.
	 * 
	 * @param player
	 *            The player object.
	 * @return <code>true</code> on success, <code>false</code> on failure.
	 */
	public boolean savePlayer(Player player);

}
