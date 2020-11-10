package com.duansq.demo.raft;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import org.apache.commons.lang3.RandomUtils;

import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class Raft2 implements IRaft {

    public int size; // 总节点数
    public int maxf; // 最大失效节点

    public static final int VO = 1; // 请求投票
    public static final int VO_R = -1; // 回复投票

    public static final int HB = 2; // 心跳包
    public static final int HB_R = -2; // 心跳包响应包
    public static final int REQ = 3; // 客户端请求
    public static final int RSP = -3; // 响应客户端请求
    public static final int CP = 4; // 复制log数据请求
    public static final int CP_R = -4; // 复制log数据响应

    public static final int ST_F = 0; // Follower
    public static final int ST_C = 1; // Candidate
    public static final int ST_L = 2; // Leader

    private volatile boolean isRun = false;
    // 消息队列
    private Queue<RaftMsg> qbm = Queues.newLinkedBlockingDeque();
    // 投票情况
    private AtomicInteger aggre_vo = new AtomicInteger();

    private Map<Integer, String> commitedLog = Maps.newHashMap();// 已经提交的数据

    private int index; // 节点标识

    private int status; // 节点状态
    /**
     * 在当前获得选票的候选人的 Id
     */
    private int votedFor = -1;
    private int voteTerm = -1; // 投票过的任期
    private int step; // 阶段（Leader）0-初始，1-发送准备log，2-确认log
    private String curData; // 当前操作的数据
    /**
     * 已知的最大的已经被提交的日志条目的索引值
     */
    private int commitIndex;
    /**
     * 最后被应用到状态机的日志条目索引值（初始化为 0，持续递增）
     */
    private int lastApplied;
    /**
     * 对于每一个服务器，需要发送给他的下一个日志条目的索引值（初始化为领导人最后索引值加一）
     */
    private int nextIndex[];
    /**
     * 对于每一个服务器，已经复制给他的日志的最高索引值
     */
    private int matchIndex[];

    private Timer timer = new Timer("timer1");
    private TimerTask lastTask;

    // 发送队列
    private Queue<String> reqQueue = Queues.newLinkedBlockingDeque(100);

    /**
     * 服务器最后一次知道的任期号（初始化为 0，持续递增）
     */
    private volatile int currentTerm;

    public Raft2(int node, int size) {
        this.index = node;
        this.size = size;
        this.maxf = size / 2;
    }

    public Raft2 start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(RaftMain.netDelay); // 模拟网络时延
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!isRun) continue;
                    while (doAction()) {
                        doAction();
                    }
                    heartbeat();
                }
            }
        }).start();
        isRun = true;
        resetElectionTimeOut();

        return this;
    }

    private void becomeLeader() {
        this.status = ST_L;
        aggre_vo.set(0);
        step = 0;
        curData = null;
        stopTimer();
    }

    private void becomeFollower(RaftMsg msg) {
        this.currentTerm = msg.getTerm();
        this.commitIndex = msg.getId();
        resetElectionTimeOut();
        this.status = ST_F;
        step = 0;
        reqQueue.clear();
        curData = null;
    }

    /**
     * 请求入列
     *
     * @param data
     */
    public void req(RaftMsg msg) {
        reqQueue.offer(msg.getData());
    }

    /**
     * 发送心跳包，携带数据，提交操作
     */
    private void heartbeat() {
        if (this.status == ST_L) {
            // heartbeat 维持心跳,如果有数据，携带数据
            RaftMsg req = new RaftMsg(HB, this.index, this.currentTerm);
            req.setId(commitIndex);
            if (step == 0) {
                curData = reqQueue.poll();
                if (curData != null) {
                    req.setId(++commitIndex);
                    req.setData(curData);
                    aggre_vo.set(1);
                    step = 1;
                }
            } else if (step == 2) {
                req.setHbt(1);
                curData = null;
                step = 0;
            }
            RaftMain.publish(req);
        }
    }

    private void onHeartbeat(RaftMsg msg) {
        if (msg.getTerm() < this.currentTerm) {
            // 小的任期报文，忽略等待心跳自动回复
//			if(this.status == ST_L){
//				// 响应最新的任期给老的领导
//				RaftMsg sed = new RaftMsg(msg);
//				sed.setType(HB_R);
//				sed.setNode(index);
//				sed.setTerm(term);
//				RaftMain.send(msg.getNode(), sed);
//			}
            // 普通节点忽略
            return;
        } else if (msg.getTerm() > this.currentTerm) {
            becomeFollower(msg);
        } else {
            resetElectionTimeOut();
        }
        if (this.commitIndex + 1 < msg.getId()) {
            // 说明数据落后，有缺失，需要发起拷贝
            copyData(msg);
        }
        if (msg.getHbt() == 1) {
            // 收到提交报文
            commit();
        } else if (msg.getData() != null) {
            // 带有数据的报文，需要进入准备阶段
            this.commitIndex = msg.getId();
            prepareLog(msg);
        }
        // 响应心跳包
        RaftMsg sed = new RaftMsg(msg);
        sed.setType(HB_R);
        sed.setNode(index);
        RaftMain.send(msg.getNode(), sed);
    }


    private void onHeartbeatRes(RaftMsg msg) {
        if (step == 1 && curData != null && msg.getId() == commitIndex) {
            // 票数 +1
            long agCou = aggre_vo.incrementAndGet();
            if (agCou >= maxf + 1) {
                // 操作确认成功
                aggre_vo.set(0);
                // 回复客户端，操作完成
                replyClient(2);
                // 模拟主节点宕机
//				close();
                // 通知其他节点，提交数据
                step = 2;
                commit();
            }
        }

    }

    protected boolean doAction() {
        RaftMsg msg = qbm.poll();
        if (msg != null) {
            System.out.println("收到消息[" + index + "]:" + msg);
            switch (msg.getType()) {
                case VO:
                    onVote(msg);
                    break;
                case VO_R:
                    onVoteR(msg);
                    break;
                case REQ:
                    onReq(msg);
                    break;
                case HB:
                    onHeartbeat(msg);
                    break;
                case HB_R:
                    onHeartbeatRes(msg);
                    break;
                case CP:
                    onCopyReq(msg);
                    break;
                case CP_R:
                    onCopyRes(msg);
                    break;
                default:
                    break;
            }
            return true;
        }
        return false;
    }

    /**
     * 复制日志响应
     *
     * @param msg
     */
    private void onCopyRes(RaftMsg msg) {
        commitedLog.put(msg.getId(), msg.getData());
        System.err.println("复制日志执行成功[" + index + "]:" + msg);
    }

    /**
     * 复制日志请求
     *
     * @param msg
     */
    private void onCopyReq(RaftMsg msg) {
        // 复制从拥有id开始到最新的id区间的数据
        for (int i = msg.getId(); i < Integer.parseInt(msg.getData()); i++) {
            RaftMsg rsp = new RaftMsg(CP_R, index, currentTerm);
            String data = commitedLog.get(msg.getId());
            rsp.setData(data);
            rsp.setId(i);
            RaftMain.send(msg.getNode(), rsp);
        }
    }

    private void replyClient(int hbt) {
        RaftMsg rsp = new RaftMsg(RSP, index, currentTerm);
        rsp.setHbt(hbt);
        if (hbt == 3) {
            rsp.setData(String.valueOf(this.votedFor));
        }
        RaftMain.sendClient(rsp);
    }

    private void stopTimer() {
        if (lastTask != null) {
            lastTask.cancel();
        }
    }

    /**
     * 重置选举超时
     */
    private void resetElectionTimeOut() {
        stopTimer();
        lastTask = new TimerTask() {
            @Override
            public void run() {
                if (status != ST_L) {
                    // 超时重新选举
                    System.out.println("leader超时[" + index + "]，重新选举！");
                    status = ST_C;
                    votedFor = index;
                    currentTerm++;
                    aggre_vo.set(1);
                    RaftMsg req = new RaftMsg(VO, index, currentTerm);
                    req.setId(commitIndex);
                    RaftMain.publish(req);
                    // 继续设定超时，防止选举无法结束
                    resetElectionTimeOut();
                }
            }
        };
        timer.schedule(lastTask, RandomUtils.nextLong(3 * RaftMain.netDelay, 6 * RaftMain.netDelay));
    }

    private void checkCommit() {
        if (curData != null) {
            // 说明处于等待提交阶段，需要进行提交
            step = 2;
            commit();
        }
    }

    private void onReq(RaftMsg msg) {
        if (this.status != ST_L) {
            replyClient(3);
            System.err.println("请求日志执行失败[" + index + "]:leader=" + this.votedFor);
            return;
        } else {
            req(msg);
        }
    }

    private void prepareLog(RaftMsg msg) {
        curData = msg.getData();
    }

    private void commit() {
        // 请求被允许，可放心执行
        commitedLog.put(this.commitIndex, curData);
        System.err.println("请求日志执行成功[" + index + "]:" + curData);
    }

    /**
     * 请求缺失数据
     */
    private void copyData(RaftMsg msg) {
        RaftMsg rsp = new RaftMsg(CP, index, currentTerm);
        rsp.setData(String.valueOf(msg.getId()));
        rsp.setId(this.commitIndex + 1);
        RaftMain.send(msg.getNode(), rsp);
    }

    private void onVote(RaftMsg msg) {
        // 投票给他
        if (status == ST_F || msg.getTerm() > this.currentTerm) {
            if (msg.getId() < this.commitIndex) {
                // 该节点没有最新的数据
                return;
            }
            if (msg.getTerm() == voteTerm) {
                // 已投票
                return;
            }
            voteTerm = msg.getTerm();
            votedFor = msg.getNode();
            // 重置投票超时
            resetElectionTimeOut();
            this.currentTerm = msg.getTerm();
            RaftMsg sed = new RaftMsg(msg);
            sed.setType(VO_R);
            sed.setNode(index);
            RaftMain.send(msg.getNode(), sed);
        }
    }

    private void onVoteR(RaftMsg msg) {
        // 票数 +1
        long agCou = aggre_vo.incrementAndGet();
        if (agCou >= maxf + 1) {
            // 成为leader
            System.out.println("----------------------成为leader[" + index + "]");
            becomeLeader();
            // 检查是否当前在提交阶段，在提交阶段主节点宕机需要补偿提交
            checkCommit();
        }
    }

    public void push(RaftMsg msg) {
        if (isRun) {
            this.qbm.add(msg);
        }
    }

    public void close() {
        System.out.println("宕机[" + index + "]--------------");
        stopTimer();
        this.isRun = false;
    }

    public void back() {
        System.out.println("节点恢复[" + index + "]--------------");
        resetElectionTimeOut();
        this.isRun = true;
    }

    public int getIndex() {
        return index;
    }

}
