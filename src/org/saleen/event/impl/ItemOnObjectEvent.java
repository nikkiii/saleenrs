package org.saleen.event.impl;

import org.saleen.event.Event;
import org.saleen.rs2.model.GameObject;
import org.saleen.rs2.model.Item;
import org.saleen.rs2.model.Player;

/**
 * Represents an ItemOnObject event
 * 
 * @author Nikki
 * 
 */
public class ItemOnObjectEvent implements Event {

	/**
	 * The player of this event
	 */
	private Player player;

	/**
	 * The item of this event
	 */
	private Item item;

	/**
	 * The object of this event
	 */
	private GameObject object;

	/**
	 * Create a new event
	 * 
	 * @param player
	 *            The player
	 * @param item
	 *            The item
	 * @param object
	 *            The object
	 */
	public ItemOnObjectEvent(Player player, Item item, GameObject object) {
		this.player = player;
		this.item = item;
		this.object = object;
	}

	/**
	 * Get the player
	 * 
	 * @return The player
	 */
	public Player getPlayer() {
		return player;
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
	 * Get the object
	 * 
	 * @return The object
	 */
	public GameObject getObject() {
		return object;
	}
}
