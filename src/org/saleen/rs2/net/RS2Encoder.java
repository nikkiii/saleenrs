package org.saleen.rs2.net;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.saleen.rs2.model.World;

/**
 * Game protocol encoding class.
 * 
 * @author Graham Edgecombe
 * 
 */
public class RS2Encoder extends OneToOneEncoder {

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object object) throws Exception {
		Packet p = (Packet) object;

		/*
		 * Check what type the packet is.
		 */
		if (p.isRaw()) {
			/*
			 * If the packet is raw, send its payload.
			 */
			return p.getPayload();
		} else {
			/*
			 * If not, get the out ISAAC cipher.
			 */
			ISAACCipher outCipher = World.getWorld().getChannelStorage()
					.get(channel).getOutCipher();

			/*
			 * Get the packet attributes.
			 */
			int opcode = p.getOpcode();
			Packet.Type type = p.getType();
			int length = p.getLength();

			/*
			 * Encrypt the packet opcode.
			 */
			opcode += outCipher.getNextValue();

			/*
			 * Compute the required size for the buffer.
			 */
			int finalLength = length + 1;
			switch (type) {
			case VARIABLE:
				finalLength += 1;
				break;
			case VARIABLE_SHORT:
				finalLength += 2;
				break;
			}

			/*
			 * Create the buffer and write the opcode (and length if the packet
			 * is variable-length).
			 */
			ChannelBuffer buffer = ChannelBuffers.buffer(finalLength);
			buffer.writeByte((byte) opcode);
			switch (type) {
			case VARIABLE:
				buffer.writeByte((byte) length);
				break;
			case VARIABLE_SHORT:
				buffer.writeShort((short) length);
				break;
			}

			/*
			 * Write the payload itself.
			 */
			buffer.writeBytes(p.getPayload());

			/*
			 * Flip and dispatch the packet.
			 */
			return buffer;
		}
	}

}
