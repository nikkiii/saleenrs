package org.saleen.buffer;

import java.nio.ByteBuffer;

/**
 * A file part, such as a player info sector
 * 
 * @author Nikki
 */
public class BinaryPart {

	/**
	 * The opcode of this part
	 */
	private int opcode;

	/**
	 * The data this part contains
	 */
	private ByteBuffer data;

	/**
	 * Constructor for this part
	 * 
	 * @param opcode
	 *            The opcode to set this for
	 * @param data
	 * 
	 */
	public BinaryPart(int opcode, ByteBuffer data) {
		this.opcode = opcode;
		this.data = data;
	}

	/**
	 * Read a standard byte from the buffer
	 * 
	 * @return The byte we read.
	 */
	public byte get() {
		return data.get();
	}

	/**
	 * Read a boolean from the buffer as a byte, 0 for false 1 for true
	 * 
	 * @return The byte we read.
	 */
	public boolean getBoolean() {
		return data.get() == 1;
	}

	/**
	 * Read a standard short from the buffer
	 * 
	 * @return The short we read.
	 */
	public short getShort() {
		return data.getShort();
	}

	/**
	 * Read a standard integer from the buffer
	 * 
	 * @return The integer we read.
	 */
	public int getInt() {
		return data.getInt();
	}

	/**
	 * Read a standard double from the buffer
	 * 
	 * @return The double we read.
	 */
	public double getDouble() {
		return data.getDouble();
	}

	/**
	 * Read a standard long from the buffer
	 * 
	 * @return The long we read.
	 */
	public long getLong() {
		return data.getLong();
	}

	/**
	 * Get the opcode of this part
	 * 
	 * @return The opcode
	 */
	public int getOpcode() {
		return opcode;
	}

	/**
	 * Gets a string from the buffer
	 * 
	 * @return The string we read
	 */
	public String getString() {
		StringBuilder bldr = new StringBuilder();
		byte b;
		while (data.hasRemaining() && (b = data.get()) != 10) {
			bldr.append((char) b);
		}
		return bldr.toString();
	}

	/**
	 * Get an unsigned byte from the buffer
	 * 
	 * @return The byte as a short
	 */
	public short getUnsigned() {
		return (short) (data.get() & 0xff);
	}

	/**
	 * Get an unsigned short from the buffer
	 * 
	 * @return The short as an integer
	 */
	public int getUnsignedShort() {
		return data.getShort() & 0xFFFF;
	}

	/**
	 * Return true if the buffer has more data
	 * 
	 * @return If the buffer has more data
	 */
	public boolean hasMoreData() {
		return data.remaining() > 0;
	}

	/**
	 * Get the data of this part
	 * 
	 * @return The data
	 */
	public ByteBuffer getData() {
		return data;
	}
}