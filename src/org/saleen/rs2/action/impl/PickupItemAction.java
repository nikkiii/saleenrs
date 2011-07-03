package org.saleen.rs2.action.impl;

import org.saleen.rs2.action.Action;
import org.saleen.rs2.model.GroundItem;
import org.saleen.rs2.model.GroundItemManager;
import org.saleen.rs2.model.Player;

public class PickupItemAction extends Action {

	private static final int DELAY = 600;

	private GroundItem item;

	public PickupItemAction(Player player, GroundItem item) {
		super(player, DELAY);
		this.item = item;
	}

	@Override
	public QueuePolicy getQueuePolicy() {
		return QueuePolicy.NEVER;
	}

	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.NON_WALKABLE;
	}

	@Override
	public void execute() {
		if (item != null) {
			if (player.getLocation().equals(item.getLocation())) {
				if (!item.isPickedUp()) {
					if (player.getInventory().add(item.getItem())) {
						GroundItemManager.remove(item);
					}
				}
				stop();
			}
		} else {
			stop();
		}
	}

}
