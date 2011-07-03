package org.saleen.rs2.task.impl;

import java.util.logging.Logger;

import org.jboss.netty.channel.Channel;
import org.saleen.rs2.GameEngine;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.World;
import org.saleen.rs2.task.Task;

/**
 * A task that is executed when a session is closed.
 * 
 * @author Graham Edgecombe
 * 
 */
public class ChannelClosedTask implements Task {

	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger
			.getLogger(ChannelClosedTask.class.getName());

	/**
	 * The session that closed.
	 */
	private Channel channel;

	/**
	 * Creates the session closed task.
	 * 
	 * @param channel
	 *            The session.
	 */
	public ChannelClosedTask(Channel channel) {
		this.channel = channel;
	}

	@Override
	public void execute(GameEngine context) {
		logger.fine("Channel closed : " + channel.getRemoteAddress());
		if (World.getWorld().getChannelStorage().contains(channel)) {
			Player p = World.getWorld().getChannelStorage().get(channel);
			World.getWorld().unregister(p);
		}
	}

}
