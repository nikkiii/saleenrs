package org.saleen.rs2.model;

import java.io.IOException;
import java.util.logging.Logger;

import org.saleen.cache.Cache;
import org.saleen.cache.index.impl.StandardIndex;
import org.saleen.cache.item.CacheItemDefinition;
import org.saleen.cache.item.ItemDefinitionListener;
import org.saleen.cache.item.ItemDefinitionParser;

public class ItemManager implements ItemDefinitionListener {
	private static final Logger logger = Logger.getLogger(NPCManager.class
			.getName());

	private int definitionCount = 0;

	public void load(Cache cache) throws IOException {
		logger.info("Loading item definitions...");
		StandardIndex[] defIndices = cache.getIndexTable()
				.getItemDefinitionIndices();
		new ItemDefinitionParser(cache, defIndices, this).parse();
		logger.info("Loaded " + definitionCount + " item definitions.");
	}

	@Override
	public void itemParsed(CacheItemDefinition cacheItemDefinition) {
		definitionCount++;
		CacheItemDefinition.addItem(cacheItemDefinition);
	}

}
