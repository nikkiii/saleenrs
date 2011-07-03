package org.saleen.rs2.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 * A utility class for dealing with <code>IoBuffer</code>s.
 * 
 * @author Graham Edgecombe
 * 
 */
public class ChannelBufferUtils {

	/**
	 * Reads a RuneScape string from a buffer.
	 * 
	 * @param buf
	 *            The buffer.
	 * @return The string.
	 */
	public static String getRS2String(ChannelBuffer buf) {
		StringBuilder bldr = new StringBuilder();
		byte b;
		while (buf.readable() && (b = buf.readByte()) != 10) {
			bldr.append((char) b);
		}
		return bldr.toString();
	}

	/**
	 * Writes a RuneScape string to a buffer.
	 * 
	 * @param buf
	 *            The buffer.
	 * @param string
	 *            The string.
	 */
	public static void putRS2String(ChannelBuffer buf, String string) {
		for (char c : string.toCharArray()) {
			buf.writeByte((byte) c);
		}
		buf.writeByte((byte) 10);
	}

	/**
	 * Decompress the buffer into a new buffer
	 * 
	 * @param buffer
	 *            The buffer
	 * @return
	 */
	public static ChannelBuffer decompress(ChannelBuffer buffer) {
		try {
			ChannelBuffer decompressed = ChannelBuffers.dynamicBuffer();
			byte[] data = new byte[buffer.readableBytes()];
			GZIPInputStream input = new GZIPInputStream(
					new ByteArrayInputStream(data));
			byte[] temp = new byte[1024];
			while (true) {
				int read = input.read(temp, 0, temp.length);
				if (read == -1) {
					break;
				} else {
					decompressed.writeBytes(temp, 0, read);
				}
			}
			return decompressed;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
