package com.maple.unpack.client;

import java.util.Date;

/**
 * 描述:
 *
 * @author hz.lei
 * @date 2018年04月18日 上午12:28
 */
public class NettyChannel {
    private String name;


    private Date createDate;


    public NettyChannel(String name, Date createDate) {
        this.name = name;
        this.createDate = createDate;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
