package org.saleen.rs2.task.impl;

import java.util.Queue;

import org.saleen.rs2.GameEngine;
import org.saleen.rs2.model.ChatMessage;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.UpdateFlags.UpdateFlag;
import org.saleen.rs2.net.Packet;
import org.saleen.rs2.net.PacketManager;
import org.saleen.rs2.task.Task;

/**
 * A task which is executed before an <code>UpdateTask</code>. It is similar to
 * the call to <code>process()</code> but you should use <code>Event</code>s
 * instead of putting timers in this class.
 * 
 * @author Graham Edgecombe
 * 
 */
public class PlayerTickTask implements Task {

	/**
	 * The player.
	 */
	private Player player;

	/**
	 * Creates a tick task for a player.
	 * 
	 * @param player
	 *            The player to create the tick task for.
	 */
	public PlayerTickTask(Player player) {
		this.player = player;
	}

	@Override
	public void execute(GameEngine context) {
		Queue<ChatMessage> messages = player.getChatMessageQueue();
		if (messages.size() > 0) {
			player.getUpdateFlags().flag(UpdateFlag.CHAT);
			ChatMessage message = player.getChatMessageQueue().poll();
			player.setCurrentChatMessage(message);
		} else {
			player.setCurrentChatMessage(null);
		}
		Queue<Packet> packets = player.getPacketQueue();
		Packet packet = null;
		while ((packet = packets.poll()) != null) {
			PacketManager.getPacketManager().handle(player, packet);
		}
		player.getWalkingQueue().processNextMovement();
	}

}
