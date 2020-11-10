package com.niudong.demo.p2p;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.testng.util.Strings;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于springboot2.0的websocket服务端
 *
 * @author 牛冬
 */
@Order(1)
@Component
public class P2pPointServer {
    // 日志记录
    private Logger logger = LoggerFactory.getLogger(P2pPointServer.class);

    // 本机server的WebSocket端口
    // 多机测试时可改变该值
    private int port = 7001;
    private int port2 = 7002;
    private int port3 = 7003;
    private int port4 = 7004;

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
    @PostConstruct
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
                sendMessage(webSocket, "服务器端打开");

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
                logger.info("服务器端收到客户端消息：" + msg);
                sendMessage(webSocket, "收到消息");
            }

            @Override
            // 连接发生错误的时候调用,紧接着触发onClose方法
            public void onError(WebSocket webSocket, Exception e) {
                logger.info(webSocket.getRemoteSocketAddress() + "客户端链接错误！");
                localSockets.remove(webSocket);
            }

            @Override
            public void onStart() {
                logger.info("WebSocket Server服务器端启动...");
            }
        };

        socketServer.start();
        logger.info("服务器端监听socketServer端口" + port);
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
