package com.maple.heartbeat.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 链路状态控制
 * 1. 读写超时处理
 * 2. 连接数监控
 * 3. 流量监控s
 *
 * @author ever
 * @date 2018-05-29
 */
@ChannelHandler.Sharable
public class SoaLinkStateHandler extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(SoaLinkStateHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf pros = Unpooled.copiedBuffer((ByteBuf) msg);
        try {
            int length = pros.readInt();
            if (length == 0) {
                logger.info("来自客户端的心跳连接. msg:{}", length);
                ctx.writeAndFlush(ctx.alloc().buffer(1).writeInt(0));
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

                logger.info(getClass().getName() + "::写超时，发送心跳包:" + ctx.channel());

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
