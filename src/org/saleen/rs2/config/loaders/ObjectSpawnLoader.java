package org.saleen.rs2.config.loaders;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import org.saleen.rs2.config.ConfigLoader;
import org.saleen.rs2.model.GameObject;
import org.saleen.rs2.model.Location;
import org.saleen.rs2.model.World;
import org.saleen.rs2.model.definition.GameObjectDefinition;

public class ObjectSpawnLoader extends ConfigLoader {

	private static final Logger logger = Logger
			.getLogger(ObjectSpawnLoader.class.getName());

	public void load(ByteBuffer data) {
		logger.info("Loading object spawns...");
		int spawnCount = data.getShort();
		for (int i = 0; i < spawnCount; i++) {
			int objectId = data.getShort();
			Location location = Location.create(data.getShort(),
					data.getShort(), data.get());
			int face = data.get();
			int type = data.get();
			GameObject object = new GameObject(
					GameObjectDefinition.forId(objectId), location, type, face);
			World.getWorld().getRegionManager().getRegionByLocation(location)
					.getGameObjects().add(object);
		}
		logger.info("Loaded " + spawnCount + " object spawns");
	}
}
