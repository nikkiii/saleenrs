package org.saleen.commandplugin;

import org.saleen.event.impl.CommandEvent;
import org.saleen.rs2.plugin.Plugin;

/**
 * Adds additional commands to Saleen
 * 
 * @author Nikki
 *
 */
public class CommandPlugin extends Plugin {

	/**
	 * The listener
	 */
	private CommandListener listener;
	
	@SuppressWarnings("unchecked")
	@Override
	public void onLoad() {
		listener = new CommandListener();
		listener.bind(CommandEvent.class);
	}

	@Override
	public void onUnload() {
		listener.unbindAll();
	}
}
