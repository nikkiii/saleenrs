package org.saleen.ls;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.saleen.ls.NodeConfiguration.WorldType;
import org.saleen.rs2.util.ChannelBufferUtils;
import org.saleen.util.login.LoginPacket;

/**
 * Handles the login server connections.
 * 
 * @author Graham Edgecombe
 * 
 */
public class WorldlistConnectionHandler extends SimpleChannelHandler {

	/**
	 * The login server.
	 */
	private LoginServer server;

	/**
	 * Creates the connection handler.
	 * 
	 * @param server
	 *            The server.
	 */
	public WorldlistConnectionHandler(LoginServer server) {
		this.server = server;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getChannel().close();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, final MessageEvent e)
			throws Exception {
		server.pushTask(new Runnable() {
			public void run() {
				handleWorldlist(e.getChannel(), (LoginPacket) e.getMessage());
			}
		});
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		/*
		 * session.getFilterChain().addFirst("protocolCodecFilter", new
		 * ProtocolCodecFilter(new LoginPipelineFactory()));
		 */
	}

	private void handleWorldlist(Channel channel, LoginPacket message) {
		ChannelBuffer data = ChannelBuffers.dynamicBuffer();
		data.writeByte((byte) NodeManager.getNodeManager().listConfigurations()
				.size());
		if (message.getOpcode() == 0) {
			// Node list
			for (NodeConfiguration config : NodeManager.getNodeManager()
					.listConfigurations()) {
				data.writeByte((byte) config.getNodeid());
				ChannelBufferUtils.putRS2String(data, config.getHost());// HOST
				data.writeShort((short) config.getPort());
				if (NodeManager.getNodeManager().isNodeOnline(
						config.getNodeid())) {
					data.writeShort((short) NodeManager.getNodeManager()
							.getNode(config.getNodeid()).getPlayerCount());
				} else {
					data.writeShort(Short.MAX_VALUE);
				}
				ChannelBufferUtils.putRS2String(data, config.getDescription());
				data.writeByte((byte) (config.getType() == WorldType.MEMBERS ? 1
						: 0));
			}
		} else {
			// Node update
			for (NodeConfiguration config : NodeManager.getNodeManager()
					.listConfigurations()) {
				data.writeByte((byte) config.getNodeid());
				if (NodeManager.getNodeManager().isNodeOnline(
						config.getNodeid())) {
					data.writeShort((short) NodeManager.getNodeManager()
							.getNode(config.getNodeid()).getPlayerCount());
				} else {
					data.writeShort(Short.MAX_VALUE);
				}
			}
		}
		channel.write(new LoginPacket(message.getOpcode(), data)).addListener(
				ChannelFutureListener.CLOSE);
	}
}
