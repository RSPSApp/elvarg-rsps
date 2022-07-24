package com.elvarg.net.websocket;

import com.elvarg.net.NetworkConstants;
import com.elvarg.net.PlayerSession;
import com.elvarg.net.channel.ChannelEventHandler;
import com.elvarg.net.channel.ChannelFilter;
import com.elvarg.net.codec.LoginDecoder;
import com.elvarg.net.codec.LoginEncoder;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;
        /*
        import org.apollo.net.codec.handshake.HandshakeDecoder;
        import org.apollo.net.websocket.ByteBufToWebSockBufferEncoder;
        import org.apollo.net.websocket.HttpServerHandler;
        import org.apollo.net.websocket.WebSockBufferToByteBufDecoder;
        import org.apollo.net.websocket.WebsocketSender;
*/
/**
 * A {@link ChannelInitializer} for the service pipeline.
 *
 * @author Graham
 */
public final class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * The network event handler.
     */
    private ChannelEventHandler handler;

    /**
     * The part of the pipeline that limits connections, and checks for any banned hosts.
     */
    private ChannelFilter filter;

    /**
     * Creates the service pipeline factory.
     */
    public WebSocketChannelInitializer(ChannelEventHandler handler, ChannelFilter filter) {
        this.handler = handler;
        this.filter = filter;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        ch.attr(NetworkConstants.SESSION_KEY).setIfAbsent(new PlayerSession(ch));

        pipeline.addLast("httpServerCodec", new HttpServerCodec());
        pipeline.addLast("httpHandler", new HttpServerHandler());

        pipeline.addLast("channel-filter", filter);
        pipeline.addLast("decoder", new LoginDecoder());
        pipeline.addLast("encoder", new LoginEncoder());
        pipeline.addLast("timeout", new IdleStateHandler(NetworkConstants.SESSION_TIMEOUT, 0, 0));
        pipeline.addLast("channel-handler", handler);

        //pipeline.addLast("handshakeDecoder", new HandshakeDecoder());
        //pipeline.addLast("timeout", new IdleStateHandler(NetworkConstants.IDLE_TIME, 0, 0));
        //pipeline.addLast("handler", handler);

        pipeline.addLast("byteBufToWebsocketEncoder", new ByteBufToWebSockBufferEncoder());
//		pipeline.addBefore("byteBufToWebsocketEncoder", "websocketEncoder", new WebSocket13FrameEncoder(false));
//		pipeline.addLast("websocketSender", new WebsocketSender());
    }

}