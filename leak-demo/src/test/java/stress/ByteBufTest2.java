package stress;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

import java.nio.charset.Charset;


/**
 * @author maple 2018.10.08 1:19 PM
 */
public class ByteBufTest2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(ByteBufTest2.class);

    @Autowired
    private ConversionService conversionService;

    public static void main(String[] args) {
        String content = "hello world";
        ByteBufAllocator alloc = ByteBufAllocator.DEFAULT;
        LOGGER.info("content length => " + content.length());

        ByteBuf msg = alloc.buffer(content.length() + 4);

        msg.writeBytes(content.getBytes(CharsetUtil.UTF_8));

        // String result = msg.readBytes(readable - 2).toString(Charset.forName("UTF-8"));

        LOGGER.info("readBytes is {}", msg.readableBytes());

        //不会内存泄漏
        String result = msg.toString(msg.readerIndex(), msg.readableBytes() - 1, Charset.forName("UTF-8"));

           /* ByteBuf resultBuf = null;
            String result;
            try {
                resultBuf = msg.readBytes(readable - 2);
                result = resultBuf.toString(Charset.forName("UTF-8"));

            } finally {
                resultBuf.release();
            }*/

        // 处理直接缓冲区以及复合缓冲区
//        msg.readerIndex(msg.readableBytes());
        log(msg);

        byte[] bytes = new byte[msg.readableBytes()];
        // getBytes 也不会改变读写索引。
        msg.getBytes(msg.readerIndex(), bytes);

        String result1 = new String(bytes, 0, msg.readableBytes());

        log(msg);

        byte[] bytes1 = new byte[msg.readableBytes()];

        msg.readBytes(bytes1);

        String result2 = new String(bytes1);
        System.out.println("result2 " + result2);
        log(msg);


    }

    private static void log(ByteBuf msg) {
        int i = msg.readerIndex();
        System.out.println("读索引位置: " + i);
        boolean readable1 = msg.isReadable();
        System.out.println("是否可读： " + readable1);
    }

    public static String convertByteBufToString(ByteBuf buf) {
        String str;
        if (buf.hasArray()) {
            // 处理堆缓冲区
            str = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
        } else {
            // 处理直接缓冲区以及复合缓冲区
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            str = new String(bytes, 0, buf.readableBytes());
        }
        return str;
    }


}
