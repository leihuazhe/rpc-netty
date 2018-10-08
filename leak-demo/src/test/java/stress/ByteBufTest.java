package stress;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

/**
 * @author maple 2018.09.12 上午11:13
 */
public class ByteBufTest {

    public static void main(String[] args) {

        String content = "hello world";

        ByteBufAllocator alloc = ByteBufAllocator.DEFAULT;

        System.out.println("content length => " + content.length());

        ByteBuf wrapBuf = alloc.buffer(content.length() + 4);

        System.out.println("wrapBuf writableBytes ===>  " + wrapBuf.writableBytes());

        wrapBuf.writeInt(content.length());

        System.out.println("wrapBuf writableBytes ===>  " + wrapBuf.writableBytes());


        byte[] bytes = content.getBytes(CharsetUtil.UTF_8);

        System.out.println("byte[] length ===> " + bytes.length);


        wrapBuf.writeBytes(content.getBytes(CharsetUtil.UTF_8));

        System.out.println("wrapBuf writableBytes ===>  " + wrapBuf.writableBytes());




        String s = dumpToStr(wrapBuf);


        System.out.println(s);

        System.out.println("=======> 开始解码!!!");


        System.out.println("readIndex: " + wrapBuf.readerIndex());

        System.out.println("read_able: " + wrapBuf.readableBytes());

        String s1 = wrapBuf.toString(wrapBuf.readerIndex() + 4, wrapBuf.readableBytes() - wrapBuf.readerIndex() - 4, Charset.forName("UTF-8"));

        System.out.println("read msg ====> " + s1);

        wrapBuf.release();


    }


    public static String dumpToStr(ByteBuf buffer) {
        int readerIndex = buffer.readerIndex();
        int availabe = buffer.readableBytes();

        StringBuilder sb = new StringBuilder();

        // XX XX XX XX XX XX XX XX  XX XX XX XX XX XX XX XX  ASCII....
        sb.append("=======[" + availabe + "]\n");
        int i = 0;
        for (; i < availabe; i++) {
            byte b = buffer.getByte(readerIndex + i);

            String it = String.format("%02x ", b & 0xFF);
            sb.append(it);

            if (i % 16 == 15) {
                //int from = i - 15;
                sb.append(' ');
                for (int j = i - 15; j <= i; j++) {
                    char ch = (char) buffer.getByte(readerIndex + j);
                    if (ch >= 0x20 && ch < 0x7F) sb.append(ch);
                    else sb.append('.');
                }
                sb.append("\n");
            }
        }
        i -= 1;
        int from = i / 16 * 16;
        if (i % 16 != 15) {
            for (int j = i; j % 16 != 15; j++) sb.append("   ");
            sb.append(' ');
            for (int j = from; j <= i; j++) {
                char ch = (char) buffer.getByte(readerIndex + j);
                if (ch >= 0x20 && ch < 0x7F) sb.append(ch);
                else sb.append('.');
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
