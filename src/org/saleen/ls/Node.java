package org.saleen.ls;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.saleen.rs2.WorldLoader.LoginResult;
import org.saleen.rs2.model.PlayerDetails;
import org.saleen.rs2.util.ChannelBufferUtils;
import org.saleen.rs2.util.NameUtils;
import org.saleen.util.login.LoginPacket;

/**
 * Manages a single node (world).
 * 
 * @author Graham Edgecombe
 * 
 */
public class Node {

	/**
	 * The node configuration
	 */
	private NodeConfiguration config;

	/**
	 * The login server.
	 */
	private LoginServer server;

	/**
	 * The session.
	 */
	private Channel channel;

	/**
	 * The id.
	 */
	private int id;

	/**
	 * A map of players.
	 */
	private Map<String, PlayerData> players = new HashMap<String, PlayerData>();

	/**
	 * Creates a node.
	 * 
	 * @param server
	 *            The server.
	 * @param session
	 *            The session.
	 * @param id
	 *            The id.
	 */
	public Node(LoginServer server, Channel channel, int id) {
		this.server = server;
		this.channel = channel;
		this.id = id;
	}

	/**
	 * Registers a new player.
	 * 
	 * @param player
	 *            The player to add.
	 */
	public void register(PlayerData player) {
		players.put(player.getName(), player);
	}

	/**
	 * Removes an old player.
	 * 
	 * @param player
	 *            The player to remove.
	 */
	public void unregister(PlayerData player) {
		players.remove(player.getName());
	}

	/**
	 * Gets a player by their name.
	 * 
	 * @param name
	 *            The player name.
	 * @return The player.
	 */
	public PlayerData getPlayer(String name) {
		return players.get(name);
	}

	/**
	 * Gets the players in this node.
	 * 
	 * @return The players in this node.
	 */
	public Collection<PlayerData> getPlayers() {
		return players.values();
	}

	/**
	 * Gets the channel.
	 * 
	 * @return The session.
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * Gets the id.
	 * 
	 * @return The id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Handles an incoming packet.
	 * 
	 * @param packet
	 *            The incoming packet.
	 */
	public void handlePacket(LoginPacket packet) {
		ChannelBuffer buffer = packet.getPayload();
		switch (packet.getOpcode()) {
		case LoginPacket.CHECK_LOGIN: {
			String name = NameUtils.formatNameForProtocol(ChannelBufferUtils
					.getRS2String(buffer));
			String password = ChannelBufferUtils.getRS2String(buffer);
			int uid = buffer.readInt();
			String profile = ChannelBufferUtils.getRS2String(buffer);

			LoginResult res = server.getLoader().checkLogin(
					new PlayerDetails(null, name, password, uid, profile, null,
							null));
			if (res.getReturnCode() == 2) {
				if (res.getPlayerData() != null)
					NodeManager.getNodeManager().register(res.getPlayerData(),
							this);
				else
					NodeManager.getNodeManager().register(
							new PlayerData(name, 0), this);
			}
			ChannelBuffer resp = ChannelBuffers.dynamicBuffer();
			ChannelBufferUtils.putRS2String(resp, name);
			resp.writeByte((byte) res.getReturnCode());
			channel.write(new LoginPacket(LoginPacket.CHECK_LOGIN_RESPONSE,
					resp));
			break;
		}
		case LoginPacket.LOAD: {
			String name = NameUtils.formatNameForProtocol(ChannelBufferUtils
					.getRS2String(buffer));
			ChannelBuffer playerDataBuf = server.getLoader().loadPlayerFile(
					new PlayerDetails(null, name, "", 0, null, null, null));
			int code = playerDataBuf != null ? 1 : 0;
			ChannelBuffer resp = ChannelBuffers.dynamicBuffer();
			ChannelBufferUtils.putRS2String(resp, name);
			resp.writeByte((byte) code);
			if (code == 1) {
				resp.writeShort((short) playerDataBuf.readableBytes());
				resp.writeBytes(playerDataBuf);
			}
			channel.write(new LoginPacket(LoginPacket.LOAD_RESPONSE, resp));
			break;
		}
		case LoginPacket.SAVE: {
			String name = NameUtils.formatNameForProtocol(ChannelBufferUtils
					.getRS2String(buffer));
			int dataLength = buffer.readUnsignedShort();
			byte[] data = new byte[dataLength];
			buffer.readBytes(data);

			int code = server.getLoader().savePlayer(name, data) ? 1 : 0;

			ChannelBuffer resp = ChannelBuffers.dynamicBuffer();
			ChannelBufferUtils.putRS2String(resp, name);
			resp.writeByte((byte) code);
			channel.write(new LoginPacket(LoginPacket.SAVE_RESPONSE, resp));
			break;
		}
		case LoginPacket.DISCONNECT: {
			String name = NameUtils.formatNameForProtocol(ChannelBufferUtils
					.getRS2String(buffer));
			PlayerData p = NodeManager.getNodeManager().getPlayer(name);
			if (p != null) {
				NodeManager.getNodeManager().unregister(p);
			}
		}
			break;
		}
	}

	/**
	 * Get the player count of this server
	 * 
	 * @return The player count
	 */
	public int getPlayerCount() {
		return players.size();
	}

	/**
	 * Set the node configuration
	 * 
	 * @param config
	 */
	public void setNodeConfiguration(NodeConfiguration config) {
		this.config = config;
	}

	/**
	 * Get the node configuration
	 * 
	 * @return
	 */
	public NodeConfiguration getConfig() {
		return config;
	}

}
