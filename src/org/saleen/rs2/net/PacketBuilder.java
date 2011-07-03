package org.saleen.rs2.net;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.saleen.rs2.net.Packet.Type;

/**
 * A utility class for building packets.
 * 
 * @author Graham Edgecombe
 * 
 */
public class PacketBuilder {

	/**
	 * Bit mask array.
	 */
	public static final int[] BIT_MASK_OUT = new int[32];

	/**
	 * Creates the bit mask array.
	 */
	static {
		for (int i = 0; i < BIT_MASK_OUT.length; i++) {
			BIT_MASK_OUT[i] = (1 << i) - 1;
		}
	}

	/**
	 * The opcode.
	 */
	private int opcode;

	/**
	 * The type.
	 */
	private Type type;

	/**
	 * The payload.
	 */
	private ChannelBuffer payload = ChannelBuffers.dynamicBuffer();

	/**
	 * The current bit position.
	 */
	private int bitPosition;

	/**
	 * Creates a raw packet builder.
	 */
	public PacketBuilder() {
		this(-1);
	}

	/**
	 * Creates a fixed packet builder with the specified opcode.
	 * 
	 * @param opcode
	 *            The opcode.
	 */
	public PacketBuilder(int opcode) {
		this(opcode, Type.FIXED);
	}

	/**
	 * Creates a packet builder with the specified opcode and type.
	 * 
	 * @param opcode
	 *            The opcode.
	 * @param type
	 *            The type.
	 */
	public PacketBuilder(int opcode, Type type) {
		this.opcode = opcode;
		this.type = type;
	}

	/**
	 * Writes a byte.
	 * 
	 * @param b
	 *            The byte to write.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder put(byte b) {
		payload.writeByte(b);
		return this;
	}

	/**
	 * Writes an array of bytes.
	 * 
	 * @param b
	 *            The byte array.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder put(byte[] b) {
		payload.writeBytes(b);
		return this;
	}

	/**
	 * Writes a short.
	 * 
	 * @param s
	 *            The short.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putShort(int s) {
		payload.writeShort((short) s);
		return this;
	}

	/**
	 * Writes an integer.
	 * 
	 * @param i
	 *            The integer.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putInt(int i) {
		payload.writeInt(i);
		return this;
	}

	/**
	 * Writes a long.
	 * 
	 * @param l
	 *            The long.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putLong(long l) {
		payload.writeLong(l);
		return this;
	}

	/**
	 * Converts this PacketBuilder to a packet.
	 * 
	 * @return The Packet object.
	 */
	public Packet toPacket() {
		return new Packet(opcode, type, payload.copy());
	}

