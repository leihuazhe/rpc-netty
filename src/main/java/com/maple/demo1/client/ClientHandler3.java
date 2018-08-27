package com.maple.demo1.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * 描述:
 *
 * @author hz.lei
 * @date 2018年04月18日 上午1:05
 */
public class ClientHandler3 extends ChannelOutboundHandlerAdapter {



    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("ClientHandler3 msg:  " + msg);
        System.out.println("xxxx  ClientHandler3");
        super.write(ctx, msg, promise);
    }
}
