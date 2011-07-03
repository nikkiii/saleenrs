package org.saleen.commandplugin.commands;

import org.saleen.commandplugin.Command;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.Player.Rights;
import org.saleen.rs2.plugin.Plugin;
import org.saleen.rs2.plugin.PluginLoader;

/**
 * Reload a plugin by name
 * 
 * @author Nikki
 *
 */
public class ReloadPlugin implements Command {

	
	@Override
	public void handle(Player player, String params) {
		Plugin plugin = PluginLoader.getInstance().getPlugin(params);
		if(plugin != null) {
			PluginLoader.getInstance().reload(plugin);
			player.getActionSender().sendMessage("Plugin "+plugin.getInfo().getName()+" successfully reloaded.");
		}
	}

	@Override
	public Rights getRights() {
		return Rights.PLAYER;
	}

}
