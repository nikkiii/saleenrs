package org.saleen.cache.npc;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.saleen.cache.Archive;
import org.saleen.cache.Cache;
import org.saleen.cache.index.impl.StandardIndex;
import org.saleen.cache.util.ByteBufferUtils;

/**
 * Parses NPC definitions from the cache
 * 
 * @author Nikki
 */
public class NPCDefinitionParser {
	/**
	 * The cache.
	 */
	private Cache cache;

	/**
	 * The index.
	 */
	private StandardIndex[] indices;

	/**
	 * The listener.
	 */
	private NPCDefinitionListener listener;

	/**
	 * Creates the object definition parser.
	 * 
	 * @param cache
	 *            The cache.
	 * @param indices
	 *            The indices in the cache.
	 * @param listener
	 *            The object definition listener.
	 */
	public NPCDefinitionParser(Cache cache, StandardIndex[] indices,
			NPCDefinitionListener listener) {
		this.cache = cache;
		this.indices = indices;
		this.listener = listener;
	}

	/**
	 * Parses the object definitions in the cache.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public void parse() throws IOException {
		ByteBuffer buf = new Archive(cache.getFile(0, 2))
				.getFileAsByteBuffer("npc.dat");

		for (StandardIndex index : indices) {
			int id = index.getIdentifier();
			int offset = index.getFile(); // bad naming, should be getOffset()
			buf.position(offset);

			String name = "null";
			String desc = "null";
			int combatLevel = -1;
			int size = -1;
			String[] actions = new String[10];

			outer_loop: do {
				int configCode;
				do {
					configCode = buf.get() & 0xff;
					if (configCode == 0) {
						break outer_loop;
					}
					switch (configCode) {
					case 1:
						int someCounter = buf.get() & 0xff;
						for (int i = 0; i < someCounter; i++) {
							buf.getShort();
						}
						break;
					case 2:
						name = ByteBufferUtils.getNewString(buf);
						break;
					case 3:
						desc = ByteBufferUtils.getString(buf);
						break;

					case 12:
						size = buf.get();
						break;
					case 13:
						// Stand anim
						buf.getShort();
						break;
					case 14:
						// Walk anim
						buf.getShort();
						break;
					case 17:
						// walk etc
						buf.getShort();
						buf.getShort();
						buf.getShort();
						buf.getShort();
						break;
					case 30:
					case 31:
					case 32:
					case 33:
					case 34:
					case 35:
					case 36:
					case 37:
					case 38:
					case 39:
						actions[configCode - 30] = ByteBufferUtils
								.getNewString(buf); // actions
						break;
					case 40:
						someCounter = buf.get() & 0xff; // model colours
						for (int i = 0; i < someCounter; i++) {
							buf.getShort();
							buf.getShort();
						}
						break;
					case 60:
						someCounter = buf.get() & 0xff;
						for (int i = 0; i < someCounter; i++) {
							buf.getShort();
						}
						break;
					case 90:
					case 91:
					case 92:
						buf.getShort();
						break;
					case 95:
						combatLevel = buf.getShort();
						break;
					case 97:
						buf.getShort();
						break;
					case 98:
						buf.getShort();
						break;
					case 100:
					case 101:
						buf.get();
						break;
					case 102:
					case 103:
						buf.getShort();
						break;
					case 106:
						buf.getShort();
						buf.getShort();
						someCounter = buf.get() & 0xff;
						for (int i2 = 0; i2 <= someCounter; i2++) {
							buf.getShort();
						}
						break;
					}
				} while (true);
			} while (true);

			listener.npcParsed(new CacheNPCDefinition(id, name, desc,
					combatLevel, size, actions));
		}
	}
}
