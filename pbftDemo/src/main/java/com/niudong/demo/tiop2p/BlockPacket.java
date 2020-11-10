package com.niudong.demo.tiop2p;

import org.tio.core.intf.Packet;

/**
 * 区块链底层定制的 Packet
 *
 * @author 牛冬
 */
public class BlockPacket extends Packet {
    // 网络传输需序列化，这里采用Java自带序列化方式
    private static final long serialVersionUID = -172060606924066412L;
    // 消息头的长度
    public static final int HEADER_LENGHT = 4;
    // 字符编码类型
    public static final String CHARSET = "utf-8";
    // 传输内容的字节
    private byte[] body;

    /**
     * @return the body
     */
    public byte[] getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(byte[] body) {
        this.body = body;
    }
}
