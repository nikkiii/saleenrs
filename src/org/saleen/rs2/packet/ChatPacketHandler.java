package org.saleen.rs2.packet;

import java.sql.PreparedStatement;

import org.saleen.rs2.database.DatabaseConnection;
import org.saleen.rs2.model.ChatMessage;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.World;
import org.saleen.rs2.net.Packet;
import org.saleen.rs2.util.NameUtils;
import org.saleen.rs2.util.TextUtils;

/**
 * Handles public chat messages.
 * 
 * @author Graham Edgecombe
 * 
 */
public class ChatPacketHandler implements PacketHandler {

	private static final int CHAT_QUEUE_SIZE = 4;

	@Override
	public void handle(Player player, Packet packet) {
		int effects = packet.getByteA() & 0xFF;
		int colour = packet.getByteA() & 0xFF;
		int size = packet.getLength() - 2;
		byte[] chatData = new byte[size];
		packet.getReverseA(chatData, 0, size);
		if (player.getChatMessageQueue().size() >= CHAT_QUEUE_SIZE) {
			return;
		}
		String unpacked = TextUtils.textUnpack(chatData, size);
		unpacked = TextUtils.filterText(unpacked);
		unpacked = TextUtils.optimizeText(unpacked);
		byte[] packed = new byte[size];
		TextUtils.textPack(packed, unpacked);
		player.getChatMessageQueue().add(
				new ChatMessage(effects, colour, packed));

		try {
			DatabaseConnection connection = World.getWorld()
					.getConnectionPool().nextFree();

			PreparedStatement stmt = connection
					.getConnection()
					.prepareStatement(
							"INSERT INTO chatlogs(`time`, `username`, `text`) VALUES(?, ?, ?)");
			stmt.setInt(1, (int) (System.currentTimeMillis() / 1000));
			stmt.setString(2, NameUtils.formatNameForProtocol(player.getName()));
			stmt.setString(3, TextUtils.xlateText(packed, packed.length));
			stmt.executeUpdate();
			stmt.close();

			connection.returnConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
