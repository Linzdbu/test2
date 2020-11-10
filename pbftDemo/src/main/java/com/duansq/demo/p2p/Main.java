package com.duansq.demo.p2p;

/**
 * P2P网络中每个节点即是服务端又是客户端
 */
public class Main {
    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            System.out.println("主方法没有参数！");
            return;
        }
        P2PServer p2pServer = new P2PServer();
        P2PClient p2pClient = new P2PClient();
        int p2pPort = Integer.valueOf(args[0]);
        // 启动p2p服务端
        p2pServer.initServer(p2pPort);
        if (args.length == 2 && args[1] != null) {
            // 作为p2p客户端连接p2p服务端
            p2pClient.connectPeer(args[1]);
        }
    }
}
