package com.duansq.demo.raft;

import com.google.common.collect.Queues;
import org.apache.commons.lang3.RandomUtils;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class RaftClient {

    public int size; // 总节点数
    public int maxf; // 最大失效节点

    private int leader = -1;
    private String curData; // 当前操作的数据

    private Timer timer = new Timer("timer1");
    private TimerTask lastTask;

    // 发送队列
    private Queue<String> reqQueue = Queues.newLinkedBlockingDeque(100);
    // 消息队列
    private Queue<RaftMsg> qbm = Queues.newLinkedBlockingDeque();

    public RaftClient(int size) {
        this.size = size;
        this.maxf = (size - 1) / 2;
    }

    public RaftClient start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(RaftMain.netDelay); // 模拟网络时延
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    doSendMsg();
                    doReadMsg();
                }
            }
        }).start();

        return this;
    }


    protected void doReadMsg() {
        RaftMsg msg = qbm.poll();
        if (msg != null) {
            if (msg.getHbt() == 3) {
                System.err.println("客户端收到更新leader消息[" + msg.getNode() + "]:" + msg.getData());
                this.leader = Integer.parseInt(msg.getData());
                // leader -1会继续随机选主
                doSend();
            } else {
                System.err.println("客户端收到成功消息[" + msg.getNode() + "]:" + curData);
                curData = null;
                if (lastTask != null) {
                    lastTask.cancel();
                }
            }
        }
    }

    /**
     * 请求入列
     *
     * @param data
     */
    public void req(String data) {
        reqQueue.offer(data);
    }

    protected boolean doSendMsg() {
        if (curData == null) {
            curData = reqQueue.poll();
            doSend();
        }
        return false;
    }

    private void doSend() {
        if (curData != null) {
            RaftMsg req = new RaftMsg(Raft.REQ, 0, 0);
            req.setData(curData);
            if (leader < 0) {
                // 随机选择leader发送
                leader = RandomUtils.nextInt(0, size);
            }
            RaftMain.send(this.leader, req);
            System.err.println("客户端发送消息:leader=" + leader + ":" + curData);
            resetTimeOut();
        }
    }

    /**
     * 请求处理超时
     */
    private void resetTimeOut() {
        if (lastTask != null) {
            lastTask.cancel();
        }
        lastTask = new TimerTask() {
            @Override
            public void run() {
                // 超时则重新选择leader重发
                System.out.println("客户端请求超时，重新选择leader重发！");
                leader = (++leader) % size;
                doSend();
            }
        };
        timer.schedule(lastTask, 5 * RaftMain.netDelay);
    }

    public void push(RaftMsg msg) {
        this.qbm.add(msg);
    }

    public boolean finish() {
        return reqQueue.isEmpty() && curData == null;
    }

}
