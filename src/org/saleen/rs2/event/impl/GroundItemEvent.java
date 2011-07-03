package org.saleen.rs2.event.impl;

import org.saleen.rs2.event.Event;
import org.saleen.rs2.model.GroundItem;
import org.saleen.rs2.model.GroundItemManager;

/**
 * Handles functions for ground items.
 * 
 * @author Nikki
 * @author Shoes
 * 
 */
public class GroundItemEvent extends Event {

	/**
	 * The ground item instance.
	 */
	private GroundItem item;

	/**
	 * The function for the event to perform.
	 * 
	 * @author Shoes
	 * 
	 */
	public enum Function {
		REMOVE(150000), SHOW(60000), RESPAWN(150000);

		private Function(int delay) {
			this.delay = delay;
		}

		public int getDelay() {
			return delay;
		}

		private int delay;
	}

	/**
	 * The function the event will perform.
	 */
	private Function function;

	public GroundItemEvent(GroundItem item, Function function) {
		super(function.getDelay());
		this.item = item;
		this.function = function;
	}

	@Override
	public void execute() {
		if (item != null && !item.isPickedUp()) {
			switch (function) {
			case SHOW:
				if (!item.isPickedUp() && !item.isGlobal()) {
					GroundItemManager.globalize(item);
				}
				break;
			case REMOVE:
				GroundItemManager.remove(item);
				break;
			}
		}
		stop();
	}

}