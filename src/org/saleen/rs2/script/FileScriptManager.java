package org.saleen.rs2.script;

import java.io.File;

import org.saleen.util.FileUtils;

/**
 * A script manager based on Lua
 * 
 * @author Nikki
 */
public class FileScriptManager extends ScriptManager {

	/**
	 * The singleton of this class.
	 */
	private static final FileScriptManager INSTANCE = new FileScriptManager();

	/**
	 * Gets the ScriptManager singleton.
	 * 
	 * @return The ScriptManager singleton.
	 */
	public static FileScriptManager getScriptManager() {
		return INSTANCE;
	}

	/**
	 * The script path
	 */
	private File scriptDir;

	/**
	 * Initialize the class
	 */
	private FileScriptManager() {
		super();
	}

	/**
	 * Load scripts
	 * 
	 * @param dirPath
	 *            The path to load from
	 */
	public void loadScripts(String dirPath) {
		scriptDir = new File(dirPath);
		if (scriptDir.exists() && scriptDir.isDirectory()) {
			loadScriptsInternal(scriptDir);
		}
	}

	/**
	 * Load the scripts from the specified directory, this can only be called
	 * from within, since this will be used when we know the directory to load
	 * from
	 * 
	 * @param file
	 *            The directory to load from
	 */
	private void loadScriptsInternal(File file) {
		File[] children = file.listFiles();
		for (File child : children) {
			if (child.isDirectory()) {
				loadScriptsInternal(child);
			} else if (child.getName().endsWith(".lua")) {
				try {
					String scriptName = child.getPath().substring(
							child.getPath().indexOf("pts\\") + 4,
							child.getPath().indexOf("."));
					scriptName = scriptName.replace("\\", ".");
					defineScript(scriptName, FileUtils.readContents(child));
				} catch (Exception e) {
					// Continue on..
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void reload() {
		clear();
		loadScriptsInternal(scriptDir);
	}
}
