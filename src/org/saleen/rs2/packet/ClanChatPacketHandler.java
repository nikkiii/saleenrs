package org.saleen.rs2.packet;

import org.saleen.rs2.model.Player;
import org.saleen.rs2.net.Packet;
import org.saleen.rs2.util.NameUtils;

public class ClanChatPacketHandler implements PacketHandler {
	private static final int CLANCHAT_JOIN = 60;

	@Override
	public void handle(Player player, Packet packet) {
		switch (packet.getOpcode()) {
		case CLANCHAT_JOIN:
			joinClan(player, packet);
			break;
		}
	}

	public void joinClan(Player player, Packet packet) {
		long name = packet.getLong();
		player.getActionSender().sendMessage(
				"Join clan [name=" + NameUtils.longToName(name) + "]");
	}
}
