package org.saleen.rs2.packet;

import org.saleen.event.EventProducer;
import org.saleen.event.impl.ItemOnObjectEvent;
import org.saleen.event.impl.ItemOptionEvent;
import org.saleen.rs2.model.GameObject;
import org.saleen.rs2.model.Item;
import org.saleen.rs2.model.Location;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.container.Bank;
import org.saleen.rs2.model.container.Container;
import org.saleen.rs2.model.container.Equipment;
import org.saleen.rs2.model.container.Inventory;
import org.saleen.rs2.model.definition.ItemDefinition;
import org.saleen.rs2.net.Packet;

/**
 * Remove item options.
 * 
 * @author Graham Edgecombe
 * 
 */
public class ItemOptionPacketHandler extends EventProducer implements
		PacketHandler {

	/**
	 * Examine opcode
	 */
	private static final int EXAMINE = 216;

	/**
	 * Item select opcode
	 */
	private static final int SELECT = 122;

	/**
	 * Item on object opcode
	 */
	private static final int ITEM_ON_OBJ = 192;

	/**
	 * Option 1 opcode.
	 */
	private static final int OPTION_1 = 145;

	/**
	 * Option 2 opcode.
	 */
	private static final int OPTION_2 = 117;

	/**
	 * Option 3 opcode.
	 */
	private static final int OPTION_3 = 43;

	/**
	 * Option 4 opcode.
	 */
	private static final int OPTION_4 = 129;

	/**
	 * Option 5 opcode.
	 */
	private static final int OPTION_5 = 135;

	@Override
	public void handle(Player player, Packet packet) {
		switch (packet.getOpcode()) {
		case EXAMINE:
			player.getActionSender().sendMessage(
					ItemDefinition.forId(packet.getLEShort()).getDescription());
			break;
		case SELECT:
			handleItemSelect(player, packet);
			break;
		case ITEM_ON_OBJ:
			handleItemOnObject(player, packet);
			break;
		case OPTION_1:
			handleItemOption1(player, packet);
			break;
		case OPTION_2:
			handleItemOption2(player, packet);
			break;
		case OPTION_3:
			handleItemOption3(player, packet);
			break;
		case OPTION_4:
			handleItemOption4(player, packet);
			break;
		case OPTION_5:
			handleItemOption5(player, packet);
			break;
		}
	}

	private void handleItemOnObject(Player player, Packet packet) {
		packet.getShortA();
		int usedOn = packet.getLEShort();
		int y = packet.getLEShortA();
		int slot = packet.getLEShortA() - 128;
		int x = packet.getLEShortA();
		int itemId = packet.getShort();

		Item item = player.getInventory().get(slot);
		if (item == null || item.getId() != itemId) {
			return;
		}
		GameObject object = player.getRegion().getObject(usedOn,
				Location.create(x, y, 0));
		if (object != null) {
			produce(new ItemOnObjectEvent(player, item, object));
		}
	}

	/**
	 * Handle an Item Select event
	 * 
	 * @param player
	 *            The player
	 * @param packet
	 *            The packet
	 */
	private void handleItemSelect(Player player, Packet packet) {
		packet.getLEShortA();
		int slot = packet.getShortA();
		int itemId = packet.getLEShort();
		Item item = player.getInventory().get(slot);
		if (item != null && item.getId() == itemId) {
			produce(new ItemOptionEvent.ItemSelect(player, slot, item));
		}
	}

	/**
	 * Handles item option 1.
	 * 
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleItemOption1(Player player, Packet packet) {
		int interfaceId = packet.getShortA() & 0xFFFF;
		int slot = packet.getShortA() & 0xFFFF;
		int id = packet.getShortA() & 0xFFFF;

		switch (interfaceId) {
		case Equipment.INTERFACE:
			if (slot >= 0 && slot < Equipment.SIZE) {
				if (!Container.transfer(player.getEquipment(),
						player.getInventory(), slot, id)) {
					// indicate it failed
				}
			}
			break;
		case Bank.PLAYER_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				Bank.deposit(player, slot, id, 1);
			}
			break;
		case Bank.BANK_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Bank.SIZE) {
				Bank.withdraw(player, slot, id, 1);
			}
			break;
		}
	}

	/**
	 * Handles item option 2.
	 * 
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleItemOption2(Player player, Packet packet) {
		int interfaceId = packet.getLEShortA() & 0xFFFF;
		int id = packet.getLEShortA() & 0xFFFF;
		int slot = packet.getLEShort() & 0xFFFF;

		switch (interfaceId) {
		case Bank.PLAYER_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				Bank.deposit(player, slot, id, 5);
			}
			break;
		case Bank.BANK_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Bank.SIZE) {
				Bank.withdraw(player, slot, id, 5);
			}
			break;
		}
	}

	/**
	 * Handles item option 3.
	 * 
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleItemOption3(Player player, Packet packet) {
		int interfaceId = packet.getLEShort() & 0xFFFF;
		int id = packet.getShortA() & 0xFFFF;
		int slot = packet.getShortA() & 0xFFFF;

		switch (interfaceId) {
		case Bank.PLAYER_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				Bank.deposit(player, slot, id, 10);
			}
			break;
		case Bank.BANK_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Bank.SIZE) {
				Bank.withdraw(player, slot, id, 10);
			}
			break;
		}
	}

	/**
	 * Handles item option 4.
	 * 
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleItemOption4(Player player, Packet packet) {
		int slot = packet.getShortA() & 0xFFFF;
		int interfaceId = packet.getShort() & 0xFFFF;
		int id = packet.getShortA() & 0xFFFF;

		switch (interfaceId) {
		case Bank.PLAYER_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				Bank.deposit(player, slot, id,
						player.getInventory().getCount(id));
			}
			break;
		case Bank.BANK_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Bank.SIZE) {
				Bank.withdraw(player, slot, id, player.getBank().getCount(id));
			}
			break;
		}
	}

	/**
	 * Handles item option 5.
	 * 
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleItemOption5(Player player, Packet packet) {
		int slot = packet.getLEShort() & 0xFFFF;
		int interfaceId = packet.getShortA() & 0xFFFF;
		int id = packet.getLEShort() & 0xFFFF;

		switch (interfaceId) {
		case Bank.PLAYER_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				player.getInterfaceState().openEnterAmountInterface(
						interfaceId, slot, id);
			}
			break;
		case Bank.BANK_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Bank.SIZE) {
				player.getInterfaceState().openEnterAmountInterface(
						interfaceId, slot, id);
			}
			break;
		}
	}

}
