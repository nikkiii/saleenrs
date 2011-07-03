package org.saleen.rs2.packet;

import org.saleen.rs2.action.impl.PickupItemAction;
import org.saleen.rs2.model.GroundItem;
import org.saleen.rs2.model.GroundItemManager;
import org.saleen.rs2.model.Item;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.net.Packet;

public class GroundItemPacketHandler implements PacketHandler {

	private static final int DROP_ITEM = 87, PICKUP_ITEM = 236;

	@Override
	public void handle(Player player, Packet packet) {
		switch (packet.getOpcode()) {
		case DROP_ITEM:
			handleDropItem(player, packet);
			break;
		case PICKUP_ITEM:
			handlePickupItem(player, packet);
			break;
		}
	}

	/**
	 * Handle a drop item
	 * 
	 * @param player
	 *            The player
	 * @param packet
	 *            The packet
	 */
	private void handleDropItem(Player player, Packet packet) {
		int id = packet.getShortA();
		packet.getByte();
		packet.getByte();
		int slot = packet.getShortA();
		Item inventoryItem = player.getInventory().get(slot);
		if (inventoryItem != null) {
			if (inventoryItem.getId() == id) {
				player.getInventory().remove(slot, inventoryItem);
				GroundItemManager.itemDropped(player, new GroundItem(player,
						inventoryItem, player.getLocation(), false));
			}
		}
	}

	/**
	 * Handle a pickup item
	 * 
	 * @param player
	 *            The player
	 * @param packet
	 *            The packet
	 */
	private void handlePickupItem(Player player, Packet packet) {
		int y = packet.getLEShort();
		int id = packet.getShort();
		int x = packet.getLEShort();
		GroundItem item = player.getRegion().getGroundItem(id, x, y);
		if (item != null) {
			player.getActionQueue().addAction(
					new PickupItemAction(player, item));
		}
	}
}
