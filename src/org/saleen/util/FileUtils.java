package org.saleen.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class FileUtils {

	private static String lineSeparator = System.getProperty("line.separator");

	public static ByteBuffer getDecompressedFileData(File file)
			throws Exception {
		InputStream input = new GZIPInputStream(new FileInputStream(file));
		ByteBuffer buf = ByteBuffer.allocate((int) file.length());
		while (true) {
			byte[] temp = new byte[1024];
			int read = input.read(temp, 0, temp.length);
			if (read == -1) {
				break;
			} else {
				buf.put(temp, 0, read);
			}
		}
		buf.flip();
		input.close();
		return buf;
	}

	public static String readContents(File file) {
		StringBuilder builder = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;
				builder.append(line + lineSeparator);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	public static LinkedList<File> list(File file, Filter<File> filter) {
		LinkedList<File> files = new LinkedList<File>();
		for (File f : file.listFiles()) {
			if (filter.accept(f))
				files.add(f);
		}
		return files;
	}

	public static LinkedList<File> listRecursive(File file, Filter<File> filter) {
		LinkedList<File> files = new LinkedList<File>();
		for (File f : file.listFiles()) {
			if (f.isDirectory()) {
				files.addAll(listRecursive(f, filter));
			} else {
				if (filter.accept(f))
					files.add(f);
			}
		}
		return files;
	}

	public static ChannelBuffer decompressToChannelBuffer(File file)
			throws IOException {
		InputStream input = new GZIPInputStream(new FileInputStream(file));
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		while (true) {
			byte[] temp = new byte[1024];
			int read = input.read(temp, 0, temp.length);
			if (read == -1) {
				break;
			} else {
				buf.writeBytes(temp, 0, read);
			}
		}
		input.close();
		return buf;
	}

	public static byte[] readFile(String name) {
		try {
			RandomAccessFile raf = new RandomAccessFile(name, "r");
			ByteBuffer buf = raf.getChannel().map(
					FileChannel.MapMode.READ_ONLY, 0, raf.length());
			try {
				if (buf.hasArray()) {
					return buf.array();
				} else {
					byte[] array = new byte[buf.remaining()];
					buf.get(array);
					return array;
				}
			} finally {
				raf.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
