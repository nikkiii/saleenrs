package org.saleen.cache.obj;

import java.io.IOException;
import java.util.logging.Logger;

import org.saleen.cache.Cache;
import org.saleen.cache.InvalidCacheException;
import org.saleen.cache.index.impl.MapIndex;
import org.saleen.cache.index.impl.StandardIndex;
import org.saleen.cache.map.LandscapeListener;
import org.saleen.cache.map.LandscapeParser;
import org.saleen.rs2.model.GameObject;
import org.saleen.rs2.model.World;
import org.saleen.rs2.model.definition.GameObjectDefinition;

/**
 * Manages all of the in-game objects.
 * 
 * @author Graham Edgecombe
 * 
 */
public class ObjectManager implements LandscapeListener,
		ObjectDefinitionListener {

	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(ObjectManager.class
			.getName());

	/**
	 * The number of definitions loaded.
	 */
	private int definitionCount = 0;

	/**
	 * The object count
	 */
	private int objectCount = 0;

	/**
	 * Loads the objects in the map.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @throws InvalidCacheException
	 *             if the cache is invalid.
	 */
	public void load(Cache cache) throws IOException {
		logger.info("Loading definitions...");
		StandardIndex[] defIndices = cache.getIndexTable()
				.getObjectDefinitionIndices();
		new ObjectDefinitionParser(cache, defIndices, this).parse();
		logger.info("Loaded " + definitionCount + " object definitions.");
		logger.info("Loading map...");
		MapIndex[] mapIndices = cache.getIndexTable().getMapIndices();
		for (MapIndex index : mapIndices) {
			new LandscapeParser(cache, index.getIdentifier(), this).parse();
		}
		logger.info("Loaded " + objectCount + " objects.");
	}

	@Override
	public void objectParsed(GameObject obj) {
		objectCount++;
		World.getWorld().getRegionManager()
				.getRegionByLocation(obj.getLocation()).getGameObjects()
				.add(obj);
	}

	@Override
	public void objectDefinitionParsed(GameObjectDefinition def) {
		definitionCount++;
		GameObjectDefinition.addDefinition(def);
	}

}
