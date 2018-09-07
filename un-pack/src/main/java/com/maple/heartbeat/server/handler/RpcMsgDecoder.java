package com.maple.heartbeat.server.handler;

import com.google.gson.Gson;
import com.maple.heartbeat.entity.RpcObject;
import com.maple.rpc.common.util.RpcException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;

import static com.maple.heartbeat.util.FrameProtocolUtil.ETX;
import static com.maple.heartbeat.util.FrameProtocolUtil.STX;
import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;


/**
 * @author maple 2018.09.07 上午9:40
 */
public class RpcMsgDecoder extends MessageToMessageDecoder<ByteBuf> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcMsgDecoder.class);

    private Gson gson = new Gson();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(getClass().getSimpleName() + "::decode");
            }
            int readable = msg.readableBytes();

            byte stx = msg.readByte();
            // 通讯协议不正确
            if (stx != STX) {
                throw new RpcException("Err-Rpc-001", "通讯协议不正确(起始符)");
            }

            byte etx = msg.getByte(readable - 1);
            // 通讯协议不正确
            if (etx != ETX) {
                throw new RpcException("Err-Rpc-002", "通讯协议不正确(结束符)");
            }

            // 为什么这里是 -2
            String result = msg.readBytes(readable - 2).toString(Charset.forName("UTF-8"));
            out.add(gson.fromJson(result, RpcObject.class));
        } catch (RpcException e) {
            LOGGER.error(e.getMessage(), e);
            RpcObject rpcObject = new RpcObject(9999, e.getMessage());

            ctx.writeAndFlush(rpcObject).addListener(FIRE_EXCEPTION_ON_FAILURE);
        }
    }

}
