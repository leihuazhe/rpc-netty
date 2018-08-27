package com.maple.demo3.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author maple 2018.08.27 16:25
 */
public class SoaIdleHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SoaIdleHandler.class);

    /**
     * 不能release两次
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf pros = Unpooled.copiedBuffer((ByteBuf) msg);
        try {
            int length = pros.readInt();
            if (length == 0) {
                logger.info("来自服务端的心跳连接. msg:{}", length);
                return;
            }
        } finally {
            ReferenceCountUtil.release(pros);
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;

            if (e.state() == IdleState.READER_IDLE) {
                ctx.close();
                logger.info(getClass().getName() + "::读超时，关闭连接:" + ctx.channel());

            } else if (e.state() == IdleState.WRITER_IDLE) {
                ctx.writeAndFlush(ctx.alloc().buffer(1).writeInt(0));
                if (logger.isDebugEnabled()) {
                    logger.debug(getClass().getName() + "::写超时，发送心跳包:" + ctx.channel());
                }

            } else if (e.state() == IdleState.ALL_IDLE) {
                if (logger.isDebugEnabled()) {
                    logger.debug(getClass().getName() + "::读写都超时，发送心跳包:" + ctx.channel());
                }
            }
        }

    }
}
