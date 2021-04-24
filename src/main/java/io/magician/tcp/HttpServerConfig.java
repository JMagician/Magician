package io.magician.tcp;

import io.magician.tcp.http.handler.MagicianHandler;
import io.magician.tcp.protocol.parsing.ProtocolParsing;
import io.magician.tcp.protocol.parsing.impl.HttpProtocolParsing;
import io.magician.tcp.protocol.parsing.impl.WebSocketProtocolParsing;
import io.magician.tcp.websocket.handler.WebSocketHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * http服务配置
 */
public class HttpServerConfig {

    /**
     * 端口号
     */
    private static int port;
    /**
     * 最大连接数
     */
    private static int backLog;
    /**
     * 读取超时时间
     */
    private static long readTimeout = 10000;
    /**
     * 写入超时时间
     */
    private static long writeTimeout = 10000;
    /**
     * 每次读取大小
     */
    private static int readSize = 1024;
    /**
     * 单个文件限制
     */
    private static long fileSizeMax = 2*1024*1024;
    /**
     * 上传文件总大小限制
     */
    private static long sizeMax = 10*1024*1024;
    /**
     * 允许几个线程同时解析数据
     */
    private static int readThreadSize = 3;
    /**
     * 允许几个线程同时执行业务逻辑
     */
    private static int executeThreadSize = 3;
    /**
     * 协议解析器
     */
    private static List<ProtocolParsing> protocolParsingList;
    /**
     * 联络器
     */
    private static Map<String, MagicianHandler> martianServerHandlerMap = new HashMap<>();
    /**
     * webSocket联络器
     */
    private static Map<String, WebSocketHandler> martianWebSocketHandlerMap = new HashMap<>();

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        HttpServerConfig.port = port;
    }

    public static int getBackLog() {
        return backLog;
    }

    public static void setBackLog(int backLog) {
        HttpServerConfig.backLog = backLog;
    }

    public static long getReadTimeout() {
        return readTimeout;
    }

    public static void setReadTimeout(long readTimeout) {
        HttpServerConfig.readTimeout = readTimeout;
    }

    public static long getWriteTimeout() {
        return writeTimeout;
    }

    public static void setWriteTimeout(long writeTimeout) {
        HttpServerConfig.writeTimeout = writeTimeout;
    }

    public static int getReadSize() {
        return readSize;
    }

    public static void setReadSize(int readSize) {
        HttpServerConfig.readSize = readSize;
    }

    public static long getFileSizeMax() {
        return fileSizeMax;
    }

    public static void setFileSizeMax(long fileSizeMax) {
        HttpServerConfig.fileSizeMax = fileSizeMax;
    }

    public static long getSizeMax() {
        return sizeMax;
    }

    public static void setSizeMax(long sizeMax) {
        HttpServerConfig.sizeMax = sizeMax;
    }

    public static int getReadThreadSize() {
        return readThreadSize;
    }

    public static void setReadThreadSize(int readThreadSize) {
        if(readThreadSize < 3){
            readThreadSize = 3;
        }
        HttpServerConfig.readThreadSize = readThreadSize;
    }

    public static int getExecuteThreadSize() {
        return executeThreadSize;
    }

    public static void setExecuteThreadSize(int executeThreadSize) {
        if(executeThreadSize < 3){
            executeThreadSize = 3;
        }
        HttpServerConfig.executeThreadSize = executeThreadSize;
    }

    public static Map<String, MagicianHandler> getMartianServerHandlerMap() {
        return martianServerHandlerMap;
    }

    public static Map<String, WebSocketHandler> getMartianWebSocketHandlerMap() {
        return martianWebSocketHandlerMap;
    }

    public static void addMartianServerHandler(String path, MagicianHandler magicianHandler) throws Exception {
        path = path.toUpperCase();
        if(martianServerHandlerMap.containsKey(path)
                || martianWebSocketHandlerMap.containsKey(path)){
            throw new Exception("已经存在地址为"+path+"的handler");
        }
        HttpServerConfig.martianServerHandlerMap.put(path, magicianHandler);
    }

    public static void addMartianWebSocketHandler(String path, WebSocketHandler webSocketHandler) throws Exception {
        if(path.equals("/")){
            throw new Exception("webSocket不可以监听根路径");
        }
        path = path.toUpperCase();
        if(martianServerHandlerMap.containsKey(path)
                || martianWebSocketHandlerMap.containsKey(path)){
            throw new Exception("已经存在地址为"+path+"的handler");
        }
        HttpServerConfig.martianWebSocketHandlerMap.put(path, webSocketHandler);
    }

    public static List<ProtocolParsing> getProtocolParsingList() {
        if(HttpServerConfig.protocolParsingList == null){
            HttpServerConfig.protocolParsingList = new ArrayList<ProtocolParsing>();
            HttpServerConfig.protocolParsingList.add(new HttpProtocolParsing());
            HttpServerConfig.protocolParsingList.add(new WebSocketProtocolParsing());
        }
        return protocolParsingList;
    }

    public static void addProtocolParsingList(ProtocolParsing protocolParsing) {
        if(HttpServerConfig.protocolParsingList == null){
            HttpServerConfig.protocolParsingList = new ArrayList<ProtocolParsing>();
        }
        HttpServerConfig.protocolParsingList.add(protocolParsing);
    }
}
