package org.saleen.rs2.event.impl;

import org.saleen.rs2.event.Event;
import org.saleen.rs2.model.GameObject;
import org.saleen.rs2.model.Location;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.World;
import org.saleen.rs2.model.region.Region;

/**
 * An event which runs after a rock or tree needs to be restored, after
 * woodcutting of course.
 * 
 * @author Nikki
 * 
 */
public class ObjectRestoreEvent extends Event {

	/**
	 * The location this event represents
	 */
	private Location location;

	/**
	 * The object we replace after the specified interval
	 */
	private GameObject object;

	/**
	 * The regions surrounding the restore event
	 */
	private Region[] regions;

	/**
	 * Creates the event to restore the object
	 */
	public ObjectRestoreEvent(GameObject object, long delay) {
		super(delay);
		this.object = object;
		this.location = object.getLocation();
		this.regions = World.getWorld().getRegionManager()
				.getSurroundingRegions(location);
	}

	@Override
	public void execute() {
		for (Region region : regions) {
			for (Player player : region.getPlayers()) {
				if (player.getLocation().isWithinDistance(this.location))
					player.getActionSender().sendCreateObject(location,
							object.getDefinition().getId(),
							object.getRotation(), object.getType());
			}
		}
		this.stop();
	}
}
