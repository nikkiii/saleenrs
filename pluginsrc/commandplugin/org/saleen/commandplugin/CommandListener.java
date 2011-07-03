package org.saleen.commandplugin;

import org.saleen.event.Event;
import org.saleen.event.EventConsumer;
import org.saleen.event.impl.CommandEvent;

/**
 * An EventConsumer which handles command events
 * 
 * @author Nikki
 *
 */
public class CommandListener extends EventConsumer {
	
	@Override
	public void consume(Event event) {
		CommandEvent evt = (CommandEvent) event;
		CommandHandler.handleCommand(evt.getPlayer(), evt.getCommandString());
	}
}
