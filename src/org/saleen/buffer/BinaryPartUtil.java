package org.saleen.buffer;

import java.io.InputStream;
import java.nio.ByteBuffer;

public class BinaryPartUtil {
	public static BinaryPart readPartFromInput(InputStream is) {
		try {
			int partId = is.read();
			int partLen = ((is.read() << 24) + (is.read() << 16)
					+ (is.read() << 8) + (is.read() << 0));
			byte[] partData = new byte[partLen];
			is.read(partData, 0, partLen);
			return new BinaryPart(partId, ByteBuffer.wrap(partData));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
