package org.saleen.rs2.plugin;

import org.saleen.util.configuration.ConfigurationNode;

/**
 * Represents a basic plugin
 * 
 * @author Nikki
 * 
 */
public abstract class Plugin {

	/**
	 * The configuration of this Plugin
	 */
	protected ConfigurationNode configuration;

	/**
	 * The plugin information
	 */
	private PluginInfo pluginInfo;

	/**
	 * Called on plugin load, this method should register event listeners, etc
	 */
	public abstract void onLoad() throws Exception;

	/**
	 * Called on plugin unload, this method should save all data if needed...
	 */
	public abstract void onUnload() throws Exception;

	/**
	 * Set the plugin's configuration
	 * 
	 * @param configuration
	 *            The configuration
	 */
	public void setConfiguration(ConfigurationNode configuration) {
		this.configuration = configuration;
	}

	/**
	 * Set the plugin information
	 * 
	 * @param pluginInfo
	 *            The plugin information
	 */
	public void setPluginInfo(PluginInfo pluginInfo) {
		this.pluginInfo = pluginInfo;
	}

	/**
	 * Get the plugin's information
	 */
	public PluginInfo getInfo() {
		return pluginInfo;
	}
}