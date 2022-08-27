package com.maple.heartbeat.server.handler;

import com.google.gson.Gson;
import com.maple.heartbeat.entity.RpcObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

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
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    log.info("remote server {}, channelRead, msg:{}", ctx.channel().remoteAddress(), msg);

    int i = new Random().nextInt(10);

       /* if (i > 8) {
            throw new RuntimeException("故意异常");
        }*/


    RpcObject request = gson.fromJson((String) msg, RpcObject.class);
    RpcObject rpcResponse = new RpcObject(request.getSeqId(), "处理成功,返回结果: " + request.getSeqId());

    //注意序列化和编解码
    String resp = gson.toJson(rpcResponse);
    ctx.writeAndFlush(resp);

  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    log.error("[ServerHandler]:" + cause.getMessage(), cause);
    ctx.close();
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    log.info("与客户端的channel断开,channel:" + ctx.channel().id());
    super.channelInactive(ctx);
  }
}
