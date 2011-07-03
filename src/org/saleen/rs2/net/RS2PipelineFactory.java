package org.saleen.rs2.net;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

/**
 * A factory which produces codecs for the RuneScape protocol.
 * 
 * @author Nikki
 * 
 */
public class RS2PipelineFactory implements ChannelPipelineFactory {

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("counter", new ThroughputCounter());
		pipeline.addLast("protocolDecoder", new RS2LoginDecoder());
		pipeline.addLast("protocolEncoder", new RS2Encoder());
		pipeline.addLast("handler", new ConnectionHandler());
		return pipeline;
	}
}
