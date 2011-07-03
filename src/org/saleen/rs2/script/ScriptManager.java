package org.saleen.rs2.script;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;

/**
 * A script manager based on Lua
 * 
 * @author Nikki
 */
public abstract class ScriptManager {

	/**
	 * The logger for this manager.
	 */
	private final Logger logger = Logger.getLogger(ScriptManager.class
			.getName());

	/**
	 * The script storage map
	 */
	private HashMap<String, LuaState> scripts = new HashMap<String, LuaState>();

	/**
	 * Initialize the class
	 */
	public ScriptManager() {
		// Nothing
	}

	/**
	 * Invokes a script which by default is the default script, otherwise it
	 * will find the specified script
	 * 
	 * @param identifier
	 *            The identifier, such as script.method or package.script.method
	 * @param args
	 *            The arguments
	 */
	public Object invoke(String identifier, Object... args) {
		String method = identifier.substring(identifier.lastIndexOf(":") + 1);
		String script = "default";
		if (identifier.contains(":")) {
			script = identifier.substring(0, identifier.lastIndexOf(":"));
		}
		try {
			if (!scripts.containsKey(script)) {
				throw new Exception("Unknown script : " + script);
			}
			LuaState state = scripts.get(script);
			state.pcall(0, 0, 0);
			state.getGlobal(method);
			if (args != null) {
				for (Object object : args) {
					state.pushJavaObject(object);
				}
			}
			int resp = state.pcall(args.length, 0, 0);
			if (resp != 0) {
				throw new Exception("Error when running script : code " + resp);
			}
		} catch (Exception e) {
			logger.severe("Error running script : " + script + " method "
					+ method);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Define a script
	 * 
	 * @param name
	 *            The name
	 * @param bytes
	 *            The bytes
	 */
	public void defineScript(String name, byte[] bytes) {
		defineScript(name, new String(bytes));
	}

	/**
	 * Define a script
	 * 
	 * @param name
	 *            The name of the script
	 * @param bytes
	 *            The
	 */
	public void defineScript(String name, String script) {
		LuaState state = createState();
		state.LdoString(script);
		scripts.put(name, state);
	}

	/**
	 * Define a script from a file
	 * 
	 * @param name
	 *            The name
	 * @param file
	 *            The file
	 */
	public void defineScript(String name, File file) {
		LuaState state = createState();
		state.LdoFile(file.getPath());
		scripts.put(name, state);
	}

	/**
	 * Creates a new lua state, and saves us space...
	 * 
	 * @return the newly created state
	 */
	public LuaState createState() {
		LuaState state = LuaStateFactory.newLuaState();
		state.openLibs();
		return state;
	}

	/**
	 * Reload scripts
	 */
	public abstract void reload();

	/**
	 * Clear the scripts
	 */
	protected void clear() {
		scripts.clear();
	}
}
