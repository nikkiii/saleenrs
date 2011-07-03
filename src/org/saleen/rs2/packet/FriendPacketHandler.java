package org.saleen.rs2.packet;

import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.Settings.ChatMode;
import org.saleen.rs2.model.World;
import org.saleen.rs2.net.Packet;
import org.saleen.rs2.util.NameUtils;

public class FriendPacketHandler implements PacketHandler {

	private static final int ADD_FRIEND = 188, ADD_IGNORE = 133,
			REMOVE_FRIEND = 215, REMOVE_IGNORE = 74, SEND_MESSAGE = 126,
			CHANGE_CHAT_MODE = 95;

	@Override
	public void handle(Player player, Packet packet) {
		switch (packet.getOpcode()) {
		case ADD_FRIEND:
			addFriend(player, packet);
			break;
		case ADD_IGNORE:
			addIgnore(player, packet);
			break;
		case REMOVE_FRIEND:
			removeFriend(player, packet);
			break;
		case REMOVE_IGNORE:
			removeIgnore(player, packet);
			break;
		case SEND_MESSAGE:
			sendPrivateMessage(player, packet);
			break;
		case CHANGE_CHAT_MODE:
			changeChatMode(player, packet);
			break;
		default:
			player.getActionSender().sendMessage(
					"Unhandled friend packet: " + packet.getOpcode());
			break;
		}
	}

	/**
	 * Add a friend to the list
	 * 
	 * @param player
	 *            The player to handle this packet for
	 * @param packet
	 *            The packet to read from
	 */
	private void addFriend(Player player, Packet packet) {
		long name = packet.getLong();
		if (player.getFriendsList().contains(name)) {
			// Already contains
		} else {
			player.getFriendsList().add(name);
			player.getFriendsList().updateStatus(name);
			player.getFriendsList().update(name);
		}
		player.getActionSender().sendMessage(
				"Add friend [name=" + NameUtils.longToName(name) + "]");
	}

	/**
	 * Add an ignore to the list
	 * 
	 * @param player
	 *            The player to handle this packet for
	 * @param packet
	 *            The packet to read from
	 */
	private void addIgnore(Player player, Packet packet) {
		long name = packet.getLong();
		if (player.getIgnoreList().contains(name)) {
			// Already has
		} else {
			player.getIgnoreList().add(name);
			player.getIgnoreList().update();
		}
		player.getActionSender().sendMessage(
				"Add ignore [name=" + NameUtils.longToName(name) + "]");
	}

	/**
	 * Remove a friend from their list
	 * 
	 * @param player
	 *            The player to handle this packet for
	 * @param packet
	 *            The packet to read from
	 */
	private void removeFriend(Player player, Packet packet) {
		long name = packet.getLong();
		if (!player.getFriendsList().contains(name)) {
			// Doesn't contain
		} else {
			player.getFriendsList().remove(name);
			player.getActionSender().sendMessage(
					"Remove friend [name=" + NameUtils.longToName(name) + "]");
		}
	}

	/**
	 * Remove an ignore from the list
	 * 
	 * @param player
	 *            The player to handle this packet for
	 * @param packet
	 *            The packet to read from
	 */
	private void removeIgnore(Player player, Packet packet) {
		long name = packet.getLong();
		if (!player.getIgnoreList().contains(name)) {
			// Doesn't contain
		} else {
			player.getIgnoreList().remove(name);
			player.getIgnoreList().update();
		}
		player.getActionSender().sendMessage(
				"Remove ignore [name=" + NameUtils.longToName(name) + "]");
	}

	/**
	 * Send a private message from this player to another player
	 * 
	 * @param player
	 *            The player to send from
	 * @param packet
	 *            The packet to read from
	 */
	private void sendPrivateMessage(Player player, Packet packet) {
		long playerTo = packet.getLong();
		int messageSize = packet.getLength() - 8;
		byte[] messageBytes = new byte[messageSize];
		packet.get(messageBytes);

		if (player.getFriendsList().contains(playerTo)) {
			Player messageTo = World.getWorld().findPlayer(playerTo);
			if (messageTo.getFriendsList().contains(player.getNameAsLong())
					&& messageTo.getSettings().getPrivateChatMode() != ChatMode.OFF
					&& player.getRights().toInteger() < 2
					|| messageTo.getSettings().getPrivateChatMode() == ChatMode.ON
					|| player.getRights().toInteger() > 1) {
				messageTo.getActionSender().sendPrivateMessage(
						player.getNameAsLong(), player.getRights().toInteger(),
						messageBytes, messageSize);
			} else {
				player.getActionSender().sendMessage(
						"This player is currently offline");
			}
		} else {
			player.getActionSender().sendMessage(
					"That player is not on your Friends list.");
		}
	}

	/**
	 * Change chat modes for the player
	 * 
	 * @param player
	 *            The player to change for
	 * @param packet
	 *            The packet to read from
	 */
	private void changeChatMode(Player player, Packet packet) {
		// 0 = on, 1 = friends, 2 = off, 3 = hide
		int publicMode = packet.get();
		int privateMode = packet.get();
		int tradeMode = packet.get();
		player.getSettings().setChatModes(publicMode, privateMode, tradeMode);
		// TODO update the player's friends list!
		long name = player.getNameAsLong();
		for (Player pl : World.getWorld().getPlayers()) {
			if (pl.getFriendsList().contains(name)) {
				pl.getFriendsList().updateStatus(name);
				pl.getFriendsList().update(name);
			}
		}
	}
}
