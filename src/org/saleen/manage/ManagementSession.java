package org.saleen.manage;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jboss.netty.channel.Channel;

public class ManagementSession {

	/**
	 * The Channel belonging to this session
	 */
	private Channel channel;

	/**
	 * A list of subscribed channels, by defualt none.
	 */
	private List<String> subscribedChannels = Collections
			.synchronizedList(new LinkedList<String>());

	/**
	 * Whether authenticated or not
	 */
	private boolean authenticated = false;

	/**
	 * Create a session instance
	 * 
	 * @param session
	 *            The IoSession of this session
	 */
	public ManagementSession(Channel channel) {
		this.channel = channel;
	}

	/**
	 * Subscribe to a channel
	 * 
	 * @param channel
	 *            The channel to subscribe to
	 */
	public void subscribe(String channel) {
		subscribedChannels.add(channel);
	}

	/**
	 * Unsubscribe from a channel
	 * 
	 * @param channel
	 *            The channel to unsubscribe from
	 */
	public void unsubscribe(String channel) {
		subscribedChannels.remove(channel);
	}

	/**
	 * Get the IoSession
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * Check if a session is subscribed to a channel
	 * 
	 * @param channel
	 *            The channel to check
	 * @return True if the list contains
	 */
	public boolean isSubscribedTo(String channel) {
		return subscribedChannels.contains(channel);
	}

	/**
	 * Write a string to this session
	 * 
	 * @param construct
	 *            The string
	 */
	public void write(String construct) {
		channel.write(construct);
	}

	/**
	 * Set whether this session is authenticated
	 * 
	 * @param authenticated
	 *            True if authenticated
	 */
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	/**
	 * Check if this session is authenticated
	 * 
	 * @return the value of <code>authenticated</code>
	 */
	public boolean isAuthenticated() {
		return authenticated;
	}
}
