package io.magician.tcp;

import io.magician.tcp.http.handler.MagicianHandler;
import io.magician.tcp.protocol.parsing.ProtocolParsing;
import io.magician.tcp.websocket.handler.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.channels.*;

/**
 * http服务创建
 */
public class HttpServerCreate {

    private Logger log = LoggerFactory.getLogger(HttpServerCreate.class);

    /**
     * 绑定端口
     * @param port
     * @return
     */
    public HttpServerCreate bind(int port){
        bind(port,100);
        return this;
    }

    /**
     * 绑定端口，设置最大连接数
     * @param port
     * @param backLog
     * @return
     */
    public HttpServerCreate bind(int port, int backLog){
        HttpServerConfig.setPort(port);
        HttpServerConfig.setBackLog(backLog);
        return this;
    }

    /**
     * 设置读取超时时间,
     * 暂时没用到
     * @param readTimeout
     * @return
     */
    @Deprecated
    public HttpServerCreate readTimeout(long readTimeout){
        HttpServerConfig.setReadTimeout(readTimeout);
        return this;
    }
    /**
     * 设置写入超时时间
     * 暂时没用到
     * @param writeTimeout
     * @return
     */
    @Deprecated
    public HttpServerCreate writeTimeout(long writeTimeout){
        HttpServerConfig.setWriteTimeout(writeTimeout);
        return this;
    }

    /**
     * 设置每次读取大小
     * @param readSize
     * @return
     */
    public HttpServerCreate readSize(int readSize){
        HttpServerConfig.setReadSize(readSize);
        return this;
    }

    /**
     * 单个文件限制
     * @param fileSizeMax
     * @return
     */
    public HttpServerCreate fileSizeMax(long fileSizeMax){
        HttpServerConfig.setFileSizeMax(fileSizeMax);
        return this;
    }

    /**
     * 文件总大小限制
     * @param sizeMax
     * @return
     */
    public HttpServerCreate sizeMax(long sizeMax){
        HttpServerConfig.setSizeMax(sizeMax);
        return this;
    }

    /**
     * 设置允许几个线程同时解析数据
     * @param threadSize
     * @return
     */
    public HttpServerCreate readThreadSize(int threadSize){
        HttpServerConfig.setReadThreadSize(threadSize);
        return this;
    }

    /**
     * 设置允许几个线程同时执行业务逻辑
     * @param threadSize
     * @return
     */
    public HttpServerCreate execThreadSize(int threadSize){
        HttpServerConfig.setExecuteThreadSize(threadSize);
        return this;
    }

    /**
     * 添加协议解析器
     * @param protocolParsing
     * @return
     */
    public HttpServerCreate addProtocolParsing(ProtocolParsing protocolParsing){
        HttpServerConfig.addProtocolParsingList(protocolParsing);
        return this;
    }

    /**
     * 设置联络器
     * @param magicianHandler
     * @return
     */
    public HttpServerCreate httpHandler(String path, MagicianHandler magicianHandler) throws Exception {
        HttpServerConfig.addMartianServerHandler(path, magicianHandler);
        return this;
    }

    /**
     * 设置联络器
     * @param webSocketHandler
     * @return
     */
    public HttpServerCreate webSocketHandler(String path, WebSocketHandler webSocketHandler) throws Exception {
        HttpServerConfig.addMartianWebSocketHandler(path, webSocketHandler);
        return this;
    }

    /**
     * 开启服务
     * @throws Exception
     */
    public void start() throws Exception {

        /* 开始监听端口 */
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(HttpServerConfig.getPort()), HttpServerConfig.getBackLog());

        /* 标识服务是否已经启动 */
        log.info("启动成功");

        /* 监听Http */
        HttpServerMonitor.doMonitor(serverSocketChannel);
    }
}
