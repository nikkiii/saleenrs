package org.saleen;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelLocal;

/**
 * A class to store attachments to channels
 * 
 * @author Nikki
 * 
 * @param <T>
 *            The class type
 */
public class ChannelStorage<T> {

	/**
	 * The channel local instance
	 */
	private ChannelLocal<T> channelLocal = new ChannelLocal<T>();

	/**
	 * Remove a channel
	 * 
	 * @param channel
	 *            The channel
	 */
	public void remove(Channel channel) {
		channelLocal.remove(channel);
	}

	/**
	 * Set a channel's attachment
	 * 
	 * @param channel
	 *            The channel
	 * @param t
	 *            The attachment
	 */
	public void set(Channel channel, T t) {
		channelLocal.set(channel, t);
	}

	/**
	 * Get a channel attachment
	 * 
	 * @param channel
	 *            The channel
	 * @return The attachment
	 */
	public T get(Channel channel) {
		return (T) channelLocal.get(channel);
	}

	/**
	 * Check if the channellocal contains the attachment
	 * 
	 * @param channel
	 *            The channel
	 * @return
	 */
	public boolean contains(Channel channel) {
		return channelLocal.get(channel) != null;
	}
}
