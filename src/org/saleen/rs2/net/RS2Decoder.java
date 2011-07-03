package org.saleen.rs2.net;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.saleen.rs2.Constants;
import org.saleen.rs2.model.World;
import org.saleen.rs2.net.Packet.Type;

/**
 * Game protocol decoding class.
 * 
 * @author Graham Edgecombe
 * @author Nikki
 * 
 */
public class RS2Decoder extends FrameDecoder {

	/**
	 * The cached opcode
	 */
	private int opcode = -1;

	/**
	 * The cached size
	 */
	private int size = -1;

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer) throws Exception {
		/*
		 * Fetch the ISAAC cipher for this session.
		 */
		ISAACCipher inCipher = World.getWorld().getChannelStorage()
				.get(channel).getInCipher();

		/*
		 * If the opcode is not present.
		 */
		if (opcode == -1) {
			/*
			 * Check if it can be read.
			 */
			if (buffer.readableBytes() >= 1) {
				/*
				 * Read and decrypt the opcode.
				 */
				opcode = buffer.readByte() & 0xFF;
				opcode = (opcode - inCipher.getNextValue()) & 0xFF;

				/*
				 * Find the packet size.
				 */
				size = Constants.PACKET_SIZES[opcode];
			} else {
				/*
				 * We need to wait for more data.
				 */
				return null;
			}
		}

		/*
		 * If the packet is variable-length.
		 */
		if (size == -1) {
			/*
			 * Check if the size can be read.
			 */
			if (buffer.readableBytes() >= 1) {
				/*
				 * Read the packet size and cache it.
				 */
				size = buffer.readByte() & 0xFF;
			} else {
				/*
				 * We need to wait for more data.
				 */
				return null;
			}
		}

		/*
		 * If the packet payload (data) can be read.
		 */
		if (buffer.readableBytes() >= size) {
			/*
			 * Read it.
			 */
			byte[] data = new byte[size];
			buffer.readBytes(data);
			ChannelBuffer payload = ChannelBuffers.buffer(size);
			payload.writeBytes(data);

			/*
			 * Produce and write the packet object.
			 */
			try {
				return new Packet(opcode, Type.FIXED, payload);
			} finally {
				opcode = -1;
				size = -1;
			}
		}

		/*
		 * We need to wait for more data.
		 */
		return null;
	}

}
