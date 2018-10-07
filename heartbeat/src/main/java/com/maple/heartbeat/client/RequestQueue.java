package com.maple.heartbeat.client;

import com.maple.heartbeat.entity.RpcObject;
import com.maple.rpc.common.util.RpcException;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author maple 2018.09.13 上午9:36
 */
public class RequestQueue {

    private static class AsyncRequestWithTimeout {
        public AsyncRequestWithTimeout(int seqid, long timeout, CompletableFuture future) {
            this.seqid = seqid;
            this.expired = System.currentTimeMillis() + timeout;
            this.future = future;
        }

        final long expired;
        final int seqid;
        final CompletableFuture<?> future;
    }

    private static final Map<Integer, CompletableFuture<RpcObject>> FUTURE_CACHES =
            new ConcurrentHashMap<>();
    private static final PriorityBlockingQueue<RequestQueue.AsyncRequestWithTimeout> FUTURES_CACHES_WITH_TIMEOUT =
            new PriorityBlockingQueue<>(256,
                    (o1, o2) -> (int) (o1.expired - o2.expired));

    public static void put(int seqId, CompletableFuture<RpcObject> requestFuture) {
        FUTURE_CACHES.put(seqId, requestFuture);
    }

    public static void putAsync(int seqId, CompletableFuture<RpcObject> requestFuture, long timeout) {
        FUTURE_CACHES.put(seqId, requestFuture);

        RequestQueue.AsyncRequestWithTimeout fwt = new RequestQueue.AsyncRequestWithTimeout(seqId, timeout, requestFuture);
        FUTURES_CACHES_WITH_TIMEOUT.add(fwt);
    }

    public static CompletableFuture<RpcObject> remove(int seqId) {
        return FUTURE_CACHES.remove(seqId);
        // remove from prior-queue
    }

    /**
     * 一次检查中超过50个请求超时就打印一下日志
     */
    public static void checkTimeout() {
        long now = System.currentTimeMillis();

        RequestQueue.AsyncRequestWithTimeout fwt = FUTURES_CACHES_WITH_TIMEOUT.peek();
        while (fwt != null && fwt.expired < now) {
            CompletableFuture future = fwt.future;
            if (!future.isDone()) {
                future.completeExceptionally(new RpcException("Err-Core-407", "请求服务超时"));
            }

            FUTURES_CACHES_WITH_TIMEOUT.remove();
            remove(fwt.seqid);

            fwt = FUTURES_CACHES_WITH_TIMEOUT.peek();
        }
    }
}
