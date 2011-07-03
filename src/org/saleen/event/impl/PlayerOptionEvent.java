package org.saleen.event.impl;

import org.saleen.event.Event;
import org.saleen.rs2.model.Player;

public class PlayerOptionEvent implements Event {

	/**
	 * The player clicking
	 */
	private Player player;

	/**
	 * The player clicked
	 */
	private Player interactWith;

	/**
	 * The option which was clicked
	 */
	private ClickOption option;

	public PlayerOptionEvent(Player player, Player interactWith,
			ClickOption option) {
		this.player = player;
		this.interactWith = interactWith;
		this.option = option;
	}

	/**
	 * Get the interacting player
	 * 
	 * @return The player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * The player the first player is interacting with
	 * 
	 * @return The player
	 */
	public Player getInteractingPlayer() {
		return interactWith;
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
	 * A sub-class of a PlayerOptionEvent for Player Option 1
	 * 
	 * @author Nikki
	 * 
	 */
	public static class PlayerOption1 extends PlayerOptionEvent {
		public PlayerOption1(Player player, Player interactWith) {
			super(player, interactWith, ClickOption.CLICK_1);
		}
	}

	/**
	 * A sub-class of a PlayerOptionEvent for Player Option 2
	 * 
	 * @author Nikki
	 * 
	 */
	public static class PlayerOption2 extends PlayerOptionEvent {
		public PlayerOption2(Player player, Player interactWith) {
			super(player, interactWith, ClickOption.CLICK_2);
		}
	}

	/**
	 * A sub-class of a PlayerOptionEvent for Player Option 3
	 * 
	 * @author Nikki
	 * 
	 */
	public static class PlayerOption3 extends PlayerOptionEvent {
		public PlayerOption3(Player player, Player interactWith) {
			super(player, interactWith, ClickOption.CLICK_3);
		}
	}
}
