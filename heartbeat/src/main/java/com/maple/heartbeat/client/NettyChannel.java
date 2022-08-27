package com.maple.heartbeat.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 描述:
 *
 * @author hz.lei
 * @date 2018年04月18日 上午12:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NettyChannel {

  private String name;

  private Date createDate;
}
