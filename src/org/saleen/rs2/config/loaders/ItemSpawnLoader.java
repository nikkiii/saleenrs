package org.saleen.rs2.config.loaders;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import org.saleen.rs2.config.ConfigLoader;

public class ItemSpawnLoader extends ConfigLoader {

	private static final Logger logger = Logger.getLogger(ItemSpawnLoader.class
			.getName());

	@SuppressWarnings("unused")
	public void load(ByteBuffer data) {
		logger.info("Loading item spawns...");
		int spawnCount = data.getShort();
		for (int i = 0; i < spawnCount; i++) {
			int itemId = data.getShort();
			int posX = data.getShort();
			int posY = data.getShort();
			int posZ = data.get();
			// TODO load
		}
		logger.info("Loaded " + spawnCount + " item spawns");
	}
}
