package com.maple.unpack.server.handler;

import com.google.gson.Gson;
import com.maple.unpack.common.entity.RpcObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * 描述:
 *
 * @author hz.lei
 * @date 2018年04月18日 上午12:17
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(ServerHandler.class);



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("remote server {}, channelRead, msg:{}", ctx.channel().remoteAddress(), msg);

        int i = new Random().nextInt(10);

       /* if (i > 8) {
            throw new RuntimeException("故意异常");
        }*/
        RpcObject request = (RpcObject) msg;

        RpcObject rpcResponse = new RpcObject(request.getSeqId(), "处理成功,返回结果: " + request.getSeqId());

        ctx.writeAndFlush(rpcResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("[ServerHandler] exceptionCaught :" + cause.getMessage(), cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        logger.info("与客户端的channel断开,channel: {} ip: {}", ctx.channel().id(), ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }
}
