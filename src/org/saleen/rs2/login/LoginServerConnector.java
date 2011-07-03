package org.saleen.rs2.login;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.AdaptiveReceiveBufferSizePredictor;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.saleen.rs2.WorldLoader.LoginResult;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.PlayerDetails;
import org.saleen.rs2.model.World;
import org.saleen.rs2.util.ChannelBufferUtils;
import org.saleen.rs2.util.NameUtils;
import org.saleen.util.CommonConstants;
import org.saleen.util.login.LoginPacket;
import org.saleen.util.login.LoginPipelineFactory;

/**
 * <p>
 * The <code>LoginServerConnector</code> manages the communication between the
 * game server and the login server.
 * </p>
 * 
 * @author Graham Edgecombe
 * 
 */
public class LoginServerConnector extends SimpleChannelHandler {

	private static final BigInteger PUBLIC_MODULUS = new BigInteger(
			"119056917842199477384824577629731593889161233963726676241122467995112076543057540982833407056066032567769511237126685352355534729195747518774275507841123651194666618239749539089515833960877660455810691679998524799477896365070704127522441581953124147767050945383112030910926070054900173876240992521473621328997");
	private static final BigInteger PUBLIC_EXPONENT = new BigInteger("65537");

	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger
			.getLogger(LoginServerConnector.class.getName());

	/**
	 * The connector.
	 */
	private ClientBootstrap clientBootstrap;

	/**
	 * The login server address.
	 */
	private String address;

	/**
	 * The login server password.
	 */
	private String password;

	/**
	 * The world server node id.
	 */
	private int node;

	/**
	 * The client session.
	 */
	private Channel channel;

	/**
	 * Authenticated flag.
	 */
	private boolean authenticated = false;

