package org.saleen.util.pluginmanager;

import java.util.LinkedList;
import java.util.List;

/**
 * A basic repository
 * 
 * @author Nikki
 * 
 */
public class Repository {

	/**
	 * The URL of the repository file
	 */
	private String url;

	/**
	 * The comment
	 */
	private String comment;

	/**
	 * The list of plugins in this repository
	 */
	private List<PluginManifest> plugins = new LinkedList<PluginManifest>();

	/**
	 * Create an instance of this class
	 * 
	 * @param url
	 *            The URL
	 * @param comment
	 *            The comment
	 */
	public Repository(String url, String comment) {
		this.url = url;
		this.comment = comment;
	}

	/**
	 * Get the repository url
	 * 
	 * @return The url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Get the repository comment
	 * 
	 * @return The comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Get the repository base
	 * 
	 * @return The base
	 */
	public String getDownloadBase() {
		return url.substring(0, url.lastIndexOf('/'));
	}

	/**
	 * Get the list of plugins in this repository
	 * 
	 * @return The list of plugins
	 */
	public List<PluginManifest> getPlugins() {
		return plugins;
	}

	@Override
	public String toString() {
		return url;
	}
}
