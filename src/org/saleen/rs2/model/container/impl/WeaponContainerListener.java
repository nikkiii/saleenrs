package org.saleen.rs2.model.container.impl;

import org.saleen.rs2.model.Item;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.container.Container;
import org.saleen.rs2.model.container.ContainerListener;
import org.saleen.rs2.model.container.Equipment;

/**
 * A listener which updates the weapon tab.
 * 
 * @author Graham Edgecombe
 * 
 */
public class WeaponContainerListener implements ContainerListener {

	/**
	 * The player.
	 */
	private Player player;

	/**
	 * Creates the listener.
	 * 
	 * @param player
	 *            The player.
	 */
	public WeaponContainerListener(Player player) {
		this.player = player;
	}

	@Override
	public void itemChanged(Container container, int slot) {
		if (slot == Equipment.SLOT_WEAPON) {
			sendWeapon();
		}
	}

	@Override
	public void itemsChanged(Container container, int[] slots) {
		for (int slot : slots) {
			if (slot == Equipment.SLOT_WEAPON) {
				sendWeapon();
				return;
			}
		}
	}

	@Override
	public void itemsChanged(Container container) {
		sendWeapon();
	}

	/**
	 * Sends weapon information.
	 */
	private void sendWeapon() {
		Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON);
		int id = -1;
		String name = null;
		if (weapon == null) {
			name = "Unarmed";
		} else {
			name = weapon.getDefinition().getName();
			id = weapon.getId();
		}
		String genericName = filterWeaponName(name).trim();
		sendWeapon(id, name, genericName);
	}

	/**
	 * Sends weapon information.
	 * 
	 * @param id
	 *            The id.
	 * @param name
	 *            The name.
	 * @param genericName
	 *            The filtered name.
	 */
	private void sendWeapon(int id, String name, String genericName) {
		if (name.equals("Unarmed")) {
			player.getActionSender().sendSidebarInterface(0, 5855);
			player.getActionSender().sendString(5857, name);
		} else if (name.endsWith("whip")) {
			player.getActionSender().sendSidebarInterface(0, 12290);
			player.getActionSender().sendInterfaceModel(12291, 200, id);
			player.getActionSender().sendString(12293, name);
		} else if (name.endsWith("Scythe")) {
			player.getActionSender().sendSidebarInterface(0, 776);
			player.getActionSender().sendInterfaceModel(777, 200, id);
			player.getActionSender().sendString(779, name);
		} else if (name.endsWith("bow") || name.startsWith("Crystal bow")
				|| name.startsWith("Toktz-xil-ul")) {
			player.getActionSender().sendSidebarInterface(0, 1764);
			player.getActionSender().sendInterfaceModel(1765, 200, id);
			player.getActionSender().sendString(1767, name);
		} else if (name.startsWith("Staff") || name.endsWith("staff")) {
			player.getActionSender().sendSidebarInterface(0, 328);
			player.getActionSender().sendInterfaceModel(329, 200, id);
			player.getActionSender().sendString(331, name);
		} else if (genericName.startsWith("dart")) {
			player.getActionSender().sendSidebarInterface(0, 4446);
			player.getActionSender().sendInterfaceModel(4447, 200, id);
			player.getActionSender().sendString(4449, name);
		} else if (genericName.startsWith("dagger")) {
			player.getActionSender().sendSidebarInterface(0, 2276);
			player.getActionSender().sendInterfaceModel(2277, 200, id);
			player.getActionSender().sendString(2279, name);
		} else if (genericName.startsWith("pickaxe")) {
			player.getActionSender().sendSidebarInterface(0, 5570);
			player.getActionSender().sendInterfaceModel(5571, 200, id);
			player.getActionSender().sendString(5573, name);
		} else if (genericName.startsWith("axe")
				|| genericName.startsWith("battleaxe")) {
			player.getActionSender().sendSidebarInterface(0, 1698);
			player.getActionSender().sendInterfaceModel(1699, 200, id);
			player.getActionSender().sendString(1701, name);
		} else if (genericName.startsWith("Axe")
				|| genericName.startsWith("Battleaxe")) {
			player.getActionSender().sendSidebarInterface(0, 1698);
			player.getActionSender().sendInterfaceModel(1699, 200, id);
			player.getActionSender().sendString(1701, name);
		} else if (genericName.startsWith("halberd")) {
			player.getActionSender().sendSidebarInterface(0, 8460);
			player.getActionSender().sendInterfaceModel(8461, 200, id);
			player.getActionSender().sendString(8463, name);
		} else if (genericName.endsWith("godsword")
				|| genericName.endsWith("2h sword")) {
			player.getActionSender().sendSidebarInterface(0, 4705);
			player.getActionSender().sendInterfaceModel(4708, 200, id);
			player.getActionSender().sendString(4708, name);
			if (isSpecialWeapon(id, name)) {
				player.getActionSender().sendConfig(300, 1000);
			}
		} else {
			player.getActionSender().sendSidebarInterface(0, 2423);
			player.getActionSender().sendInterfaceModel(2424, 200, id);
			player.getActionSender().sendString(2426, name);
		}
	}

	private boolean isSpecialWeapon(int id, String name) {
		if (name.endsWith("godsword")) {
			return true;
		}
		return false;
	}

	/**
	 * Filters a weapon name.
	 * 
	 * @param name
	 *            The original name.
	 * @return The filtered name.
	 */
	private String filterWeaponName(String name) {
		final String[] filtered = new String[] { "Iron", "Steel", "Scythe",
				"Black", "Mithril", "Adamant", "Rune", "Granite", "Dragon",
				"Crystal", "Bronze" };
		for (String filter : filtered) {
			name = name.replaceAll(filter, "");
		}
		return name;
	}

}
