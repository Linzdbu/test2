package com.duansq.demo.raft;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * server启动时怎么初始化leader
 * leader响应client后,还没发送提交报文给其他follower时宕机，怎么办？
 * client第一次发送数据时，怎么发送，怎么找leader
 * 宕机后重启怎么复制丢失的数据？
 *
 * @author andy
 */
public class RaftMain {

    public static final int size = 10;

    private static List<IRaft> nodes = Lists.newArrayList();
    private static RaftClient client;

    public static long netDelay = 1000;

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < size; i++) {
            nodes.add(new Raft(i, 10).start());
        }
        client = new RaftClient(size);
        client.start();

        // 模拟请求端发送请求
        for (int i = 0; i < 1; i++) {
            client.req("test" + i);
        }

        // 节点宕机
        IRaft fail = nodes.get(0);
        while (!client.finish()) {
            Thread.sleep(100);
        }
        // 等客户端的数据发完后再宕机
        fail.close();
        for (int i = 1; i < 2; i++) {
            client.req("testD" + i);
        }

        while (!client.finish()) {
            Thread.sleep(100);
        }
        // 等客户端的数据发完后再恢复，看之前的数据是否有同步过来
        // 节点恢复运行
        fail.back();

        for (int i = 1; i < 2; i++) {
            client.req("testB" + i);
        }

    }

    /**
     * 广播消息，除了自己
     *
     * @param msg
     */
    public static void publish(RaftMsg msg) {
        System.out.println("广播消息[" + msg.getNode() + "]:" + msg);
        for (IRaft raft : nodes) {
            if (raft.getIndex() != msg.getNode()) {
                raft.push(new RaftMsg(msg));
            }
        }
    }

    /**
     * 发送消息到指定节点
     *
     * @param toIndex
     * @param msg
     */
    public static void send(int toIndex, RaftMsg msg) {
        nodes.get(toIndex).push(msg);
    }

    public static void sendClient(RaftMsg msg) {
        client.push(msg);
    }
}
