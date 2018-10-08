package com.maple.leak.server.handler;

import com.google.gson.Gson;
import com.maple.leak.common.entity.RpcObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

import static com.maple.leak.common.util.FrameProtocolUtil.ETX;
import static com.maple.leak.common.util.FrameProtocolUtil.STX;


/**
 * @author maple 2018.09.07 上午9:40
 */
public class RpcMsgEncoder extends MessageToByteEncoder<RpcObject> {
    private Gson gson = new Gson();

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcObject msg, ByteBuf out) throws Exception {
        int beginIndex = out.readerIndex();
        String content = gson.toJson(msg);

        //先写,占位
        out.writeInt(0);
        out.writeByte(STX);
        out.writeBytes(content.getBytes(CharsetUtil.UTF_8));
        out.writeByte(ETX);

        int endIndex = out.writerIndex();
        int length = endIndex - beginIndex - Integer.BYTES;
        out.writerIndex(beginIndex).writeInt(length);
        out.writerIndex(endIndex);
    }
}
