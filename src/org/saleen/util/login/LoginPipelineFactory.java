package org.saleen.util.login;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

/**
 * A factory which produces codecs for the login server protocol.
 * 
 * @author Graham Edgecombe
 * 
 */
public class LoginPipelineFactory implements ChannelPipelineFactory {

	private ChannelHandler handler;

	public LoginPipelineFactory(ChannelHandler handler) {
		this.handler = handler;
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("protocolEncoder", new LoginEncoder());
		pipeline.addLast("protocolDecoder", new LoginDecoder());
		pipeline.addLast("handler", handler);
		return pipeline;
	}
}
