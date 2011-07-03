package org.saleen.rs2.plugin;

import java.io.File;

/**
 * Represents a loaded plugin's info, such as location of file, name and
 * description
 * 
 * @author Nikki
 * 
 */
public class PluginInfo {
	/**
	 * The name of the plugin
	 */
	private String name;

	/**
	 * The plugin description
	 */
	private String description;

	/**
	 * The plugin author
	 */
	private String author;

	/**
	 * The plugin file
	 */
	private File file;

	public PluginInfo(String name, String description, String author, File file) {
		this.name = name;
		this.description = description;
		this.author = author;
		this.file = file;
	}

	/**
	 * Get the plugin name
	 * 
	 * @return The plugin name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the plugin description
	 * 
	 * @return The plugin description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Get the plugin author
	 * 
	 * @return The plugin author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Get the file this plugin was loaded from
	 * 
	 * @return The file
	 */
	public File getFile() {
		return file;
	}
}
