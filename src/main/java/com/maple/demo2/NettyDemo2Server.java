package com.maple.demo2;

import com.maple.demo1.server.ServerHandler;
import com.maple.demo1.server.ServerNoChildHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.nio.channels.spi.SelectorProvider;

/**
 * 描述: NettyDemo2Server
 *
 * @author hz.lei
 * @date 2018年04月18日 上午12:15
 */
public class NettyDemo2Server {

    private int port;

    public NettyDemo2Server(int port) {
        this.port = port;
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(bossGroup, workerGroup)
                    .channelFactory(new ChannelFactory<NioServerSocketChannel>() {
                        public NioServerSocketChannel newChannel() {
                            return new NioServerSocketChannel(SelectorProvider.provider());
                        }
                    })

                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .localAddress(new InetSocketAddress(port))
                    .childAttr(AttributeKey.newInstance("childAttr"), "mapleNetty")
                    //服务端启动过程中的逻辑
                    .handler(new ServerNoChildHandler())

                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("decoder", new StringDecoder());
                            ch.pipeline().addLast("encoder", new StringEncoder());
                            ch.pipeline().addLast(new ServerHandler());
                        }

                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 绑定端口，开始接收进来的连接

            ChannelFuture future = bootstrap.bind(port).sync();

            System.out.println("Server start listen at " + port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new NettyDemo2Server(port).start();
    }
}


