package com.duansq.demo.raft;

public class RaftMsg {

    private int id; // 消息id

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int type; // 消息类型
    private int node; // 节点
    private int term; // 任期

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    private long time; // 时间戳
    private int hbt; // 心跳类型0-准备/空，1-提交,2-回复成功客户端，3-回复客户端真正的leader
    private String data; // 数据,表示数据的hash,必须唯一

    public RaftMsg(int type, int node, int term) {
        this.type = type;
        this.node = node;
        this.term = term;
        this.time = System.currentTimeMillis();
    }

    public RaftMsg(RaftMsg msg) {
        this.id = msg.id;
        this.type = msg.type;
        this.node = msg.node;
        this.term = msg.term;
        this.data = msg.data;
        this.time = msg.time;
        this.hbt = msg.hbt;
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getNode() {
        return node;
    }

    public void setNode(int node) {
        this.node = node;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "RaftMsg [id=" + id + ", type=" + type + ", node=" + node + ", term=" + term + ", hbt=" + hbt
                + ", data=" + data + "]";
    }

    public int getHbt() {
        return hbt;
    }

    public void setHbt(int hbt) {
        this.hbt = hbt;
    }


}
