package org.saleen.util.login;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * Login protocol decoding class.
 * 
 * @author Graham Edgecombe
 * 
 */
public class LoginDecoder extends FrameDecoder {

	/**
	 * The current opcode.
	 */
	private int opcode = -1;

	/**
	 * The current length.
	 */
	private int length = -1;

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer in) throws Exception {
		if (opcode == -1) {
			if (in.readableBytes() >= 1) {
				opcode = in.readUnsignedByte();
			} else {
				return null;
			}
		}
		if (length == -1) {
			if (in.readableBytes() >= 2) {
				length = in.readShort();
			} else {
				return null;
			}
		}
		if (in.readableBytes() >= length) {
			byte[] payload = new byte[length];
			in.readBytes(payload);
			try {
				return new LoginPacket(opcode,
						ChannelBuffers.wrappedBuffer(payload));
			} finally {
				opcode = -1;
				length = -1;
			}
		} else {
			return null;
		}
	}
}
