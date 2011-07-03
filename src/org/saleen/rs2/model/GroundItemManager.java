package org.saleen.rs2.model;

import org.saleen.rs2.event.impl.GroundItemEvent;
import org.saleen.rs2.event.impl.GroundItemEvent.Function;
import org.saleen.rs2.model.region.Region;

public class GroundItemManager {

	public static void itemDropped(Player player, GroundItem item) {
		player.getRegion().addGroundItem(item);
		player.getActionSender().addGroundItem(item);
		World.getWorld().submit(new GroundItemEvent(item, Function.SHOW));
	}

	/**
	 * Make an item global
	 * 
	 * @param item
	 *            The item
	 */
	public static boolean globalize(GroundItem item) {
		if (!item.isPickedUp()) {
			item.setGlobal(true);
			Region region = World.getWorld().getRegionManager()
					.getRegionByLocation(item.getLocation());
			for (Player player : region.getPlayers()) {
				if (player.getLocation().isWithinDistance(item.getLocation())) {
					player.getActionSender().addGroundItem(item);
				}
			}
			World.getWorld().submit(new GroundItemEvent(item, Function.REMOVE));
			return true;
		}
		return false;
	}

	/**
	 * Remove a grounditem from this world
	 * 
	 * @param item
	 *            The item to remove
	 */
	public static void remove(GroundItem item) {
		// Set it picked up in case.
		item.setPickedUp(true);
		Region region = World.getWorld().getRegionManager()
				.getRegionByLocation(item.getLocation());
		for (Player player : region.getPlayers()) {
			if (player.getLocation().isWithinDistance(item.getLocation())) {
				player.getActionSender().removeGroundItem(item);
				System.out.println("Removing item for player : "
						+ player.getName());
			}
		}
		item.getDropper().getActionSender().removeGroundItem(item);
		region.removeGroundItem(item);
	}
}
