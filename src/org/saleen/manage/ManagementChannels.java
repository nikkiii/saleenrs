package org.saleen.manage;

import org.json.JSONObject;

/**
 * A class to handle management console channels, like console messages, server
 * info etc
 * 
 * @author Nikki
 * 
 */
public class ManagementChannels {

	public static final String SERVER = "server";

	public static final String CONSOLE = "console";

	public static final String PLAYERS = "players";

	/**
	 * Construct the data string
	 * 
	 * @param channel
	 *            The channel to send to
	 * @param object
	 *            The data
	 * @return The constructed string
	 */
	public static String construct(String channel, JSONObject object) {
		StringBuilder builder = new StringBuilder();
		builder.append(channel).append(" ").append(object.toString());
		return builder.toString();
	}

}
