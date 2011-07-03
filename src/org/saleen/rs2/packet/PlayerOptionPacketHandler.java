package org.saleen.rs2.packet;

import org.saleen.event.EventProducer;
import org.saleen.event.impl.PlayerOptionEvent;
import org.saleen.rs2.Constants;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.World;
import org.saleen.rs2.net.Packet;

public class PlayerOptionPacketHandler extends EventProducer implements
		PacketHandler {

	private static final int OPTION_1 = 128, OPTION_2 = 37, OPTION_3 = 227;

	@Override
	public void handle(Player player, Packet packet) {
		switch (packet.getOpcode()) {
		case OPTION_1:
			option1(player, packet);
			break;
		case OPTION_2:
			option2(player, packet);
			break;
		case OPTION_3:
			option3(player, packet);
			break;
		}
	}

	/**
	 * Handles the first option on a player option menu.
	 * 
	 * @param player
	 * @param packet
	 */
	private void option1(final Player player, Packet packet) {
		int id = packet.getShort() & 0xFFFF;
		if (id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		Player victim = World.getWorld().getPlayer(id);
		if (victim != null
				&& player.getLocation().isWithinInteractionDistance(
						victim.getLocation())) {
			produce(new PlayerOptionEvent.PlayerOption1(player, victim));
		}
	}

	/**
	 * Handles the second option on a player option menu.
	 * 
	 * @param player
	 * @param packet
	 */
	private void option2(Player player, Packet packet) {
		int id = packet.getShort() & 0xFFFF;
		if (id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		Player victim = World.getWorld().getPlayer(id);
		if (victim != null
				&& player.getLocation().isWithinInteractionDistance(
						victim.getLocation())) {
			produce(new PlayerOptionEvent.PlayerOption2(player, victim));
		}
	}

	/**
	 * Handles the third option on a player option menu.
	 * 
	 * @param player
	 * @param packet
	 */
	private void option3(Player player, Packet packet) {
		int id = packet.getLEShortA() & 0xFFFF;
		if (id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		Player victim = World.getWorld().getPlayer(id);
		if (victim != null
				&& player.getLocation().isWithinInteractionDistance(
						victim.getLocation())) {
			produce(new PlayerOptionEvent.PlayerOption3(player, victim));
		}
	}

}
