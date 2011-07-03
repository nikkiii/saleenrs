package org.saleen.event.impl;

import org.saleen.event.Event;
import org.saleen.rs2.model.GameObject;
import org.saleen.rs2.model.Player;

/**
 * Represents an event which would be sent when an NPCOption is called
 * 
 * @author Nikki
 * 
 */
public class ObjectOptionEvent implements Event {

	/**
	 * The player clicking the option
	 */
	private Player player;

	/**
	 * The subject of the option
	 */
	private GameObject object;

	/**
	 * The option which was clicked
	 */
	private ClickOption option;

	public ObjectOptionEvent(Player player, GameObject object,
			ClickOption option) {
		this.player = player;
		this.object = object;
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
	 * Get the npc
	 * 
	 * @return The npc of this event
	 */
	public GameObject getObject() {
		return object;
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
	 * A sub-class of an Object Option Event for Object Option 1
	 * 
	 * @author Nikki
	 * 
	 */
	public static class ObjectOption1 extends ObjectOptionEvent {
		public ObjectOption1(Player player, GameObject object) {
			super(player, object, ClickOption.CLICK_1);
		}
	}

	/**
	 * A sub-class of an Object Option Event for Object Option 2
	 * 
	 * @author Nikki
	 * 
	 */
	public static class ObjectOption2 extends ObjectOptionEvent {
		public ObjectOption2(Player player, GameObject object) {
			super(player, object, ClickOption.CLICK_2);
		}
	}

	/**
	 * A sub-class of an Object Option Event for Object Option 3
	 * 
	 * @author Nikki
	 * 
	 */
	public static class ObjectOption3 extends ObjectOptionEvent {
		public ObjectOption3(Player player, GameObject object) {
			super(player, object, ClickOption.CLICK_3);
		}
	}
}
