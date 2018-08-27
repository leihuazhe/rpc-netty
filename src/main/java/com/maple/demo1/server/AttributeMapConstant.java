package com.maple.demo1.server;

import com.maple.demo1.client.NettyChannel;
import io.netty.util.AttributeKey;

/**
 * 描述:
 *
 * @author hz.lei
 * @date 2018年04月18日 上午12:27
 */
public class AttributeMapConstant {

    public static final AttributeKey<NettyChannel> NETTY_CHANNEL_KEY = AttributeKey.valueOf("netty.channel");

}
