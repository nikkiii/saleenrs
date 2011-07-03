package org.saleen.rs2.task.impl;

import java.util.logging.Logger;

import org.jboss.netty.channel.Channel;
import org.saleen.rs2.GameEngine;
import org.saleen.rs2.task.Task;

/**
 * A task that is executed when a session is opened.
 * 
 * @author Graham Edgecombe
 * 
 */
public class ChannelOpenedTask implements Task {

	/**
	 * The logger class.
	 */
	private static final Logger logger = Logger
			.getLogger(ChannelOpenedTask.class.getName());

	/**
	 * The session.
	 */
	private Channel channel;

	/**
	 * Creates the session opened task.
	 * 
	 * @param session
	 *            The session.
	 */
	public ChannelOpenedTask(Channel channel) {
		this.channel = channel;
	}

	@Override
	public void execute(GameEngine context) {
		logger.fine("Session opened : " + channel.getRemoteAddress());
	}

}
