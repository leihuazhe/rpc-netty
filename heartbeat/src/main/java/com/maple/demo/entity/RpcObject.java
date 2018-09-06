package com.maple.demo.entity;

/**
 * desc: RpcObject
 *
 * @author hz.lei
 * @since 2018年08月26日 下午11:16
 */
public class RpcObject {

    private int seqId;

    private String message;

    public RpcObject() {
    }

    public RpcObject(int seqId, String message) {
        this.seqId = seqId;
        this.message = message;
    }

    public int getSeqId() {
        return seqId;
    }

    public void setSeqId(int seqId) {
        this.seqId = seqId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "RpcObject{" +
                "seqId=" + seqId +
                ", message='" + message + '\'' +
                '}';
    }
}
