package com.maple.demo.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 链路状态控制
 * 1. 读写超时处理
 * 2. 连接数监控
 * 3. 流量监控s
 *
 * @author ever
 * @date 2018-05-29
 */
@Slf4j
@ChannelHandler.Sharable
public class SoaLinkStateHandler extends ChannelDuplexHandler {
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf pros = Unpooled.copiedBuffer((ByteBuf) msg);
    try {
      int length = pros.readInt();
      if (length == 0) {
        log.info("来自客户端的心跳连接. msg:{}", length);
        ctx.writeAndFlush(ctx.alloc().buffer(1).writeInt(0));
        return;
      }
    } catch (Exception e) {
      log.warn("Unexpected error: {}", e.getMessage());
    } finally {
      ReferenceCountUtil.release(pros);
    }
    super.channelRead(ctx, msg);
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

    if (evt instanceof IdleStateEvent) {
      IdleStateEvent e = (IdleStateEvent) evt;

      switch (e.state()) {
        case READER_IDLE:
          ctx.close();
          log.info("读超时，关闭连接: " + ctx.channel());
          break;
        case WRITER_IDLE:
          ctx.writeAndFlush(ctx.alloc().buffer(1).writeInt(0));
          log.info("写超时，发送心跳包完成: " + ctx.channel());
          break;
        case ALL_IDLE:
          log.info("读写都超时，发送心跳包:" + ctx.channel());
          break;
        default:
          break;
      }
    }
  }
}
