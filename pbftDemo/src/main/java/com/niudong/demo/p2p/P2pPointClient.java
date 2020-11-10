package com.niudong.demo.p2p;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.testng.util.Strings;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


/**
 * 基于springboot2.0的websocket客户端
 *
 * @author 牛冬
 */
@Order(1)
@Component
public class P2pPointClient {
    // 日志记录
    private Logger logger = LoggerFactory.getLogger(P2pPointClient.class);

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
    @PostConstruct
    //@Order(2)
    public void connectPeer() {
        try {
            // 创建WebSocket的客户端
            final WebSocketClient socketClient = new WebSocketClient(new URI(wsUrl)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    sendMessage(this, "客户端打开");

                    localSockets.add(this);
                }

                @Override
                public void onMessage(String msg) {
                    logger.info("服务端发送的消息:" + msg);
                }

                @Override
                public void onClose(int i, String msg, boolean b) {
                    logger.info("客户端关闭");
                    localSockets.remove(this);
                }

                @Override
                public void onError(Exception e) {
                    logger.info("客户端报错");
                    localSockets.remove(this);
                }
            };
            // 客户端 开始连接服务端
            socketClient.connect();
        } catch (URISyntaxException e) {
            logger.info("客户端连接错误:" + e.getMessage());
        }
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
