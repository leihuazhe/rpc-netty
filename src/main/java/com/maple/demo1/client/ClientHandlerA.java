package com.maple.demo1.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 描述:
 *
 * @author hz.lei
 * @date 2018年04月18日 上午12:25
 */
public class ClientHandlerA extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println(" ClientHandlerA ...  Active");

        /**
         * 触发对下一个 ChannelInboundHandler 上的 channelActive()方法(已连接)的调用
         */
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        System.out.println("ClientHandlerA read Message:   " + msg);

        ChannelHandler handler = ctx.handler();
        System.out.println(handler == this);

        /**
         * 触发对下一个 ChannelInboundHandler 上的 channelRead()方法(已接收的消息)的调用
         */
        // 如果我把这条消息，向后传递，我使用这个，如果我向换，使用其他的. writeAndFlush
//        ctx.fireChannelRead(msg);
        ctx.writeAndFlush(" 我是 ClientHandlerA ，我修改了消息内容，原内容 " + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ClientHandlerA 已经被添加到了 ch");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("userEventTriggered变动" + evt);
    }
}
