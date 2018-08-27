package com.maple.demo1.nio;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 描述: 内存映射读取文件模式   MappedByteBuffer
 *
 * @author hz.lei
 * @date 2018年05月03日 下午8:17
 */
public class MapperBufferTest {

    public static void main(String[] args) {
        //定义文件操作目录
        File read = new File("/data/test.xml");

        //定义读写流，通道
        FileInputStream in;
        FileChannel fin = null;
        try {

            in = new FileInputStream(read);
            fin = in.getChannel();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //声明内存映射
        MappedByteBuffer map = null;


        try {
            //通过通道获取内存映射
            map = fin.map(FileChannel.MapMode.READ_ONLY, 0, read.length());
        } catch (IOException e) {

            e.printStackTrace();
        }

        //将存映射放入到变量中进行输出
        byte[] data = new byte[(int) read.length()];
        int foot = 0;

        while (map.hasRemaining()) {

            data[foot++] = map.get();
        }

        try {
            System.out.println(new String(data, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


}
