package com.maple.unpack;


import com.maple.unpack.common.DirectMemoryReporter;
import com.maple.unpack.server.NettySimpleServer;

/**
 * desc: AppServer
 *
 * @author hz.lei
 * @since 2018年08月26日 下午11:48
 */
public class AppServer {

    public static void main(String[] args) {
        //40M
        System.setProperty("io.netty.maxDirectMemory", "41943040");
        System.setProperty("io.netty.leakDetectionLevel", "DISABLE");
        NettySimpleServer simpleServer = new NettySimpleServer(8000);
        DirectMemoryReporter reporter = DirectMemoryReporter.getIntance();
        reporter.startReport();

        simpleServer.start();
    }
}
