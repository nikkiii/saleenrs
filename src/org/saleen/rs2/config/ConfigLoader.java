package org.saleen.rs2.config;

import java.io.File;
import java.nio.ByteBuffer;

import org.apache.commons.codec.digest.DigestUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.saleen.buffer.BinaryBuffer;
import org.saleen.buffer.BinaryPart;
import org.saleen.util.FileUtils;

/**
 * Loads all data, such as object spawns, item spawns, npc spawns etc from a
 * central file, divided into parts, And dispatches them to the appropriate
 * loader
 * 
 * @author Nikki
 * 
 */
public abstract class ConfigLoader {

	/**
	 * The config loader array, initialized after opcode 0
	 */
	private static ConfigLoader[] loaders;

	/**
	 * The file size
	 */
	private static long fileSize;

	/**
	 * The md5 hash
	 */
	private static String md5Hash;

	/**
	 * Central loading point, loads from the single file into an IoBuffer, then
	 * the BinaryBuffer. Opcode 0 is always our "control" opcode, controlling
	 * which data gets sent to which loader
	 * 
	 * @param file
	 * @throws Exception
	 *             If any loading/parsing errors occur, it will halt the server.
	 */
	public static void load(File file) throws Exception {
		ChannelBuffer data = FileUtils.decompressToChannelBuffer(file);
		fileSize = data.readableBytes();
		md5Hash = "";
		BinaryBuffer buffer = new BinaryBuffer(data);
		while (buffer.hasNextPart()) {
			BinaryPart part = buffer.nextPart();
			switch (part.getOpcode()) {
			case 0:
				loadOpcodes(part);
				break;
			default:
				// Don't intentionally throw an error in case something messed
				// up...
				if (part.getOpcode() <= loaders.length
						&& loaders[part.getOpcode()] != null)
					loaders[part.getOpcode()].load(part.getData());
				break;
			}
		}
	}

	/**
	 * Load the loader config index, and set the correct class
	 * 
	 * @param part
	 *            The part to load from
	 */
	private static void loadOpcodes(BinaryPart part) {
		int loaderLength = part.get();
		loaders = new ConfigLoader[loaderLength + 1];
		while (part.hasMoreData()) {
			int opcode = part.get();
			String className = part.getString();
			try {
				Class<?> c = Class.forName(className);
				loaders[opcode] = (ConfigLoader) c.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Each loader must implement this method to dispatch the loading operation.
	 * 
	 * @param data
	 *            The data from the part.
	 */
	public abstract void load(ByteBuffer data);

	/**
	 * Get the total file size
	 * 
	 * @return the file size
	 */
	public static long getFileSize() {
		return fileSize;
	}

	/**
	 * Get the MD5 hash of the file
	 * 
	 * @return The final hash
	 */
	public static String getMD5Hash() {
		return md5Hash;
	}

	/**
	 * Get the loader count
	 * 
	 * @return The loader array length
	 */
	public static int getLength() {
		return loaders.length;
	}

	/**
	 * Recalculate the hash
	 * 
	 * @param data
	 */
	public static void recalculateHash(byte[] data) {
		fileSize = data.length;
		md5Hash = DigestUtils.md5Hex(data);
	}
}
