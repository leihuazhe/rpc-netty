package com.maple.demo1.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * 描述:
 * <p>
 * addLast ClientLogHandler
 * <p>
 * addLast ClientHandler2
 * <p>
 * result
 * <p>
 * <p>
 * c1  c2
 * <p>
 * ------------>  inbound
 *
 * @author hz.lei
 * @date 2018年04月18日 上午12:24
 */
public class NettySimpleClient {

    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8080"));
    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    public static void main(String[] args) throws Exception {
        initChannel();
    }


    public static void initChannel() throws InterruptedException {
        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast("decoder", new StringDecoder());
                            p.addLast("encoder", new StringEncoder());
                            p.addLast(new ClientHandler3());
                            p.addLast(new ClientHandlerA());
                            p.addLast(new ClientHandlerB());
                            p.addLast(new ClientHandler5());
                            p.addLast(new ClientHandler3());

                        }
                    });
            ChannelFuture future = b.connect(HOST, PORT).sync();
            future.channel().writeAndFlush("hello Netty,Test attributeMap");


            future.channel().writeAndFlush("dalao . . . . ");

            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }

    }
}
