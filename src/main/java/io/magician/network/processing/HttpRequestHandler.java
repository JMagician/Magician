package io.magician.network.processing;

import io.magician.application.distribution.Distribution;
import io.magician.common.constant.CommonConstant;
import io.magician.common.constant.HttpConstant;
import io.magician.network.processing.enums.ParamType;
import io.magician.network.processing.exchange.HttpExchange;
import io.magician.network.processing.model.ParamModel;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理请求
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object obj) throws Exception {
        if (obj instanceof FullHttpRequest) {
            // 处理http请求
            FullHttpRequest fullHttpRequest = (FullHttpRequest)obj;

            HttpExchange exchange = new HttpExchange();
            exchange.setUrl(fullHttpRequest.uri());
            exchange.setMethod(fullHttpRequest.method());
            exchange.setHttpHeaders(fullHttpRequest.headers());
            exchange.setFullHttpRequest(fullHttpRequest);
            exchange.setChannelHandlerContext(channelHandlerContext);

            exchange = parse(fullHttpRequest, exchange);

            Distribution.execute(exchange);
        } else if (obj instanceof WebSocketFrame) {
            // 处理websocket消息
            WebSocketFrame webSocketFrame = (WebSocketFrame)obj;
            Distribution.handleWebSocketFrame(channelHandlerContext, webSocketFrame);
        }
    }

    /**
     * 解析请求参数
     *
     * @return 包含所有请求参数的键值对, 如果没有参数, 则返回空Map
     */
    public HttpExchange parse(FullHttpRequest fullReq, HttpExchange exchange) throws Exception {
        HttpMethod method = fullReq.method();

        Map<String, ParamModel> paramMap = new HashMap<>();

        if (HttpMethod.GET == method) {
            QueryStringDecoder decoder = new QueryStringDecoder(fullReq.uri());
            decoder.parameters().entrySet().forEach( entry -> {

                ParamModel paramModel = paramMap.get(entry.getKey());
                if (paramModel == null) {
                    paramModel = new ParamModel();
                }
                paramModel.setType(ParamType.OTHER);
                paramModel.setValue(entry.getValue());

                paramMap.put(entry.getKey(), paramModel);
            });
        } else {
            if (isJSON(fullReq.headers().get(HttpConstant.CONTENT_TYPE))) {
                ByteBuf buf = fullReq.content();
                byte[] content = new byte[buf.capacity()];
                buf.readBytes(content);
                exchange.setJsonParam(new String(content, CommonConstant.ENCODING));
                return exchange;
            }

            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(fullReq);
            decoder.offer(fullReq);

            List<InterfaceHttpData> paramList = decoder.getBodyHttpDatas();

            for (InterfaceHttpData param : paramList) {
                if (param instanceof FileUpload) {
                    FileUpload fileUpload = (FileUpload) param;

                    ParamModel paramModel = paramMap.get(fileUpload.getName());
                    if (paramModel == null) {
                        paramModel = new ParamModel();
                    }
                    paramModel.setType(ParamType.FILE);
                    paramModel.setValueItem(fileUpload);

                    paramMap.put(fileUpload.getName(), paramModel);
                } else {
                    Attribute data = (Attribute) param;

                    ParamModel paramModel = paramMap.get(data.getName());
                    if (paramModel == null) {
                        paramModel = new ParamModel();
                    }
                    paramModel.setType(ParamType.OTHER);
                    paramModel.setValueItem(data.getValue());

                    paramMap.put(data.getName(), paramModel);
                }
            }
        }

        exchange.setParam(paramMap);
        return exchange;
    }

    /**
     * 是否是json格式
     * @param contentType 内容类型
     * @return
     */
    private static boolean isJSON(String contentType){
        if(contentType == null){
            return false;
        }
        contentType = contentType.toLowerCase();

        return contentType.startsWith(HttpConstant.CONTENT_TYPE_JSON.toLowerCase()) || contentType.equals(HttpConstant.CONTENT_TYPE_JSON.toLowerCase());
    }
}
