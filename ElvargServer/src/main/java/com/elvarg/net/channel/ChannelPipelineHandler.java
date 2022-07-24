package com.elvarg.net.channel;

import com.elvarg.net.NetworkConstants;
import com.elvarg.net.PlayerSession;
import com.elvarg.net.codec.LoginDecoder;
import com.elvarg.net.codec.LoginEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Handles a channels events..
 *
 * @author Swiffy
 */
public class ChannelPipelineHandler extends ChannelInitializer<SocketChannel> {

    /**
     * The part of the pipeline that limits connections, and checks for any banned hosts.
     */
    private ChannelFilter filter;

    /**
     * The part of the pipeline that handles exceptions caught, channels being read, in-active
     * channels, and channel triggered events.
     */
    private ChannelEventHandler handler;


    /**
     * Creates the service pipeline factory.
     */
    public ChannelPipelineHandler(ChannelEventHandler handler, ChannelFilter filter) {
        this.handler = handler;
        this.filter = filter;
    }



    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        final ChannelPipeline pipeline = channel.pipeline();

        channel.attr(NetworkConstants.SESSION_KEY).setIfAbsent(new PlayerSession(channel));

        pipeline.addLast("channel-filter", filter);
        pipeline.addLast("decoder", new LoginDecoder());
        pipeline.addLast("encoder", new LoginEncoder());
        pipeline.addLast("timeout", new IdleStateHandler(NetworkConstants.SESSION_TIMEOUT, 0, 0));
        pipeline.addLast("channel-handler", handler);
    }
}
