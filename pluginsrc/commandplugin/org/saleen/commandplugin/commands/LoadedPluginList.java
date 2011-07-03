package org.saleen.commandplugin.commands;

import java.util.Iterator;

import org.saleen.commandplugin.Command;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.Player.Rights;
import org.saleen.rs2.plugin.Plugin;
import org.saleen.rs2.plugin.PluginInfo;
import org.saleen.rs2.plugin.PluginLoader;

public class LoadedPluginList implements Command {

	@Override
	public void handle(Player player, String params) {
		player.getActionSender().clearQuestInterface();
		player.getActionSender().sendString(8144, "Loaded plugins");
		
		int startIdx = 8147;

		Iterator<Plugin> it = PluginLoader.getInstance().getLoadedPlugins().iterator();
		while(it.hasNext()) {
			Plugin plugin = it.next();
			PluginInfo info = plugin.getInfo();
			player.getActionSender().sendString(startIdx++, "@or1@"+info.getName()+"@bla@ by @gre@"+info.getAuthor()+"@bla@");
		}
		player.getActionSender().sendInterface(8134);
	}

	@Override
	public Rights getRights() {
		return Rights.PLAYER;
	}

}
