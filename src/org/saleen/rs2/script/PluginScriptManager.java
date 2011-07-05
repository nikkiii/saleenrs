package org.saleen.rs2.script;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.saleen.util.Streams;

/**
 * A <code>ScriptManager</code> which loads scripts from a plugin jar file
 * 
 * @author Nikki
 * 
 */
public class PluginScriptManager extends ScriptManager {

	/**
	 * A map of ClassLoader -> Script Manager
	 */
	private static Map<ClassLoader, PluginScriptManager> scriptManagers = new HashMap<ClassLoader, PluginScriptManager>();

	/**
	 * Create a script manager for the classloader
	 * 
	 * @param loader
	 *            The loader
	 */
	public PluginScriptManager(ClassLoader loader) {
		scriptManagers.put(loader, this);
	}

	/**
	 * load a script by the specified name
	 * 
	 * @param name
	 *            The name
	 * @param input
	 *            The inputstream
	 * @throws IOException
	 *             If an error occurred reading
	 */
	public void loadScript(String name, String extension, InputStream input) throws IOException {
		defineScript(name, extension, Streams.readContents(input));
	}

	/**
	 * Get the script manager for a specified class
	 * 
	 * @param clazz
	 *            The class
	 * @return The script manager
	 */
	public static PluginScriptManager getManager(Class<?> clazz) {
		ClassLoader loader = clazz.getClassLoader();
		if (scriptManagers.containsKey(loader)) {
			return scriptManagers.get(loader);
		}
		return null;
	}
}
