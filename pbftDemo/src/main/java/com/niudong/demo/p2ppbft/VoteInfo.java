package com.niudong.demo.p2ppbft;

import java.util.List;

/**
 * 投票信息类
 *
 * @author 牛冬
 */
public class VoteInfo {

    // 投票状态码
    private int code;
    // 待写入区块的内容
    private List<String> list;
    // 待写入区块的内容的Merkle root hash值
    private String hash;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
