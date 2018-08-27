package com.maple.demo1.client;

import com.maple.demo1.server.AttributeMapConstant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;

import java.util.Date;

/**
 * 描述:
 *
 * @author hz.lei
 * @date 2018年04月18日 上午12:25
 */
public class ClientHandler2 extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Attribute<NettyChannel> attr = ctx.attr(AttributeMapConstant.NETTY_CHANNEL_KEY);
        NettyChannel nChannel = attr.get();
        if (nChannel == null) {
            NettyChannel newNChannel = new NettyChannel("ClientHandler2", new Date());
            nChannel = attr.setIfAbsent(newNChannel);
        } else {
            System.out.println("ClientHandler2 => channelActive attributeMap 中是有值的");
            System.out.println("ClientHandler2 =>    " + nChannel.getName() + "=======" + nChannel.getCreateDate());
        }
        System.out.println("ClientHandler2 ...  Active");
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Attribute<NettyChannel> attr = ctx.attr(AttributeMapConstant.NETTY_CHANNEL_KEY);
        NettyChannel nChannel = attr.get();
        if (nChannel == null) {
            NettyChannel newNChannel = new NettyChannel("ClientHandler2", new Date());
            nChannel = attr.setIfAbsent(newNChannel);
        } else {
            System.out.println("channelRead attributeMap 中是有值的");
            System.out.println(nChannel.getName() + "=======" + nChannel.getCreateDate());
        }
        System.out.println("ClientHandler2 read Message:   " + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ClientHandler2 已经被添加到了 ch");
    }
}
