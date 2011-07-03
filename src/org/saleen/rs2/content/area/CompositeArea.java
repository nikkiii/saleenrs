package org.saleen.rs2.content.area;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.saleen.rs2.model.Location;

/**
 * Represents an area with multiple sub-areas
 * 
 * @author Nikki
 */
public class CompositeArea implements Area {

	/**
	 * The list of areas, composite or basic
	 */
	private List<Area> subAreas = new LinkedList<Area>();

	/**
	 * Create a class instance with an array of areas
	 * 
	 * @param areas
	 *            The array of areas
	 */
	public CompositeArea(Area... areas) {
		subAreas.addAll(Arrays.asList(areas));
	}

	/**
	 * Create a class instance with a list of areas
	 * 
	 * @param list
	 *            The list
	 */
	public CompositeArea(List<Area> list) {
		subAreas.addAll(list);
	}

	@Override
	public boolean contains(Location location) {
		for (Area area : subAreas) {
			if (area.contains(location)) {
				return true;
			}
		}
		return false;
	}
}
