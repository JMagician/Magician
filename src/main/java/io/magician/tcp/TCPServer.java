package io.magician.tcp;

import io.magician.common.event.EventGroup;
import io.magician.tcp.codec.ProtocolCodec;
import io.magician.tcp.load.LoadTCPResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.channels.*;

/**
 * tcp服务创建
 */
public class TCPServer {

    private Logger log = LoggerFactory.getLogger(TCPServer.class);

    /**
     * 连接超时时间
     */
    private int soTimeout = 10000;
    /**
     * io事件执行器组合
     * 每个端口对应里面的一个 事件执行器
     */
    private EventGroup ioEventGroup;
    /**
     * 业务事件执行器组合
     * 每个连接对应里面的一个事件执行器，一个事件执行器对应多个连接的多个任务
     */
    private EventGroup workerEventGroup;
    /**
     * 配置
     */
    private TCPServerConfig tcpServerConfig;

    /**
     * 创建一个默认事件组合的服务，用于监听端口
     */
    public TCPServer() {
        this(
                new EventGroup(1),
                new EventGroup(3)
        );
    }

    /**
     * 自定义事件组合，创建服务用于监听端口
     *
     * @param ioEventGroup
     * @param workerEventGroup
     */
    public TCPServer(EventGroup ioEventGroup, EventGroup workerEventGroup) {
        this.ioEventGroup = ioEventGroup;
        this.workerEventGroup = workerEventGroup;
        this.tcpServerConfig = new TCPServerConfig();
    }

    /**
     * 连接超时时间
     *
     * @param soTimeout
     * @return
     */
    public TCPServer soTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
        return this;
    }

    /**
     * 添加配置
     *
     * @param tcpServerConfig
     * @return
     */
    public TCPServer config(TCPServerConfig tcpServerConfig) {
        tcpServerConfig.setMagicianHandlerMap(this.tcpServerConfig.getMagicianHandlerMap());
        tcpServerConfig.setWebSocketHandlerMap(this.tcpServerConfig.getWebSocketHandlerMap());
        tcpServerConfig.setProtocolCodec(this.tcpServerConfig.getProtocolCodec());

        this.tcpServerConfig = tcpServerConfig;
        return this;
    }

    /**
     * 设置协议解析器
     *
     * @param protocolCodec
     * @return
     */
    public TCPServer protocolCodec(ProtocolCodec protocolCodec) {
        this.tcpServerConfig.setProtocolCodec(protocolCodec);
        return this;
    }

    /**
     * 扫描所需资源
     * @param packageName
     * @return
     */
    public TCPServer scan(String packageName) throws Exception {
        LoadTCPResource.loadHandler(packageName, this.tcpServerConfig);
        return this;
    }

    /**
     * 开启服务
     *
     * @param port
     * @throws Exception
     */
    public void bind(int port) throws Exception {
        bind(port, 1000);
    }

    /**
     * 开启服务
     *
     * @param port
     * @param backLog
     * @throws Exception
     */
    public void bind(int port, int backLog) throws Exception {

        /* 开始监听端口 */
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().setSoTimeout(soTimeout);
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(port), backLog);

        /* 开启NIOSelector监听器 */
        ioEventGroup
                .getEventRunner()
                .addEvent(new TCPServerMonitorTask(serverSocketChannel, tcpServerConfig, ioEventGroup, workerEventGroup));

        /* 标识服务是否已经启动 */
        log.info("启动TCP服务成功, [port:" + port + "]");
    }
}
