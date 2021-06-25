package io.magician.tcp.codec.impl.websocket.parsing;

import io.magician.tcp.codec.impl.websocket.connection.WebSocketExchange;
import io.magician.tcp.codec.impl.websocket.constant.WebSocketEnum;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * websocket报文解析
 */
public class WebSocketMessageParsing {

    /**
     * 报文数据
     */
    private ByteArrayOutputStream outputStream;

    /**
     * webSocket数据中转器
     */
    private WebSocketExchange webSocketExchange;

    /**
     * 构造函数
     * @param outputStream
     * @param webSocketExchange
     */
    public WebSocketMessageParsing(ByteArrayOutputStream outputStream, WebSocketExchange webSocketExchange){
        this.outputStream = outputStream;
        this.webSocketExchange = webSocketExchange;
    }

    /**
     * 解析报文
     * @return
     * @throws Exception
     */
    public WebSocketExchange completed() throws Exception {

        byte[] bytesData = outputStream.toByteArray();
        if (bytesData.length < 1) {
            return null;
        }

        int opcode = bytesData[0] & 0x0f;
        if (opcode == 8) {
            webSocketExchange.setWebSocketEnum(WebSocketEnum.CLOSE);
            return webSocketExchange;
        }
        if (bytesData.length < 2) {
            return null;
        }

        int formIndex = 6;

        int payloadLength = (bytesData[1] & 0x7f);
        if(payloadLength < 1){
            return null;
        }
        if(payloadLength == 126){
            // TODO
        } else if(payloadLength == 127){
            // TODO
        }

        if(bytesData.length < (payloadLength + formIndex)){
            return null;
        }
        byte[] mask = Arrays.copyOfRange(bytesData, 2, formIndex);
        byte[] payloadData = Arrays.copyOfRange(bytesData, formIndex, payloadLength + formIndex);

        if(payloadData.length < payloadLength){
            return null;
        }

        for (int i = 0; i < payloadData.length; i++) {
            payloadData[i] = (byte) (payloadData[i] ^ mask[i % 4]);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(payloadData);

        webSocketExchange.setLength(formIndex + outputStream.size());
        webSocketExchange.setOutputStream(outputStream);
        webSocketExchange.setWebSocketEnum(WebSocketEnum.MESSAGE);

        return webSocketExchange;
    }
}
