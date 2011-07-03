package org.saleen.rs2.net;

import java.util.List;
import java.util.Random;

import org.saleen.rs2.Constants;
import org.saleen.rs2.model.Entity;
import org.saleen.rs2.model.GroundItem;
import org.saleen.rs2.model.Item;
import org.saleen.rs2.model.Location;
import org.saleen.rs2.model.Palette;
import org.saleen.rs2.model.Palette.PaletteTile;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.Skills;
import org.saleen.rs2.model.container.Equipment;
import org.saleen.rs2.model.container.Inventory;
import org.saleen.rs2.model.container.impl.EquipmentContainerListener;
import org.saleen.rs2.model.container.impl.InterfaceContainerListener;
import org.saleen.rs2.model.container.impl.WeaponContainerListener;
import org.saleen.rs2.net.Packet.Type;
import org.saleen.rs2.script.FileScriptManager;
import org.saleen.rs2.util.TextUtils;

/**
 * A utility class for sending packets.
 * 
 * @author Graham Edgecombe
 * 
 */
public class ActionSender {

	/**
	 * The player.
	 */
	private Player player;

	/**
	 * Creates an action sender for the specified player.
	 * 
	 * @param player
	 *            The player to create the action sender for.
	 */
	public ActionSender(Player player) {
		this.player = player;
	}

