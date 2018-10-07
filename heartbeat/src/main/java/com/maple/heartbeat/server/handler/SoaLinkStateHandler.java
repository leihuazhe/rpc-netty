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
            //回应？ 服务端不给客户端回应。
            if (length == 0) {
                logger.info("来自客户端的心跳连接. msg:{}", length);
//                ctx.writeAndFlush(ctx.alloc().buffer(1).writeInt(0));
                return;
            }
        } finally {
            ReferenceCountUtil.release(pros);
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //IdleStateHandler(10, 0, 0));
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;

            if (e.state() == IdleState.READER_IDLE) {
                logger.warn("服务端读超时,关闭连接, channel: {}", ctx.channel());
                ctx.close();
                //writerIdleTimeSeconds 设置为0
                // 服务端禁用了写超时，就是不回写给客户端,客户端不会了解服务端状况。
            } else if (e.state() == IdleState.WRITER_IDLE) {
                ctx.writeAndFlush(ctx.alloc().buffer(1).writeInt(0));
                logger.warn("服务端写超时，发送心跳包给客户端, channel: {}", ctx.channel());

            } else if (e.state() == IdleState.ALL_IDLE) {
                logger.warn("服务端 读写都超时,关闭连接, channel: {}", ctx.channel());

            }
        }

    }
}
