package com.maple.demo3.client.handler;

import com.maple.demo3.client.NettyChannel;
import com.maple.demo3.server.AttributeMapConstant;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 描述:
 *
 * @author hz.lei
 * @date 2018年04月18日 上午12:25
 */
public class ClientLogHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(ClientLogHandler.class);


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Attribute<NettyChannel> attr = ctx.attr(AttributeMapConstant.NETTY_CHANNEL_KEY);
        NettyChannel nChannel = attr.get();
        if (nChannel == null) {
            NettyChannel newNChannel = new NettyChannel(" 你好，我在read设置的初始值 ", new Date());
            nChannel = attr.setIfAbsent(newNChannel);
        } else {
            logger.info("channelRead attributeMap 中是有值的");
            logger.info(nChannel.getName() + "=======" + nChannel.getCreateDate());
        }
        logger.info("ClientLogHandler read Message:   " + msg);


        ChannelHandler handler = ctx.handler();

        logger.info("handler是否为自己? {}", handler == this);

        ctx.fireChannelRead(msg);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info("ClientLogHandler 已经被添加到了 ch");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        logger.info("userEventTriggered变动" + evt);
    }
}
