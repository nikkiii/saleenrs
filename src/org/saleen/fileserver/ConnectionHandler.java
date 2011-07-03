package org.saleen.fileserver;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.saleen.ChannelStorage;
import org.saleen.fileserver.UpdateSession.Type;

/**
 * Handles connection events.
 * 
 * @author Graham Edgecombe
 * 
 */
public class ConnectionHandler extends SimpleChannelHandler {

	private static ChannelStorage<UpdateSession> storage = new ChannelStorage<UpdateSession>();

	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger
			.getLogger(ConnectionHandler.class.getName());

	/**
	 * The type of handler we are.
	 */
	private Type type;

	/**
	 * Creates the handler.
	 * 
	 * @param type
	 *            The type of handler.
	 */
	public ConnectionHandler(Type type) {
		this.type = type;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		logger.log(Level.SEVERE, "Error while handling request.", e.getCause());
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		storage.get(e.getChannel()).readLine((String) e.getMessage());
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		storage.set(e.getChannel(), new UpdateSession(type, e.getChannel()));
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		storage.remove(e.getChannel());
	}
}
