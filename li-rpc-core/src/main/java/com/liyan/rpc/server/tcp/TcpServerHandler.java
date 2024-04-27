package com.liyan.rpc.server.tcp;

import com.liyan.rpc.model.RpcRequest;
import com.liyan.rpc.model.RpcResponse;
import com.liyan.rpc.protocol.*;
import com.liyan.rpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
@Slf4j
public class TcpServerHandler implements Handler<NetSocket> {
    @Override
    public void handle(NetSocket netSocket) {
        //处理连接
        TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            //接收请求，解码
            ProtocolMessage<RpcRequest> protocolMessage;
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (Exception e) {
                throw new RuntimeException("协议消息解码错误", e);
            }
            RpcRequest rpcRequest = protocolMessage.getBody();
            //处理请求
            //构造响应结果对象
            RpcResponse rpcResponse = new RpcResponse();
            //获取服务 通过反射调用
            try {
                //反射调用
                final Class<?> service = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = service.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                final Object result = method
                        .invoke(service.newInstance(), rpcRequest.getArgs());
                //封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("调用成功");
                log.info("调用成功={}",result);
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage("调用失败");
                rpcResponse.setException(e);
            }
            //发送响应，编码
            ProtocolMessage.Header header = protocolMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            header.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());
            try {
                Buffer encode = ProtocolMessageEncoder.encode(new ProtocolMessage<>(header, rpcResponse));
                netSocket.write(encode);
            } catch (Exception e) {
                throw new RuntimeException("协议消息编码错误", e);
            }
        });
        netSocket.handler(tcpBufferHandlerWrapper);
    }
}
