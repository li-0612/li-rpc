package com.liyan.rpc.protocol;

import cn.hutool.core.util.IdUtil;
import com.liyan.rpc.constant.RpcConstant;
import com.liyan.rpc.model.RpcRequest;
import io.vertx.core.buffer.Buffer;
import org.junit.Assert;
import org.junit.Test;

public class ProtocolMessageTest {
    @Test
    public void test() throws Exception {
        ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setMagic(ProtocolConstant.MAGIC);
        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
        header.setSerializer((byte) ProtocolMessageSerializerEnum.JDK.getKey());
        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
        header.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());
        header.setRequestId(IdUtil.getSnowflakeNextId());
        header.setBodyLength(0);
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName("com.liyan.rpc.service.HelloService");
        rpcRequest.setMethodName("sayHello");
        rpcRequest.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        rpcRequest.setParameterTypes(new Class[]{String.class});
        rpcRequest.setArgs(new Object[]{"liyan"});
        protocolMessage.setHeader(header);
        protocolMessage.setBody(rpcRequest);
        System.out.println(ProtocolMessageEncoder.encode(protocolMessage));
        Buffer encode = ProtocolMessageEncoder.encode(protocolMessage);
        ProtocolMessage<?> message = ProtocolMessageDecoder.decode(encode);
        System.out.println(message);
        Assert.assertNotNull(message);
    }
}
