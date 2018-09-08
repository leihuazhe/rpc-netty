package com.maple.unpack;

import com.maple.unpack.client.NettyClient;
import com.maple.unpack.common.entity.RpcObject;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * desc: AppClient
 *
 * @author hz.lei
 * @since 2018年08月26日 下午11:48
 */
public class AppClient {
    private static final Logger logger = LoggerFactory.getLogger(AppClient.class);
    private Channel channel;
    private NettyClient nettyClient;
    private final String host;
    private final int port;

    private final static AtomicInteger seqidAtomic = new AtomicInteger(0);

    public AppClient(String host, int port) {
        this.host = host;
        this.port = port;
        nettyClient = new NettyClient();
        try {
            channel = nettyClient.connect(host, port);
        } catch (InterruptedException e) {
            logger.error("connect to {}:{} failed", host, port);
        }
    }

    public CompletableFuture<RpcObject> sendMessage(int seq, String message) {
        RpcObject rpcObject = new RpcObject(seq, message);
        try {
            checkChannel();
            CompletableFuture<RpcObject> resp = nettyClient.sendAsync(channel, seq, rpcObject, 5000L);
            return resp;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 重连接机制
     *
     * @throws Exception
     */
    private void checkChannel() throws Exception {
        if (channel == null) {
            nettyClient.connect(host, port);
        } else if (!channel.isActive()) {
            logger.info("channel 掉线");
            try {
                channel.close();
            } finally {
                channel = null;
                channel = nettyClient.connect(host, port);
            }
        }
    }


    public static void main(String[] args) {
//        AppClient client = new AppClient("127.0.0.1", 8000);
        AppClient client = new AppClient("115.159.41.97", 8000);
        logger.info("请输入内容:\n");
        while (true) {
            try {
                Scanner scanner = new Scanner(System.in);
                if (scanner.hasNext()) {
                    String msg = scanner.next();
                    int seq = seqidAtomic.incrementAndGet();
                    CompletableFuture<RpcObject> response = client.sendMessage(seq, msg);
                    response.whenComplete((result, ex) -> {
                        if (ex != null) {
                            logger.info(ex.getMessage(), ex);
                        }
                        logger.info("seq为 {} 的请求,服务端返回结果为:{}", seq, result.toString());
                    });
                } else {
                    int seq = seqidAtomic.incrementAndGet();
                    CompletableFuture<RpcObject> response = client.sendMessage(seq, "准备关闭:" + seq);
                    response.whenComplete((result, ex) -> {
                        if (ex != null) {
                            logger.info(ex.getMessage(), ex);
                        }
                        logger.info("seq为 {} 的请求,服务端返回结果为:{}", seq, result.toString());
                    });
                }


            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
