package com.maple.demo3;


import com.maple.demo3.server.NettySimpleServer;

/**
 * desc: AppServer
 *
 * @author hz.lei
 * @since 2018年08月26日 下午11:48
 */
public class AppServer {

    public static void main(String[] args) {
        NettySimpleServer simpleServer = new NettySimpleServer(8000);
        simpleServer.start();
    }
}
