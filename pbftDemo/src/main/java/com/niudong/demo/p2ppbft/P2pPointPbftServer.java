package com.niudong.demo.p2ppbft;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.testng.util.Strings;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.JSONScanner;
import com.niudong.demo.util.SHAUtil;
import com.niudong.demo.util.SimpleMerkleTree;


/**
 * 基于springboot2.0的websocket服务端
 *
 * @author 牛冬
 */
@Component
public class P2pPointPbftServer {
    // 日志记录
    private Logger logger = LoggerFactory.getLogger(P2pPointPbftServer.class);

    // 本机server的WebSocket端口
    // 多机测试时可改变该值
    private int port = 7001;

    // 所有连接到服务端的WebSocket缓存器
    private List<WebSocket> localSockets = new ArrayList<WebSocket>();

    public List<WebSocket> getLocalSockets() {
        return localSockets;
    }

    public void setLocalSockets(List<WebSocket> localSockets) {
        this.localSockets = localSockets;
    }

    /**
     * 初始化P2P Server端
     *
     * @param Server端的端口号port
     */
    //@PostConstruct
    //@Order(1)
    public void initServer() {
        /**
         * 初始化WebSocket的服务端 定义内部类对象socketServer，源于WebSocketServer； new
         * InetSocketAddress(port)是WebSocketServer构造器的参数 InetSocketAddress是(IP地址+端口号)类型，亦即端口地址类型
         */
        final WebSocketServer socketServer = new WebSocketServer(new InetSocketAddress(port)) {
            /**
             * 重写5个事件方法，事件发生时触发对应的方法
             */

            @Override
            // 创建连接成功时触发
            public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
                sendMessage(webSocket, "好未来北京服务端开打");

                // 当成功创建一个WebSocket连接时，将该链接加入连接池
                localSockets.add(webSocket);
            }

            @Override
            // 断开连接时候触发
            public void onClose(WebSocket webSocket, int i, String s, boolean b) {
                logger.info(webSocket.getRemoteSocketAddress() + "客户端与服务器断开连接！");

                // 当客户端断开连接时，WebSocket连接池删除该链接
                localSockets.remove(webSocket);
            }

            @Override
            // 收到客户端发来消息的时候触发
            public void onMessage(WebSocket webSocket, String msg) {
                logger.info("好未来北京接收到客户端消息：" + msg);

                //收到入库的消息则不再发送
                if ("好未来北京客户端开始区块入库啦".equals(msg)) {
                    return;
                }

                // 如果收到的不是JSON话数据，说明是在双方建立连接的过程中。目前连接以及建立完毕，发起投票
                if (!msg.startsWith("{")) {
                    VoteInfo vi = createVoteInfo(VoteEnum.PREPREPARE);

                    sendMessage(webSocket, JSON.toJSONString(vi));
                    logger.info("好未来北京发送到客户端pbft消息：" + JSON.toJSONString(vi));
                    return;
                }

                // 如果是json化数据，则进入到了pbft投票阶段
                JSONObject json = JSON.parseObject(msg);
                if (!json.containsKey("code")) {
                    logger.info("好未来北京收到非JSON化数据");
                }

                int code = json.getIntValue("code");
                if (code == VoteEnum.PREPARE.getCode()) {
                    // 校验hash
                    VoteInfo voteInfo = JSON.parseObject(msg, VoteInfo.class);
                    if (!voteInfo.getHash().equals(SimpleMerkleTree.getTreeNodeHash(voteInfo.getList()))) {
                        logger.info("好未来北京收到错误的JSON化数据");
                        return;
                    }

                    // 校验成功，发送下一个状态的数据
                    VoteInfo vi = createVoteInfo(VoteEnum.COMMIT);
                    sendMessage(webSocket, JSON.toJSONString(vi));
                    logger.info("好未来北京发送到客户端pbft消息：" + JSON.toJSONString(vi));
                }

            }

            @Override
            // 连接发生错误的时候调用,紧接着触发onClose方法
            public void onError(WebSocket webSocket, Exception e) {
                logger.info(webSocket.getRemoteSocketAddress() + "客户端链接错误！");
                localSockets.remove(webSocket);
            }

            @Override
            public void onStart() {
                logger.info("好未来北京的WebSocket Server端启动...");
            }
        };

        socketServer.start();
        logger.info("好未来北京监听socketServer端口" + port);
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
     * 向连接到本机的某客户端发送消息
     *
     * @param ws
     * @param message
     */
    public void sendMessage(WebSocket ws, String message) {
        logger.info("发送给" + ws.getRemoteSocketAddress().getPort() + "的p2p消息是:" + message);
        ws.send(message);
    }

    /**
     * 向所有连接到本机的客户端广播消息
     *
     * @param message：待广播内容
     */
    public void broatcast(String message) {
        if (localSockets.size() == 0 || Strings.isNullOrEmpty(message)) {
            return;
        }

        logger.info("Glad to say broatcast to clients being startted!");
        for (WebSocket socket : localSockets) {
            this.sendMessage(socket, message);
        }
        logger.info("Glad to say broatcast to clients has overred!");
    }
}
