package com.niudong.demo.p2ppbft;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.testng.util.Strings;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.niudong.demo.util.SimpleMerkleTree;


/**
 * 基于springboot2.0的websocket客户端
 *
 * @author 牛冬
 */
@Component
public class P2pPointPbftClient {
    // 日志记录
    private Logger logger = LoggerFactory.getLogger(P2pPointPbftClient.class);

    // P2P网络中的节既是Server端，又是Client端。作为Server运行在7001端口（P2pPointServer的port字段），同时作为Client通过ws://localhost:7001连接到服务端
    private String wsUrl = "ws://localhost:7001/";

    // 所有客户端WebSocket的连接池缓存
    private List<WebSocket> localSockets = new ArrayList<WebSocket>();


    public List<WebSocket> getLocalSockets() {
        return localSockets;
    }

    public void setLocalSockets(List<WebSocket> localSockets) {
        this.localSockets = localSockets;
    }

    /**
     * 连接到服务端
     */
    //@PostConstruct
    //@Order(2)
    public void connectPeer() {
        try {
            // 创建WebSocket的客户端
            final WebSocketClient socketClient = new WebSocketClient(new URI(wsUrl)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    sendMessage(this, "好未来北京客户端打开");

                    localSockets.add(this);
                }

                @Override
                public void onMessage(String msg) {
                    logger.info("收到好未来北京服务端发送的消息:" + msg);

                    // 如果收到的是非JSON数据则说明不是pbft阶段
                    if (!msg.startsWith("{")) {
                        return;
                    }

                    // //如果收到的是JSON数据则说明是pbft阶段
                    // 如果是json化数据，则进入到了pbft投票阶段
                    JSONObject json = JSON.parseObject(msg);
                    if (!json.containsKey("code")) {
                        logger.info("好未来北京收到非JSON化数据");
                    }

                    int code = json.getIntValue("code");
                    if (code == VoteEnum.PREPREPARE.getCode()) {
                        // 校验hash
                        VoteInfo voteInfo = JSON.parseObject(msg, VoteInfo.class);
                        if (!voteInfo.getHash().equals(SimpleMerkleTree.getTreeNodeHash(voteInfo.getList()))) {
                            logger.info("收到好未来北京服务端错误的JSON化数据");
                            return;
                        }

                        // 校验成功，发送下一个状态的数据
                        VoteInfo vi = createVoteInfo(VoteEnum.PREPARE);
                        sendMessage(this, JSON.toJSONString(vi));
                        logger.info("好未来北京发送到客户端pbft消息：" + JSON.toJSONString(vi));
                        return;
                    }

                    if (code == VoteEnum.COMMIT.getCode()) {
                        // 校验hash
                        VoteInfo voteInfo = JSON.parseObject(msg, VoteInfo.class);
                        if (!voteInfo.getHash().equals(SimpleMerkleTree.getTreeNodeHash(voteInfo.getList()))) {
                            logger.info("收到好未来北京服务端错误的JSON化数据");
                            return;
                        }

                        // 校验成功，检验节点确认个数是否有效
                        if (getConnecttedNodeCount() >= getLeastNodeCount()) {
                            sendMessage(this, "好未来北京客户端开始区块入库啦");
                            logger.info("好未来北京客户端开始区块入库啦");
                        }
                    }
                }

                @Override
                public void onClose(int i, String msg, boolean b) {
                    logger.info("好未来北京客户端关闭");
                    localSockets.remove(this);
                }

                @Override
                public void onError(Exception e) {
                    logger.info("好未来北京客户端报错");
                    localSockets.remove(this);
                }
            };
            // 客户端 开始连接服务端
            socketClient.connect();
        } catch (URISyntaxException e) {
            logger.info("好未来北京连接错误:" + e.getMessage());
        }
    }

    // 已经在连接的节点的个数
    private int getConnecttedNodeCount() {
        // 本机测试时，写死为1.实际开发部署多个节点时，按实际情况返回
        return 1;
    }

    // pbft消息节点最少确认个数计算
    private int getLeastNodeCount() {
        // 本机测试时，写死为1.实际开发部署多个节点时，pbft算法中拜占庭节点数量f，总节点数3f+1
        return 1;
    }

    // 根据VoteEnum构建对应状态的VoteInfo
    private VoteInfo createVoteInfo(VoteEnum ve) {
        VoteInfo vi = new VoteInfo();
        vi.setCode(ve.getCode());

        List<String> list = new ArrayList<>();
        list.add("AI");
        list.add("BlockChain");
        vi.setList(list);
        vi.setHash(SimpleMerkleTree.getTreeNodeHash(list));

        return vi;
    }

    /**
     * 向服务端发送消息 当前WebSocket的远程Socket地址，就是服务器端
     *
     * @param ws：
     * @param message
     */
    public void sendMessage(WebSocket ws, String message) {
        logger.info("发送给" + ws.getRemoteSocketAddress().getPort() + "的p2p消息:" + message);
        ws.send(message);
    }

    /**
     * 向所有连接过的服务端广播消息
     *
     * @param message：待广播的消息
     */
    public void broatcast(String message) {
        if (localSockets.size() == 0 || Strings.isNullOrEmpty(message)) {
            return;
        }

        logger.info("Glad to say broatcast to servers being startted!");
        for (WebSocket socket : localSockets) {
            this.sendMessage(socket, message);
        }
        logger.info("Glad to say broatcast to servers has overred!");
    }
}
