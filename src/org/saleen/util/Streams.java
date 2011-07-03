package org.saleen.util;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * A utility class that assists in dealing with <code>InputStream</code> and
 * <code>OutputStream</code> classes.
 * 
 * @author Graham Edgecombe
 * 
 */
public class Streams {

	/**
	 * Writes a null-terminated string to the specified
	 * <code>OutputStream</code>.
	 * 
	 * @param out
	 *            The output stream.
	 * @param string
	 *            The string.
	 * @throws IOException
	 *             if an I/O error occurs, such as the stream closing.
	 */
	public static void writeString(OutputStream out, String string)
			throws IOException {
		for (char c : string.toCharArray()) {
			out.write((byte) c);
		}
		out.write(0);
	}

	/**
	 * Reads a RuneScape string from the specified <code>InputStream</code>.
	 * 
	 * @param in
	 *            The input stream.
	 * @return The string.
	 * @throws IOException
	 *             if an I/O error occurs, such as the stream closing.
	 */
	public static String readRS2String(InputStream in) throws IOException {
		StringBuilder bldr = new StringBuilder();
		while (true) {
			int b = in.read();
			if (b == -1 || b == 10) {
				break;
			} else {
				bldr.append((char) ((byte) b));
			}
		}
		return bldr.toString();
	}

	/**
	 * Reads a null-terminated string from the specified
	 * <code>InputStream</code>.
	 * 
	 * @param in
	 *            The input stream.
	 * @return The string.
	 * @throws IOException
	 *             if an I/O error occurs, such as the stream closing.
	 */
	public static String readString(InputStream in) throws IOException {
		StringBuilder bldr = new StringBuilder();
		byte b;
		while ((b = (byte) in.read()) != 0) {
			bldr.append((char) b);
		}
		return bldr.toString();
	}

	/**
	 * Writes a line to the specified <code>OutputStream</code>.
	 * 
	 * @param out
	 *            The output stream.
	 * @param line
	 *            The line.
	 * @throws IOException
	 *             if an I/O error occurs, such as the stream closing.
	 */
	public static void writeLine(OutputStream out, String line)
			throws IOException {
		out.write((line + "\n").getBytes());
	}

	/**
	 * Reads a line from the specified <code>InputStream</code>.
	 * 
	 * @param in
	 *            The input stream.
	 * @return The line.
	 * @throws IOException
	 *             if an I/O error occurs, such as the stream closing.
	 */
	public static String readLine(InputStream in) throws IOException {
		StringBuilder bldr = new StringBuilder();
		byte b;
		while ((b = (byte) in.read()) != '\n') {
			bldr.append((char) b);
		}
		return bldr.toString().trim();
	}

	/**
	 * Read a regular rs2 string..?
	 * 
	 * @param buffer
	 *            The buffer
	 * @return The string
	 */
	public static String readString(byte[] buffer) {
		StringBuilder bldr = new StringBuilder();
		byte b;
		int idx = 0;
		while ((b = (byte) buffer[idx++]) != 0) {
			bldr.append((char) b);
		}
		return bldr.toString();
	}

	/**
	 * Read string contents from an InputStream
	 * 
	 * @param input
	 *            The input
	 * @return The string
	 * @throws IOException
	 *             If an error occurred reading
	 */
	public static String readContents(InputStream input) throws IOException {
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		while (true) {
			String line = reader.readLine();
			if (line == null)
				break;
			builder.append(line + "\n");
		}
		reader.close();
		return builder.toString();
	}

	public static void copy(InputStream input, FileOutputStream output)
			throws IOException {
		byte[] buffer = new byte[1024];
		while (true) {
			int read = input.read(buffer, 0, buffer.length);
			if (read == -1) {
				break;
			}
			output.write(buffer, 0, read);
		}
	}

}
