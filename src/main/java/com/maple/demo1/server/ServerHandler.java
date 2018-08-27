package com.maple.demo1.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Random;

/**
 * 描述:
 *
 * @author hz.lei
 * @date 2018年04月18日 上午12:17
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server channelRead..");
        System.out.println(ctx.channel().remoteAddress() + "->Server :" + msg.toString());
        int i = new Random().nextInt(5);

        if (i < 3) {
            throw new RuntimeException("serverHandler throw Exception: " + i);
        }
        ctx.write("server write" + msg);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("[ServerHandler]:" + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
