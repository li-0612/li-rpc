package com.liyan.rpc.protocol;


import com.google.rpc.context.AttributeContext;
import com.liyan.rpc.model.RpcRequest;
import com.liyan.rpc.model.RpcResponse;
import com.liyan.rpc.serializer.Serializer;
import com.liyan.rpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

/**
 * 协议消息解码器
 */
public class ProtocolMessageDecoder {
    public static ProtocolMessage<?> decode(Buffer buffer) throws Exception{
        //分别从指定的位置读出Buffer
         ProtocolMessage.Header header = new ProtocolMessage.Header();
        byte magic = buffer.getByte(0);
        //校验魔数
        if (magic != ProtocolConstant.MAGIC) {
            throw new Exception("消息 magic 非法");
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));
        //解决粘包问题，只读指定长度数据
        byte[] bodyBytes = buffer.getBytes(17, 17 + header.getBodyLength());
        //解析消息体
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getByKey(header.getSerializer());

        if (serializerEnum == null) {
            throw new Exception("序列化消息的协议不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getByKey(header.getType());
        if (messageTypeEnum == null) {
            throw new Exception("序列化消息类型不存在");
        }
        switch (messageTypeEnum) {
            case REQUEST:
                RpcRequest request = serializer.deserialize(bodyBytes, RpcRequest.class);
                return new ProtocolMessage<>(header, request);
            case RESPONSE:
                RpcResponse rpcResponse = serializer.deserialize(bodyBytes, RpcResponse.class);
                return new ProtocolMessage<>(header, rpcResponse);
            case OTHERS:
            case HEART_BEAT:
            default:
                throw new Exception("暂不支持该消息类型");
        }


    }
}
