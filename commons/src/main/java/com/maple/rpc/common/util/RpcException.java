package com.maple.rpc.common.util;

import lombok.Data;

/**
 * desc: RpcException
 *
 * @author hz.lei
 * @since 2018年08月26日 下午10:16
 */
@Data
public class RpcException extends RuntimeException {

  private static final long serialVersionUID = -129682168859027730L;

  private final String code;
  private final String msg;

  public RpcException(String code, String msg) {
    super(code + ":" + msg);
    this.code = code;
    this.msg = msg;
  }

  public RpcException(String code, String msg, Throwable cause) {
    super(cause);
    this.code = code;
    this.msg = msg;
  }

  @Override
  public String toString() {
    return code + ":" + msg;
  }

}
