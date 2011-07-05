package org.saleen.rs2.script;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * A script manager based on Lua
 * 
 * @author Nikki
 */
public class ScriptManager {
	
	private ScriptEngineManager manager = new ScriptEngineManager();

	/**
	 * The logger for this manager.
	 */
	private final Logger logger = Logger.getLogger(ScriptManager.class
			.getName());

	/**
	 * The script storage map
	 */
	private HashMap<String, ScriptEngine> scripts = new HashMap<String, ScriptEngine>();

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
			Invocable inv = (Invocable) scripts.get(script);
			
			return inv.invokeFunction(method, args);
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
	public void defineScript(String name, String extension, byte[] bytes) {
		defineScript(name, extension, new String(bytes));
	}

	/**
	 * Define a script
	 * 
	 * @param name
	 *            The name of the script
	 * @param bytes
	 *            The
	 */
	public void defineScript(String name, String extension, String script) {
		ScriptEngine engine = manager.getEngineByExtension(extension);
        try {
			engine.eval(new String(script));
		} catch (ScriptException e) {
			e.printStackTrace();
		}
        scripts.put(name, engine);
	}

	/**
	 * Clear the scripts
	 */
	protected void clear() {
		scripts.clear();
	}
}
