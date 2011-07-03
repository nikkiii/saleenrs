package org.saleen.rs2.net;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.saleen.rs2.GameEngine;
import org.saleen.rs2.model.World;
import org.saleen.rs2.task.impl.ChannelClosedTask;
import org.saleen.rs2.task.impl.ChannelMessageTask;
import org.saleen.rs2.task.impl.ChannelOpenedTask;

/**
 * The <code>ConnectionHandler</code> processes incoming events from Netty
 * (Converted from MINA!), submitting appropriate tasks to the
 * <code>GameEngine</code>, and increasing the appropriate counters.
 * 
 * @author Graham Edgecombe
 * @author Nikki
 * 
 */
public class ConnectionHandler extends SimpleChannelHandler {

	/**
	 * The <code>GameEngine</code> instance.
	 */
	private final GameEngine engine = World.getWorld().getEngine();

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getChannel().close();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		Packet packet = (Packet) e.getMessage();
		engine.pushTask(new ChannelMessageTask(e.getChannel(), packet));
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		engine.pushTask(new ChannelClosedTask(e.getChannel()));
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		engine.pushTask(new ChannelOpenedTask(e.getChannel()));
	}

}
