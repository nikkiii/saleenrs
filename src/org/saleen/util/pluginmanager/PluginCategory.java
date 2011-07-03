package org.saleen.util.pluginmanager;

import java.util.List;

/**
 * Represents a specific plugin category, like skills etc
 * 
 * @author Nikki
 * 
 */
public class PluginCategory {

	/**
	 * The name of this category
	 */
	private String name;

	/**
	 * The list of plugins
	 */
	private List<PluginManifest> plugins;

	public PluginCategory(String name, List<PluginManifest> plugins) {
		this.name = name;
		this.plugins = plugins;
	}

	/**
	 * Get the name of this category
	 * 
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the list of plugins
	 * 
	 * @return The plugins
	 */
	public List<PluginManifest> getPlugins() {
		return plugins;
	}

	@Override
	public String toString() {
		return name;
	}
}
