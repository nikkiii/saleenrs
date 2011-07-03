package org.saleen.rs2.model;

import org.jboss.netty.channel.Channel;
import org.saleen.rs2.net.ISAACCipher;

/**
 * Contains details about a player (but not the actual <code>Player</code>
 * object itself) that has not logged in yet.
 * 
 * @author Graham Edgecombe
 * 
 */
public class PlayerDetails {

	/**
	 * The session.
	 */
	private Channel session;

	/**
	 * The player name.
	 */
	private String name;

	/**
	 * The player password.
	 */
	private String pass;

	/**
	 * The player's UID.
	 */
	private int uid;

	/**
	 * The player's hardware profile
	 */
	private String profile;

	/**
	 * The incoming ISAAC cipher.
	 */
	private ISAACCipher inCipher;

	/**
	 * The outgoing ISAAC cipher.
	 */
	private ISAACCipher outCipher;

	/**
	 * Creates the player details class.
	 * 
	 * @param session
	 *            The session.
	 * @param name
	 *            The name.
	 * @param pass
	 *            The password.
	 * @param uid
	 *            The unique id.
	 * @param inCipher
	 *            The incoming cipher.
	 * @param outCipher
	 *            The outgoing cipher.
	 */
	public PlayerDetails(Channel session, String name, String pass, int uid,
			String profile, ISAACCipher inCipher, ISAACCipher outCipher) {
		this.session = session;
		this.name = name;
		this.pass = pass;
		this.uid = uid;
		this.profile = profile;
		this.inCipher = inCipher;
		this.outCipher = outCipher;
	}

	/**
	 * Gets the <code>Channel</code>.
	 * 
	 * @return The <code>Channel</code>.
	 */
	public Channel getSession() {
		return session;
	}

	/**
	 * Gets the name.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the password.
	 * 
	 * @return The password.
	 */
	public String getPassword() {
		return pass;
	}

	/**
	 * Gets the unique id.
	 * 
	 * @return The unique id.
	 */
	public int getUID() {
		return uid;
	}

	/**
	 * Gets the incoming ISAAC cipher.
	 * 
	 * @return The incoming ISAAC cipher.
	 */
	public ISAACCipher getInCipher() {
		return inCipher;
	}

	/**
	 * Gets the outgoing ISAAC cipher.
	 * 
	 * @return The outgoing ISAAC cipher.
	 */
	public ISAACCipher getOutCipher() {
		return outCipher;
	}

	/**
	 * Gets the player's hardware profile hash
	 * 
	 * @return The profile hash
	 */
	public String getProfile() {
		return profile;
	}

}
