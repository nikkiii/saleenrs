package org.saleen.rs2.model.objects;

import java.util.LinkedList;

import org.saleen.rs2.model.Location;
import org.saleen.rs2.model.Player;

public class CoordinateCheckedObject extends AbstractObject {

	private LinkedList<Location> validCoordinates = new LinkedList<Location>();

	public CoordinateCheckedObject(Location location, int type, int rotation) {
		super(location, type, rotation);
	}

	@Override
	public boolean canUse(Player player) {
		if (validCoordinates.contains(player.getLocation())) {
			return true;
		} else {
			for (Location l : validCoordinates) {
				if (player.getLocation().equals(l)) {
					return true;
				}
			}
		}
		return false;
	}
}
