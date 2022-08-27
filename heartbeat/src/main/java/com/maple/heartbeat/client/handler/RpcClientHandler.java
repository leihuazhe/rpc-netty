package com.maple.heartbeat.client.handler;

import com.google.gson.Gson;
import com.maple.heartbeat.entity.RpcObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * @author maple 2018.08.26 23:03
 */
@Slf4j
public class RpcClientHandler extends ChannelInboundHandlerAdapter {
  private final Gson gson = new Gson();

  private final CallBack callBack;


  public interface CallBack {
    void onSuccess(RpcObject msg) throws ExecutionException;
  }

  public RpcClientHandler(CallBack callBack) {
    this.callBack = callBack;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    if (msg.equals("0")) {
      // 心跳
      log.info("来自服务端的心跳响应!!!");
      return;
    }
    RpcObject response = gson.fromJson((String) msg, RpcObject.class);
    if (callBack != null)
      try {
        callBack.onSuccess(response);
      } catch (ExecutionException e) {
        log.error(e.getMessage(), e);
      }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    log.error(getClass().getSimpleName() + "::exceptionCaught: " + cause.getMessage(), cause);
    log.error(getClass().getSimpleName() + "::exceptionCaught, close channel:" + ctx.channel());
    ctx.close();
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    log.warn("channel掉线了,channel: {}", ctx.channel().id());
    //使用过程中断线重连
    super.channelInactive(ctx);
  }


}
