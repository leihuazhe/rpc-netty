package com.maple.demo3.server;

import com.maple.demo3.server.handler.ServerHandler;
import com.maple.demo3.server.handler.SoaLinkStateHandler;
import io.netty.bootstrap.ServerBootstrap;
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
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            SoaLinkStateHandler linkStateHandler = new SoaLinkStateHandler();

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
                            ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(20, 0, 0));
                            ch.pipeline().addLast("linkStateHandler", new SoaLinkStateHandler());
                            ch.pipeline().addLast("decoder", new StringDecoder());
                            ch.pipeline().addLast("encoder", new StringEncoder());
                            ch.pipeline().addLast(new ServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

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