	/**
	 * Writes a RuneScape string.
	 * 
	 * @param string
	 *            The string to write.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putRS2String(String string) {
		payload.writeBytes(string.getBytes());
		payload.writeByte((byte) 10);
		return this;
	}

	/**
	 * Writes a type-A short.
	 * 
	 * @param val
	 *            The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putShortA(int val) {
		payload.writeByte((byte) (val >> 8));
		payload.writeByte((byte) (val + 128));
		return this;
	}

	/**
	 * Writes a little endian type-A short.
	 * 
	 * @param val
	 *            The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putLEShortA(int val) {
		payload.writeByte((byte) (val + 128));
		payload.writeByte((byte) (val >> 8));
		return this;
	}

	/**
	 * Checks if this packet builder is empty.
	 * 
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isEmpty() {
		return payload.writerIndex() == 0;
	}

	/**
	 * Starts bit access.
	 * 
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder startBitAccess() {
		bitPosition = payload.writerIndex() * 8;
		return this;
	}

	/**
	 * Finishes bit access.
	 * 
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder finishBitAccess() {
		payload.writerIndex((bitPosition + 7) / 8);
		return this;
	}

	/**
	 * Writes some bits.
	 * 
	 * @param numBits
	 *            The number of bits to write.
	 * @param value
	 *            The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putBits(int numBits, int value) {
		if (!payload.hasArray()) {
			throw new UnsupportedOperationException(
					"The ChannelBuffer implementation must support array() for bit usage.");
		}

		int bytes = (int) Math.ceil((double) numBits / 8D) + 1;
		payload.ensureWritableBytes((bitPosition + 7) / 8 + bytes);

		byte[] buffer = payload.array();

		int bytePos = bitPosition >> 3;
		int bitOffset = 8 - (bitPosition & 7);
		bitPosition += numBits;

		for (; numBits > bitOffset; bitOffset = 8) {
			buffer[bytePos] &= ~BIT_MASK_OUT[bitOffset];
			buffer[bytePos++] |= (value >> (numBits - bitOffset))
					& BIT_MASK_OUT[bitOffset];
			numBits -= bitOffset;
		}
		if (numBits == bitOffset) {
			buffer[bytePos] &= ~BIT_MASK_OUT[bitOffset];
			buffer[bytePos] |= value & BIT_MASK_OUT[bitOffset];
		} else {
			buffer[bytePos] &= ~(BIT_MASK_OUT[numBits] << (bitOffset - numBits));
			buffer[bytePos] |= (value & BIT_MASK_OUT[numBits]) << (bitOffset - numBits);
		}
		return this;
	}

	/**
	 * Puts an <code>IoBuffer</code>.
	 * 
	 * @param buf
	 *            The buffer.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder put(ChannelBuffer buf) {
		payload.writeBytes(buf);
		return this;
	}

	/**
	 * Writes a type-C byte.
	 * 
	 * @param val
	 *            The value to write.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putByteC(int val) {
		put((byte) (-val));
		return this;
	}

	/**
	 * Writes a little-endian short.
	 * 
	 * @param val
	 *            The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putLEShort(int val) {
		payload.writeByte((byte) (val));
		payload.writeByte((byte) (val >> 8));
		return this;
	}

	/**
	 * Writes a type-1 integer.
	 * 
	 * @param val
	 *            The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putInt1(int val) {
		payload.writeByte((byte) (val >> 8));
		payload.writeByte((byte) val);
		payload.writeByte((byte) (val >> 24));
		payload.writeByte((byte) (val >> 16));
		return this;
	}

	/**
	 * Writes a type-2 integer.
	 * 
	 * @param val
	 *            The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putInt2(int val) {
		payload.writeByte((byte) (val >> 16));
		payload.writeByte((byte) (val >> 24));
		payload.writeByte((byte) val);
		payload.writeByte((byte) (val >> 8));
		return this;
	}

	/**
	 * Writes a little-endian integer.
	 * 
	 * @param val
	 *            The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putLEInt(int val) {
		payload.writeByte((byte) (val));
		payload.writeByte((byte) (val >> 8));
		payload.writeByte((byte) (val >> 16));
		payload.writeByte((byte) (val >> 24));
		return this;
	}

	/**
	 * Puts a sequence of bytes in the buffer.
	 * 
	 * @param data
	 *            The bytes.
	 * @param offset
	 *            The offset.
	 * @param length
	 *            The length.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder put(byte[] data, int offset, int length) {
		payload.writeBytes(data, offset, length);
		return this;
	}

	/**
	 * Puts a type-A byte in the buffer.
	 * 
	 * @param val
	 *            The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putByteA(byte val) {
		payload.writeByte((byte) (val + 128));
		return this;
	}

	/**
	 * Puts a type-C byte in the buffer.
	 * 
	 * @param val
	 *            The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putByteC(byte val) {
		payload.writeByte((byte) (-val));
		return this;
	}

	/**
	 * Puts a type-S byte in the buffer.
	 * 
	 * @param val
	 *            The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putByteS(byte val) {
		payload.writeByte((byte) (128 - val));
		return this;
	}

	/**
	 * Puts a series of reversed bytes in the buffer.
	 * 
	 * @param is
	 *            The source byte array.
	 * @param offset
	 *            The offset.
	 * @param length
	 *            The length.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putReverse(byte[] is, int offset, int length) {
		for (int i = (offset + length - 1); i >= offset; i--) {
			payload.writeByte(is[i]);
		}
		return this;
	}

	/**
	 * Puts a series of reversed type-A bytes in the buffer.
	 * 
	 * @param is
	 *            The source byte array.
	 * @param offset
	 *            The offset.
	 * @param length
	 *            The length.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putReverseA(byte[] is, int offset, int length) {
		for (int i = (offset + length - 1); i >= offset; i--) {
			putByteA(is[i]);
		}
		return this;
	}

	/**
	 * Puts a 3-byte integer.
	 * 
	 * @param val
	 *            The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putTriByte(int val) {
		payload.writeByte((byte) (val >> 16));
		payload.writeByte((byte) (val >> 8));
		payload.writeByte((byte) val);
		return this;
	}

	/**
	 * Puts a byte or short.
	 * 
	 * @param val
	 *            The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putSmart(int val) {
		if (val >= 128) {
			putShort((val + 32768));
		} else {
			put((byte) val);
		}
		return this;
	}

	/**
	 * Puts a byte or short for signed use.
	 * 
	 * @param val
	 *            The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putSignedSmart(int val) {
		if (val >= 128) {
			putShort((val + 49152));
		} else {
			put((byte) (val + 64));
		}
		return this;
	}

}
