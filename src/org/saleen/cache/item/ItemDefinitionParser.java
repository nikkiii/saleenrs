package org.saleen.cache.item;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.saleen.cache.Archive;
import org.saleen.cache.Cache;
import org.saleen.cache.index.impl.StandardIndex;
import org.saleen.cache.util.ByteBufferUtils;

public class ItemDefinitionParser {
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
	private ItemDefinitionListener listener;

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
	public ItemDefinitionParser(Cache cache, StandardIndex[] indices,
			ItemDefinitionListener listener) {
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
				.getFileAsByteBuffer("obj.dat");
		for (StandardIndex index : indices) {
			int id = index.getIdentifier();
			int offset = index.getFile(); // bad naming, should be getOffset()
			buf.position(offset);

			String name = "null";
			String desc = "null";
			boolean stackable = false;
			boolean members = false;
			String[] actions = new String[5];
			String[] groundActions = new String[5];
			int certID = -1;
			int certTemplateID = -1;
			int someCounter;
			int[] stackIDs = new int[10];
			int[] stackAmounts = new int[10];

			outer_loop: do {
				int configCode;
				do {
					configCode = buf.get() & 0xFF;
					if (configCode == 0) {
						break outer_loop;
					}
					switch (configCode) {
					case 1: // Model id
						buf.getShort();
						break;
					case 2: // Name
						name = ByteBufferUtils.getNewString(buf);
						break;
					case 3: // Desc
						desc = ByteBufferUtils.getString(buf);
						break;
					case 4: // Zoom
						buf.getShort();
						break;
					case 5: // Rotation 1
						buf.getShort();
						break;
					case 6: // Rotation 2
						buf.getShort();
						break;
					case 7: // Model offset 1
						buf.getShort();
						break;
					case 8: // Model offset 2
						buf.getShort();
						break;
					case 10: // Unknown
						buf.getShort();
						break;
					case 11: // Stackable
						stackable = true;
						break;
					case 12: // Unknown
						buf.getInt();
						break;
					case 16: // Members
						members = true;
						break;
					case 23: // Male equip 1
						buf.getShort();
						buf.get();
						break;
					case 24: // Female equip 1
						buf.getShort();
						break;
					case 25: // Male equip 2
						buf.getShort();
						buf.get();
						break;
					case 26: // Female equip 2
						buf.getShort();
						break;
					case 30:
					case 31:
					case 32:
					case 33:
					case 34:
						groundActions[configCode - 30] = ByteBufferUtils
								.getNewString(buf);
						if (groundActions[configCode - 30]
								.equalsIgnoreCase("hidden"))
							groundActions[configCode - 30] = null;
						break;
					case 35:
					case 36:
					case 37:
					case 38:
					case 39:
						actions[configCode - 35] = ByteBufferUtils
								.getNewString(buf);
						break;
					case 40: // Model colors
						someCounter = buf.get() & 0xff;
						for (int i = 0; i < someCounter; i++) {
							buf.getShort();
							buf.getShort();
						}
						break;
					case 78:
					case 79:
					case 90:
					case 91:
					case 92:
					case 93:
					case 95:
						buf.getShort();
						break;
					case 97: // Certificate (Noted?) id
						certID = buf.getShort() & 0xFFFF;
						break;
					case 98: // Cert template id, something to do with noted?
						certTemplateID = buf.getShort() & 0xFFFF;
						break;
					case 100:
					case 101:
					case 102:
					case 103:
					case 104:
					case 105:
					case 106:
					case 107:
					case 108:
					case 109:
						stackIDs[configCode - 100] = buf.getShort() & 0xFFFF;
						stackAmounts[configCode - 100] = buf.getShort() & 0xFFFF;
						break;
					case 110:
					case 111:
					case 112:
						buf.getShort();
						break;
					case 113:
					case 114:
					case 115:
						buf.get();
						break;
					}
				} while (true);

			} while (true);
			listener.itemParsed(new CacheItemDefinition(id, name, desc,
					members, stackable, groundActions, actions, certID,
					certTemplateID, stackIDs, stackAmounts));
		}
	}
}
