package org.saleen.rs2.packet;

import org.saleen.event.EventProducer;
import org.saleen.event.impl.ObjectOptionEvent.ObjectOption1;
import org.saleen.event.impl.ObjectOptionEvent.ObjectOption2;
import org.saleen.rs2.action.impl.ObjectUseAction;
import org.saleen.rs2.content.object.BankObjectAction;
import org.saleen.rs2.model.GameObject;
import org.saleen.rs2.model.Location;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.net.Packet;

/**
 * Object option packet handler.
 * 
 * @author Graham Edgecombe
 * @author Nikki
 * 
 */
public class ObjectOptionPacketHandler extends EventProducer implements
		PacketHandler {

	/**
	 * Option 1 opcode.
	 */
	private static final int OPTION_1 = 132, OPTION_2 = 252;

	@Override
	public void handle(Player player, Packet packet) {
		switch (packet.getOpcode()) {
		case OPTION_1:
			handleOption1(player, packet);
			break;
		case OPTION_2:
			handleOption2(player, packet);
			break;
		}
	}

	/**
	 * Handles the option 1 packet.
	 * 
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleOption1(Player player, Packet packet) {
		int x = packet.getLEShortA() & 0xFFFF;
		int id = packet.getShort() & 0xFFFF;
		int y = packet.getShortA() & 0xFFFF;
		Location loc = Location.create(x, y, player.getLocation().getZ());
		player.getActionSender().sendMessage("Object: " + id);
		// woodcutting
		GameObject object = player.getRegion().getObject(id, loc);
		if (object != null) {
			produce(new ObjectOption1(player, object));
		}
	}

	/**
	 * Handles the option 2 packet.
	 * 
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleOption2(final Player player, Packet packet) {
		int id = packet.getLEShortA() & 0xFFFF;
		int y = packet.getLEShort() & 0xFFFF;
		int x = packet.getShortA() & 0xFFFF;
		Location loc = Location.create(x, y, player.getLocation().getZ());
		GameObject object = player.getRegion().getObject(id, loc);
		if (object != null && player.getLocation().isWithinDistance(loc)) {
			produce(new ObjectOption2(player, object));
			switch (id) {
			case 2213:
			case 11758:
				player.getActionQueue().addAction(
						new ObjectUseAction(player, object,
								new BankObjectAction(player)));
				break;
			}
		}
	}
}
