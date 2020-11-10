package com.duansq.demo.p2p;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * p2p客户端
 */
public class P2PClient {
    //所有客户端WebSocket的连接池
    private List<WebSocket> sockets = new ArrayList<WebSocket>();

    public List<WebSocket> getSockets() {
        return sockets;
    }

    /**
     * 连接到peer
     */
    public void connectPeer(String peer) {
        try {
            /**
             * The WebSocketClient is an abstract class that expects a valid "ws://" URI to connect to.
             * When connected, an instance recieves important events related to the life of the connection.
             * A subclass must implement onOpen, onClose, and onMessage to be useful.
             * An instance can send messages to it's connected server via the send method.
             *
             * 创建有一个WebSocket的客户端
             */
            final WebSocketClient socketClient = new WebSocketClient(new URI(peer)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    write(this, "客户端打开");
                    sockets.add(this);
                }

                @Override
                public void onMessage(String msg) {
                    System.out.println("收到服务端发送的消息:" + msg);
                }

                @Override
                public void onClose(int i, String msg, boolean b) {
                    System.out.println("客户端关闭");
                    sockets.remove(this);
                }

                @Override
                public void onError(Exception e) {
                    System.out.println("客户端报错");
                    sockets.remove(this);
                }
            };
            //客户端 开始连接服务端
            socketClient.connect();
        } catch (URISyntaxException e) {
            System.out.println("连接错误:" + e.getMessage());
        }
    }

    /**
     * 向服务端发送消息
     * 当前WebSocket的远程Socket地址，就是服务器端
     *
     * @param ws：
     * @param message
     */
    public void write(WebSocket ws, String message) {
        System.out.println("发送给" + ws.getRemoteSocketAddress().getPort() + "的p2p消息:" + message);
        ws.send(message);
    }

    /**
     * 向所有服务端广播消息
     *
     * @param message
     */
    public void broatcast(String message) {
        if (sockets.size() == 0) {
            return;
        }
        System.out.println("======广播消息开始：");
        for (WebSocket socket : sockets) {
            this.write(socket, message);
        }
        System.out.println("======广播消息结束");
    }
}
