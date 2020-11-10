package com.niudong.demo.tiop2p;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.tio.server.ServerGroupContext;
import org.tio.server.TioServer;
import org.tio.server.intf.ServerAioHandler;
import org.tio.server.intf.ServerAioListener;

/**
 * 基于t-io的区块链底p2p网络平台的服务端
 *
 * @author 牛冬
 */
@Component
public class BlockChainServerStarter {
    // 日志记录
    private Logger logger = LoggerFactory.getLogger(BlockChainServerStarter.class);

    // handler, 包括编码、解码、消息处理
    public static ServerAioHandler aioHandler = new BlockChainServerAioHandler();

    // 事件监听器，可以为null，但建议自己实现该接口，可以参考showcase了解些接口
    public static ServerAioListener aioListener = null;

    // 一组连接共用的上下文对象
    public static ServerGroupContext serverGroupContext =
            new ServerGroupContext("hello-tio-server", aioHandler, aioListener);

    // tioServer对象
    public static TioServer tioServer = new TioServer(serverGroupContext);

    // 有时候需要绑定ip，不需要则null
    public static String serverIp = null;//Const.SERVER;

    // 监听的端口
    public static int serverPort = Const.PORT;

    //@PostConstruct
    //@Order(1)
    public void start() {
        try {
            logger.info("好未来服务端即将启动");

            serverGroupContext.setHeartbeatTimeout(Const.TIMEOUT);
            tioServer.start(serverIp, serverPort);

            logger.info("好未来服务端启动完毕");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
