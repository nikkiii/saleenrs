package org.saleen.rs2.content.area;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import org.saleen.util.XStreamController;

public class Areas {

	/**
	 * A map containing a map of names -> areas Initialize it in case we don't
	 * load...
	 */
	private static HashMap<String, Area> areas = new HashMap<String, Area>();

	/**
	 * Load the area map from XML
	 * 
	 * @param file
	 *            The file to load from
	 * @throws IOException
	 *             If an error occurs loading
	 */
	@SuppressWarnings("unchecked")
	public static void load(File file) throws IOException {
		FileInputStream input = new FileInputStream(file);
		try {
			areas = (HashMap<String, Area>) XStreamController.getXStream()
					.fromXML(input);
		} finally {
			input.close();
		}
	}

	/**
	 * Register an area
	 * 
	 * @param name
	 *            The name of the area
	 * @param area
	 *            The area
	 */
	public static void register(String name, Area area) {
		areas.put(name, area);
	}

	/**
	 * Get an area from the map
	 * 
	 * @param name
	 *            The exact name
	 * @return The area if found
	 */
	public static Area get(String name) {
		if (!areas.containsKey(name)) {
			throw new RuntimeException("Invalid area!");
		}
		return areas.get(name);
	}
}
