package org.saleen.rs2.net;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.Timer;

/*
 * A modified ReadTimeoutHandler which modifies the behaviour when a ReadTimeout occurs.
 * Instead of throwing an exception the channel is closed and a message is printed for debugging.
 */
public class ModifiedReadTimeoutHandler extends ReadTimeoutHandler {

	public ModifiedReadTimeoutHandler(Timer timer, int timeoutSeconds) {
		super(timer, timeoutSeconds);
	}

	@Override
	public void readTimedOut(ChannelHandlerContext ctx) throws Exception {
		ctx.getChannel().close();
	}

}
