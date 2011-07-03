package org.saleen.ls;

import java.math.BigInteger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.saleen.rs2.util.ChannelBufferUtils;
import org.saleen.util.login.LoginPacket;

/**
 * Handles the login server connections.
 * 
 * @author Graham Edgecombe
 * 
 */
public class LoginConnectionHandler extends SimpleChannelHandler {

	private static final BigInteger PRIVATE_EXPONENT = new BigInteger(
			"12160565909878134513542208563916921578559367992938132211698335303100237123750357497888014813514595144859378797035659730360986152512875686874208466202122184010210564590027768351166885878447053947400736273371875215550434532549959967976619533878978773497580105520348157709227858124074243729122053740182689808073");
	private static final BigInteger PRIVATE_MODULUS = new BigInteger(
			"119056917842199477384824577629731593889161233963726676241122467995112076543057540982833407056066032567769511237126685352355534729195747518774275507841123651194666618239749539089515833960877660455810691679998524799477896365070704127522441581953124147767050945383112030910926070054900173876240992521473621328997");

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
	public LoginConnectionHandler(LoginServer server) {
		this.server = server;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getChannel().close();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		final Channel channel = e.getChannel();
		final LoginPacket packet = (LoginPacket) e.getMessage();
		server.pushTask(new Runnable() {
			public void run() {
				if (server.getNodeStorage().contains(channel)) {
					server.getNodeStorage().get(channel).handlePacket(packet);
				} else {
					handlePreAuthenticationPacket(channel, packet);
				}
			}
		});
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			final ChannelStateEvent evt) throws Exception {
		server.pushTask(new Runnable() {
			public void run() {
				if (server.getNodeStorage().contains(evt.getChannel())) {
					NodeManager.getNodeManager().unregister(
							server.getNodeStorage().get(evt.getChannel()));
				}
			}
		});
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		// TODO pipeline
	}

	/**
	 * Handles the authentication packet.
	 * 
	 * @param session
	 *            The session.
	 * @param message
	 */
	private void handlePreAuthenticationPacket(Channel channel,
			LoginPacket message) {
		if (message.getOpcode() == LoginPacket.AUTH) {

			ChannelBuffer payload = message.getPayload();

			/**
			 * Read the encrypted data length
			 */
			int len = payload.readShort();

			byte[] encryptedBytes = new byte[len];

			payload.readBytes(encryptedBytes);

			/**
			 * Wrap the decrypted data from the biginteger
			 */
			ChannelBuffer encryptedData = ChannelBuffers
					.wrappedBuffer(new BigInteger(encryptedBytes).modPow(
							PRIVATE_EXPONENT, PRIVATE_MODULUS).toByteArray());

			/**
			 * Check the first byte from the encrypted data, if it is 10 we are
			 * all good.
			 */
			int blockOpcode = encryptedData.readByte();
			if (blockOpcode != 10) {
				channel.close();
				return;
			}

			/**
			 * Read the connecting nodeid and password
			 */
			int node = encryptedData.readShort();
			String password = ChannelBufferUtils.getRS2String(encryptedData);

			/**
			 * Check if the node is valid
			 */
			boolean valid = NodeManager.getNodeManager()
					.isNodeAuthenticationValid(node, password);

			Node n = new Node(server, channel, node);
			server.getNodeStorage().set(channel, n);
			NodeManager.getNodeManager().register(n);
			int code = valid ? 0 : 1;
			ChannelBuffer resp = ChannelBuffers.buffer(1);
			resp.writeByte((byte) code);
			channel.write(new LoginPacket(LoginPacket.AUTH_RESPONSE, resp));
		} else {
			channel.close();
		}
	}

}
