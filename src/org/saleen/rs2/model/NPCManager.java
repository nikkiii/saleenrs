package org.saleen.rs2.model;

import java.io.IOException;
import java.util.logging.Logger;

import org.saleen.cache.Cache;
import org.saleen.cache.index.impl.StandardIndex;
import org.saleen.cache.npc.CacheNPCDefinition;
import org.saleen.cache.npc.NPCDefinitionListener;
import org.saleen.cache.npc.NPCDefinitionParser;

public class NPCManager implements NPCDefinitionListener {

	private static final Logger logger = Logger.getLogger(NPCManager.class
			.getName());

	private int definitionCount = 0;

	public void load(Cache cache) throws IOException {
		logger.info("Loading npc definitions...");
		StandardIndex[] defIndices = cache.getIndexTable()
				.getNpcDefinitionIndices();
		new NPCDefinitionParser(cache, defIndices, this).parse();
		logger.info("Loaded " + definitionCount + " npc definitions.");
	}

	@Override
	public void npcParsed(CacheNPCDefinition gameNPCDefinition) {
		definitionCount++;
		CacheNPCDefinition.addDefinition(gameNPCDefinition);
	}
}