	/**
	 * Sends an inventory interface.
	 * 
	 * @param interfaceId
	 *            The interface id.
	 * @param inventoryInterfaceId
	 *            The inventory interface id.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendInterfaceInventory(int interfaceId,
			int inventoryInterfaceId) {
		player.getInterfaceState().interfaceOpened(interfaceId);
		player.write(new PacketBuilder(248).putShortA(interfaceId)
				.putShort(inventoryInterfaceId).toPacket());
		return this;
	}

	/**
	 * Sends all the login packets.
	 * 
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendLogin() {
		player.setActive(true);
		sendDetails();
		FileScriptManager.getScriptManager().invoke("login:dologin", player);
		sendSkills();
		sendFriendStatus(2);
		sendWelcome();
		sendMapRegion();
		sendSidebarInterfaces();

		InterfaceContainerListener inventoryListener = new InterfaceContainerListener(
				player, Inventory.INTERFACE);
		player.getInventory().addListener(inventoryListener);

		InterfaceContainerListener equipmentListener = new InterfaceContainerListener(
				player, Equipment.INTERFACE);
		player.getEquipment().addListener(equipmentListener);
		player.getEquipment().addListener(
				new EquipmentContainerListener(player));
		player.getEquipment().addListener(new WeaponContainerListener(player));

		return this;
	}

	/**
	 * Sends the packet to construct a map region.
	 * 
	 * @param palette
	 *            The palette of map regions.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendConstructMapRegion(Palette palette) {
		player.setLastKnownRegion(player.getLocation());
		PacketBuilder bldr = new PacketBuilder(241, Type.VARIABLE_SHORT);
		bldr.putShortA(player.getLocation().getRegionY() + 6);
		bldr.startBitAccess();
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < 13; x++) {
				for (int y = 0; y < 13; y++) {
					PaletteTile tile = palette.getTile(x, y, z);
					bldr.putBits(1, tile != null ? 1 : 0);
					if (tile != null) {
						bldr.putBits(26, tile.getX() << 14 | tile.getY() << 3
								| tile.getZ() << 24 | tile.getRotation() << 1);
					}
				}
			}
		}
		bldr.finishBitAccess();
		bldr.putShort(player.getLocation().getRegionX() + 6);
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends the initial login packet (e.g. members, player id).
	 * 
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendDetails() {
		player.write(new PacketBuilder(249)
				.putByteA((byte) (player.isMembers() ? 1 : 0))
				.putLEShortA(player.getIndex()).toPacket());
		player.write(new PacketBuilder(107).toPacket());
		return this;
	}

	public ActionSender sendRights() {
		player.write(new PacketBuilder(224).put(
				(byte) player.getRights().toInteger()).toPacket());
		return this;
	}

	/**
	 * Sends the player's skills.
	 * 
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendSkills() {
		for (int i = 0; i < Skills.SKILL_COUNT; i++) {
			sendSkill(i);
		}
		return this;
	}

	/**
	 * Sends a specific skill.
	 * 
	 * @param skill
	 *            The skill to send.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendSkill(int skill) {
		PacketBuilder bldr = new PacketBuilder(134);
		bldr.put((byte) skill);
		bldr.putInt1((int) player.getSkills().getExperience(skill));
		bldr.put((byte) player.getSkills().getLevel(skill));
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends all the sidebar interfaces.
	 * 
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendSidebarInterfaces() {
		final int[] icons = Constants.SIDEBAR_INTERFACES[0];
		final int[] interfaces = Constants.SIDEBAR_INTERFACES[1];
		for (int i = 0; i < icons.length; i++) {
			sendSidebarInterface(icons[i], interfaces[i]);
		}
		return this;
	}

	/**
	 * Sends a specific sidebar interface.
	 * 
	 * @param icon
	 *            The sidebar icon.
	 * @param interfaceId
	 *            The interface id.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendSidebarInterface(int icon, int interfaceId) {
		player.write(new PacketBuilder(71).putShort(interfaceId)
				.putByteA((byte) icon).toPacket());
		return this;
	}

	/**
	 * Sends a message.
	 * 
	 * byte = type, 0 regular 1 console
	 * 
	 * @param message
	 *            The message to send.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendMessage(String message) {
		player.write(new PacketBuilder(253, Type.VARIABLE)
				.putRS2String(message).toPacket());
		return this;
	}

	public ActionSender sendURLSound(String url) {
		player.write(new PacketBuilder(76, Type.VARIABLE).put((byte) 0)
				.putRS2String(url).toPacket());
		return this;
	}

	/**
	 * Sends the map region load command.
	 * 
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendMapRegion() {
		player.setLastKnownRegion(player.getLocation());
		player.write(new PacketBuilder(73)
				.putShortA(player.getLocation().getRegionX() + 6)
				.putShort(player.getLocation().getRegionY() + 6).toPacket());
		return this;
	}

	/**
	 * Sends the logout packet.
	 * 
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendLogout() {
		player.write(new PacketBuilder(109).toPacket()); // TODO IoFuture
		return this;
	}

	/**
	 * Sends a packet to update a group of items.
	 * 
	 * @param interfaceId
	 *            The interface id.
	 * @param items
	 *            The items.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendUpdateItems(int interfaceId, Item[] items) {
		PacketBuilder bldr = new PacketBuilder(53, Type.VARIABLE_SHORT);
		bldr.putShort(interfaceId);
		bldr.putShort(items.length);
		for (Item item : items) {
			if (item != null) {
				int count = item.getCount();
				if (count > 254) {
					bldr.put((byte) 255);
					bldr.putInt2(count);
				} else {
					bldr.put((byte) count);
				}
				bldr.putLEShortA(item.getId() + 1);
			} else {
				bldr.put((byte) 0);
				bldr.putLEShortA(0);
			}
		}
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends a packet to update a single item.
	 * 
	 * @param interfaceId
	 *            The interface id.
	 * @param slot
	 *            The slot.
	 * @param item
	 *            The item.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendUpdateItem(int interfaceId, int slot, Item item) {
		PacketBuilder bldr = new PacketBuilder(34, Type.VARIABLE_SHORT);
		bldr.putShort(interfaceId).putSmart(slot);
		if (item != null) {
			bldr.putShort(item.getId() + 1);
			int count = item.getCount();
			if (count > 254) {
				bldr.put((byte) 255);
				bldr.putInt(count);
			} else {
				bldr.put((byte) count);
			}
		} else {
			bldr.putShort(0);
			bldr.put((byte) 0);
		}
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends a packet to update multiple (but not all) items.
	 * 
	 * @param interfaceId
	 *            The interface id.
	 * @param slots
	 *            The slots.
	 * @param items
	 *            The item array.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendUpdateItems(int interfaceId, int[] slots,
			Item[] items) {
		PacketBuilder bldr = new PacketBuilder(34, Type.VARIABLE_SHORT)
				.putShort(interfaceId);
		for (int i = 0; i < slots.length; i++) {
			Item item = items[slots[i]];
			bldr.putSmart(slots[i]);
			if (item != null) {
				bldr.putShort(item.getId() + 1);
				int count = item.getCount();
				if (count > 254) {
					bldr.put((byte) 255);
					bldr.putInt(count);
				} else {
					bldr.put((byte) count);
				}
			} else {
				bldr.putShort(0);
				bldr.put((byte) 0);
			}
		}
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends the enter amount interface.
	 * 
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendEnterAmountInterface() {
		player.write(new PacketBuilder(27).toPacket());
		return this;
	}

	/**
	 * Sends the player an option.
	 * 
	 * @param slot
	 *            The slot to place the option in the menu.
	 * @param top
	 *            Flag which indicates the item should be placed at the top.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendInteractionOption(String option, int slot,
			boolean top) {
		PacketBuilder bldr = new PacketBuilder(104, Type.VARIABLE);
		bldr.putByteC((byte) -slot);
		bldr.putByteA(top ? (byte) 0 : (byte) 1);
		bldr.putRS2String(option);
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends a string.
	 * 
	 * @param id
	 *            The interface id.
	 * @param string
	 *            The string.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendString(int id, String string) {
		PacketBuilder bldr = new PacketBuilder(126, Type.VARIABLE_SHORT);
		bldr.putRS2String(string);
		bldr.putShortA(id);
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends a model in an interface.
	 * 
	 * @param id
	 *            The interface id.
	 * @param zoom
	 *            The zoom.
	 * @param model
	 *            The model id.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendInterfaceModel(int id, int zoom, int model) {
		PacketBuilder bldr = new PacketBuilder(246);
		bldr.putLEShort(id).putShort(zoom).putShort(model);
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Send the friends list status
	 * 
	 * @param i
	 *            0 = connecting, 1 = connected?
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendFriendStatus(int i) {
		player.write(new PacketBuilder(221).put((byte) i).toPacket());
		return this;
	}

	/**
	 * Send the status of a friend in a list
	 * 
	 * @param name
	 *            The player's name in a long
	 * @param world
	 *            The player's current world
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendFriendStatus(long name, int world) {
		player.write(new PacketBuilder(50).putLong(name).put((byte) world)
				.toPacket());
		return this;
	}

	/**
	 * Send the status of a friend in a list
	 * 
	 * @param name
	 *            The player's name in a long
	 * @param world
	 *            The player's current world
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendPrivateMessage(long from, int rights,
			byte[] message, int size) {
		PacketBuilder bldr = new PacketBuilder(196, Type.VARIABLE);
		bldr.putLong(from).putInt((byte) new Random().nextInt())
				.put((byte) rights).put(message);
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Send the ignore list of a player
	 * 
	 * @param name
	 *            The player's name in a long
	 * @param world
	 *            The player's current world
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendIgnores(List<Long> ignores) {
		PacketBuilder pb = new PacketBuilder(214, Type.VARIABLE_SHORT);
		for (long name : ignores) {
			pb.putLong((byte) name);
		}
		player.write(pb.toPacket());
		return this;
	}

	/**
	 * Send coords
	 * 
	 * @param location
	 *            The location to send
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendCoords(Location location) {
		player.write(new PacketBuilder(85).putByteC(location.getLocalY())
				.putByteC(location.getLocalX()).toPacket());
		return this;
	}

	/**
	 * Send coords
	 * 
	 * @param location
	 *            The location to send
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendRegionCoords(Location location) {
		int regionY = location.getY()
				- (player.getLastKnownRegion().getRegionY() * 8);
		int regionX = location.getX()
				- (player.getLastKnownRegion().getRegionX() * 8);
		player.write(new PacketBuilder(85).putByteC(regionY).putByteC(regionX)
				.toPacket());
		return this;
	}

	/**
	 * Send the projectile's coordinates
	 * 
	 * @param location
	 *            The coordinates to send
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendProjectileCoords(Location location) {
		player.write(new PacketBuilder(85).putByteC(location.getLocalY() - 2)
				.putByteC(location.getLocalX() - 3).toPacket());
		return this;
	}

	/**
	 * Animates the selected object
	 */
	public ActionSender animateObject(int objx, int objy, int animationID,
			int tileObjectType, int orientation) {
		for (Player plr : player.getRegion().getPlayers()) {
			int yy = objy - (plr.getLastKnownRegion().getRegionY() * 8);
			int xx = objx - (plr.getLastKnownRegion().getRegionX() * 8);
			plr.getChannel().write(
					new PacketBuilder(85).putByteC(yy).putByteC(xx).toPacket());
			plr.getChannel()
					.write(new PacketBuilder(160)
							.putByteS((byte) 0)
							.putByteS(
									(byte) ((tileObjectType << 2) + (orientation & 3)))
							.putShortA(animationID).toPacket());
		}
		return this;
	}

