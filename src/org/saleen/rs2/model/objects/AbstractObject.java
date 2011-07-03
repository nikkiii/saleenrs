package org.saleen.rs2.model.objects;

import org.saleen.rs2.model.Location;
import org.saleen.rs2.model.Player;

public abstract class AbstractObject {

	protected Location location;
	protected int type;
	protected int rotation;

	public AbstractObject(Location location, int type, int rotation) {
		this.location = location;
		this.type = type;
		this.rotation = rotation;
	}

	public abstract boolean canUse(Player player);

	public Location getLocation() {
		return location;
	}

	public int getType() {
		return type;
	}

	public int getRotation() {
		return rotation;
	}

}
