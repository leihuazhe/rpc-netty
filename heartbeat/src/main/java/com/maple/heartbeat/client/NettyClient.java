package com.maple.heartbeat.client;

import com.google.gson.Gson;
import com.maple.heartbeat.client.handler.RpcClientHandler;
import com.maple.heartbeat.client.handler.SoaIdleHandler;
import com.maple.heartbeat.entity.RpcObject;
import com.maple.rpc.common.util.Constants;
import com.maple.rpc.common.util.RpcException;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.AbstractByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;


/**
 * @author maple 2018.08.26 22:13
 */
public class NettyClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private final Gson gson = new Gson();

    private Bootstrap bootstrap = null;
    private final EventLoopGroup workerGroup = new NioEventLoopGroup(Constants.DEFAULT_IO_THREADS, new DefaultThreadFactory("netty-client-work-group", Boolean.TRUE));

    /**
     * init
     */
    public NettyClient() {
        initBootstrap();
    }

    /**
     * init netty client
     *
     * @return
     */
    private Bootstrap initBootstrap() {
        AbstractByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);

        //保持连接（可以不写，默认为true）
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.ALLOCATOR, allocator)
                //禁用nagle算法,有消息立即发送
                .option(ChannelOption.TCP_NODELAY, true);


        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addLast(new IdleStateHandler(25, 15, 0));
                //SoaIdleHandler中增加userEventTriggered用来接收心跳检测结果,
                // event.state()的状态分别对应上面三个参数的时间设置，当满足某个时间的条件时会触发事件。
                p.addLast(new SoaIdleHandler());
                p.addLast("decoder", new StringDecoder());
                p.addLast("encoder", new StringEncoder());
                p.addLast(new RpcClientHandler(callBack));
            }
        });
        return bootstrap;
    }

    /**
     * send 将请求存入并发容器中,根据 seqId 作为 key. 然后会调用一次 writeAndFlush 将请求发出去.
     * 处理器链中会有一个 RpcClientHandler,当收到channelRead，即服务端返回后，这里便会回调下面的callback.
     * 读出返回的信息中的seqId，判断是哪一次请求，然后完成这一次请求。客服端即可返回。
     */
    public RpcObject send(Channel channel, int seqid, RpcObject request, long timeout, String service) throws RpcException {
        // send 即 put
        CompletableFuture<RpcObject> future = new CompletableFuture<>();

        RequestQueue.put(seqid, future);

        if (logger.isDebugEnabled()) {
            logger.debug("NettyClient::send, timeout:" + timeout + ", seqId:" + seqid + ",  to: " + channel.remoteAddress());
        }

        try {
            channel.writeAndFlush(request);
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            logger.error("请求服务超时[" + service + "]，seqid:" + seqid);
            throw new RpcException("Err-Core-407", "请求服务超时[" + service + "]");
        } catch (Throwable e) {
            throw new RpcException("Err-Core-400", e.getMessage() == null ? "系统出错了" : e.getMessage());
        } finally {
            RequestQueue.remove(seqid);
        }
    }

    public CompletableFuture<RpcObject> sendAsync(Channel channel, int seqid, RpcObject request, long timeout) throws Exception {
        String reqStr = gson.toJson(request);
        //不打日志吗？
        CompletableFuture<RpcObject> future = new CompletableFuture<>();

//        Promise<RpcObject> promise = new DefaultPromise<>(executors);


        RequestQueue.putAsync(seqid, future, timeout);

        channel.writeAndFlush(reqStr);

        return future;
    }

    private final RpcClientHandler.CallBack callBack = msg -> {
        CompletableFuture<RpcObject> future = RequestQueue.remove(msg.getSeqId());
        if (future != null) {
            future.complete(msg);
            logger.info("完成 msg");
        } else {
            logger.error("返回结果超时，siqid为：" + msg.getSeqId());
        }
    };

    /**
     * 定时任务，使得超时的异步任务返回异常给调用者
     */
    private static long DEFAULT_SLEEP_TIME = 100L;

    static {

        final Thread asyncCheckTimeThread = new Thread("ConnectionPool-ReqTimeout-Thread") {
            @Override
            public void run() {
                while (true) {
                    try {
                        RequestQueue.checkTimeout();
                        Thread.sleep(DEFAULT_SLEEP_TIME);
                    } catch (Exception e) {
                        logger.error("Check Async Timeout Thread Error", e);
                    }
                }
            }
        };
        asyncCheckTimeThread.start();
    }


    /**
     * 同步连接并返回channel
     *
     * @param host
     * @param port
     * @return
     * @throws InterruptedException
     */
    public Channel connect(String host, int port) throws InterruptedException {
        Channel channel = bootstrap.connect(host, port).sync().channel();
        logger.info("----------->   bind to server host {}, port:{} successful", host, port);
        return channel;
    }

    public void shutdown() {
        logger.warn("NettyClient shutdown gracefully");
        workerGroup.shutdownGracefully();
    }


}
