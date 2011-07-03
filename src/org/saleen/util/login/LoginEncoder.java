package org.saleen.util.login;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * Login protocol encoding class.
 * 
 * @author Graham Edgecombe
 * 
 */
public class LoginEncoder extends OneToOneEncoder {

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object object) throws Exception {
		LoginPacket packet = (LoginPacket) object;
		ChannelBuffer buf = ChannelBuffers.buffer(1 + 2 + packet.getLength());
		buf.writeByte((byte) packet.getOpcode());
		buf.writeShort((short) packet.getLength());
		buf.writeBytes(packet.getPayload());
		return buf;
	}

}
