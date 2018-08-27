package com.maple.demo1.client;

import com.maple.demo1.server.AttributeMapConstant;
import io.netty.channel.ChannelHandler;
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
public class ClientHandler1 extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Attribute<NettyChannel> attr = ctx.attr(AttributeMapConstant.NETTY_CHANNEL_KEY);
        NettyChannel nChannel = attr.get();
        if (nChannel == null) {
            NettyChannel newNChannel = new NettyChannel(" 你好，我在active设置的初始值 ", new Date());
            nChannel = attr.setIfAbsent(newNChannel);
        } else {
            System.out.println("ClientHandler2 =>   channelActive attributeMap 中是有值的");
            System.out.println("ClientHandler2 =>   " + nChannel.getName() + " ======= " + nChannel.getCreateDate());
        }
        System.out.println(" ClientLogHandler ...  Active");

        /**
         * 触发对下一个 ChannelInboundHandler 上的 channelActive()方法(已连接)的调用
         */
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Attribute<NettyChannel> attr = ctx.attr(AttributeMapConstant.NETTY_CHANNEL_KEY);
        NettyChannel nChannel = attr.get();
        if (nChannel == null) {
            NettyChannel newNChannel = new NettyChannel(" 你好，我在read设置的初始值 ", new Date());
            nChannel = attr.setIfAbsent(newNChannel);
        } else {
            System.out.println("channelRead attributeMap 中是有值的");
            System.out.println(nChannel.getName() + "=======" + nChannel.getCreateDate());
        }
        System.out.println("ClientLogHandler read Message:   " + msg);


        ChannelHandler handler = ctx.handler();
        System.out.println(handler == this);

        /**
         * 触发对下一个 ChannelInboundHandler 上的 channelRead()方法(已接收的消息)的调用
         */
        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ClientLogHandler 已经被添加到了 ch");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("userEventTriggered变动" + evt);
    }
}
