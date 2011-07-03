package org.saleen.commandplugin;

import java.util.HashMap;
import java.util.Map;

import org.saleen.commandplugin.commands.LoadedPluginList;
import org.saleen.commandplugin.commands.ReloadPlugin;
import org.saleen.commandplugin.commands.SpawnNPC;
import org.saleen.event.ConsumerInterruptor;
import org.saleen.rs2.model.Player;

/**
 * A command handler which has a map of String -> Command
 * 
 * @author Nikki
 *
 */
public class CommandHandler {
	
	/**
	 * The map of commands
	 */
	private static Map<String, Command> commands = new HashMap<String, Command>();
	
	static {
		commands.put("spawnnpc", new SpawnNPC());
		commands.put("reloadplugin", new ReloadPlugin());
		commands.put("loadedplugins", new LoadedPluginList());
	}
	
	/**
	 * Handle a command from a player
	 * @param player
	 * 			The player
	 * @param commandString
	 * 			The command string
	 */
	public static void handleCommand(Player player, String commandString) {
		if(player == null) {
			return;
		}
		String commandName;
		String paramaters;
		if(commandString.contains(" ")) {
			commandName = commandString.substring(0, commandString.indexOf(" "));
			paramaters = commandString.substring(commandString.indexOf(" ")+1);
		} else {
			commandName = commandString.substring(0);
			paramaters = "";
		}
		Command command = commands.get(commandName.toLowerCase());
		if(command != null) {
			if(player.getRights().toInteger() >= command.getRights().toInteger()) {
				command.handle(player, paramaters);
				//If we handled this command, throw a nice exception to stop parsing :D
				throw new ConsumerInterruptor();
			} else {
				//NO RIGHTS!
			}
		}
	}
}
