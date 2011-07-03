package org.saleen.util.pluginmanager;

/**
 * Represents a plugin's manifest version etc, not to be confused with
 * <code>PluginInfo</code>, This class is specifically used for this manager
 * 
 * @author Nikki
 * 
 */
public class PluginManifest {

	/**
	 * The plugin name
	 */
	private String name;

	/**
	 * The plugin author
	 */
	private String author;

	/**
	 * The plugin description
	 */
	private String description;

	/**
	 * The plugin version
	 */
	private double version;

	/**
	 * Initialize an instance of a PluginManifest (meh)
	 * 
	 * @param name
	 *            The name
	 * @param author
	 *            The author
	 * @param description
	 *            The description
	 * @param version
	 *            The version
	 */
	public PluginManifest(String name, String author, String description,
			double version) {
		this.name = name;
		this.author = author;
		this.description = description;
		this.version = version;
	}

	/**
	 * Get the name
	 * 
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the author
	 * 
	 * @return The author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Get the description
	 * 
	 * @return The description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Get the formatted name
	 * 
	 * @return The formatted name
	 */
	public String getFormattedName() {
		String tempName = name;
		tempName = tempName.toLowerCase();
		tempName = tempName.replace(' ', '_');
		tempName = tempName + version;
		tempName = tempName + ".jar";
		return tempName;
	}

	/**
	 * Get the version
	 * 
	 * @return The version
	 */
	public double getVersion() {
		return version;
	}

	/**
	 * @see java.lang.Object.toString()
	 */
	@Override
	public String toString() {
		return name;
	}

}
