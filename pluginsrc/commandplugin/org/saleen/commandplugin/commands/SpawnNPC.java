package org.saleen.commandplugin.commands;

import org.saleen.commandplugin.Command;
import org.saleen.rs2.model.Location;
import org.saleen.rs2.model.NPC;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.Player.Rights;
import org.saleen.rs2.model.World;
import org.saleen.rs2.model.definition.NPCDefinition;

/**
 * A command to spawn an npc
 * 
 * @author Nikki
 *
 */
public class SpawnNPC implements Command {

	@Override
	public void handle(Player player, String params) {
		String[] args = params.split(" ");
		int id = Integer.parseInt(args[0]);
		NPC npc = new NPC(NPCDefinition.forId(id));
		Location spawn = player.getLocation();
		npc.setLocation(spawn);
		npc.setRangeBottomLeft(spawn.transform(-5, -5, 0));
		npc.setRangeTopRight(spawn.transform(5, 5, 0));
		World.getWorld().register(npc);
	}

	@Override
	public Rights getRights() {
		return Rights.PLAYER;
	}
}
