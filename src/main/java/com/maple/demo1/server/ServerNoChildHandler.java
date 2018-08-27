package com.maple.demo1.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * desc: ServerNoChildHandler
 *
 * @author hz.lei
 * @since 2018年08月20日 下午10:01
 */
public class ServerNoChildHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info("handlerAdded :{}");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("handlerRemoved :{}");

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelRegistered :{}");
    }
}
