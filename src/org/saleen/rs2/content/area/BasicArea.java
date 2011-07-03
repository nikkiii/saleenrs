package org.saleen.rs2.content.area;

import org.saleen.rs2.model.Location;

/**
 * Represents an Area in the runescape map, determined by a southwestern and
 * northeastern location
 * 
 * @author Nikki
 * 
 */
public class BasicArea implements Area {

	/**
	 * The lower bounds (Southwest)
	 */
	private Location lower;

	/**
	 * The upper bounds (Northeast)
	 */
	private Location upper;

	/**
	 * Create an Area by the 2 locations specified
	 * 
	 * @param lower
	 *            The southwest location
	 * @param upper
	 *            The northeast location
	 */
	public BasicArea(Location lower, Location upper) {
		this.lower = lower;
		this.upper = upper;
	}

	/**
	 * Check if this area contains the specified location
	 * 
	 * @param location
	 *            The location containing the x and y
	 * @return True if this area contains this location
	 */
	@Override
	public boolean contains(Location location) {
		return location.getX() >= lower.getX()
				&& location.getY() >= lower.getY()
				&& location.getX() <= upper.getX()
				&& location.getY() <= upper.getY();
	}
}
