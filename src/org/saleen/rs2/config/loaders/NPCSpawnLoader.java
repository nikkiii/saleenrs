package org.saleen.rs2.config.loaders;

import java.nio.ByteBuffer;

import org.saleen.rs2.config.ConfigLoader;
import org.saleen.rs2.model.Location;
import org.saleen.rs2.model.NPC;
import org.saleen.rs2.model.World;
import org.saleen.rs2.model.definition.NPCDefinition;

/**
 * A simple loader to load the npcs spawned by default on server startup. It
 * extends the ConfigLoader class, so that it may be called by the loader
 * itself.
 * 
 * @author Nikki
 * 
 */
public class NPCSpawnLoader extends ConfigLoader {

	@Override
	public void load(ByteBuffer buffer) {
		int spawnCount = buffer.getShort();
		for (int i = 0; i < spawnCount; i++) {
			int npcId = buffer.getShort();
			int posX = buffer.getShort();
			int posY = buffer.getShort();
			int posZ = buffer.get();
			int rangeBottomLeftX = buffer.getShort();
			int rangeBottomLeftY = buffer.getShort();
			int rangeTopRightX = buffer.getShort();
			int rangeTopRightY = buffer.getShort();
			int face = buffer.get();

			NPC spawn = new NPC(NPCDefinition.forId(npcId));
			spawn.setLocation(Location.create(posX, posY, posZ));
			spawn.setRangeBottomLeft(Location.create(rangeBottomLeftX,
					rangeBottomLeftY, posZ));
			spawn.setRangeTopRight(Location.create(rangeTopRightX,
					rangeTopRightY, posZ));
			spawn.setFace(face);

			World.getWorld().register(spawn);
		}
	}
}
