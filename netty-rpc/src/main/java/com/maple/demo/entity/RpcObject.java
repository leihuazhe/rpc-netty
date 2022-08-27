package com.maple.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * desc: RpcObject
 *
 * @author hz.lei
 * @since 2018年08月26日 下午11:16
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcObject {

  private int seqId;

  private String message;
}
