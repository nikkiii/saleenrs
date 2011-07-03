package org.saleen.rs2.content.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.Settings.ChatMode;
import org.saleen.rs2.model.World;

/**
 * A class handling a <code>Player</code>'s friends list
 * 
 * @author Nikki
 * 
 */
public class FriendsList {
	/**
	 * A list containing all friends of this player
	 */
	private Map<Long, FriendStatus> friends = new HashMap<Long, FriendStatus>();

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
	public FriendsList(Player player) {
		this.player = player;
	}

	/**
	 * Update the friends list
	 */
	public void update() {
		for (Entry<Long, FriendStatus> entry : friends.entrySet()) {
			player.getActionSender().sendFriendStatus(
					entry.getKey(),
					entry.getValue() == FriendStatus.ONLINE ? World.getWorld()
							.getNodeId() : 0);
		}
	}

	/**
	 * Update the world of a specific name
	 * 
	 * @param name
	 *            The name to update
	 */
	public void update(long name) {
		player.getActionSender().sendFriendStatus(
				name,
				friends.get(name) == FriendStatus.ONLINE ? World.getWorld()
						.getNodeId() : 0);
	}

	/**
	 * Get a friend for the specified name
	 * 
	 * @param name
	 *            The name to search for
	 * @return The friend, if found
	 */
	public FriendStatus getFriendStatus(long name) {
		return friends.get(name);
	}

	/**
	 * Add a friend
	 * 
	 * @param name
	 *            The friend to add
	 */
	public void add(long name) {
		friends.put(name, FriendStatus.OFFLINE);
	}

	/**
	 * Update a friend's status according to chat modes
	 * 
	 * @param name
	 *            The name to add
	 * @param status
	 *            The status of this friend
	 */
	public void updateStatus(long name) {
		Player updatePlayer = World.getWorld().findPlayer(name);
		if (updatePlayer != null) {
			FriendStatus status = FriendStatus.ONLINE;
			// Moderators and Administrators can see everybody online, no matter
			// what
			if (player.getRights().toInteger() < 1) {
				// If chat mode is off, status is offline
				if (updatePlayer.getSettings().getPrivateChatMode() == ChatMode.OFF) {
					status = FriendStatus.OFFLINE;
					// If mode is friends, check if this player is in their list
				} else if (updatePlayer.getSettings().getPrivateChatMode() == ChatMode.FRIENDS
						&& !updatePlayer.getFriendsList().contains(
								player.getNameAsLong())) {
					status = FriendStatus.OFFLINE;
				}
			}
			player.getFriendsList().setStatus(name, status);
		} else {
			player.getFriendsList().setStatus(name, FriendStatus.OFFLINE);
		}
	}

	/**
	 * Remove a friend from this list
	 * 
	 * @param name
	 *            The name to remove
	 */
	public void remove(long name) {
		friends.remove(name);
	}

	/**
	 * Updates statuses of all friends, if they just logged in
	 */
	public void updateStatuses() {
		for (long name : friends.keySet()) {
			updateStatus(name);
		}
		update();
	}

	/**
	 * Updates status of a specified name
	 */
	public void setStatus(long name, FriendStatus status) {
		if (friends.containsKey(name)) {
			friends.remove(name);
			friends.put(name, status);
		}
	}

	/**
	 * Check if this list contains a name
	 * 
	 * @param name
	 *            The name of the player
	 * @return True, if found
	 */
	public boolean contains(long name) {
		return friends.containsKey(name);
	}

	/**
	 * Get the names of all friends
	 * 
	 * @return The list of names
	 */
	public Set<Long> getFriendNames() {
		return friends.keySet();
	}
}
