package com.maple.unpack.server.handler;

import com.maple.unpack.common.util.DumpUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author maple 2018.09.07 上午9:40
 */
public class RpcLogHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcLogHandler.class);


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("Dump消息内容: {}", DumpUtil.dumpToStr((ByteBuf) msg));
        super.channelRead(ctx, msg);
    }
}
