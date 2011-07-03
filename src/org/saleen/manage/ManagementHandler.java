package org.saleen.manage;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

/**
 * An implementation of Apache Mina's <code>IoHandlerAdapter</code> which
 * handles console management events
 * 
 * @author Nikki
 * 
 */
public class ManagementHandler extends SimpleChannelHandler {

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getChannel().close();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		ManagementConsole.getInstance().handle(e);
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		ManagementConsole.getInstance().removeSession(e.getChannel());
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		ManagementSession manage = new ManagementSession(e.getChannel());
		ManagementConsole.getInstance().addSession(manage);
	}

}
