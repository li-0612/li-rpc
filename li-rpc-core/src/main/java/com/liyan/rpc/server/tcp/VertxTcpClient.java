package com.liyan.rpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.liyan.rpc.RpcApplication;
import com.liyan.rpc.config.RpcConfig;
import com.liyan.rpc.model.RpcRequest;
import com.liyan.rpc.model.RpcResponse;
import com.liyan.rpc.model.ServiceMetaInfo;
import com.liyan.rpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class VertxTcpClient {

    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) {
        //发送tcp请求
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseCompletableFuture = new CompletableFuture<>();
        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(),
                result -> {
                    if (!result.succeeded()) {
                        System.out.println("connect to tcp server failed");
                        return;
                    }
                    NetSocket socket = result.result();
                    //发送数据
                    //构造消息
                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                    ProtocolMessage.Header header = new ProtocolMessage.Header();
                    header.setMagic(ProtocolConstant.MAGIC);
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    header.setSerializer((byte) ProtocolMessageSerializerEnum.getByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                    //生成全局请求id
                    header.setRequestId(IdUtil.getSnowflakeNextId());
                    protocolMessage.setHeader(header);
                    protocolMessage.setBody(rpcRequest);
                    //编码请求

                    try {
                        Buffer encode = ProtocolMessageEncoder.encode(protocolMessage);
//                        System.out.println("发送请求：" + new String(encode.getBytes(), StandardCharsets.UTF_8));
                        socket.write(encode);
                    } catch (Exception e) {
                        throw new RuntimeException("协议消息编码错误", e);
                    }
                    //接收响应
                    TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
                        ProtocolMessage<RpcResponse> decode = null;
                        try {
                            decode = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                        } catch (Exception e) {
                            throw new RuntimeException("协议消息解码错误", e);
                        }
                        responseCompletableFuture.complete(decode.getBody());

                    });
                    socket.handler(tcpBufferHandlerWrapper);

                });
        RpcResponse rpcResponse;
        try {
            rpcResponse = responseCompletableFuture.get();
//            System.out.println("接收到响应==rpcResponse = " + rpcResponse.getData());
        } catch (Exception e) {
            throw new RuntimeException("获取响应数据异常", e);
        }
        netClient.close();
        return rpcResponse;

    }


    public void start(int port) {

        Vertx vertx = Vertx.vertx();
        vertx.createNetClient().connect(port, "127.0.0.1", result -> {
            if (result.succeeded()) {
                System.out.println("connected to tcp server");
                NetSocket socket = result.result();
                //发送数据
                for (int i = 0; i < 1000; i++) {
//                    socket.write("hello,server!hello,server!hello,server!hello,server!");
                    Buffer buffer = Buffer.buffer();
                    String str = "hello,server!hello,server!hello,server!hello,server!";
                    buffer.appendInt(0);
                    buffer.appendInt(str.getBytes(StandardCharsets.UTF_8).length);
                    buffer.appendBytes(str.getBytes(StandardCharsets.UTF_8));
                    socket.write(buffer);
                }
                //接收响应
                socket.handler(buffer -> {
                    System.out.println("receive response from server:" + buffer.toString());
                });
            } else {
                System.out.println("failed to connect to tcp server");
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpClient().start(8084);
    }
}
