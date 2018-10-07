package com.maple.heartbeat.client.handler;

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
        // IdleStateHandler(25, 15, 0));
        //读超时 25s， 写超时 15s
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            // 客户端不应该设置读超时。客户端不需要知道服务端的状态？
            if (e.state() == IdleState.READER_IDLE) {
                logger.warn("客户端读超时,关闭连接, channel: {}", ctx.channel());
//                ctx.close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
                logger.warn("客户端写超时，发送心跳包给服务端, channel: {}", ctx.channel());
                ctx.writeAndFlush(ctx.alloc().buffer(1).writeInt(0));

            } else if (e.state() == IdleState.ALL_IDLE) {
                logger.warn("客户端 读写都超时,关闭连接, channel: {}", ctx.channel());
            }
        }


    }
}
