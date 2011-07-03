package org.saleen.rs2.content.area;

import org.saleen.rs2.model.Location;

/**
 * Represents an area in runescape.
 * 
 * @see <code>BasicArea</code>
 * @see <code>CompositeArea</code>
 * 
 * @author Nikki
 */
public interface Area {
	/**
	 * Check if this area contains the location
	 * 
	 * @param location
	 *            The location
	 * @return true, if the area contains it
	 */
	public boolean contains(Location location);
}
