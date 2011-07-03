package org.saleen.rs2.net;

import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.MessageEvent;

public class ThroughputCounter implements ChannelUpstreamHandler,
		ChannelDownstreamHandler {

	/**
	 * The amount of bytes read
	 */
	private static final AtomicLong receivedBytes = new AtomicLong();

	/**
	 * The amount of bytes sent
	 */
	private static final AtomicLong writtenBytes = new AtomicLong();

	@Override
	public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e)
			throws Exception {
		if (e instanceof MessageEvent) {
			Object msg = ((MessageEvent) e).getMessage();
			if (msg instanceof ChannelBuffer) {
				writtenBytes.addAndGet(((ChannelBuffer) msg).readableBytes());
			}
		}
		ctx.sendDownstream(e);
	}

	@Override
	public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e)
			throws Exception {
		if (e instanceof MessageEvent) {
			Object msg = ((MessageEvent) e).getMessage();
			if (msg instanceof ChannelBuffer) {
				receivedBytes.addAndGet(((ChannelBuffer) msg).readableBytes());
			} else if (msg instanceof Packet) {
				Packet packet = (Packet) msg;
				int length = packet.getLength();
				switch (packet.getType()) {
				case VARIABLE:
					length += 1;
					break;
				case VARIABLE_SHORT:
					length += 2;
					break;
				}
				receivedBytes.addAndGet(length);
			}
		}
		ctx.sendUpstream(e);
	}

	/**
	 * Get the amount of written bytes
	 * 
	 * @return The amount, as a long
	 */
	public static long getWrittenBytes() {
		return writtenBytes.get();
	}

	/**
	 * Get the amount of read bytes
	 * 
	 * @return The amount, as a long
	 */
	public static long getReceivedBytes() {
		return receivedBytes.get();
	}

}
