package org.saleen.rs2.model;

public class GroundItem {

	/**
	 * The player who dropped this item
	 */
	private Player dropper;

	/**
	 * The item of this grounditem
	 */
	private Item item;

	/**
	 * The location of this item
	 */
	private Location location;

	/**
	 * Whether this item is visible to all players
	 */
	private boolean global;

	/**
	 * Whether this item was picked up already
	 */
	private boolean pickedUp = false;

	/**
	 * Create a ground item
	 * 
	 * @param item
	 *            The item
	 * @param location
	 *            The location
	 * @param global
	 *            Whether global or not
	 */
	public GroundItem(Player dropper, Item item, Location location,
			boolean global) {
		this.dropper = dropper;
		this.item = item;
		this.location = location;
		this.global = global;
	}

	/**
	 * Create a ground item which wasn't dropped by a player
	 * 
	 * @param item
	 *            The item
	 * @param location
	 *            The location
	 * @param global
	 *            True, if global
	 */
	public GroundItem(Item item, Location location, boolean global) {
		this(null, item, location, global);
	}

	/**
	 * Get the item
	 * 
	 * @return The item
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * Get the location of this item
	 * 
	 * @return The location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Get whether this item is global
	 * 
	 * @return
	 */
	public boolean isGlobal() {
		return global;
	}

	/**
	 * Check if the item was picked up
	 * 
	 * @return True, if picked up.
	 */
	public boolean isPickedUp() {
		return pickedUp;
	}

	/**
	 * Set the ground item's global status
	 * 
	 * @param global
	 *            whether the item is global
	 */
	public void setGlobal(boolean global) {
		this.global = global;
	}

	/**
	 * Set this item picked up
	 * 
	 * @param pickedUp
	 *            Tre, if picked up
	 */
	public void setPickedUp(boolean pickedUp) {
		this.pickedUp = pickedUp;
	}

	/**
	 * The player which dropped this item, or null for a global item
	 * 
	 * @return
	 */
	public Player getDropper() {
		return dropper;
	}
}
