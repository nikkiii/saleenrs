package org.saleen.rs2.plugin;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.saleen.rs2.script.PluginScriptManager;
import org.saleen.util.FileUtils;
import org.saleen.util.Filter;
import org.saleen.util.configuration.ConfigurationNode;
import org.saleen.util.configuration.ConfigurationParser;

/**
 * A class which loads all plugins related to the server.
 * 
 * @author Nikki
 * 
 */
public class PluginLoader {

	/**
	 * The PluginLoader instance
	 */
	private static final PluginLoader instance = new PluginLoader();

	/**
	 * The logger instance
	 */
	private static final Logger logger = Logger.getLogger(PluginLoader.class
			.getName());

	/**
	 * A list containing loaded plugins
	 */
	private List<Plugin> loadedPlugins = new LinkedList<Plugin>();

	/**
	 * Load all plugins, Removed listRecursive due to directories being
	 * supported
	 */
	public void load(File folder) {
		List<File> list = FileUtils.list(folder, new Filter<File>() {
			@Override
			public boolean accept(File t) {
				return t.getName().endsWith(".jar") || t.isDirectory()
						&& !t.getName().startsWith(".");
			}
		});

		for (File file : list) {
			register(loadPlugin(file));
		}
	}

	/**
	 * Load a plugin from a file.
	 * 
	 * @param file
	 *            The file to load from
	 * @return The loaded plugin
	 */
	private Plugin loadPlugin(File file) {
		try {
			ClassLoader loader = file.isDirectory() ? new PluginClassLoader(
					file) : new URLClassLoader(
					new URL[] { file.toURI().toURL() });
			ConfigurationParser parser = new ConfigurationParser(
					loader.getResourceAsStream("plugin.conf"));
			ConfigurationNode node = parser.parse();
			// Get the sub-configuration node for the plugin type
			ConfigurationNode pluginConf = node.nodeFor("plugin");
			// Create a new class instance of the plugin's class
			Class<?> pluginClass = loader.loadClass(pluginConf
					.getString("main-class"));
			// Create a plugin instance
			Plugin plugin = (Plugin) pluginClass.newInstance();
			// Set the configuration for the plugin
			plugin.setConfiguration(node);
			// Create a new plugin info file
			plugin.setPluginInfo(new PluginInfo(
					pluginConf.has("name") ? pluginConf.getString("name")
							: file.getName(), pluginConf
							.getString("description"), pluginConf
							.getString("author"), file));
			// Check if this is a library plugin
			if(pluginConf.has("library")) {
				boolean library = pluginConf.getBoolean("library");
				if(library) {
					PluginUtils.addToClasspath(file.toURI().toURL());
				}
			}
			// Load scripts from the plugin conf
			if (pluginConf.has("scripts")) {
				PluginScriptManager manager = new PluginScriptManager(loader);
				ConfigurationNode scriptConf = node.nodeFor("scripts");
				for (Map.Entry<String, Object> entry : scriptConf.getChildren()
						.entrySet()) {
					String scriptName = entry.getKey();
					String scriptPath = (String) entry.getValue();
					String extension = scriptPath.substring(scriptPath.lastIndexOf("."));
					manager.loadScript(scriptName, extension,
							loader.getResourceAsStream(scriptPath));
				}
			}
			// Return the loaded plugin
			return plugin;
		} catch (Exception e) {
			logger.log(Level.SEVERE,
					"Unable to load plugin : " + file.getName(), e);
		}
		return null;
	}

	/**
	 * Register a plugin
	 * 
	 * @param plugin
	 *            The plugin to register
	 */
	public void register(Plugin plugin) {
		try {
			// Call the load method of the plugin, which will register classes.
			plugin.onLoad();
			logger.info("Loaded plugin : " + plugin.getInfo().getName());
			synchronized (loadedPlugins) {
				loadedPlugins.add(plugin);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error while registering plugin : "
					+ plugin.getInfo().getName() + "!", e);
		}
	}

	/**
	 * Unregister a plugin
	 * 
	 * @param plugin
	 *            The plugin to unregister
	 */
	public void unregister(Plugin plugin) {
		try {
			// Call the unload method of the plugin, which should be used to
			// unregister listeners!
			plugin.onUnload();
			logger.fine("Unloaded plugin : " + plugin.getInfo().getName());
			synchronized (loadedPlugins) {
				loadedPlugins.remove(plugin);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error while unregistering plugin : "
					+ plugin.getInfo().getName() + "!", e);
		}
	}

	/**
	 * Reload a certain plugin
	 */
	public void reload(Plugin plugin) {
		synchronized (loadedPlugins) {
			if (loadedPlugins.contains(plugin)) {
				loadedPlugins.remove(plugin);
			}
		}
		try {
			plugin.onUnload();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Load from the file again
		register(loadPlugin(plugin.getInfo().getFile()));
	}

	/**
	 * Get the instance
	 * 
	 * @return The instance
	 */
	public static PluginLoader getInstance() {
		return instance;
	}

	/**
	 * Get a plugin by the name/file name
	 * 
	 * @param params
	 *            The name or file name
	 * @return The plugin if found
	 */
	public Plugin getPlugin(String params) {
		for (Plugin plugin : loadedPlugins) {
			if (plugin.getInfo().getName().equalsIgnoreCase(params)) {
				return plugin;
			} else {
				String fileName = plugin.getInfo().getFile().getName();
				// Check if we are loading a jar file, and trim if we are
				if (fileName.contains(".")) {
					fileName = fileName.substring(0, fileName.lastIndexOf("."));
				}
				if (fileName.equals(params)) {
					return plugin;
				}
			}
		}
		return null;
	}

	/**
	 * Get a list of loaded plugins
	 * 
	 * @return The list
	 */
	public Collection<Plugin> getLoadedPlugins() {
		return Collections.unmodifiableCollection(loadedPlugins);
	}
}
