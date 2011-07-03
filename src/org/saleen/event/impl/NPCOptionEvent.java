package org.saleen.event.impl;

import org.saleen.event.Event;
import org.saleen.rs2.model.NPC;
import org.saleen.rs2.model.Player;

/**
 * Represents an event which would be sent when an NPCOption is called
 * 
 * @author Nikki
 * 
 */
public class NPCOptionEvent implements Event {

	/**
	 * The player clicking the option
	 */
	private Player player;

	/**
	 * The subject of the option
	 */
	private NPC npc;

	/**
	 * The option which was clicked
	 */
	private ClickOption option;

	public NPCOptionEvent(Player player, NPC npc, ClickOption option) {
		this.player = player;
		this.npc = npc;
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
	public NPC getNPC() {
		return npc;
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
	 * A sub-class of an NPC Option Event for NPC Option 1
	 * 
	 * @author Nikki
	 * 
	 */
	public static class NPCOption1 extends NPCOptionEvent {
		public NPCOption1(Player player, NPC npc) {
			super(player, npc, ClickOption.CLICK_1);
		}
	}

	/**
	 * A sub-class of an NPC Option Event for NPC Option 2
	 * 
	 * @author Nikki
	 * 
	 */
	public static class NPCOption2 extends NPCOptionEvent {
		public NPCOption2(Player player, NPC npc) {
			super(player, npc, ClickOption.CLICK_2);
		}
	}

	/**
	 * A sub-class of an NPC Option Event for NPC Option 3
	 * 
	 * @author Nikki
	 * 
	 */
	public static class NPCOption3 extends NPCOptionEvent {
		public NPCOption3(Player player, NPC npc) {
			super(player, npc, ClickOption.CLICK_3);
		}
	}
}
