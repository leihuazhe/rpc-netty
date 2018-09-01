package com.maple.demo3.server.handler;

import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.maple.protobuf.RpcObjectOut;
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

    private final Gson gson = new Gson();


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        int i = new Random().nextInt(10);

       /* if (i > 8) {
            throw new RuntimeException("故意异常");
        }*/
        RpcObjectOut.RpcObject rpcObject = (RpcObjectOut.RpcObject) msg;
        logger.info("remote server {}, channelRead, msg:{}", ctx.channel().remoteAddress(), rpcObject.getMessage());


        //request.getSeqId(), "处理成功,返回结果: " + request.getSeqId()

        RpcObjectOut.RpcObject.Builder builder = RpcObjectOut.RpcObject.newBuilder();
        builder.setSeqId(rpcObject.getSeqId());
        String respS = "处理成功,返回结果: " + rpcObject.getSeqId();
        builder.setMessageBytes(ByteString.copyFromUtf8(respS));
        RpcObjectOut.RpcObject rpcResponse = builder.build();

        ctx.writeAndFlush(rpcResponse);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("[ServerHandler]:" + cause.getMessage(), cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("与客户端的channel断开,channel:" + ctx.channel().id());
        super.channelInactive(ctx);
    }
}