	/**
	 * Create an object.
	 */
	public ActionSender sendCreateObject(Location location, int type,
			int orientation, int tileObjectType) {
		sendCoords(location);
		PacketBuilder bldr = new PacketBuilder(151);
		bldr.putByteS((byte) 0).putLEShort(type)
				.putByteS((byte) ((tileObjectType << 2) + (orientation & 3)));
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Reset a local object.
	 */
	public ActionSender sendDeleteObject(Location location, int face, int type) {
		sendCoords(location);
		PacketBuilder bldr = new PacketBuilder(101);
		bldr.putByteC((type << 2) + (face & 3)).put((byte) 0);
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Send a projectile to the client Credits: Luke132 for basic parts.
	 */
	public ActionSender sendProjectile(Location source, Location dest,
			int startSpeed, int gfx, int angle, int startHeight, int endHeight,
			int speed, Entity lockon) {
		sendProjectileCoords(source);
		PacketBuilder bldr = new PacketBuilder(117);
		bldr.put((byte) angle)
				.put((byte) ((source.getY() - dest.getY()) * -1))
				.put((byte) ((source.getX() - dest.getX()) * -1))
				.putShort(
						lockon instanceof Player ? (-lockon.getClientIndex() - 1)
								: lockon.getClientIndex() + 1).putShort(gfx)
				.put((byte) startHeight).put((byte) endHeight)
				.putShort(startSpeed).putShort(speed)
				.put((byte) (gfx == 53 ? 0 : 16)).put((byte) 64);
		player.write(bldr.toPacket());
		return this;
	}

	public ActionSender sendXPCounter(int skill, int xp) {
		player.write(new PacketBuilder(124).put((byte) skill).putShort(xp)
				.toPacket());
		return this;
	}

	public ActionSender sendPacket70(int i, int o, int id) {
		player.write(new PacketBuilder(70).putShort(i).putLEShort(o)
				.putLEShort(id).toPacket());
		return this;
	}

	public ActionSender sendPacket171(boolean bool, int id) {
		player.write(new PacketBuilder(70).put((byte) (bool ? 1 : 0))
				.putShort(id).toPacket());
		return this;
	}

	public ActionSender sendUpdateTime(int time, int type) {
		player.write(new PacketBuilder(114).putLEShort(time * 50 / 30)
				.toPacket());
		return this;
	}

	public ActionSender sendConfig(int id, int state) {
		player.write(new PacketBuilder(36).putLEShort(id).put((byte) state)
				.toPacket());
		return this;
	}

	public ActionSender sendChatbox(int interfaceId) {
		player.write(new PacketBuilder(164).putLEShort(interfaceId).toPacket());
		return this;
	}

	/**
	 * Adds a ground item.
	 * 
	 * @param itemId
	 *            The id of the item.
	 * @param itemX
	 *            The X location of the item.
	 * @param itemY
	 *            The Y location of the item.
	 * @param itemAmount
	 *            The amount of items.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender addGroundItem(GroundItem item) {
		sendRegionCoords(item.getLocation());
		player.write(new PacketBuilder(44).putLEShortA(item.getItem().getId())
				.putShort(item.getItem().getCount()).put((byte) 0).toPacket());
		return this;
	}

	/**
	 * Removes a ground item.
	 * 
	 * @param itemId
	 *            The id of the item.
	 * @param itemX
	 *            The X location of the item.
	 * @param itemY
	 *            The Y location of the item.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender removeGroundItem(GroundItem item) {
		sendRegionCoords(item.getLocation());
		player.write(new PacketBuilder(156).putByteS((byte) 0)
				.putShort(item.getItem().getId()).toPacket());
		return this;
	}

	public ActionSender sendCameraFocus(Location location, int height,
			int speed, int angle) {
		PacketBuilder bldr = new PacketBuilder(177);
		bldr.put((byte) location.getLocalX()).put((byte) location.getLocalY())
				.putShort(height).put((byte) speed).put((byte) angle);
		player.write(bldr.toPacket());
		return this;
	}

	public ActionSender sendFrame166(int i1, int i2, int i3, int i4, int i5) {
		player.write(new PacketBuilder(166).put((byte) i1).put((byte) i2)
				.putShort(i3).put((byte) i4).put((byte) i5).toPacket());
		return this;
	}

	public ActionSender sendFrame107() {
		player.write(new PacketBuilder(107).toPacket());
		return this;
	}

	/**
	 * 17511 = Question Type 15819 = Christmas Type 15812 = Security Type 15801
	 * = Item Scam Type 15791 = Password Safety 15774 = Good/Bad Password 15767
	 * = Drama Type
	 * 
	 * 
	 */

	public ActionSender sendFullscreenInterface(int mainFrame, int subFrame) {
		player.write(new PacketBuilder(98).putShort(mainFrame)
				.putShort(subFrame).toPacket());
		return this;
	}

	public ActionSender sendWelcome() {
		sendString(15257, "Welcome to " + Constants.SERVER_NAME);
		sendString(15270,
				"\\nYou do not have a Bank PIN. Please visit a bank\\nif you would like one.");

		PacketBuilder bldr = new PacketBuilder(176);
		bldr.putByteC(10) // Days since recov change
				.putShortA(10) // Unread messages
				.put((byte) 0) // Members
				.putInt2(TextUtils.ipToInt("199.19.227.53"))// player.getRemoteHost()))
															// //IP address
				.putShort(1); // Days since last login
		player.write(bldr.toPacket());

		sendFullscreenInterface(15244, 15819);
		return this;
	}

	public ActionSender clearQuestInterface() {
		for (int i = 8145; i < 8195; i++) {
			sendString(i, "");
		}
		return this;
	}

	/**
	 * Send an interface open packet
	 * 
	 * @param id
	 *            The id
	 * @return The instance for chaining
	 */
	public ActionSender sendInterface(int id) {
		player.getInterfaceState().interfaceOpened(id);
		player.write(new PacketBuilder(97).putShort(id).toPacket());
		return this;
	}
}
