package org.saleen.event.impl;

import org.saleen.event.Event;
import org.saleen.rs2.model.Item;
import org.saleen.rs2.model.Player;

/**
 * Represents an ItemOnItem packet event
 * 
 * @author Nikki
 * 
 */
public class ItemOnItemEvent implements Event {
	/**
	 * The player this event belongs to
	 */
	private Player player;

	/**
	 * The item used
	 */
	private Item usedItem;

	/**
	 * The item used with
	 */
	private Item usedWithItem;

	/**
	 * Create an event
	 * 
	 * @param player
	 * @param usedItem
	 * @param usedWithItem
	 */
	public ItemOnItemEvent(Player player, Item usedItem, Item usedWithItem) {
		this.player = player;
		this.usedItem = usedItem;
		this.usedWithItem = usedWithItem;
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
	 * Get the item used
	 * 
	 * @return The item
	 */
	public Item getUsed() {
		return usedItem;
	}

	/**
	 * Get the item used with
	 * 
	 * @return The item
	 */
	public Item getUsedWith() {
		return usedWithItem;
	}
}
