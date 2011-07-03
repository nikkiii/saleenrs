package org.saleen.rs2.task.impl;

import org.jboss.netty.channel.Channel;
import org.saleen.rs2.Constants;
import org.saleen.rs2.GameEngine;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.World;
import org.saleen.rs2.net.Packet;
import org.saleen.rs2.task.Task;

/**
 * A task that is executed when a session receives a message.
 * 
 * @author Graham Edgecombe
 * @author Nikki
 * 
 */
public class ChannelMessageTask implements Task {

	/**
	 * The session.
	 */
	private Channel channel;

	/**
	 * The packet.
	 */
	private Packet message;

	/**
	 * Creates the session message task.
	 * 
	 * @param session
	 *            The session.
	 * @param message
	 *            The packet.
	 */
	public ChannelMessageTask(Channel channel, Packet message) {
		this.channel = channel;
		this.message = message;
	}

	@Override
	public void execute(GameEngine context) {
		Player player = World.getWorld().getChannelStorage().get(channel);
		if (player != null) {
			if (player.getPacketQueue().size() < Constants.PLAYER_MAX_PACKETS) {
				player.getPacketQueue().add(message);
			} else {
				// TODO anything when the size is too large?
			}
		}
	}

}
