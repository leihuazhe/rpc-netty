package com.maple.heartbeat.util;

/**
 * @author maple 2018.09.07 上午12:31
 */
public class FrameProtocolUtil {

    /**
     * Frame begin flag
     */
    public static final byte STX = 0x02;
    /**
     * Frame end flag
     */
    public static final byte ETX = 0x03;
    /**
     * Soa version
     */
    public static final byte VERSION = 1;
}
