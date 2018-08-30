package com.maple.protobuf;

import com.google.gson.Gson;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author maple 2018.08.30 上午12:25
 */
public class Main {
    private static Gson gson = new Gson();

    public static void main(String[] args) throws InvalidProtocolBufferException {

        byte[] bytes = encode();

        String json = encodeJson();

        long begin = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {

            HelloProto.Hello hello = HelloProto.Hello.parseFrom(bytes);

        }
        System.out.println("耗时：" + (System.currentTimeMillis() - begin) + " ms");


        long begin1 = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            HelloProto.Hello hello = gson.fromJson(json, HelloProto.Hello.class);
        }
        System.out.println("耗时：" + (System.currentTimeMillis() - begin1) + " ms");
    }


    public static byte[] encode() {
        HelloProto.Hello.Builder builder = HelloProto.Hello.newBuilder();
        builder.setId(2);
        builder.setEmail("295482300@qq.com");
        builder.setName("maple");

        builder.addMylist(2);

        HelloProto.Hello hello = builder.build();

        return hello.toByteArray();
    }

    public static String encodeJson() {
        HelloProto.Hello.Builder builder = HelloProto.Hello.newBuilder();
        builder.setId(2);
        builder.setEmail("295482300@qq.com");
        builder.setName("maple");

        builder.addMylist(2);

        HelloProto.Hello hello = builder.build();

        String json = gson.toJson(hello, HelloProto.Hello.class);

        return json;
    }


    /*public static void main(String[] args) throws InvalidProtocolBufferException {
        HelloProto.Hello.Builder builder = HelloProto.Hello.newBuilder();
        builder.setId(2);
        builder.setEmail("295482300@qq.com");
        builder.setName("maple");

        builder.addMylist(2);

        HelloProto.Hello hello = builder.build();

        System.out.println("对象==>  " + hello);
        System.out.println("对象toString==>  " + hello.toString());

        byte[] bytes = hello.toByteArray();


        HelloProto.Hello hello1 = HelloProto.Hello.parseFrom(bytes);

        System.out.println("=======");


        System.out.println("对象==>  " + hello1);
        System.out.println("对象toString==>  " + hello1.toString());

    }*/
}
