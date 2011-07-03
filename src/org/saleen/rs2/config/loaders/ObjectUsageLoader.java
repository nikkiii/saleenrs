package org.saleen.rs2.config.loaders;

import java.nio.ByteBuffer;

import org.saleen.rs2.config.ConfigLoader;

public class ObjectUsageLoader extends ConfigLoader {

	@Override
	public void load(ByteBuffer buffer) {
		int usageCount = buffer.getShort();
		for (int i = 0; i < usageCount; i++) {

		}
	}

}
