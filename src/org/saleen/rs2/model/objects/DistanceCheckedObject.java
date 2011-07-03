package org.saleen.rs2.model.objects;

import org.saleen.rs2.model.Location;
import org.saleen.rs2.model.Player;

public class DistanceCheckedObject extends AbstractObject {

	private int maxDistance;

	public DistanceCheckedObject(Location location, int type, int rotation,
			int maxDistance) {
		super(location, type, rotation);
		this.maxDistance = maxDistance;
	}

	@Override
	public boolean canUse(Player player) {
		if (player.getLocation().distanceTo(this.location) <= maxDistance) {
			return true;
		}
		return false;
	}

}
