package com.maple.demo.server.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.maple.demo.entity.RpcObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 描述:
 *
 * @author hz.lei
 * @date 2018年04月18日 上午12:17
 */
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {
  private final Gson gson = new Gson();


  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    log.info("remote server {}, channelRead, msg: ========= \n {} \n", ctx.channel().remoteAddress(), msg);

    RpcObject request;
    try {
      request = gson.fromJson((String) msg, RpcObject.class);
    } catch (JsonSyntaxException e) {
      request = RpcObject.builder()
          .seqId(ThreadLocalRandom.current().nextInt(100))
          .message(String.valueOf(msg))
          .build();
    }
    String respPrefix = "Thanks for request the [Nebula Netty Server], your payload is:\n";
    RpcObject rpcResponse = new RpcObject(request.getSeqId(), respPrefix + request.getMessage());
    //注意序列化和编解码
    String resp = gson.toJson(rpcResponse);
    ctx.writeAndFlush(resp);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    log.error("[ServerHandler]: {}", cause.getMessage(), cause);
    ctx.close();
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    log.info("与客户端的channel断开, channel id:{},address:{}.", ctx.channel().id(), ctx.channel().remoteAddress());
    super.channelInactive(ctx);
  }
}
