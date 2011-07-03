package org.saleen.buffer;

import java.nio.ByteBuffer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 * A buffer class that can be used for binary saving, separating certain parts
 * of the file to allow more fields to be added on.
 * 
 * @author Nikki
 * 
 */
public class BinaryBuffer {

	/**
	 * The mode
	 * 
	 * @author Nikki
	 * 
	 */
	public enum Mode {
		WRITE, READ
	}

	/**
	 * The main buffer we write the data to, after the opcode is finished the
	 * sub buffer data gets written to this.
	 */
	private ChannelBuffer mainBuffer;

	/**
	 * The sub buffer, when writing, each opcode is written to this buffer and
	 * then copied to the main buffer.
	 */
	private ChannelBuffer subBuffer;

	/**
	 * The opcode currently...
	 */
	private int opcode = -1;

	/**
	 * The mode we are using, so we cannot write on a read buffer, and read on a
	 * write buffer
	 */
	private Mode mode;

	/**
	 * Constructor, configuring the main and sub buffers with auto expanding and
	 * shrinking
	 */
	public BinaryBuffer() {
		this.mode = Mode.WRITE;
		this.mainBuffer = ChannelBuffers.dynamicBuffer();
		this.subBuffer = ChannelBuffers.dynamicBuffer();
	}

	public BinaryBuffer(ChannelBuffer inputBuffer) {
		this.mode = Mode.READ;
		this.mainBuffer = inputBuffer;
	}

	/**
	 * Start a saving opcode, or a rendering opcode...
	 * 
	 * @param newOpcode
	 *            The opcode to start
	 * @return The instance, can be used for chaining.
	 */
	public BinaryBuffer startOpcode(int newOpcode) {
		if (mode != Mode.WRITE) {
			throw new RuntimeException("Buffer is currently in read mode!");
		}
		if (this.opcode != -1) {
			throw new RuntimeException(
					"Opcode must be finished before starting a new opcode!");
		}
		this.opcode = newOpcode;
		return this;
	}

	/**
	 * Finish the current opcode, writing the sub opcode to the main buffer, and
	 * then clearing the buffer again for use.
	 * 
	 * @return The instance, which can be used for chaining
	 */
	public BinaryBuffer finishOpcode() {
		if (mode != Mode.WRITE) {
			throw new RuntimeException("Buffer is currently in read mode!");
		}
		if (opcode == -1) {
			throw new RuntimeException(
					"Opcode must be started before finishing!");
		}
		mainBuffer.writeByte((byte) opcode);
		mainBuffer.writeInt(subBuffer.writerIndex());
		mainBuffer.writeBytes(subBuffer);
		subBuffer.clear();
		opcode = -1;
		return this;
	}

	/**
	 * Put a standard byte into the sub buffer
	 * 
	 * @param b
	 *            The byte to insert
	 * @return The instance for chaining.
	 */
	public BinaryBuffer put(byte b) {
		if (mode != Mode.WRITE) {
			throw new RuntimeException("Buffer is currently in read mode!");
		}
		subBuffer.writeByte(b);
		return this;
	}

	/**
	 * Put a boolean into the buffer by rendering it as a byte, 0 or 1
	 * 
	 * @param b
	 *            The boolean
	 * @return The instance for chaining.
	 */
	public BinaryBuffer putBoolean(boolean b) {
		if (mode != Mode.WRITE) {
			throw new RuntimeException("Buffer is currently in read mode!");
		}
		subBuffer.writeByte((byte) (b ? 1 : 0));
		return this;
	}

	/**
	 * Put a standard short into the sub buffer
	 * 
	 * @param s
	 *            The short to insert
	 * @return The instance for chaining.
	 */
	public BinaryBuffer putShort(short s) {
		if (mode != Mode.WRITE) {
			throw new RuntimeException("Buffer is currently in read mode!");
		}
		subBuffer.writeShort(s);
		return this;
	}

	/**
	 * Put a standard double into the sub buffer
	 * 
	 * @param d
	 *            The double to insert
	 * @return The instance for chaining.
	 */
	public BinaryBuffer putDouble(double d) {
		if (mode != Mode.WRITE) {
			throw new RuntimeException("Buffer is currently in read mode!");
		}
		subBuffer.writeDouble(d);
		return this;
	}

	/**
	 * Put a standard integer into the sub buffer
	 * 
	 * @param i
	 *            The integer to insert
	 * @return The instance for chaining.
	 */
	public BinaryBuffer putInt(int i) {
		if (mode != Mode.WRITE) {
			throw new RuntimeException("Buffer is currently in read mode!");
		}
		subBuffer.writeInt(i);
		return this;
	}

	/**
	 * Put a standard long into the sub buffer
	 * 
	 * @param l
	 *            The long to insert
	 * @return The instance for chaining.
	 */
	public BinaryBuffer putLong(long l) {
		if (mode != Mode.WRITE) {
			throw new RuntimeException("Buffer is currently in read mode!");
		}
		subBuffer.writeLong(l);
		return this;
	}

	/**
	 * Put an rs2 string into the sub buffer
	 * 
	 * @param string
	 *            The string to insert
	 * @return The instance for chaining.
	 */
	public BinaryBuffer putString(String string) {
		if (mode != Mode.WRITE) {
			throw new RuntimeException("Buffer is currently in read mode!");
		}
		for (char c : string.toCharArray()) {
			subBuffer.writeByte((byte) c);
		}
		subBuffer.writeByte((byte) 10);
		return this;
	}

	/**
	 * Get the next data storage block
	 */
	public BinaryPart nextPart() {
		if (mode != Mode.READ) {
			throw new RuntimeException("Buffer is currently in write mode!");
		}
		if (mainBuffer.readableBytes() >= 5) {
			int opcode = mainBuffer.readByte();
			if (opcode != -1) {
				int length = mainBuffer.readInt();
				byte[] data = new byte[length];
				mainBuffer.readBytes(data);
				return new BinaryPart(opcode, ByteBuffer.wrap(data));
			}
		}
		return null;
	}

	/**
	 * Check if we have enough data to read another part from the buffer
	 * 
	 * @return true, if we have more than 3 bytes
	 */
	public boolean hasNextPart() {
		if (mode != Mode.READ) {
			throw new RuntimeException("Buffer is currently in write mode!");
		}
		return mainBuffer.readableBytes() >= 5;
	}

	/**
	 * Return the buffer that we can read from to write to the file.
	 * 
	 * @return The finished buffer.
	 */
	public ChannelBuffer toChannelBuffer() {
		return mainBuffer;
	}

	/**
	 * Return the buffer as a bytebuffer
	 * 
	 * @return The buffer
	 */
	public ByteBuffer toByteBuffer() {
		return mainBuffer.toByteBuffer();
	}
}
