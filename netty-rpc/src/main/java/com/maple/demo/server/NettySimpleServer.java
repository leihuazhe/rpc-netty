package com.maple.demo.server;

import com.maple.demo.server.handler.ServerHandler;
import com.maple.demo.server.handler.SoaLinkStateHandler;
import com.maple.rpc.common.util.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 描述:
 *
 * @author hz.lei
 * @date 2018年04月18日 上午12:15
 */
public class NettySimpleServer {

    private static Logger logger = LoggerFactory.getLogger(NettySimpleServer.class);

    private int port;

    public NettySimpleServer(int port) {
        this.port = port;
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("netty-server-boss-group", Boolean.TRUE));
        EventLoopGroup workerGroup = new NioEventLoopGroup(Constants.DEFAULT_IO_THREADS, new DefaultThreadFactory("netty-server-work-group", Boolean.TRUE));
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
//                    .childOption(ChannelOption.TCP_NODELAY, true)
//                    .localAddress(new InetSocketAddress(port))
//                    .childAttr(AttributeKey.newInstance("childAttr"), "mapleNetty")
                    //服务端启动过程中的逻辑
//                    .handler(new ServerNoChildHandler())

                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(10, 0, 0));
                            ch.pipeline().addLast("linkStateHandler", new SoaLinkStateHandler());
                            ch.pipeline().addLast("decoder", new StringDecoder());
                            ch.pipeline().addLast("encoder", new StringEncoder());
                            ch.pipeline().addLast(new ServerHandler());
                        }
                    })

                    /**
                     * BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，Java将使用默认值50。
                     */
                    .option(ChannelOption.SO_BACKLOG, 1024)

                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);


            // 绑定端口，开始接收进来的连接
            ChannelFuture future = bootstrap.bind(port).sync();

            logger.info("Server start listen at " + port);
            future.channel().closeFuture().sync();

        } catch (Exception e) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


}


