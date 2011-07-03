package org.saleen.event.impl;

import org.saleen.event.Event;
import org.saleen.rs2.model.Item;
import org.saleen.rs2.model.Player;

/**
 * Represents an event which would be sent when an NPCOption is called
 * 
 * @author Nikki
 * 
 */
public class ItemOptionEvent implements Event {

	/**
	 * The player clicking the option
	 */
	private Player player;

	/**
	 * The item slot
	 */
	private int slot;

	/**
	 * The subject of the option
	 */
	private Item item;

	/**
	 * The option which was clicked
	 */
	private ClickOption option;

	public ItemOptionEvent(Player player, int slot, Item item,
			ClickOption option) {
		this.player = player;
		this.slot = slot;
		this.item = item;
		this.option = option;
	}

	/**
	 * Get the player
	 * 
	 * @return The player of this event
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Get the slot of this option event
	 * 
	 * @return The slot
	 */
	public int getSlot() {
		return slot;
	}

	/**
	 * Get the item
	 * 
	 * @return The item of this event
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * Check if the option is a certain option
	 * 
	 * @param option
	 *            The option to check
	 * @return True, if the option matches
	 */
	public boolean isOption(ClickOption option) {
		return this.option == option;
	}

	/**
	 * A sub-class of an Item Option Event for Item Option 1
	 * 
	 * @author Nikki
	 * 
	 */
	public static class ItemSelect extends ItemOptionEvent {
		public ItemSelect(Player player, int slot, Item item) {
			super(player, slot, item, ClickOption.SELECT);
		}
	}

	/**
	 * A sub-class of an Item Option Event for Item Option 1
	 * 
	 * @author Nikki
	 * 
	 */
	public static class ItemOption1 extends ItemOptionEvent {
		public ItemOption1(Player player, int slot, Item item) {
			super(player, slot, item, ClickOption.CLICK_1);
		}
	}

	/**
	 * A sub-class of an Item Option Event for Item Option 2
	 * 
	 * @author Nikki
	 * 
	 */
	public static class ItemOption2 extends ItemOptionEvent {
		public ItemOption2(Player player, int slot, Item item) {
			super(player, slot, item, ClickOption.CLICK_2);
		}
	}

	/**
	 * A sub-class of an Item Option Event for Item Option 3
	 * 
	 * @author Nikki
	 * 
	 */
	public static class ItemOption3 extends ItemOptionEvent {
		public ItemOption3(Player player, int slot, Item item) {
			super(player, slot, item, ClickOption.CLICK_3);
		}
	}

	/**
	 * A sub-class of an Item Option Event for Item Option 4
	 * 
	 * @author Nikki
	 * 
	 */
	public static class ItemOption4 extends ItemOptionEvent {
		public ItemOption4(Player player, int slot, Item item) {
			super(player, slot, item, ClickOption.CLICK_4);
		}
	}

	/**
	 * A sub-class of an Item Option Event for Item Option 1
	 * 
	 * @author Nikki
	 * 
	 */
	public static class ItemOption5 extends ItemOptionEvent {
		public ItemOption5(Player player, int slot, Item item) {
			super(player, slot, item, ClickOption.CLICK_5);
		}
	}
}
