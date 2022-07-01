package com.elvarg.net;

import com.elvarg.net.channel.ChannelEventHandler;
import com.elvarg.net.channel.ChannelFilter;
import com.elvarg.net.channel.ChannelPipelineHandler;
import com.elvarg.net.websocket.HttpChannelInitializer;
import com.elvarg.net.websocket.WebSocketChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * The network builder for the Runescape #317 protocol. This class is used to
 * start and configure the {@link ServerBootstrap} that will control and manage
 * the entire network.
 *
 * @author lare96 <http://github.com/lare96>
 */
public final class NetworkBuilder {

    /**
     * The bootstrap that will oversee the management of the entire network.
     */
    private final ServerBootstrap bootstrap = new ServerBootstrap();

    /**
     * The bootstrap that will oversee the management of the entire network.
     */
    private final ServerBootstrap websocketBootstrap = new ServerBootstrap();

    /**
     * The {@link ServerBootstrap} for the HTTP listener.
     */
    private final ServerBootstrap httpBootstrap = new ServerBootstrap();

    /**
     * The event loop group that will be attached to the bootstrap.
     */
    private final EventLoopGroup loopGroup = new NioEventLoopGroup();


    /**
     * The network event handler.
     */
    private final ChannelEventHandler handler = new ChannelEventHandler();

    /**
     * The part of the pipeline that limits connections, and checks for any banned hosts.
     */
    private final ChannelFilter filter = new ChannelFilter();


    /**
     * The {@link ChannelInitializer} that will determine how channels will be
     * initialized when registered to the event loop group.
     */
    private final ChannelInitializer<SocketChannel> channelInitializer = new ChannelPipelineHandler(handler, filter);

    /**
     * The {@link ChannelInitializer} that will determine how channels will be
     * initialized when registered to the event loop group for Websockets.
     */
    private final ChannelInitializer<SocketChannel> websocketChannelInitializer = new WebSocketChannelInitializer(handler, filter);

    /**
     * Initializes this network handler effectively preparing the server to
     * listen for connections and handle network events.
     *
     * @param port the port that this network will be bound to.
     * @throws Exception if any issues occur while starting the network.
     */
    public void initialize(int port) throws IOException, InterruptedException {
        ResourceLeakDetector.setLevel(Level.DISABLED);
        bootstrap.group(loopGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(NetworkConstants.WEBSOCKETS ? websocketChannelInitializer : channelInitializer);
        //bootstrap.bind(port).syncUninterruptibly();

        SocketAddress socketAddress = new InetSocketAddress(NetworkConstants.GAME_PORT);
        this.bind(bootstrap, socketAddress);

        /*
        websocketBootstrap.group(loopGroup);

        //SocketAddress http = new InetSocketAddress(NetworkConstants.HTTP_PORT);
        //httpBootstrap.bind(http).sync();



        if (NetworkConstants.WEBSOCKETS) {
            websocketBootstrap.channel(NioServerSocketChannel.class);
            websocketBootstrap.childHandler(websocketChannelInitializer);
            websocketBootstrap.bind(port).syncUninterruptibly();
        } else {
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(channelInitializer);
            bootstrap.bind(port).syncUninterruptibly();
        }

        ChannelInitializer<SocketChannel> http = new HttpChannelInitializer(handler);
        httpBootstrap.channel(NioServerSocketChannel.class);
        httpBootstrap.childHandler(http);

         */
    }

    /**
     * Attempts to bind the specified ServerBootstrap to the specified SocketAddress.
     *
     * @param bootstrap The ServerBootstrap.
     * @param address   The SocketAddress.
     * @throws IOException If the ServerBootstrap fails to bind to the SocketAddress.
     */
    private void bind(ServerBootstrap bootstrap, SocketAddress address) throws IOException {
        try {
            bootstrap.bind(address).sync();
        } catch (Exception cause) {
            throw new IOException("Failed to bind to " + address, cause);
        }
    }
}