	/**
	 * Creates the login server connector.
	 * 
	 * @param address
	 *            The address of the login server.
	 */
	public LoginServerConnector(String address) {
		this.address = address;
		clientBootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));
		clientBootstrap.setPipelineFactory(new LoginPipelineFactory(this));
		clientBootstrap.setOption("child.receiveBufferSizePredictor",
				new AdaptiveReceiveBufferSizePredictor(1, 2048, 10240));
		clientBootstrap.setOption("receiveBufferSizePredictor",
				new AdaptiveReceiveBufferSizePredictor(1024, 2048, 3096));
	}

	/**
	 * Checks if the client is connected.
	 * 
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isConnected() {
		return channel != null && channel.isConnected();
	}

	/**
	 * Checks if the client is authenticated.
	 * 
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isAuthenticated() {
		return isConnected() && authenticated;
	}

	/**
	 * Connects to the server.
	 * 
	 * @param password
	 *            The password.
	 * @param node
	 *            The node id.
	 */
	public void connect(final String password, final int node) {
		this.password = password;
		this.node = node;
		logger.info("Connecting to login server : " + address + ":"
				+ CommonConstants.LOGIN_PORT + "...");
		ChannelFuture future = clientBootstrap.connect(new InetSocketAddress(
				address, CommonConstants.LOGIN_PORT));
		logger.info("Waiting on connection...");
		future.awaitUninterruptibly();
		if (!future.isSuccess() && (channel == null || !channel.isConnected())) {
			logger.severe("Connection to login server failed. Retrying...");
			// this stops stack overflow errors
			World.getWorld().getEngine().submitLogic(new Runnable() {
				public void run() {
					World.getWorld().getLoginServerConnector()
							.connect(password, node);
				}
			});
		} else {
			channel = future.getChannel();
			logger.info("Connected.");
			// create and send auth packet
			ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
			buf.writeByte((byte) 10);
			buf.writeShort((short) node);
			ChannelBufferUtils.putRS2String(buf, password);
			/**
			 * Encrypt the data since we should be protecting our password, or
			 * random users can sniff the password and steal character files!
			 */
			byte[] data = new byte[buf.readableBytes()];
			buf.readBytes(data);

			ChannelBuffer encryptedBuffer = ChannelBuffers.dynamicBuffer();
			byte[] encrypt = new BigInteger(data).modPow(PUBLIC_EXPONENT,
					PUBLIC_MODULUS).toByteArray();
			encryptedBuffer.writeShort((short) encrypt.length);
			encryptedBuffer.writeBytes(encrypt);
			this.channel.write(new LoginPacket(LoginPacket.AUTH,
					encryptedBuffer));
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getChannel().close();
		e.getCause().printStackTrace();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		read((LoginPacket) e.getMessage());
	}

	/**
	 * Write a packet.
	 * 
	 * @param packet
	 *            The packet to write.
	 */
	public void write(LoginPacket packet) {
		if (!this.isConnected()) {
			channel.write(packet);
		} else {
			throw new IllegalStateException("Not connected.");
		}
	}

	/**
	 * Read and process a packet.
	 * 
	 * @param packet
	 *            The packet to read.
	 */
	private void read(LoginPacket packet) {
		final ChannelBuffer payload = packet.getPayload();
		switch (packet.getOpcode()) {
		case LoginPacket.AUTH_RESPONSE: {
			int code = payload.readUnsignedByte();
			if (code == 0) {
				authenticated = true;
				logger.info("Authenticated as node : World-" + node + ".");
			} else {
				logger.severe("Login server authentication error : " + code
						+ ". Check your password and node id.");
				channel.close();
			}
			break;
		}
		case LoginPacket.CHECK_LOGIN_RESPONSE: {
			String name = ChannelBufferUtils.getRS2String(payload);
			int returnCode = payload.readUnsignedByte();
			synchronized (checkLoginResults) {
				checkLoginResults.put(name, returnCode);
				checkLoginResults.notifyAll();
			}
			break;
		}
		case LoginPacket.LOAD_RESPONSE: {
			String name = ChannelBufferUtils.getRS2String(payload);
			int returnCode = payload.readUnsignedByte();
			if (returnCode == 1) {
				synchronized (playerLoadResults) {
					playerLoadResults.put(name,
							payload.readBytes(payload.readUnsignedShort()));
					playerLoadResults.notifyAll();
				}
			} else {
				synchronized (playerLoadResults) {
					playerLoadResults.put(name, null);
					playerLoadResults.notifyAll();
				}
			}
			break;
		}
		case LoginPacket.SAVE_RESPONSE: {
			String name = ChannelBufferUtils.getRS2String(payload);
			int success = payload.readUnsignedByte();
			synchronized (playerSaveResults) {
				playerSaveResults.put(name, success == 1 ? Boolean.TRUE
						: Boolean.FALSE);
				playerSaveResults.notifyAll();
			}
			break;
		}
		}
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		logger.info("Disconnected. Retrying...");
		connect(password, node);
	}

	/**
	 * Check login results.
	 */
	private Map<String, Integer> checkLoginResults = new HashMap<String, Integer>();

	/**
	 * Player load results.
	 */
	private Map<String, ChannelBuffer> playerLoadResults = new HashMap<String, ChannelBuffer>();

	/**
	 * Player save results.
	 */
	private Map<String, Boolean> playerSaveResults = new HashMap<String, Boolean>();

	/**
	 * Checks the login of a player.
	 * 
	 * @param pd
	 *            The player details.
	 * @return The login result.
	 */
	public LoginResult checkLogin(PlayerDetails pd) {
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		ChannelBufferUtils.putRS2String(buf, pd.getName());
		ChannelBufferUtils.putRS2String(buf, pd.getPassword());
		buf.writeInt(pd.getUID());
		ChannelBufferUtils.putRS2String(buf, pd.getProfile());
		channel.write(new LoginPacket(LoginPacket.CHECK_LOGIN, buf));
		synchronized (checkLoginResults) {
			while (!checkLoginResults.containsKey(NameUtils
					.formatNameForProtocol(pd.getName()))) {
				try {
					checkLoginResults.wait();
				} catch (InterruptedException e) {
					continue;
				}
			}
			int code = checkLoginResults.remove(NameUtils
					.formatNameForProtocol(pd.getName()));
			if (code == 2) {
				return new LoginResult(code, new Player(pd));
			} else {
				return new LoginResult(code);
			}
		}
	}

	/**
	 * Loads a player.
	 * 
	 * @param player
	 *            The player.
	 * @return <code>true</code> on success, <code>false</code> on error.
	 */
	public boolean loadPlayer(Player player) {
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		ChannelBufferUtils.putRS2String(buf,
				NameUtils.formatNameForProtocol(player.getName()));
		channel.write(new LoginPacket(LoginPacket.LOAD, buf));
		synchronized (playerLoadResults) {
			while (!playerLoadResults.containsKey(NameUtils
					.formatNameForProtocol(player.getName()))) {
				try {
					playerLoadResults.wait();
				} catch (InterruptedException e) {
					continue;
				}
			}
			ChannelBuffer playerData = playerLoadResults.remove(NameUtils
					.formatNameForProtocol(player.getName()));
			if (playerData == null) {
				return false;
			} else {
				player.deserialize(playerData);
			}
		}
		return true;
	}

	/**
	 * Saves a player.
	 * 
	 * @param player
	 *            The player.
	 * @return <code>true</code> on success, <code>false</code> on error.
	 */
	public boolean savePlayer(Player player) {
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		ChannelBufferUtils.putRS2String(buf,
				NameUtils.formatNameForProtocol(player.getName()));

		ChannelBuffer data = ChannelBuffers.dynamicBuffer();
		player.serialize(data);
		buf.writeShort((short) data.writerIndex());
		buf.writeBytes(data);

		channel.write(new LoginPacket(LoginPacket.SAVE, buf));
		synchronized (playerSaveResults) {
			while (!playerSaveResults.containsKey(NameUtils
					.formatNameForProtocol(player.getName()))) {
				try {
					playerSaveResults.wait();
				} catch (InterruptedException e) {
					continue;
				}
			}
			return playerSaveResults.remove(
					NameUtils.formatNameForProtocol(player.getName()))
					.booleanValue();
		}
	}

	/**
	 * Sends a notification of player disconnection to the login server.
	 * 
	 * @param name
	 *            The player name.
	 */
	public void disconnected(String name) {
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		ChannelBufferUtils.putRS2String(buf,
				NameUtils.formatNameForProtocol(name));
		channel.write(new LoginPacket(LoginPacket.DISCONNECT, buf));
	}

}
